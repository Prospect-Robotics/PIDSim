package sim;

public class LinearMotion extends MotionPlan {
	private double speed;
	
	public LinearMotion(double speed) {
		this.speed = speed;
	}
	@Override
	public double getDesiredPosition(double time) {
		// TODO Auto-generated method stub
		return speed * time;
	}

}
