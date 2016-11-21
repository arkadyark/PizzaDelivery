import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.utility.KalmanFilter;

public class KalmanFilterLocalizer {
	double currentPose[];
	NXTRegulatedMotor leftMotor;
	NXTRegulatedMotor rightMotor;
	EV3GyroSensor gyro;
	double tachoCount;
	
	public KalmanFilterLocalizer(double[] start, NXTRegulatedMotor leftMotor, NXTRegulatedMotor rightMotor,
			EV3GyroSensor gyro) {
		currentPose = start;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.gyro = gyro;
		tachoCount = 0;
	}
	
	public void updateAngle() {
		// If necessary, use a Kalman filter to update this
		currentPose[2] = PizzaDeliveryUtils.getAngle(gyro);
	}
	
	public void updateDistance() {
		double tachoTicks = 0.5*(leftMotor.getTachoCount() + rightMotor.getTachoCount());
		double tachoDifference = tachoTicks - tachoCount;
		currentPose[0] += Math.cos(currentPose[2]/PizzaDeliveryUtils.RAD_TO_DEG)*tachoDifference/PizzaDeliveryUtils.DIST_TO_DEG;
		currentPose[1] += Math.sin(currentPose[2]/PizzaDeliveryUtils.RAD_TO_DEG)*tachoDifference/PizzaDeliveryUtils.DIST_TO_DEG;
		tachoCount = tachoTicks;
	}

	public double[] getPose() {
		return currentPose;
	}
	
	public void setPose(double newPose[]) {
		this.currentPose = newPose;
	}
}
