import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3GyroSensor;

/***
 * 
 * Class representing current pose. Provides implementation of updating pose.
 *
 */

public class Localizer {
	double currentPose[];
	NXTRegulatedMotor leftMotor;
	NXTRegulatedMotor rightMotor;
	EV3GyroSensor gyro;
	double tachoCount;
	
	public Localizer(double[] start, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
			EV3GyroSensor gyro) {
		currentPose = start.clone();
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.gyro = gyro;
		tachoCount = 0;
	}
	
	public void updateAngle() {
		currentPose[2] = PizzaDeliveryUtils.getAngle(gyro);
	}
	
	public void updatePosition() {
		double tachoTicks = 0.5*(leftMotor.getTachoCount() + rightMotor.getTachoCount());
		double tachoDifference = tachoTicks - tachoCount;
		currentPose[0] += Math.cos(currentPose[2]/PizzaDeliveryUtils.RAD_TO_DEG)*tachoDifference/PizzaDeliveryUtils.DIST_TO_DEG;
		currentPose[1] += Math.sin(currentPose[2]/PizzaDeliveryUtils.RAD_TO_DEG)*tachoDifference/PizzaDeliveryUtils.DIST_TO_DEG;
		tachoCount = tachoTicks;
	}

	public double[] getPose() {
		return currentPose;
	}
	
	public double getAngle() {
		return currentPose[2];
	}

	public void update() {
		updateAngle();
	}
}
