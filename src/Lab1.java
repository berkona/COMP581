import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

/**
 * Team:
 * Jon Monroe PID 7141-87927
 * Jake Sellinger PID 720534409
 */
public class Lab1 {

	
	public static void main(String[] args) {
		Lab1 lab1 = new Lab1();
		lab1.Run();
		lab1.Close();
	}
	
	EV3LargeRegulatedMotor leftMotor;
	EV3LargeRegulatedMotor rightMotor;
	
	EV3TouchSensor bumpSensor;
	EV3UltrasonicSensor rangeSensor;
	
	DriveUnit driveUnit;
	
	int averageSize = 5;
	
	public float rangeThreshold = 0.005f;
	
	public Lab1() {
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		
		driveUnit = new DriveUnit(leftMotor, rightMotor, Robot.WHEEL_RADIUS, Robot.ROBOT_WIDTH);
		driveUnit.SetAcceleration(720);
		driveUnit.SetSpeed(180);
		
		bumpSensor = new EV3TouchSensor(SensorPort.S1);
		rangeSensor = new EV3UltrasonicSensor(SensorPort.S2);
	}
	
	public void Run() {
		Sound.setVolume(100);
		
		System.out.println("Goal 1 ready.");
		WaitForEnter();
		Goal1();
		Sound.beep();
		
		System.out.println("Goal 2 ready.");
		WaitForEnter();
		Goal2();
		Sound.beep();
		
		System.out.println("Goal 3 ready.");
		WaitForEnter();
		Goal3();
		Sound.beep();
		
		System.out.println("Goals complete.");
	}
	
	public void Close() {
		driveUnit = null;
		leftMotor.close();
		rightMotor.close();
		bumpSensor.close();
		rangeSensor.close();
	}
	
	void Goal1() {
		driveUnit.straight(1.5);
		driveUnit.waitMove();
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
	
	// move until range sensor reports a specific distance
	void MoveUntilDistance(float distance, boolean forward) {
		float currentDistance = GetCurrentDistance();
		if (!Float.isFinite(currentDistance)) {
			System.out.println("Searching for wall");
			// move until we see the wall
			if (forward) { 
				driveUnit.forward(); 
			} else { 
				driveUnit.backward(); 
			}
			while (!Float.isFinite(currentDistance)) {
				currentDistance = GetCurrentDistance();
			}
		}
		
		while (Math.abs(currentDistance - distance) > rangeThreshold) {
			float distLeft = currentDistance - distance;
			System.out.println("Found wall @ delta " + distLeft);
			driveUnit.straight(distLeft);
			driveUnit.waitMove();
			currentDistance = GetCurrentDistance();
		}
		
		System.out.println("Terminating with delta " + (currentDistance - distance));
	}
	
	// average distance over some number of measurements to reduce artifacts
	float GetCurrentDistance() {
		float[] distances = new float[averageSize];
		
		for (int i = 0; i < averageSize; i++) {
			rangeSensor.fetchSample(distances, i);
		}
		
		float sum = 0;
		for (float d : distances) {
			if (!Float.isFinite(d)) return Float.POSITIVE_INFINITY;
			sum += d;
		}
		return sum / ((float) averageSize);
	}
	
	void WaitForEnter() {
		while (Button.waitForAnyPress() != Button.ID_ENTER) {}
	}
}
