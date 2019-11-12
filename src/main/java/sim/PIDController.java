package sim;

public class PIDController extends Controller {
	private MotionPlan motionPlan;
	private double currentTime;
	private double iScale;
	private double pScale;
	private double dScale;
	private double desiredValue;
	private double controlValue;
	private double lastError;
	private double accumulatedError;
	private boolean firstInterval;
	
	public PIDController(double p, double i, double d, double startTime)
	{
		pScale = p;
		iScale = i;
		dScale = d;
		currentTime = startTime;
		firstInterval = true;
	}
	
	public void setDesiredValue(double d)
	{
		desiredValue = d;
	}
	
	private double getDesiredValue(double t) {
		if (motionPlan != null) {
			return motionPlan.getDesiredPosition(t);
		} else {
			return desiredValue;
		}
	}
	
	public void setMotionPlan(MotionPlan p) {
		motionPlan = p;
	}
	
	@Override
	public double getControlValue() {
		return controlValue;
	}

	@Override
	public void forwardTo(double t) throws IllegalStateException {
		if (t <= currentTime)
			throw new IllegalStateException("Time cannot go backwards");
		double dt = t - currentTime;
		double currentValue = myStateModel.getCurrentValue();
		double error = getDesiredValue(t) - currentValue;
		controlValue = error * pScale;
		if (!firstInterval) {
			controlValue += (error - lastError) * dScale / dt;
		}
		firstInterval = false;
		
		accumulatedError += error * dt;
		controlValue += accumulatedError * iScale;
		
		lastError = error;
		currentTime = t;
	}

	@Override
	public double getCurrentTime() {
		return currentTime;
	}

	@Override
	public double getErrorValue() {
		return lastError;
	}

	@Override
	public double getPlanValue() {
		return getDesiredValue(currentTime);
	}

}
