package Lab2;

public class PIDController {
	
	private float setPoint = Float.NaN;
	
	private float kP;
	private float kI;
	private float kD;
	
	private float sumError = 0;
	private float lastError = Float.NaN;
	
	public PIDController(float kP, float kI, float kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}
	
	public void DesiredSetPoint(float setPoint) {
		if (!Float.isFinite(setPoint))
			throw new RuntimeException("Invalid setpoint!");
		
		this.setPoint = setPoint;
	}
	
	public float Update(float y) {
		if (!Float.isFinite(y))
			throw new RuntimeException("Invalid y: " + y);
		
		float error = setPoint - y;
		float pTerm = kP * error;
		
		sumError += error;
		float iTerm = kI * sumError;
		
		float dTerm = kD * (Float.isFinite(lastError) ? error - lastError : error);
		
		lastError = error;
		
		return pTerm + iTerm + dTerm;
	}
}
