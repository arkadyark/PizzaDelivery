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
	private static EV3UltrasonicSensor ultrasonic = new EV3UltrasonicSensor(SensorPort.S4);
	private static EV3ColorSensor color = new EV3ColorSensor(SensorPort.S1);
	private static EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S2);
	
	// Define actuators
	private static NXTRegulatedMotor ultrasonicMotor = Motor.B;
	private static NXTRegulatedMotor leftMotor = Motor.A;
	private static NXTRegulatedMotor rightMotor = Motor.C;
	private static NXTRegulatedMotor armMotor = Motor.D;
	
	// Define state variables
	private int targetHouse;
	private double roadCoords[];
	private double pizzaCoords[];
	private String deliverySide;
	private KalmanFilterLocalizer currentPose;
	
	// For logging purposes
	public static String status;

	public PizzaDelivery(PizzaDeliverySettings settings) {
		status = "CONSTRUCTING";

		targetHouse = settings.getHouseNumber();
		roadCoords = settings.getRoadCoords();
		deliverySide = settings.getDeliverySide();
		pizzaCoords = settings.getPizzaCoords();
		
		currentPose = new KalmanFilterLocalizer(START, leftMotor, rightMotor, gyro);
		
		PizzaDeliveryUtils.displayStatus(currentPose,
				"house " + Integer.toString(targetHouse) + " on the " + deliverySide.toLowerCase());
	}
	
	private void initialize() {
		status = "INITIALIZING";
		PizzaDeliveryUtils.displayStatus(currentPose);
		
		armMotor.rotateTo(0);
		ultrasonicMotor.rotateTo(0);
		
		leftMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		
		PizzaDeliveryUtils.displayStatus(currentPose, "resetting gyroscope");
		gyro.reset();
		Delay.msDelay(1000);
	}
	
	private void driveToPizza() {
		status = "DRIVING TO PIZZA";
		PizzaDeliveryUtils.displayStatus(currentPose);
		
		PointToPointDriver driver = new PointToPointDriver(currentPose, pizzaCoords, leftMotor, rightMotor);
		driver.driveUntilStopped();
		currentPose = driver.getCurrentPose();
	}

	private void pickUpPizza() {
		status = "PICKING UP PIZZA";
		
		PizzaDeliveryUtils.displayStatus(currentPose, "backing in");
		leftMotor.rotate((int) Math.round(-10*PizzaDeliveryUtils.DIST_TO_DEG), true);
		rightMotor.rotate((int) Math.round(-10*PizzaDeliveryUtils.DIST_TO_DEG));
		currentPose.update();
		PizzaDeliveryUtils.displayStatus(currentPose, "grabbing");
		armMotor.rotateTo(180);
		PizzaDeliveryUtils.displayStatus(currentPose, "driving out");
		leftMotor.rotate((int) Math.round(10*PizzaDeliveryUtils.DIST_TO_DEG), true);
		rightMotor.rotate((int) Math.round(10*PizzaDeliveryUtils.DIST_TO_DEG));
		currentPose.update();
	}

	private void driveToRoad() {
		status = "DRIVING TO ROAD";
		PizzaDeliveryUtils.displayStatus(currentPose);
		
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose, roadCoords, leftMotor, rightMotor, obstacleDetector);
			PizzaDeliveryUtils.displayStatus(currentPose, "driving along");
			gotToTarget = driver.driveUntilStopped();
			currentPose = driver.getCurrentPose();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider(currentPose, leftMotor, rightMotor, ultrasonic, ultrasonicMotor);
			PizzaDeliveryUtils.displayStatus(currentPose, "avoiding an obstacle");
			obstacleAvoider.drivePastObstacle();
			currentPose = obstacleAvoider.getCurrentPose();
		}
	}

	private void followRoadToHouse() {
		status = "FOLLOWING ROAD TO HOUSE";
		PizzaDeliveryUtils.displayStatus(currentPose);
		
		HouseCounter houseCounter =  new HouseCounter(targetHouse, deliverySide, ultrasonic, ultrasonicMotor);
		LineFollower follower = new LineFollower(currentPose, leftMotor, rightMotor, color, houseCounter);
		follower.driveUntilStopped();
		currentPose = follower.getCurrentPose();
	}
	
	private void turnToFaceHouse() {
		status = "TURNING TO HOUSE";
		PizzaDeliveryUtils.displayStatus(currentPose, "house on the " + deliverySide.toLowerCase());
		
		Driver driver = null;
		if (deliverySide == "LEFT") {
			driver = new Driver(currentPose, leftMotor, rightMotor);
			driver.turn(-90);
		} else if (deliverySide == "RIGHT") {
			driver = new Driver(currentPose, leftMotor, rightMotor);
			driver.turn(90);
		}
		currentPose.updateAngle();
	}
	
	private void dropOffPizza() {
		status = "DROPPING OFF PIZZA";
		PizzaDeliveryUtils.displayStatus(currentPose);
		
		armMotor.rotateTo(0);
	}

	private void driveToStart() {
		status = "RETURNING TO START";
		PizzaDeliveryUtils.displayStatus(currentPose);
		
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose, START, leftMotor, rightMotor, obstacleDetector);
			PizzaDeliveryUtils.displayStatus(currentPose, "driving along");
			gotToTarget = driver.driveUntilStopped();
			currentPose = driver.getCurrentPose();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider(currentPose, leftMotor, rightMotor, ultrasonic, ultrasonicMotor);
			PizzaDeliveryUtils.displayStatus(currentPose, "avoiding an obstacle");
			obstacleAvoider.drivePastObstacle();
			currentPose = obstacleAvoider.getCurrentPose();
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
