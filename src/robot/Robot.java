package robot;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import sensing.Sonar;

public final class Robot {
	
	// in meters
	public static final float WHEEL_RADIUS = 0.0275f;
	
	public static final float ROBOT_WIDTH = .127f/2;
	
	public static final Port LEFT_MOTOR_PORT = MotorPort.B;
	public static final Port RIGHT_MOTOR_PORT = MotorPort.C;
	
	public static final Port SENSOR_MOTOR_PORT = MotorPort.D;
	
	public static final Port TOUCH_PORT = SensorPort.S1;
	public static final Port TOUCH_PORT_2 = SensorPort.S3;
	
	public static final Port ULTRASONIC_PORT = SensorPort.S2;
	
	public DriveUnit driveUnit;
	public Sonar sonar;
	
	public EV3TouchSensor bump1;
	public EV3TouchSensor bump2;
	
	public Robot(Vector2 initPos, int initHeading) {
		driveUnit = new DriveUnit(
				new EV3LargeRegulatedMotor(LEFT_MOTOR_PORT),
				new EV3LargeRegulatedMotor(RIGHT_MOTOR_PORT),
				WHEEL_RADIUS,
				ROBOT_WIDTH,
				initPos,
				initHeading
		);
		
		driveUnit.setAcceleration(720);
		driveUnit.setSpeed(360);
		
		sonar = new Sonar(
				new EV3UltrasonicSensor(ULTRASONIC_PORT), 
				new EV3MediumRegulatedMotor(SENSOR_MOTOR_PORT),
				driveUnit,
				new int[] {-90, -60, -30, 0, 30, 60, 90}
		);
		
		bump1 = new EV3TouchSensor(TOUCH_PORT);
		bump2 = new EV3TouchSensor(TOUCH_PORT_2);
	}
	
	float[] samples = new float[2];
	
	public boolean IsBumping() {
		bump1.fetchSample(samples, 0);
		bump2.fetchSample(samples, 1);
		return samples[0] != 0 || samples[1] != 0;
	}
}
