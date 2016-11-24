import lejos.hardware.motor.NXTRegulatedMotor;

public class PointToPointDriver {

	private static final double EPSILON = 1.0;
	public Interruptor interruptor;
	public double x, y, theta;
	private KalmanFilterLocalizer currentPose;
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	
	public PointToPointDriver(KalmanFilterLocalizer currentPose, double[] targetPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(currentPose, targetPose, leftMotor, rightMotor, null);
	}
	
	public PointToPointDriver(KalmanFilterLocalizer currentPose, double targetPose[], 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, Interruptor interruptor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		leftMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		rightMotor.setSpeed(PizzaDeliveryUtils.SPEED);
		
		this.currentPose = currentPose;
		this.interruptor = interruptor;
		
		// Change in pose
		x = targetPose[0] - currentPose.getPose()[0];
		y = targetPose[1] - currentPose.getPose()[1];
		theta = targetPose[2] - currentPose.getPose()[2];
		
		// For theta, convert the angle to be between -180 and 180
		theta %= 360f;
		if(theta > 180) {
			theta -= 360;
		}
	}	
	
	private void turn(double degrees){
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
	
	private double normalizeAngle(double angle) {
		return ((angle + 180) % 180) - 180;
	}

	private boolean straight(double degrees){		
		if(interruptor == null) {
			rightMotor.rotate((int)Math.round(degrees), true);
			leftMotor.rotate((int)Math.round(degrees));
		}
		else {
			int startTachoCount = leftMotor.getTachoCount();
			leftMotor.forward();
			rightMotor.forward();
			while(leftMotor.getTachoCount() - startTachoCount < degrees) {
				if(interruptor.isFinished()) {
					leftMotor.stop();
					rightMotor.stop();
					currentPose.updateDistance();
					return false;
				}
			}
		}
		
		leftMotor.stop();
		rightMotor.stop();
		currentPose.updateDistance();
		return true;
	}

	public boolean driveUntilStopped() {
		double rise = Math.atan2(y, x)*PizzaDeliveryUtils.RAD_TO_DEG;
		turn(rise);
		boolean finished = straight(Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5)*PizzaDeliveryUtils.DIST_TO_DEG);
		if (finished) {
			turn(theta-rise);
		}
		return finished;
	}

}
