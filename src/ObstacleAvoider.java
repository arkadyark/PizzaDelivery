import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class ObstacleAvoider extends Driver {
	private static final float CLEARANCE_THRESHOLD = 0.50f;
	private EV3UltrasonicSensor ultrasonic;
	private NXTRegulatedMotor ultrasonicMotor;

	public ObstacleAvoider(KalmanFilterLocalizer currentPose, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
			EV3UltrasonicSensor ultrasonic, NXTRegulatedMotor ultrasonicMotor) {
		super(currentPose, leftMotor, rightMotor);
		this.ultrasonic = ultrasonic;
		this.ultrasonicMotor = ultrasonicMotor;
	}
	
	public void drivePastObstacle() {
		ultrasonicMotor.rotateTo(90);
		float leftDistance = PizzaDeliveryUtils.getDistance(ultrasonic);
		ultrasonicMotor.rotateTo(-90);
		float rightDistance = PizzaDeliveryUtils.getDistance(ultrasonic);
		ultrasonicMotor.rotateTo(0);
		if (leftDistance > rightDistance) {
			turn(90);
			ultrasonicMotor.rotateTo(90);
		} else {
			turn(-90);
			ultrasonicMotor.rotateTo(-90);
		}
		currentPose.updateAngle();
		float distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		leftMotor.forward();
		rightMotor.forward();
		while (distance < CLEARANCE_THRESHOLD) {
			distance = PizzaDeliveryUtils.getDistance(ultrasonic);
		}
		rightMotor.stop();
		leftMotor.stop();
		ultrasonicMotor.rotateTo(0);
		currentPose.update();
	}
}
