package mapping;

import robot.Vector2;

public interface IMap {
	public void addPoint(Vector2 pt);
	public Iterable<Vector2> getPoints();
	public boolean collides(Vector2 from, Vector2 to, float r);
}
