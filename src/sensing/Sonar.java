package sensing;

import java.util.Arrays;

import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import robot.DriveUnit;
import robot.Vector2;

public class Sonar {
	private EV3UltrasonicSensor rangeSensor;
	private EV3MediumRegulatedMotor rangeMotor;
	private DriveUnit driveUnit;
	
	private int[] angles;
	private float[] distances;
	private Vector2[] points;
	
	private float[] pings = new float[5];
	
	public Sonar(EV3UltrasonicSensor rangeSensor, EV3MediumRegulatedMotor rangeMotor, DriveUnit driveUnit, int[] angles) {
		this.rangeSensor = rangeSensor;
		this.rangeMotor = rangeMotor;
		this.driveUnit = driveUnit;
		setAngles(angles);
	}
	
	public void setAngles(int[] angles) {
		this.angles = angles;
		this.distances = new float[angles.length];
		this.points = new Vector2[angles.length];
	}
	
	public float ping() {
		return getDist();
	}
	
	public float distanceAt(int theta) {
		if (rangeMotor.isMoving())
			throw new RuntimeException("Sweep in progress");
		
		if (rangeMotor.getLimitAngle() != theta)
			rangeMotor.rotateTo(theta);
		
		return getDist();
	}
	
	public void sweep() {
		int n = angles.length;
		float heading = driveUnit.heading();
		// heading is in degrees, we need to use radians
		heading = (float) Math.toRadians(heading);
		
		Vector2 pos = driveUnit.position();
		
		float cos = (float) Math.cos(-heading);
		float sin = (float) Math.sin(-heading);
		
		for (int i = 0; i < n; i++) {
			int theta = angles[i];
			float d = distanceAt(theta);
			distances[i] = d;
			Vector2 pt = null;
			if (Float.isFinite(d)) {
				// convert from polar form to cartesian form
				float asRad = (float) Math.toRadians(theta);
				float x = (float) (Math.cos(asRad) * d);
				float y = (float) (Math.sin(asRad) * d);
				// rotate using clockwise matrix and translate origin to world origin
				x = x * cos + y * sin + pos.x;
				y = x * -sin + y * cos + pos.y;
				pt = new Vector2(x, y);
			}
			points[i] = pt;
		}
	}
	
	public int[] getAngles() {
		return angles;
	}
	
	public float[] getDistances() {
		return distances;
	}
	
	public Vector2[] getPoints() {
		return points;
	}
	
	protected float getDist() {
		int nFinite = 0;
		float sum = 0;
		for (int i = 0; i < pings.length; i++) {
			rangeSensor.fetchSample(pings, i);
			if (Float.isFinite(pings[i])) {
				nFinite++;
				sum += pings[i];
			}
		}
		if (nFinite >= pings.length / 2) {
			return sum / nFinite;
		} else {
			return Float.POSITIVE_INFINITY;
		}
	}
}
