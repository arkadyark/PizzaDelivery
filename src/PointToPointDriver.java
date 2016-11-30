import lejos.hardware.motor.NXTRegulatedMotor;

/***
 * 
 * Driver class for driving from one point to another in a straight line. 
 * Updates position as it goes, and can be interrupted by an Interruptor (such as ObstacleDetector)
 */

public class PointToPointDriver extends Driver {
	public Interruptor interruptor;
	public double x, y, theta;
	
	public PointToPointDriver(Localizer currentPose, double[] targetPose, 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor) {
		this(currentPose, targetPose, leftMotor, rightMotor, null);
	}
	
	public PointToPointDriver(Localizer currentPose, double targetPose[], 
			NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor, Interruptor interruptor) {
		super(currentPose, leftMotor, rightMotor);
		
		// Change in pose
		x = targetPose[0] - currentPose.getPose()[0];
		y = targetPose[1] - currentPose.getPose()[1];
		
		// For theta, convert the angle to be between -180 and 180
		theta = normalizeAngle(targetPose[2]);
		
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
					currentPose.updatePosition();
					return false;
				}
			}
		}
		
		leftMotor.stop();
		rightMotor.stop();
		currentPose.updatePosition();
		currentPose.updateAngle();
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
