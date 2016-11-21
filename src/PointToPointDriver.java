import lejos.hardware.motor.NXTRegulatedMotor;

public class PointToPointDriver {

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
		// Can use gyroscope/Kalman filter reading and put this in a loop to get it more accurate
		rightMotor.rotate((int)Math.round(degrees), true);
		leftMotor.rotate((int)Math.round(-degrees));
		currentPose.updateAngle();
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
		double rise = Math.atan2(y, x);
		turn(rise*PizzaDeliveryUtils.DEG_TO_DEG);
		boolean interrupted = straight(Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5)*PizzaDeliveryUtils.DIST_TO_DEG);
		if (!interrupted) {
			turn((theta-rise)*PizzaDeliveryUtils.DEG_TO_DEG);
		}
		return interrupted;
	}

}
