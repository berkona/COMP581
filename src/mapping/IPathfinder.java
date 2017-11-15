package mapping;

import robot.Vector2;

public interface IPathfinder {
	public Vector2[] findPath(Vector2 start, Vector2 goal);
}
