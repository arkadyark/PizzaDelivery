import lejos.hardware.motor.NXTRegulatedMotor;

public class PointToPointDriver extends Driver {
	public Interruptor interruptor;
	public double x, y, theta;
	
	public PointToPointDriver(KalmanFilterLocalizer currentPose, double[] targetPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(currentPose, targetPose, leftMotor, rightMotor, null);
	}
	
	public PointToPointDriver(KalmanFilterLocalizer currentPose, double targetPose[], 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, Interruptor interruptor) {
		super(currentPose, leftMotor, rightMotor);
		
		// Change in pose
		x = targetPose[0] - currentPose.getPose()[0];
		y = targetPose[1] - currentPose.getPose()[1];
		theta = targetPose[2];
		
		// For theta, convert the angle to be between -180 and 180
		theta = normalizeAngle(theta);
		
		this.interruptor = interruptor;
	}

	private boolean straight(double degrees){
		if(interruptor == null) {
			rightMotor.rotate((int)Math.round(degrees), true);
			leftMotor.rotate((int)Math.round(degrees));
		} else {
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
		turnTo(rise);
		boolean finished = straight(Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5)*PizzaDeliveryUtils.DIST_TO_DEG);
		if (finished) {
			turnTo(theta);
		}
		return finished;
	}
}
