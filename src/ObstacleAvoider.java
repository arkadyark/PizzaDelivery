import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class ObstacleAvoider {
	private static final double EPSILON = 1;
	private static final float CLEARANCE_THRESHOLD = 0;
	private KalmanFilterLocalizer currentPose;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	private EV3UltrasonicSensor ultrasonic;
	private NXTRegulatedMotor ultrasonicMotor;

	public ObstacleAvoider(KalmanFilterLocalizer currentPose, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
			EV3UltrasonicSensor ultrasonic, NXTRegulatedMotor ultrasonicMotor) {
		this.currentPose = currentPose;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.ultrasonic = ultrasonic;
		this.ultrasonicMotor = ultrasonicMotor;
	}
	
	// TODO: Refactor to not copy this in PointToPoint and here
	private void turn(double degrees){
		// Can use gyroscope/Kalman filter reading and put this in a loop to get it more accurate
		double angle = currentPose.getPose()[2];
		double desired = (angle + degrees) % 360;
		if (degrees > 0) {			
			rightMotor.forward();
			leftMotor.backward();
		} else {
			rightMotor.backward();
			leftMotor.forward();
		}
		while (Math.abs(angle - desired) > EPSILON) {
			currentPose.updateAngle();
			angle = currentPose.getPose()[2];
		}
		leftMotor.stop();
		rightMotor.stop();
	}
	
	public void drivePastObstacle() {
		ultrasonicMotor.rotateTo(90);
		float leftDistance = PizzaDeliveryUtils.getDistance(ultrasonic);
		ultrasonicMotor.rotateTo(-90);
		float rightDistance = PizzaDeliveryUtils.getDistance(ultrasonic);
		ultrasonicMotor.rotateTo(0);
		if (leftDistance > rightDistance) {
			turn(90);
			ultrasonicMotor.rotateTo(-90);
		} else {
			turn(-90);
			ultrasonicMotor.rotateTo(90);
		}
		float distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		leftMotor.forward();
		rightMotor.forward();
		while (distance < CLEARANCE_THRESHOLD) {
			distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		}
		leftMotor.stop();
		rightMotor.stop();
		ultrasonicMotor.rotateTo(0);
	}
}
