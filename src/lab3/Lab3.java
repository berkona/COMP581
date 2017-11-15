package lab3;

import Lab2.PIDController;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import robot.Robot;
import robot.Vector2;

public class Lab3 {

	public static float averageDistance(int[] angles, float[] distances, int start, int end, int offset) {
		float sum = 0;
		for (int i = start; i <= end; i++) {
			sum += Math.cos(Math.toRadians(offset + angles[i])) * distances[i];
		}
		return sum / (end - start + 1); 
	}
	
	public static final float MOVE_DISTANCE = 0.1f;
	public static final int TURN_ANGLE = 30;
	public static final float TURN_THRESHOLD = 0.1f;
	public static final float SET_POINT = 0.15f;
	public static final float SANITY_THRESHOLD = 1f;
	
	static Robot r;
	
	static Object distanceLock = new Object();
	
	static boolean wallFollowing = true;
	
	static Runnable driveRunnable = new Runnable() {

		@Override
		public void run() {
			PIDController pid = new PIDController(200, 0, 50);
			pid.DesiredSetPoint(SET_POINT);
			int nMoves = 0;
			while (wallFollowing) {
				int nextTurn = 0;
				
				synchronized(distanceLock) {
					if (perpendicularDirty) {
						if (Float.isFinite(perpendicularDist)) {
							float u = pid.Update(perpendicularDist);
							System.out.println("u " + u);
							if (u > TURN_ANGLE) {
								u = TURN_ANGLE;
							} else if (u < -TURN_ANGLE) {
								u = -TURN_ANGLE;
							}
							nextTurn = Math.round(-u);
							//r.driveUnit.turn(Math.round(u));
						} else {
							nextTurn = -TURN_ANGLE;
//							r.driveUnit.turn(-TURN_ANGLE);
						}
						perpendicularDirty = false;
					}
				}
				
				if (r.IsBumping()) {
					r.driveUnit.straight(-MOVE_DISTANCE);
					r.driveUnit.turn(-45);
				} else {
					r.driveUnit.turn(nextTurn);				
					r.driveUnit.straight(MOVE_DISTANCE);
				}
				
				nMoves++;
				float dist = Vector2.sub(hitPoint, r.driveUnit.position()).magnitude();
				System.out.println("Dist from hitpoint " + dist);
				wallFollowing = !(nMoves >= 10 && dist < 0.2f);
			}
			
			Sound.beep();
			r.driveUnit.moveTo(new Vector2(0, 0));
			Sound.beep();
		}
		
	};
	
	static Runnable sensorRunnable = new Runnable() {
		@Override
		public void run() {
			while (wallFollowing) {
				r.sonar.sweep();
				float newDist = FindPerpendicularDist(
						r.sonar.getDistances(), 
						r.sonar.getAngles()
				);
				synchronized (distanceLock) {
					perpendicularDist = newDist;
					perpendicularDirty = true;
				}
				System.out.println("pDist " + perpendicularDist);
			}
		}
	};
	
	static Thread driveThread = new Thread(driveRunnable, "Drive Thread");
	static Thread sensorThread = new Thread(sensorRunnable, "Sensor Thread");
	
	static float perpendicularDist = Float.NaN; 
	static boolean perpendicularDirty = false;
	
	static Vector2 hitPoint;
	
	public static void main(String[] args) {
		r = new Robot(new Vector2(0, 0), 0);
		r.driveUnit.setSpeed(720);
		Sound.setVolume(100);
		Sound.beep();
		System.out.println("Waiting for enter");
		while(Button.waitForAnyPress() != Button.ID_ENTER) {}
		
		r.sonar.setAngles(new int[] { -15, -10, -5, 0, 5, 10, 15 });
		r.sonar.sweep();
		float wallDist = averageDistance(
				r.sonar.getAngles(), 
				r.sonar.getDistances(), 
				0, r.sonar.getAngles().length-1, 0);

		hitPoint = new Vector2(wallDist - 0.2f, 0f);
		r.driveUnit.moveTo(hitPoint, -90);
		System.out.println("Hit point " + hitPoint);
		Sound.beep();
		r.driveUnit.setSpeed(360);
		
		r.sonar.setAngles(new int[] { -90, -80, -70, -60 });
		
//		// the rest is in here
//		sensorThread.start();
//		driveThread.start();
//		
//		try {
//			sensorThread.join();
//			driveThread.join();
//		} catch (InterruptedException e) {
//			System.out.println("Could not join all threads");
//			e.printStackTrace();
//		}
		
		int nSweeps = 0;
		
		PIDController pid = new PIDController(200, 0, 50);
		pid.DesiredSetPoint(SET_POINT);
		
		while (true) {
			nSweeps++;
			r.sonar.sweep();
			//distances = r.sonar.getDistances();
			//float forwardDist = averageDistance(r.sonar.getAngles(), r.sonar.getDistances(), 0, 2);
//			float perpendicularDist = averageDistance(
//					r.sonar.getAngles(), 
//					r.sonar.getDistances(), 
//					0, r.sonar.getAngles().length-1, 90);
			float perpendicularDist = FindPerpendicularDist(r.sonar.getDistances(), r.sonar.getAngles());
//			
			System.out.println("pDist " + perpendicularDist);
			//float delta = forwardDist - perpendicularDist;
			
			//System.out.println("delta " + delta);
			
//			if (delta > TURN_THRESHOLD) {
//				r.driveUnit.turn(-TURN_ANGLE);
//				r.driveUnit.straight(MOVE_DISTANCE/2f);
//			} else if (delta < -TURN_THRESHOLD) {
//				r.driveUnit.turn(TURN_ANGLE);
//				r.driveUnit.straight(MOVE_DISTANCE/2f);
//			} else {
				if (r.IsBumping()) {
					r.driveUnit.straight(-MOVE_DISTANCE);
					r.driveUnit.turn(-45);
				} else {
					if (Float.isFinite(perpendicularDist)) {
						//int u = GetTurnAngle(perpendicularDist, SET_POINT);
						float u = pid.Update(perpendicularDist);
						System.out.println("u " + u);
						if (u > TURN_ANGLE) {
							u = TURN_ANGLE;
						} else if (u < -TURN_ANGLE) {
							u = -TURN_ANGLE;
						}
						r.driveUnit.turn(Math.round(-u));
					} else {
						r.driveUnit.turn(-TURN_ANGLE);
					}
					r.driveUnit.straight(MOVE_DISTANCE);
				}
//			}
			
			System.out.println("Pos " + r.driveUnit.position());
			if (nSweeps > 10 && Vector2.sub(hitPoint, r.driveUnit.position()).magnitude() < 0.2f) {
				break;
			}
		}
		Sound.beep();
		r.driveUnit.moveTo(new Vector2(0, 0));
		Sound.beep();
	}
	
//	static int GetTurnAngle(float dist, float setPoint) {
//		float delta = setPoint - dist;
//		int angle = Math.round((float) (90.0 - Math.abs(Math.toDegrees(Math.acos(delta / MOVE_DISTANCE)))));
//		if (delta < 0) {
//			return -angle;
//		} else if (delta > 0) {
//			return angle;
//		} else {
//			return 0;
//		}
//	}
	
	static boolean ValidateDistance(float d) {
		return Float.isFinite(d) && d <= SANITY_THRESHOLD;
	}
	
	static float FindPerpendicularDist(float[] distances, int[] angles) {
		
		boolean d1Valid = ValidateDistance(distances[0]);
		boolean d2Valid = ValidateDistance(distances[1]);
		boolean d3Valid = ValidateDistance(distances[2]);
		boolean d4Valid = ValidateDistance(distances[3]);
		
		float a = Float.NaN;
		if (d1Valid && d2Valid) {
			a = (distances[0] + distances[1]) / 2;
		} else if (d1Valid && !d2Valid) {
			a = distances[0];
		} else if (!d1Valid && d2Valid) {
			a = distances[1];
		}
		
		float b = Float.NaN;
		if (d3Valid && d4Valid) {
			b = (distances[2] + distances[3])/2;
		} else if (d3Valid && !d4Valid) {
			b = distances[2];
		} else if (!d3Valid && d4Valid) {
			b = distances[3];
		}
		
//		boolean aFinite = ValidateDistance(a);
//		boolean bFinite = ValidateDistance(b);
		
		// there are four cases:
		// normal case: a and b are finite
		// edge 1: a is finite, b is non-finite
		// edge 2: a is non-finite, b is finite
		// edge 3: a is non-finite, b is non-finite
//		if (aFinite && bFinite) {
		float gamma = Math.abs(angles[0] - angles[angles.length-1]);
		// law of cosines
		float c = (float) Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(Math.toRadians(gamma)));
		float beta = (float) Math.acos((b*b - a*a - c*c) / (-2.0 * a * c));
		return (float) (a * Math.sin(beta));
//		} else if (aFinite && !bFinite) {
//			// prefer forward estimate over invalid backward estimate
//			return a;
//		} else if (!aFinite && bFinite) {
//			return b;
//		} else {
//			// give up and return NaN
//			return Float.NaN;
//		}
	}

}
