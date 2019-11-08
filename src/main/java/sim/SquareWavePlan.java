package sim;

public class SquareWavePlan extends MotionPlan {

	private double amplitude;
	private double period;

	public SquareWavePlan(double a, double period)
	{
		this.amplitude = a;
		this.period = period;
	}
	@Override
	public double getDesiredPosition(double time) {
		// TODO Auto-generated method stub
		double phase = time/(period * 2);
		int p = (int)phase;
		
		return (p & 1) != 0 ? amplitude : 0;
	}

}
