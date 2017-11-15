package robot;

public class Vector2 {
	
	public static Vector2 add(Vector2 a, Vector2 b) {
		return new Vector2(a.x + b.x, a.y + b.y);
	}
	
	public static Vector2 sub(Vector2 a, Vector2 b) {
		return new Vector2(a.x - b.x, a.y - b.y);
	}
	
	public static Vector2 mult(Vector2 a, float b) {
		return new Vector2(a.x * b, a.y * b);
	}
	
	public static Vector2 div(Vector2 a, float b) {
		return new Vector2(a.x / b, a.y / b);
	}
	
	public static Vector2 fromPolarCoords(float theta, float length) {
		float x = (float) Math.cos(theta) * length;
		float y = (float) Math.sin(theta) * length;
		return new Vector2(x, y);
	}
	
	public static double Dot(Vector2 a, Vector2 b) {
		return a.x * b.x + a.y * b.y;
	}
	
	public static double AngleBetween(Vector2 a, Vector2 b) {
		return Math.acos(Dot(a, b) / (a.magnitude() * b.magnitude()));
	}
	
	/**
	 * Rotates a vector around the origin by theta using counter-clockwise-positive rotation
	 * @param v - vector to rotate
	 * @param theta - theta to rotate by, in radians
	 * @return rotated vector
	 */
	public static Vector2 rotate(Vector2 v, float theta) {
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);
		return new Vector2(
				v.x * cos  - v.y * sin,
				v.x * sin + v.y * cos
		);
	}
	
	public float x;
	public float y;
	
	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float magnitude() {
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public String toString() {
		return String.format("[ %.2f , %.2f ]", x, y);
	}
}
