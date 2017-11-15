package mapping;

import java.util.Random;

import robot.Vector2;

public class MonteCarloLocalization implements ILocalizer {
	
	Vector2[] particles;
	
	Random noiseGenerator = new Random();
	float noisePower;
	
	public MonteCarloLocalization(int M, Vector2 topLeft, Vector2 bottomRight, float noisePower) {
		this.noisePower = noisePower;
		
		Random r = new Random();
		
		float dX = topLeft.x - bottomRight.x;
		float dY = topLeft.y - bottomRight.y;
		
		// create M particles at random positions in the workspace
		particles = new Vector2[M];
		for (int i = 0; i < M; i++) {
			float x = dX * r.nextFloat() + bottomRight.x;
			float y = dY * r.nextFloat() + bottomRight.y;
			particles[i] = new Vector2(x, y);
		}
	}
	
	public Vector2 Update(Vector2 motion, Vector2[] features, float[] distances) {
		int M = particles.length;
		Vector2[] newParticles = new Vector2[M];
		float[] weights = new float[M];
		
		// update particle positions by motion and estimate prob. by distanceReading
		for (int i = 0; i < M; i++) {
			newParticles[i] = Vector2.add(particles[i], motion);
			float w = 0;
		}
		
		return null;
	}
}
