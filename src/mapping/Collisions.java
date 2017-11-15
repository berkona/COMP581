package mapping;

import robot.Vector2;

public class Collisions {
	public static boolean RectPointCollides(Vector2 topLeft, Vector2 bottomRight, Vector2 point) {
		return point.x >= bottomRight.x 
			&& point.x <= topLeft.x 
			&& point.y >= topLeft.y 
			&& point.y <= bottomRight.y;
	}
}
