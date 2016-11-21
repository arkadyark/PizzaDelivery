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
	
	public void update() {
		// Simple odometry, no Kalman filtering being done here (yet)
		// TODO: Add conversion factors from wheel rotation to position change
		// TODO: Add Kalman filtering to get accurate theta
		double v_average = 0.5*(leftMotor.getRotationSpeed() + rightMotor.getRotationSpeed());
		double v_difference = (rightMotor.getRotationSpeed() - leftMotor.getRotationSpeed());
		double tachometerTicks = 0.5*(leftMotor.getTachoCount() + rightMotor.getTachoCount());
		double tachometerDelta = tachometerTicks - tachoCount;
		currentPose[0] += v_average*Math.sin(currentPose[2])*tachometerDelta;
		currentPose[1] += v_average*Math.cos(currentPose[2])*tachometerDelta;
		currentPose[2] += v_difference*tachometerDelta;
		tachoCount = tachometerTicks;
	}

	public double[] getPose() {
		return currentPose;
	}
	
	public void setPose(double newPose[]) {
		this.currentPose = newPose;
	}
}
