import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/***
 * 
 * Class that implements widely used functions for reading sensors, logging, and global parameters
 *
 */

public class PizzaDeliveryUtils {
	public static final double RAD_TO_DEG = 180/Math.PI; 
	public static final double DIST_TO_DEG = 360/(Math.PI*5.5); // Conversion between centimeters traveled and wheel rotation degrees
	public static final int SPEED = 200; // Speed used for driving straight
	public static final int TURNING_SPEED = 50; // Speed used for turning
	
	public static float getDistance(EV3UltrasonicSensor ultrasonic) {
		int sampleSize = ultrasonic.sampleSize();
		float[] distance = new float[sampleSize];
		ultrasonic.getDistanceMode().fetchSample(distance, 0);
		return distance[0];
	}
	
	public static float getAngle(EV3GyroSensor gyro) {
		/**
		 * Average 30 samples from the gyroscope to get a more accurate angle reading
		 */
		float N = 30;
		float estimatedAngle = 0;
		for (int i = 0; i < N; i++) {			
			int sampleSize = gyro.sampleSize();
			float[] angle = new float[sampleSize];
			gyro.getAngleMode().fetchSample(angle, 0);
			estimatedAngle += angle[0];
		}
		return estimatedAngle/N;
	}
	
	public static float getReflectedLight(EV3ColorSensor color) {
		int sampleSize = color.sampleSize();
		float[] redsample = new float[sampleSize];
		color.getRedMode().fetchSample(redsample, 0);
		return redsample[0];
	}
	
	public static void displayStatus(Localizer currentPose) {
		/**
		 * Log current pose to display
		 */
		LCD.clearDisplay();

		LCD.drawString(PizzaDelivery.status, 0, 0);
		String poseString = "[" + Math.round(currentPose.getPose()[0]) + " " +
				Math.round(currentPose.getPose()[1]) + " " +
				Math.round(currentPose.getPose()[2]) + "]";
		LCD.drawString(poseString, 0, 1);
	}
	
	public static void displayStatus(Localizer pose, String other) {
		/**
		 * Log current pose, plus a line of additional information, to display
		 */
		displayStatus(pose);
		LCD.drawString(other, 0, 2);
	}
}
