import lejos.hardware.motor.NXTRegulatedMotor;

/***
 * 
 * Base class for driving, subclassed by PointToPointDriver and ObstacleAvoider
 * Provides turning functionality.
 */

public class Driver {
	private static final double EPSILON = 1.0;
	protected Localizer currentPose;
	protected NXTRegulatedMotor leftMotor;
	protected NXTRegulatedMotor rightMotor;
	protected Interruptor interruptor;
	
	public Driver(Localizer currentPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this.currentPose = currentPose;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
	}
	
	protected void turnTo(double desired) {
		/**
		 * Given a desired angle, turn to that angle
		 */
		double currentAngle = normalizeAngle(currentPose.getAngle());
		boolean leftFaster = turningLeftFaster(currentAngle, desired);
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
			currentAngle = normalizeAngle(currentPose.getAngle());
			leftFaster = turningLeftFaster(currentAngle, desired);
			
			double distance1 = Math.abs(currentAngle - desired);
			double distance2 = 360 - distance1;
			distance = Math.min(distance1, distance2);
		}
		currentPose.updateAngle();
		leftMotor.stop();
		rightMotor.stop();
		currentPose.updateAngle();
		leftMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.SPEED);
	}

	protected void turn(double degrees){
		/**
		 * Turn the robot by degrees
		 */
		double angle = normalizeAngle(currentPose.getAngle());
		double desired = normalizeAngle(angle + degrees);
		turnTo(desired);
	}
	
	protected double normalizeAngle(double angle) {
		/**
		 * Normalize angle to fall within [-180, 180]
		 */
		angle %= 360;
		if (angle > 180) {
			angle -= 360;
		}
		return angle;
	}
	
	private boolean turningLeftFaster(double angle, double desired) {
		/**
		 * Determine whether it is faster to turn left or right to get from angle to desired
		 */
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


	protected boolean straight(double distance){
		double degrees = distance * PizzaDeliveryUtils.DIST_TO_DEG;
		double startTachoCount = 0.5*(leftMotor.getTachoCount() + rightMotor.getTachoCount());
		leftMotor.forward();
		rightMotor.forward();
		while(0.5*(leftMotor.getTachoCount() + rightMotor.getTachoCount()) - startTachoCount < degrees) {
			currentPose.update();
			PizzaDeliveryUtils.displayStatus(currentPose);
			if(interruptor != null && interruptor.isFinished()) {
				stop();
				currentPose.updatePosition();
				return false;
			}
		}
		
		stop();
		currentPose.updatePosition();
		return true;
	}
	
	protected void stop() {
		// Fix rotation caused by stopping wheels not at the same time
		currentPose.updateAngle();
		double angle = currentPose.getAngle();
		leftMotor.stop();
		rightMotor.stop();
		turnTo(angle);
	}
	
	public Localizer getCurrentPose() {
		return currentPose;
	}
}
