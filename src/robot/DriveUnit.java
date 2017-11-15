package robot;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class DriveUnit {
	EV3LargeRegulatedMotor left;
	EV3LargeRegulatedMotor right;
	float wheelRadius;
	float wheelbaseLength;
	
	float wheelCircumference;
	float wheelbaseCircumference;
	
	Vector2 position;
	int theta;
	
	public DriveUnit(
			EV3LargeRegulatedMotor left, 
			EV3LargeRegulatedMotor right, 
			float wheelRadius, 
			float wheelbaseLength,
			Vector2 position,
			int theta) {
			
			this.left = left;
			this.right = right;
			this.wheelRadius = wheelRadius;
			this.wheelbaseLength = wheelbaseLength;
			
			this.position = position;
			this.theta = theta;
			
			left.synchronizeWith(new EV3LargeRegulatedMotor[] { right });
			
			wheelCircumference = (float) (2 * Math.PI * wheelRadius);
			wheelbaseCircumference = (float) (2 * Math.PI * wheelbaseLength);
	}
	
	/**
	 * Get the current odometry-based heading of the robot.
	 * 0 is the X-axis, positive is clockwise rotation.
	 * @return The current heading
	 */
	public int heading() {
		return theta;
	}
	
	/**
	 * Get the current odometry-based position of the robot in world-space
	 * @return position as Vector2
	 */
	public Vector2 position() {
		return position;
	}
	
	public void setAcceleration(int degreesPerSecondPerSecond) {
		left.setAcceleration(degreesPerSecondPerSecond);
		right.setAcceleration(degreesPerSecondPerSecond);
	}
	
	public void setSpeed(int degreesPerSecond) {
		setSpeed(degreesPerSecond, degreesPerSecond);
	}
	
	public void setSpeed(int leftSpeed, int rightSpeed) {
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
	
	public void straight(float distance) {
		int degrees = degreesForDistance(distance);
		left.startSynchronization();
		left.rotate(degrees);
		right.rotate(degrees);
		left.endSynchronization();
		waitMove();
		position.x += distance * Math.cos(Math.toRadians(theta));
		position.y += distance * Math.sin(Math.toRadians(theta));
	}
	
	public void turn(int degrees) {
		degrees = degrees % 360;
		if (degrees == 0) return;
		float arcLength = (float) (wheelbaseCircumference * ( Math.abs(degrees) / 360.0));
		int wheelDegrees = degreesForDistance(arcLength);
		left.startSynchronization();
		if (degrees > 0) {
			left.rotate(-wheelDegrees);
			right.rotate(wheelDegrees);
		} else {
			left.rotate(wheelDegrees);
			right.rotate(-wheelDegrees);
		}
		left.endSynchronization();
		waitMove();
		theta = (theta + degrees) % 360;
	}
	
	public void rotate(int leftWheel, int rightWheel) {
		left.startSynchronization();
		left.rotate(leftWheel);
		right.rotate(rightWheel);
		left.endSynchronization();
		waitMove();
	}
	
	public void turnTo(int degrees) {
		turn(theta + degrees);
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
		waitMove();
	}
	
	public void moveTo(Vector2 newPos, float newHeading) {
		Vector2 delta = Vector2.sub(newPos, position);
		int angleToPos = (int) Math.round(Math.toDegrees(Math.atan2(delta.y, delta.x)));
		float distanceToTravel = delta.magnitude();
		turnTo(angleToPos);
		straight(distanceToTravel);
		turnTo(Math.round(newHeading));
	}
	
	public void moveTo(Vector2 newPos) {
		Vector2 delta = Vector2.sub(newPos, position);
		int angleToPos = (int) Math.round(Math.toDegrees(Math.atan2(delta.y, delta.x)));
		System.out.println("Angle to pos" + angleToPos);
		float distanceToTravel = delta.magnitude();
		turnTo(angleToPos);
		straight(distanceToTravel);
	}
	
	int degreesForDistance(float distance) {
		 return (int) Math.round((distance / wheelCircumference) * 360.0);
	}
}
