import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class PizzaDelivery {
	private static final double START[] = {0, 0, 0};

	// Define sensors and actuators
	static EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S1);
	static EV3ColorSensor color = new EV3ColorSensor(SensorPort.S2);
	static EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S3);
	
	static NXTRegulatedMotor ultraSonicMotor = Motor.A;
	static NXTRegulatedMotor leftMotor = Motor.B;
	static NXTRegulatedMotor rightMotor = Motor.C;
	static NXTRegulatedMotor armMotor = Motor.D;
	
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

	private static PizzaDeliverySettings getInputs() {
		return new PizzaDeliverySettings();
	}
	
	private void driveToPizza() {
		PointToPointDriver driver = new PointToPointDriver(currentPose.getPose(), pizzaCoords);
		driver.driveUntilStopped();
	}

	private void pickUpPizza() {
		armMotor.rotateTo(180);
	}

	private void driveToRoad() {
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose.getPose(), roadCoords, obstacleDetector);
			gotToTarget = driver.driveUntilStopped();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider();
			obstacleAvoider.drivePastObstacle();
		}
	}

	private void followRoadToHouse() {
		HouseCounter houseCounter =  new HouseCounter(targetHouse, ultrasonic);
		LineFollower follower = new LineFollower(houseCounter);
		follower.driveUntilStopped();
	}
	
	private void turnToFaceHouse() {
		double desiredPose[] = currentPose.getPose();
		if (deliverySide == "LEFT") {
			desiredPose[2] += 90;
			PointToPointDriver driver = new PointToPointDriver(currentPose.getPose(), desiredPose);
			driver.driveUntilStopped();
		} else if (deliverySide == "RIGHT") {
			desiredPose[2] -=  - 90;
			PointToPointDriver driver = new PointToPointDriver(currentPose.getPose(), desiredPose);
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
			PointToPointDriver driver = new PointToPointDriver(currentPose.getPose(), START, obstacleDetector);
			gotToTarget = driver.driveUntilStopped();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider();
			obstacleAvoider.drivePastObstacle();
		}
	}

	private void deliver() {
		driveToPizza();
		pickUpPizza();
		driveToRoad();
		followRoadToHouse();
		turnToFaceHouse();
		dropOffPizza();
		driveToStart();
	}

	public static void main(String[] args) {
		PizzaDeliverySettings settings = getInputs();
		PizzaDelivery delivery = new PizzaDelivery(settings);
		delivery.deliver();
	}
}
