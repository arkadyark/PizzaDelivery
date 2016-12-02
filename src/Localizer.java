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
		updatePosition(currentPose[2]);
	}
	
	public void updatePosition(double angle) {
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
		double previousAngle = currentPose[2] % 360;
		updateAngle();
		double currentAngle = currentPose[2] % 360;
		double average = getAverageAngle(previousAngle, currentAngle);
		updatePosition(average);
	}

	private double getAverageAngle(double previousAngle, double currentAngle) {
		// TODO: Implement properly averaging angle
		return previousAngle;
	}
}
