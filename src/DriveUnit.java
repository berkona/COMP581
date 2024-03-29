import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class DriveUnit {
	EV3LargeRegulatedMotor left;
	EV3LargeRegulatedMotor right;
	double wheelRadius;
	double wheelbaseLength;
	
	double wheelCircumference;
	double wheelbaseCircumference;
	
	public DriveUnit(
			EV3LargeRegulatedMotor left, 
			EV3LargeRegulatedMotor right, 
			double wheelRadius, 
			double wheelbaseLength) {
			this.left = left;
			this.right = right;
			this.wheelRadius = wheelRadius;
			this.wheelbaseLength = wheelbaseLength;
			
			left.synchronizeWith(new EV3LargeRegulatedMotor[] { right });
			
			wheelCircumference = 2 * Math.PI * wheelRadius;
			wheelbaseCircumference = 2 * Math.PI * wheelbaseLength;
	}
	
	public void SetAcceleration(int degreesPerSecondPerSecond) {
		left.setAcceleration(degreesPerSecondPerSecond);
		right.setAcceleration(degreesPerSecondPerSecond);
	}
	
	public void SetSpeed(int degreesPerSecond) {
		SetSpeed(degreesPerSecond, degreesPerSecond);
	}
	
	public void SetSpeed(int leftSpeed, int rightSpeed) {
		left.setSpeed(leftSpeed);
		right.setSpeed(rightSpeed);
	}
	
	public void waitMove() {
		left.waitComplete();
		right.waitComplete();
	}
	
	public boolean isMoving() {
		return left.isMoving() || right.isMoving();
	}
	
	public void straight(double distance) {
		int degrees = degreesForDistance(distance);
		left.startSynchronization();
		left.rotate(degrees, true);
		right.rotate(degrees, true);
		left.endSynchronization();
	}
	
	public void turn(int degrees) {
		if (degrees == 0) return;
		double arcLength = wheelbaseCircumference * ( Math.abs(degrees) / 360.0);
		int wheelDegrees = degreesForDistance(arcLength);
		left.startSynchronization();
		if (degrees > 0) {
			left.rotate(wheelDegrees);
			right.rotate(-wheelDegrees);
		} else {
			left.rotate(-wheelDegrees);
			right.rotate(wheelDegrees);
		}
		left.endSynchronization();
	}
	
	public void forward() {
		left.startSynchronization();
		left.forward();
		right.forward();
		left.endSynchronization();
	}
	
	public void backward() {
		left.startSynchronization();
		left.backward();
		right.backward();
		left.endSynchronization();
	}
	
	public void stop() {
		left.startSynchronization();
		left.stop(true);
		right.stop(true);
		left.endSynchronization();
	}
	
	int degreesForDistance(double distance) {
		 return (int) Math.round((distance / wheelCircumference) * 360.0);
	}
}
