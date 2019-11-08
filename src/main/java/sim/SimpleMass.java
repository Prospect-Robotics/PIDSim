package sim;

public class SimpleMass extends StateModel {
	private double mass;
	private double friction;
	private double velocity;
	private double position;
	private double currentTime;

	public SimpleMass(double m, double f, double v, double startTime) {
		mass = m;
		friction = f;
		velocity = v;
		currentTime = startTime;
	}
	
	@Override
	public double getCurrentValue() {
		return position;
	}

	@Override
	public void forwardTo(double t) throws IllegalStateException {
		if (t <= currentTime)
			throw new IllegalStateException("Time cannot go backwards");

		double dt = t - currentTime;
		double f = myController.getControlValue();
		double friction_force = friction;
		if (velocity != 0) {
			friction_force *= (velocity > 0 ? -1 : 1);
		} else {
			friction_force *= (f > 0 ? -1 : 1);
		}
		if (velocity != 0) {
			double time_to_zero_v = (-velocity * mass) / (f + friction_force);
			if (time_to_zero_v > 0 && time_to_zero_v < dt) {
				position += velocity * time_to_zero_v;
				velocity = 0;
			} else {
				velocity += (f + friction_force) * dt / mass;
				position += velocity * dt;
			}
		} else if (Math.abs(f) > Math.abs(friction_force)) {
			velocity += (f + friction_force) * dt / mass;
			position += velocity * dt;
		}
		
		currentTime = t;
	}

	@Override
	public double getCurrentTime() {
		return currentTime;
	}

}
