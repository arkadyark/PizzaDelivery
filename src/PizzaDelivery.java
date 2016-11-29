import java.util.Arrays;

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
		
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()),
				"house " + Integer.toString(targetHouse) + " on the " + deliverySide.toLowerCase());
	}
	
	private void initialize() {
		status = "INITIALIZING";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()));
		
		armMotor.rotateTo(0);
		ultrasonicMotor.rotateTo(0);
		
		leftMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "resetting gyroscope");
		gyro.reset();
		Delay.msDelay(1000);
	}
	
	private void driveToPizza() {
		status = "DRIVING TO PIZZA";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "pizza at " + pizzaCoords.toString());
		
		PointToPointDriver driver = new PointToPointDriver(currentPose, pizzaCoords, leftMotor, rightMotor);
		driver.driveUntilStopped();
		currentPose = driver.getCurrentPose();
	}

	private void pickUpPizza() {
		status = "PICKING UP PIZZA";
		
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "backing in");
		leftMotor.rotate((int) Math.round(-10*PizzaDeliveryUtils.DIST_TO_DEG), true);
		rightMotor.rotate((int) Math.round(-10*PizzaDeliveryUtils.DIST_TO_DEG));
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "grabbing");
		armMotor.rotateTo(180);
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "driving out");
		leftMotor.rotate((int) Math.round(10*PizzaDeliveryUtils.DIST_TO_DEG), true);
		rightMotor.rotate((int) Math.round(10*PizzaDeliveryUtils.DIST_TO_DEG));
		currentPose.update();
	}

	private void driveToRoad() {
		status = "DRIVING TO ROAD";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()));
		
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose, roadCoords, leftMotor, rightMotor, obstacleDetector);
			PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "driving along");
			gotToTarget = driver.driveUntilStopped();
			currentPose = driver.getCurrentPose();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider(currentPose, leftMotor, rightMotor, ultrasonic, ultrasonicMotor);
			PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "avoiding an obstacle");
			obstacleAvoider.drivePastObstacle();
			currentPose = obstacleAvoider.getCurrentPose();
		}
	}

	private void followRoadToHouse() {
		status = "FOLLOWING ROAD TO HOUSE";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()));
		
		HouseCounter houseCounter =  new HouseCounter(targetHouse, deliverySide, ultrasonic, ultrasonicMotor);
		LineFollower follower = new LineFollower(currentPose, leftMotor, rightMotor, color, houseCounter);
		follower.driveUntilStopped();
		currentPose = follower.getCurrentPose();
	}
	
	private void turnToFaceHouse() {
		status = "TURNING TO HOUSE";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "house on the " + deliverySide.toLowerCase());
		
		double desiredPose[] = currentPose.getPose();
		PointToPointDriver driver = null;
		if (deliverySide == "LEFT") {
			desiredPose[2] -= 90;
			driver = new PointToPointDriver(currentPose, desiredPose, leftMotor, rightMotor);
			driver.driveUntilStopped();
		} else if (deliverySide == "RIGHT") {
			desiredPose[2] += 90;
			driver = new PointToPointDriver(currentPose, desiredPose, leftMotor, rightMotor);
			driver.driveUntilStopped();
		}
		currentPose = driver.getCurrentPose();
	}
	
	private void dropOffPizza() {
		status = "DROPPING OFF PIZZA";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()));
		
		armMotor.rotateTo(0);
	}

	private void driveToStart() {
		status = "RETURNING TO START";
		PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()));
		
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector(ultrasonic);
			PointToPointDriver driver = new PointToPointDriver(currentPose, START, leftMotor, rightMotor, obstacleDetector);
			PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "driving along");
			gotToTarget = driver.driveUntilStopped();
			currentPose = driver.getCurrentPose();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider(currentPose, leftMotor, rightMotor, ultrasonic, ultrasonicMotor);
			PizzaDeliveryUtils.displayStatus(Arrays.toString(currentPose.getPose()), "avoiding an obstacle");
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
