import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Lab1 {

	
	public static void main(String[] args) {
		new Lab1().Run();
	}
	
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	EV3TouchSensor bumpSensor;
	EV3UltrasonicSensor rangeSensor;
	
	DriveUnit driveUnit;
	
	public Lab1() {
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		
		driveUnit = new DriveUnit(leftMotor, rightMotor, Robot.WHEEL_RADIUS, Robot.ROBOT_WIDTH);
		
		bumpSensor = new EV3TouchSensor(SensorPort.S1);
		rangeSensor = new EV3UltrasonicSensor(SensorPort.S2);
	}
	
	public void Run() {
		WaitForEnter();
		driveUnit.straight(1.5);
		Sound.beep();
	}
	
	void Goal1() {
		driveUnit.
	}
	
	void Goal2() {
		
	}
	
	void Goal3() {
		
	}
	
	void WaitForEnter() {
		while (Button.waitForAnyPress() != Button.ID_ENTER) {}
	}
}
