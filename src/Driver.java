import lejos.hardware.motor.NXTRegulatedMotor;

public class Driver {
	private static final double EPSILON = 1.0;
	private KalmanFilterLocalizer currentPose;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	public Driver(KalmanFilterLocalizer currentPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.currentPose = currentPose;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.SPEED);
	}
	

	protected void turn(double degrees){
		double angle = currentPose.getPose()[2];
		double desired = normalizeAngle(angle + degrees);
		boolean leftFaster = turningLeftFaster(angle, desired);
		double distance = Math.min(Math.abs(angle - desired), Math.abs((desired + 360) % 360) - ((desired + 360) % 360));
		
		while (distance < EPSILON) {
			if (leftFaster) {
				leftMotor.backward();
				rightMotor.forward();
			} else {
				leftMotor.forward();
				rightMotor.backward();
			}
			currentPose.updateAngle();
			angle = currentPose.getPose()[2];
			leftFaster = turningLeftFaster(angle, desired);
			distance = Math.min(Math.abs(angle - desired), Math.abs((desired + 360) % 360) - ((desired + 360) % 360));
		}
		leftMotor.stop();
		rightMotor.stop();
	}
	
	protected double normalizeAngle(double angle) {
		return ((angle + 180) % 180) - 180;
	}
	
	private boolean turningLeftFaster(double angle, double desired) {
		double leftPosition = angle;
		double rightPosition = angle;
		for (int i = 0; i < 180; i++) {
			leftPosition = normalizeAngle(leftPosition + 1);
			rightPosition = normalizeAngle(rightPosition - 1);
			if (Math.abs(leftPosition - desired) < 1) {
				return true;
			} else if (Math.abs(rightPosition - desired) < 1) {
				return false;
			}
		}
		
		// Should never reach here
		return true;
	}
}
