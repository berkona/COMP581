package Lab2;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import robot.DriveUnit;
import robot.Robot;
import robot.Vector2;

/**
 * Jon Monroe - PID: 714187927
 * Jake Sellinger - PID: 720534409
 */
public class PIDTest {

	static double RANGE_SANITY_THRESHOLD = 0.3;
	static int GLITCH_SAMPLES = 5;
	
	static EV3TouchSensor bumpSensor;
	static EV3UltrasonicSensor rangeSensor;
	
	static EV3LargeRegulatedMotor leftMotor;
	static EV3LargeRegulatedMotor rightMotor;
	static DriveUnit driveUnit;

	static PIDController controller;
	static int speed = 180;
	static float setPoint;
	
	static int kP;
	static int kI;
	static int kD; 
	
	static float forwardIncr;
	static float turnIncr;
	
	static int turnAmt;
	
	public static void main(String[] args) {
		rangeSensor = new EV3UltrasonicSensor(SensorPort.S2);
		
		bumpSensor = new EV3TouchSensor(SensorPort.S1);
		
		leftMotor = new EV3LargeRegulatedMotor(MotorPort.B);
		rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);
		
		driveUnit = new DriveUnit(
				leftMotor, rightMotor, 
				Robot.WHEEL_RADIUS, Robot.ROBOT_WIDTH, 
				new Vector2(0.5f, 0.5f), 90
		);
		
		driveUnit.setAcceleration(720);
		driveUnit.setSpeed(speed);
		
//		leftMotor.setAcceleration(720);
//		rightMotor.setAcceleration(720);
		kP = GetParam("kP = %d", 1000, 50);
		kI = GetParam("kI = %d", 0, 1);
		kD = GetParam("kD = %d", 200, 10);
		
		controller = new PIDController(kP, kI, kD);
		
		setPoint = GetParam("Setpoint = %d cm", 5, 1) / 100.0f;
		controller.DesiredSetPoint(setPoint);
//		
//		forwardIncr = GetParam("Forward Increment = %d cm", 5, 1) / 100.0f;
//		turnIncr = GetParam("Turn Increment = %d cm", 1, 1) / 100.0f;
//		
//		turnAmt = GetParam("Turn Amt = %d deg", 15, 5);
//		leftMotor.synchronizeWith(new EV3LargeRegulatedMotor[] { rightMotor });
//		
//		leftMotor.setSpeed(speed);
//		rightMotor.setSpeed(speed);
		
		// wait for enter
		System.out.println("Waiting for enter");
		while (Button.waitForAnyPress() != Button.ID_ENTER) {}
		
		System.out.println("Move until bump");
		MoveUntilBump();
		
		System.out.println("Wall follow");
		WallFollow2();
		
		System.out.println("Go to goal");
		GoToGoal();
	}
	
	static int GetParam(String fmtStr, int init, int step) {
		while (true) {
			System.out.println(String.format(fmtStr, init));
			int pressed = Button.waitForAnyPress();
			if (pressed == Button.ID_DOWN) {
				init -= step;
			} else if (pressed == Button.ID_UP) {
				init += step;
			} else if (pressed == Button.ID_ENTER) {
				break;
			}
		}
		return init;
	}
	
	static void GoToGoal() {
		driveUnit.setSpeed(speed);
		driveUnit.straight(0.1f);
		int turned = GetHeading() / 2;
		driveUnit.rotate(-turned, turned);
		driveUnit.straight(0.75f);
	}
	
	static void WallFollow() {
		driveUnit.forward();
		
		int numInf = 0;
		
		while (true) {
			
			float y = GetDistance();
			if (!Float.isFinite(y) || y > RANGE_SANITY_THRESHOLD) {
				numInf++;
			} else {
				numInf = 0;
			}
			
			// glitch handling, wall end detection
			if (numInf > 5) {
				boolean foundWall = false;
				driveUnit.stop();
				for (int i = 0; i < 9; i++) {
					driveUnit.turn(5);
					y = GetDistance();
					if (Float.isFinite(y) && y <= RANGE_SANITY_THRESHOLD) {
						foundWall = true;
						break;
					}
				}
				
				if (foundWall) {
					driveUnit.forward();
				} else {
					driveUnit.turn(-45);
					for (int i = 0; i < 9; i++) {
						driveUnit.turn(-5);
						y = GetDistance();
						if (Float.isFinite(y) && y <= RANGE_SANITY_THRESHOLD) {
							foundWall = true;
							break;
						}
					}
					if (foundWall) {
						driveUnit.forward();
					} else {
						driveUnit.turn(45);
						driveUnit.forward();
					}
				}
			}

			float u = controller.Update(y);
			
			//u = Math.max(uMin, u);
			//u = Math.min(uMax, u);
			
			int sL = speed;
			int sR = speed;
			
			// if u < 0 -> turn left
			double absU = Math.abs(u);
			if (absU > 20) {
				if (u < 0) {
					sL -= absU;
					sR += absU;
				} 
				// if u > 0 -> turn right
				else if (u > 0) {
					sL += absU;
					sR -= absU;
				}
			}
			
			System.out.println(String.format("y = %.2f, u = %.0f, sL = %d, sR = %d", y, u, sL, sR));
			
			leftMotor.setSpeed(sL);
			rightMotor.setSpeed(sR);
		}
	}
	
	static void WallFollowBangBang() {
		//float[] bump = new float[1];

		//driveUnit.forward();
		
		float threshold = 0.02f;
		while (true) {
			//rangeSensor.fetchSample(bump, 0);
			float y = GetDistance();
			float err = setPoint - y;
			//float absErr = Math.abs(err);
			//int rDiff = Math.round(turnAmt * absErr);
			if (err > threshold) {
				driveUnit.turn(turnAmt);
				driveUnit.straight(turnIncr);
				driveUnit.turn(-turnAmt);
			} else if (err < -threshold) {
				driveUnit.turn(-turnAmt);
				driveUnit.straight(turnIncr);
				driveUnit.turn(turnAmt);
			} else {
				driveUnit.straight(forwardIncr);
			}
		}
	}
	
	static void WallFollow2() {
		//float threshold = 0.02f;
		driveUnit.forward();
		int numInf = 0;
		while (true) {
			//System.out.println("Heading: " + heading);
			if (IsBumping()) {
				System.out.println("IsBumping");
				// inside corner, turn right
				driveUnit.stop();
				driveUnit.setSpeed(speed);
				driveUnit.straight(-0.1f);
				//FindWall();
				driveUnit.turn(45);
				driveUnit.forward();
			} else {
				float y = GetDistance();
				if (!Float.isFinite(y) || y > RANGE_SANITY_THRESHOLD) {
					numInf++;
					// glitch handling, wall end detection
					if (numInf > 10) {
						System.out.println("Find Wall -- glitch");
						y = FindWall();
						if (!Float.isFinite(y)) {
							continue;
						}
						numInf = 0;
					} else {
						continue;
					}
				} else {
					numInf = 0;
				}
				
				float u = controller.Update(y);
				
				int sL = speed;
				int sR = speed;
				
				// if u < 0 -> turn left
				double absU = Math.abs(u);
				if (absU > 10) {
					if (u < 0) {
						sL -= absU;
						sR += absU;
					} 
					// if u > 0 -> turn right
					else if (u > 0) {
						sL += absU;
						sR -= absU;
					}
				}
				
				// clamp sL, sR
				sL = Math.max(Math.min(speed, sL), 1);
				sR = Math.max(Math.min(speed, sR), 1);
				
				driveUnit.setSpeed(sL, sR);
				
				System.out.println(String.format("y = %.2f, u = %.0f, sL = %d, sR = %d", y, u, sL, sR));
			}
			
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {}
		}
		//driveUnit.stop();
	}

	static float FindWall() {
		boolean foundWall = false;
		driveUnit.stop();
		driveUnit.setSpeed(speed);
		float y = 0;
		
		for (int i = 0; i < 9; i++) {
			driveUnit.turn(5);
			y = GetDistance();
			if (Float.isFinite(y) && y <= RANGE_SANITY_THRESHOLD) {
				foundWall = true;
				break;
			}
		}
		
		if (foundWall) {
			driveUnit.forward();
			return y;
		}
		
		driveUnit.turn(-45);
		for (int i = 0; i < 9; i++) {
			driveUnit.turn(-5);
			y = GetDistance();
			if (Float.isFinite(y) && y <= RANGE_SANITY_THRESHOLD) {
				foundWall = true;
				break;
			}
		}
		
		if (foundWall) {
			driveUnit.forward();
			return y;
		} else {
			driveUnit.turn(45);
			driveUnit.forward();
			//driveUnit.stop();
			return Float.NaN;
		}
	}
	
	static void MoveUntilBump() {
		driveUnit.forward();
		while (!IsBumping()) {}
		driveUnit.stop();
		driveUnit.straight(-0.05f);
		driveUnit.turn(75);
	}
	
	static boolean IsBumping() {
		float[] bump = new float[1];
		bumpSensor.fetchSample(bump, 0);
		return bump[0] != 0;
	}
	
	static float GetDistance() {
		float[] range = new float[1];
		float sum = 0;
		for (int i = 0; i < 5; i++) {
			rangeSensor.fetchSample(range, 0);
			sum += range[0];
		}
		return sum / 5;
	}
	
	static int GetHeading() {
		return (leftMotor.getTachoCount() - rightMotor.getTachoCount()) % 360;
	}

	static float GetSweep() {
		float[] range = new float[1];
		rangeSensor.fetchSample(range, 0);
		float y = range[0];
		driveUnit.turn(15);
		rangeSensor.fetchSample(range, 0);
		y = Math.min(y, range[0]);
		driveUnit.turn(15);
		rangeSensor.fetchSample(range, 0);
		y = Math.min(y, range[0]);
		driveUnit.turn(-30);
		return y;
	}

}
