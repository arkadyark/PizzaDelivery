import lejos.hardware.motor.Motor;

public class PointToPointDriver {
	/***
	 * TODO:
	 * - Implement PointToPoint control from lab 4
	 * - Implement support for interruptions from the Interruptor, if there is one
	 */

	public static double DEG_TO_DEG = (9+14.5)/(2*5.5);
	public static double DIST_TO_DEG = 360/(Math.PI*5.5);
	public static int SPEED = 200;
	public Interruptor interruptor;
	public double x, y, theta;
	
	
	public void turn(double degrees){	
		Motor.C.rotate((int)Math.round(degrees), true);
		Motor.B.rotate((int)Math.round(-degrees));
	}
	
	public boolean straight(double degrees){		
		if(interruptor == null) {
			Motor.C.rotate((int)Math.round(degrees), true);
			Motor.B.rotate((int)Math.round(degrees));
		}
		else {
			Motor.B.resetTachoCount();
			Motor.B.forward();
			Motor.C.forward();
			while(Motor.B.getTachoCount() < degrees) {
				if(interruptor.isFinished()) {
					Motor.B.stop();
					Motor.C.stop();
					return false;
				}
			}
			Motor.B.stop();
			Motor.C.stop();
		}
		return true;
	}

	public boolean pose(double x, double y, double theta){
		double rise = Math.atan2(y, x);
		boolean interrupted;
		turn(rise*DEG_TO_DEG);
		interrupted = straight(Math.pow((Math.pow(x,  2) + Math.pow(y, 2)), .5)*DIST_TO_DEG);
		turn((theta-rise)*DEG_TO_DEG);
		return interrupted;
	}
	
	public PointToPointDriver(double currentPose[], double targetPose[], Interruptor interruptor) {
		this(currentPose, targetPose);
		this.interruptor = interruptor;
	}

	public PointToPointDriver(double[] currentPose, double[] targetPose) {
		// Change in pose
		x = targetPose[0] - currentPose[0];
		y = targetPose[1] - currentPose[1];
		theta = targetPose[2] - currentPose[2];
		
		// For theta, convert the angle to be between -180 and 180
		theta = theta % 360f;
		if(theta > 180) {
			theta -= 360;
		}
		Motor.B.setSpeed(SPEED);
		Motor.C.setSpeed(SPEED);
	}

	public boolean driveUntilStopped() {
		return pose(x, y, theta);
	}

}
