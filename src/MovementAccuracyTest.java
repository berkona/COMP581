import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;

public class MovementAccuracyTest {

	static int driveSpeed = 360;
	static int acceleration = 6000;
	
	static final double TEST_DIST = 0.3048;
	
	public static void main(String[] args) {
		RegulatedMotor left = new EV3LargeRegulatedMotor(MotorPort.B);
		RegulatedMotor right = new EV3LargeRegulatedMotor(MotorPort.C);
		
		DriveUnit dr = new DriveUnit(left, right, Robot.WHEEL_RADIUS, Robot.ROBOT_WIDTH);
		
		KeyListener listener = new DriveSpeedListener();
		System.out.println("A: " + acceleration);
		
		Button.UP.addKeyListener(listener);
		Button.DOWN.addKeyListener(listener);
		Button.ENTER.waitForPressAndRelease();
		
		dr.SetSpeed(driveSpeed);
		dr.SetAcceleration(acceleration);
		
		for (int i = 0; i < 32; i++) {
			System.out.println("Step #"+(i+1));
			dr.straight(TEST_DIST);
			dr.waitMove();
			dr.straight(-TEST_DIST);
			dr.waitMove();
		}
		
		left.close();
		right.close();
	}
	
	public static class DriveSpeedListener implements KeyListener {

		@Override
		public void keyPressed(Key k) {}

		@Override
		public void keyReleased(Key k) {
//			System.out.println("Key released: " + k.getId() + " " + k.getName());
			if (k.getId() == Button.ID_UP) {
				acceleration += 90;
			} else if (k.getId() == Button.ID_DOWN) {
				acceleration -= 90;
			} else {
				System.out.println("Invalid key pressed! Key: " + k.getId() + " " + k.getName());
				return;
			}
			System.out.println("A: " + acceleration);
		}
		
	}
}
