import java.util.LinkedList;
import java.util.Queue;

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
		Goal1();
		Sound.beep();
		
		WaitForEnter();
		Goal2();
		Sound.beep();
		
		WaitForEnter();
		Goal3();
		Sound.beep();
	}
	
	void Goal1() {
		driveUnit.SetAcceleration(600);
		driveUnit.straight(1.5);
		driveUnit.waitMove();
	}
	
	Queue<Float> distanceSamples = new LinkedList<Float>();
	int averageSize = 5;
	float sum = 0;
	
	void MoveUntilDistance(float distance, boolean forward) {
		float[] distances = new float[rangeSensor.sampleSize()];
		if (forward) { 
			driveUnit.forward(); 
		} else { 
			driveUnit.backward(); 
		}
		while (true) {
			rangeSensor.fetchSample(distances, 0);
			float distToWall = distances[0];
			if (Float.isFinite(distToWall)) {
				sum += distToWall;
				distanceSamples.add(distToWall);
				if (distanceSamples.size() == averageSize + 1) {
					float f = distanceSamples.remove();
					sum -= f;
					float averageDist = sum / averageSize;
					System.out.println(averageDist);
					if (Math.abs(distance - averageDist) < 0.005) {
						driveUnit.stop();
						driveUnit.waitMove();
						break;
					}
				}
			}
		}
	}
	
	void Goal2() {
		MoveUntilDistance(0.45f, true);
	}
	
	void Goal3() {
		float[] bumpSamples = new float[bumpSensor.sampleSize()];
		driveUnit.forward();
		while (true) {
			bumpSensor.fetchSample(bumpSamples, 0);
			if (bumpSamples[0] != 0) {
				driveUnit.stop();
				driveUnit.waitMove();
				break;
			}
		}
		
		MoveUntilDistance(0.45f, false);
	}
	
	void WaitForEnter() {
		while (Button.waitForAnyPress() != Button.ID_ENTER) {}
	}
}
