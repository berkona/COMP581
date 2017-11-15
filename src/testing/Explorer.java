package testing;

import lejos.hardware.Button;
import mapping.GridMap;
import robot.Robot;
import robot.Vector2;

public class Explorer {

	public static void main(String[] args) {
		new Explorer().run();
	}
	
	private Robot robot = new Robot(new Vector2(0f, 0f), 0);
	
	//private GridMap collisionMap = new GridMap(0.1f);
	//private IPathfinder pathfinder = new AStarPathfinder(collisionMap);
	
	public void run() {
		System.out.println("Waiting for enter");
		while (Button.waitForAnyPress() != Button.ID_ENTER) {}
		
		int[] angles = new int[19];
		int j = 0;
		for (int i = -90; i <= 90; i += 10) {
			angles[j++] = i;
		}
		robot.sonar.setAngles(angles);
		
		while (true) {
			robot.sonar.sweep();
			float[] points = robot.sonar.getDistances();
			
			float minPoint = Float.POSITIVE_INFINITY;
			float maxPoint = Float.NEGATIVE_INFINITY;
			int maxAngle = 0;
			for (int i = 0; i < points.length; i++) {
				float d = points[i];
				float a = angles[i];
				if ( a >= -30 && a <= 30) {
					if (d < minPoint) {
						minPoint = d;
					}
				}
				if (d >= maxPoint) {
					maxPoint = d;
					maxAngle = angles[i];
				}
			}
			
			robot.driveUnit.straight(minPoint - 0.2f);
			robot.driveUnit.turn(maxAngle);
		}
	}
}
