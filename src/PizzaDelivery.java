import lejos.hardware.motor.Motor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class PizzaDelivery {
	private static final double START[] = {0, 0};

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
	private double currentPosition[];
	private double targetRoad[];

	public PizzaDelivery(PizzaDeliverySettings inputs) {
		targetHouse = inputs.getTargetHouse();
	}

	private static PizzaDeliverySettings getInputs() {
		PizzaDeliverySettings settings = new PizzaDeliverySettings();
		// Add all inputs to settings
		return settings;
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

	private void driveToStart() {
		PointToPointDriver driver = new PointToPointDriver(currentPosition, START);
		driver.driveUntilStopped();
	}

	private void dropOffPizza() {
	}

	private void turnToFaceHouse() {
	}

	private void followRoadToHouse() {
		HouseCounter houseCounter =  new HouseCounter(targetHouse);
		LineFollower follower = new LineFollower(houseCounter);
	}

	private void driveToRoad() {
		boolean gotToTarget = false;
		while (!gotToTarget) {
			ObstacleDetector obstacleDetector = new ObstacleDetector();
			PointToPointDriver driver = new PointToPointDriver(currentPosition, targetRoad, obstacleDetector);
			gotToTarget = driver.driveUntilStopped();
			if (gotToTarget) return;
			ObstacleAvoider obstacleAvoider = new ObstacleAvoider();
			obstacleAvoider.drivePastObstacle();
		}
	}

	private void pickUpPizza() {
		
	}

	private void driveToPizza() {
		
	}

	public static void main(String[] args) {
		PizzaDeliverySettings inputs = getInputs();
		PizzaDelivery delivery = new PizzaDelivery(inputs);
		delivery.deliver();
	}
}
