import lejos.robotics.SampleProvider;

public class Collider {
	public static final float COLLIDER_THRESHOLD = 0.2f;
	
	private SampleProvider distance;
	private SampleProvider bump1;
	private SampleProvider bump2;
	
	private boolean isBumping = false;
	
	public Collider(SampleProvider distance, SampleProvider bump1, SampleProvider bump2) {
		this.distance = distance;
		this.bump1 = bump1;
		this.bump2 = bump2;
		
		if (distance.sampleSize() != 1 || bump1.sampleSize() != 1 || bump2.sampleSize() != 1)
			throw new IllegalArgumentException("Invalid sample size for sensors!");
	}
	
	public boolean isColliding() {
		isBumping = false;
		
		float[] samples = new float[1];
		
		bump1.fetchSample(samples, 0);
		if (samples[0] > 0) {
			isBumping = true;
			return true;
		}
		
		bump2.fetchSample(samples, 0);
		if (samples[0] > 0) {
			isBumping = true;
			return true;
		}
		
		distance.fetchSample(samples, 0);
		//System.out.println("Distance from object: " + samples[0]);
		if (samples[0] < COLLIDER_THRESHOLD) {
			return true;
		}
		
		return false;
	}
	
	public boolean isBumping() {
		return isBumping;
	}
}
