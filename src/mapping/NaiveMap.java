package mapping;

import java.util.LinkedList;
import java.util.List;

import robot.Vector2;

public class NaiveMap implements IMap {

	List<Vector2> points = new LinkedList<Vector2>();
	
	@Override
	public void addPoint(Vector2 pt) {
		points.add(pt);
	}

	@Override
	public Iterable<Vector2> getPoints() {
		return points;
	}

	@Override
	public boolean collides(Vector2 p, Vector2 pPrime, float r) {
		// circle-point collision around to with radius r
		for (Vector2 pt : points) {
			if (Vector2.sub(pPrime, pt).magnitude() <= r)
				return true;
		}
		
		// vector from->to
		Vector2 dP = Vector2.sub(p, pPrime);
		// (unit vector of dP) * r
		Vector2 dPR = Vector2.mult(Vector2.div(dP, dP.magnitude()), r);
		Vector2 left = Vector2.rotate(dPR, (float) Math.toRadians(90));
		Vector2 right = Vector2.rotate(dPR, (float) Math.toRadians(-90));
		
		Vector2 a = Vector2.add(left, p);
		Vector2 b = Vector2.add(right, p);
		Vector2 c = Vector2.add(left, pPrime);
		Vector2 d = Vector2.add(right, pPrime);
		
		return false;
	}

}
