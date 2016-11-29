import lejos.hardware.motor.NXTRegulatedMotor;

public class Driver {
	private static final double EPSILON = 1.0;
	protected KalmanFilterLocalizer currentPose;
	protected NXTRegulatedMotor leftMotor;
	protected NXTRegulatedMotor rightMotor;
	
	public Driver(KalmanFilterLocalizer currentPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.currentPose = currentPose;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	protected void turnTo(double desired) {
		double angle = normalizeAngle(currentPose.getPose()[2]);
		boolean leftFaster = turningLeftFaster(angle, desired);
		double distance = 360;
		leftMotor.setSpeed(PizzaDeliveryUtils.TURNING_SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.TURNING_SPEED);
		while (distance > EPSILON) {
			PizzaDeliveryUtils.displayStatus(currentPose, 
					Double.toString(distance) + " from " + Double.toString(desired));
			if (leftFaster) {
				leftMotor.backward();
				rightMotor.forward();
			} else {
				leftMotor.forward();
				rightMotor.backward();
			}
			currentPose.updateAngle();
			angle = normalizeAngle(currentPose.getPose()[2]);
			leftFaster = turningLeftFaster(angle, desired);
			double distance1 = Math.abs(angle - desired);
			double distance2 = Math.abs(((angle + 360) % 360) - ((desired + 360) % 360));
			distance = Math.min(distance1, distance2);
		}
		currentPose.update();
		leftMotor.stop();
		rightMotor.stop();
		currentPose.updateAngle();
		leftMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.SPEED);
	}

	protected void turn(double degrees){
		double angle = normalizeAngle(currentPose.getPose()[2]);
		double desired = normalizeAngle(angle + degrees);
		turnTo(desired);
	}
	
	protected double normalizeAngle(double angle) {
		angle %= 360;
		if (angle > 180) {
			angle -= 360;
		}
		return angle;
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
	
	public KalmanFilterLocalizer getCurrentPose() {
		return currentPose;
	}
}
