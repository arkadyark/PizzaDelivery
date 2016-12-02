import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;

/***
 * 
 * Class to drive along a line, uses a P controller
 *
 */

public class LineFollower {
	private NXTRegulatedMotor leftMotor;
	private NXTRegulatedMotor rightMotor;
	private Localizer currentPose;
	private EV3ColorSensor color;
	private Interruptor interruptor;
	
	private static final float desired = 0.04f; // Color reading when we are 50% on the line
	private static final float kP = 1200;

	public LineFollower(Localizer currentPose, 
			NXTRegulatedMotor rightMotor, NXTRegulatedMotor leftMotor, 
			EV3ColorSensor color, Interruptor interruptor) {
		this.currentPose = currentPose;
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
			rightMotor.setSpeed(Math.round(PizzaDeliveryUtils.SPEED - correction/2.0));
			leftMotor.forward();
			rightMotor.forward();
			
			currentPose.update();
		}
		currentPose.update();
		leftMotor.stop();
		rightMotor.stop();
		currentPose.update();
	}

	public Localizer getCurrentPose() {
		return currentPose;
	}
}
