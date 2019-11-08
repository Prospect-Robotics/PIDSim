package sim;

public abstract class  StateModel {
	protected Controller myController;
	public void setController(Controller c)
	{
		myController = c;
	}

	public abstract double getCurrentValue();
	public abstract void forwardTo(double t) throws IllegalStateException;
	public abstract double getCurrentTime();

}
