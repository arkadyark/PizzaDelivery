import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;

public class LineFollower {
	/***
	 * TODO:
	 * - Implement P controller to drive along line (Lab 3) reading from ColorSensor
	 * - Tune P controller/put in tuning from Lab 3/4
	 * - Implement interruption support, poll interruptor periodically
	 */

	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	private EV3ColorSensor color;
	private Interruptor interruptor;
	
	static float desired = 0.12f;
	private float kP;
	private float speed;

	public LineFollower(NXTRegulatedMotor rightMotor, NXTRegulatedMotor leftMotor, EV3ColorSensor color, Interruptor interruptor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.color = color;
		this.interruptor = interruptor;
	}

	public void driveUntilStopped() {
		while (!interruptor.isFinished()){
			float current = getReflectedLight();
			float error = (desired - current);			
			float correction = kP*error;
			
			leftMotor.setSpeed(Math.round(speed + correction/2.0));
			leftMotor.forward();
			rightMotor.setSpeed(Math.round(speed - correction/2.0));
			rightMotor.forward();
		}
		leftMotor.stop();
		rightMotor.stop();
	}
	
	protected float getReflectedLight() {
		int sampleSize = color.sampleSize();
		float[] redsample = new float[sampleSize];
		color.getRedMode().fetchSample(redsample, 0);
		return redsample[0];
	}	

}
