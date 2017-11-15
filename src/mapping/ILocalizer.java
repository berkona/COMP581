package mapping;

import robot.Vector2;

public interface ILocalizer {
	public Vector2 Update(Vector2 motion, Vector2[] features, float[] distances);
}
