import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.utility.Delay;

public class PizzaDelivery {
	private static final double START[] = {0, 0, 0};

	// Define sensors
	private static EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S1);
	private static EV3ColorSensor color = new EV3ColorSensor(SensorPort.S2);
	private static EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S4);
	
	// Define actuators
	private static NXTRegulatedMotor ultrasonicMotor = Motor.B;
	private static NXTRegulatedMotor leftMotor = Motor.A;
	private static NXTRegulatedMotor rightMotor = Motor.D;
	private static NXTRegulatedMotor armMotor = Motor.C;
	
	// Define state variables
	private int targetHouse;
	private double roadCoords[];
	private double pizzaCoords[];
	private String deliverySide;
	
	private KalmanFilterLocalizer currentPose;

	public PizzaDelivery(PizzaDeliverySettings settings) {
		targetHouse = settings.getHouseNumber();
		roadCoords = settings.getRoadCoords();
		deliverySide = settings.getDeliverySide();
		pizzaCoords = settings.getPizzaCoords();
		
		currentPose = new KalmanFilterLocalizer(START, leftMotor, rightMotor, gyro);
	}

	private void initialize() {
		armMotor.rotateTo(0);
		
		gyro.reset();
		Delay.msDelay(1000);
	}
	
	private void driveToPizza() {
		PointToPointDriver driver = new PointToPointDriver(currentPose, pizzaCoords, leftMotor, rightMotor);
		driver.driveUntilStopped();
	}

	private void pickUpPizza() {
		leftMotor.rotate((int) Math.round(-3/PizzaDeliveryUtils.DIST_TO_DEG), true);
		rightMotor.rotate((int) Math.round(-3/PizzaDeliveryUtils.DIST_TO_DEG));
		armMotor.rotateTo(180);
		leftMotor.rotate((int) Math.round(5/PizzaDeliveryUtils.DIST_TO_DEG), true);
		rightMotor.rotate((int) Math.round(5/PizzaDeliveryUtils.DIST_TO_DEG));
		currentPose.updateAngle();
		currentPose.updateDistance();
	}

	private void driveToRoad() {
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose, roadCoords, leftMotor, rightMotor, obstacleDetector);
			gotToTarget = driver.driveUntilStopped();
			if (gotToTarget) break;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider(currentPose, leftMotor, rightMotor, ultrasonic, ultrasonicMotor);
			obstacleAvoider.drivePastObstacle();
		}
	}

	private void followRoadToHouse() {
		HouseCounter houseCounter =  new HouseCounter(targetHouse, deliverySide, ultrasonic, ultrasonicMotor);
		LineFollower follower = new LineFollower(leftMotor, rightMotor, color, houseCounter);
		follower.driveUntilStopped();
	}
	
	private void turnToFaceHouse() {
		double desiredPose[] = currentPose.getPose();
		if (deliverySide == "LEFT") {
			desiredPose[2] += 90;
			PointToPointDriver driver = new PointToPointDriver(currentPose, desiredPose, leftMotor, rightMotor);
			driver.driveUntilStopped();
		} else if (deliverySide == "RIGHT") {
			desiredPose[2] -= 90;
			PointToPointDriver driver = new PointToPointDriver(currentPose, desiredPose, leftMotor, rightMotor);
			driver.driveUntilStopped();
		}
	}
	
	private void dropOffPizza() {
		armMotor.rotateTo(0);
	}

	private void driveToStart() {
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose, START, leftMotor, rightMotor, obstacleDetector);
			gotToTarget = driver.driveUntilStopped();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider(currentPose, leftMotor, rightMotor, ultrasonic, ultrasonicMotor);
			obstacleAvoider.drivePastObstacle();
		}
	}

	private void deliver() {
		initialize();
		driveToPizza();
		pickUpPizza();
		driveToRoad();
		followRoadToHouse();
		turnToFaceHouse();
		dropOffPizza();
		driveToStart();
	}

	public static void main(String[] args) {
		//PizzaDeliverySettings settings = new PizzaDeliverySettings();
		PizzaDeliverySettings settings = new PizzaDeliverySettings(true);
		PizzaDelivery delivery = new PizzaDelivery(settings);
		delivery.deliver();
	}
}
