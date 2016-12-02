import lejos.hardware.motor.NXTRegulatedMotor;

/***
 * 
 * Driver class for driving from one point to another in a straight line. 
 * Updates position as it goes, and can be interrupted by an Interruptor (such as ObstacleDetector)
 */

public class PointToPointDriver extends Driver {
	private double x, y, theta;
	private double targetPose[];
	
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
		this.targetPose = targetPose;
		
		// For theta, convert the angle to be between -180 and 180
		theta = normalizeAngle(targetPose[2]);
		
		this.interruptor = interruptor;
	}

	public boolean driveUntilStopped() {
		double rise = Math.atan2(y, x)*PizzaDeliveryUtils.RAD_TO_DEG;
		turnTo(rise);
		boolean finished = straight(Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5));
		if (finished) {
			x = targetPose[0] - currentPose.getPose()[0];
			y = targetPose[1] - currentPose.getPose()[1];
			double distanceToDesired = Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5);
			if (distanceToDesired > 3) {
				rise = Math.atan2(y, x)*PizzaDeliveryUtils.RAD_TO_DEG;
				turnTo(rise);
				straight(Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5));
				currentPose.update();
			}
			turnTo(theta);
		}
		return finished;
	}
}
