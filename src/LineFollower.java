import lejos.hardware.motor.NXTRegulatedMotor;

/***
 * 
 * Class to drive along a line, uses a P controller
 *
 */

public class LineFollower extends Driver {
	private Interruptor interruptor;
	
	private double desired;
	private static final float kP = 5;

	public LineFollower(Localizer currentPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, 
			double desiredAngle, Interruptor interruptor) {
		super(currentPose, leftMotor, rightMotor);
		this.desired = desiredAngle;
		this.interruptor = interruptor;
	}

	public void driveUntilStopped() {
		while (!interruptor.isFinished()) {
			double current = normalizeAngle(currentPose.getAngle());
			double error = (desired - current);
			double correction = kP*error;
			
			leftMotor.setSpeed(Math.round(PizzaDeliveryUtils.SPEED*0.5 - correction/2.0));
			rightMotor.setSpeed(Math.round(PizzaDeliveryUtils.SPEED*0.5  + correction/2.0));
			leftMotor.forward();
			rightMotor.forward();
			
			currentPose.update();
		}
		currentPose.update();
		leftMotor.stop(true);
		rightMotor.stop();
		currentPose.update();
		straight(10); // Line up with house
		currentPose.update();
	}
}

