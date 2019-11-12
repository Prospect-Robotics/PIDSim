package sim;

public class LinearMotion extends MotionPlan {
	private double speed;
	private double delay;
	private double duration;
	
	public LinearMotion(double speed, double delay, double duration) {
		this.speed = speed;
		this.delay = delay;
		this.duration = duration;
	}
	@Override
	public double getDesiredPosition(double time) {
		if (time < delay)
			return 0;
		if (time < delay + duration)
			return speed * (time - delay);
		return speed * duration;
	}

}
