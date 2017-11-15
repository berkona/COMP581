package testing;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import robot.Robot;
import robot.Vector2;

public class SonarTest {
	
	static void waitForEnter() {
		System.out.println("Waiting for enter");
		Sound.setVolume(100);
		Sound.beepSequenceUp();
		while (Button.waitForAnyPress() != Button.ID_ENTER) {}
	}
	
	public static void main(String[] args) {
		Robot r = new Robot(new Vector2(0f, 0f), 0);
		waitForEnter();
		r.sonar.sweep();
		Vector2[] pts = r.sonar.getPoints();
		Vector2 inFront = pts[pts.length/2];
		inFront = Vector2.sub(inFront, new Vector2(0.0508f, 0f));
		System.out.println("In front pos " + inFront);
		for (int i = 0; i < 5; i++) {
			r.driveUnit.moveTo(inFront);
			waitForEnter();
			r.driveUnit.straight(-0.5f);
		}
	}
}
