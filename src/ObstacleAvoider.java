import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/***
 * 
 * Driver class used to avoid obstacles.
 * Turns perpendicular, looks towards the obstacle, and drives until it has sufficient clearance
 */

public class ObstacleAvoider extends Driver {
	private static final float CLEARANCE_THRESHOLD = 0.40f;
	private EV3UltrasonicSensor ultrasonic;
	private NXTRegulatedMotor ultrasonicMotor;

	public ObstacleAvoider(Localizer currentPose, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
			EV3UltrasonicSensor ultrasonic, NXTRegulatedMotor ultrasonicMotor) {
		super(currentPose, leftMotor, rightMotor);
		this.ultrasonic = ultrasonic;
		this.ultrasonicMotor = ultrasonicMotor;
	}
	
	public void drivePastObstacle() {
		ultrasonicMotor.rotateTo(90);
		float rightDistance = PizzaDeliveryUtils.getDistance(ultrasonic);
		ultrasonicMotor.rotateTo(-90);
		float leftDistance = PizzaDeliveryUtils.getDistance(ultrasonic);
		if (leftDistance > rightDistance) {
			// There's more space on the left, turn left and look to our right
			turn(90);
			ultrasonicMotor.rotateTo(90);
		} else {
			// There's more space on the right, turn right and look to our left
			turn(-90);
			ultrasonicMotor.rotateTo(-90);
		}
		currentPose.updateAngle();
		float distanceToObstacle = PizzaDeliveryUtils.getDistance(ultrasonic);
		leftMotor.forward();
		rightMotor.forward();
		while (distanceToObstacle < CLEARANCE_THRESHOLD) {
			distanceToObstacle = PizzaDeliveryUtils.getDistance(ultrasonic);
		}
		stop();
		// Keep driving until cleared obstacle
		straight(30);		
		
		ultrasonicMotor.rotateTo(0);
	}
}
