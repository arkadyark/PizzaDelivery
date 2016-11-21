import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;

public class LineFollower {
	/***
	 * TODO:
	 * - Tune P controller/put in tuning from Lab 3/4
	 */

	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	private EV3ColorSensor color;
	private Interruptor interruptor;
	
	private static float desired = 0.12f;
	private static float kP = 300;

	public LineFollower(NXTRegulatedMotor rightMotor, NXTRegulatedMotor leftMotor, EV3ColorSensor color, Interruptor interruptor) {
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.color = color;
		this.interruptor = interruptor;
	}

	public void driveUntilStopped() {
		while (!interruptor.isFinished()){
			float current = PizzaDeliveryUtils.getReflectedLight(color);
			float error = (desired - current);			
			float correction = kP*error;
			
			leftMotor.setSpeed(Math.round(PizzaDeliveryUtils.SPEED + correction/2.0));
			leftMotor.forward();
			rightMotor.setSpeed(Math.round(PizzaDeliveryUtils.SPEED - correction/2.0));
			rightMotor.forward();
		}
		leftMotor.stop();
		rightMotor.stop();
	}
}
