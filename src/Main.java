import java.util.Random;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;

public class Main {
	
	public static void main(String[] args) {
		RegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.B);
		RegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.C);
		
		DriveUnit dr = new DriveUnit(left, right, Robot.WHEEL_RADIUS, Robot.ROBOT_WIDTH);
		
		dr.SetSpeed(360);
		
		SampleProvider distance = new EV3UltrasonicSensor(SensorPort.S1).getDistanceMode();
		SampleProvider bump1 = new EV3TouchSensor(SensorPort.S2).getTouchMode();
		SampleProvider bump2 = new EV3TouchSensor(SensorPort.S3).getTouchMode();
		
		Collider collider = new Collider(distance, bump1, bump2);
		
		Random rand = new Random();
		while (true) {
			if (collider.isColliding()) {
				//System.out.println("Collision detected");
				dr.stop();
				dr.waitMove();
				if (collider.isBumping()) {
					System.out.println("Bumped objected");
					dr.straight(-Collider.COLLIDER_THRESHOLD);
					dr.waitMove();
				} else {
					System.out.println("Too close");
				}
				if (rand.nextDouble() < 0.5) {
					dr.turn(-45);
				} else {
					dr.turn(45);
				}
				dr.waitMove();
			} else if (!dr.isMoving()){
				dr.forward();
			}
		}
	}
	
	public static int RotationGivenDistance(double radius, double distance) {
		double circum = 2 * Math.PI * radius;
		return (int) Math.round((distance / circum) * 360.0);
	}
}
