package sim;

public abstract class Controller {
	protected StateModel myStateModel;
	public void setStateModel(StateModel m)
	{
		myStateModel = m;
	}

	public abstract double getControlValue();
	public abstract double getErrorValue();
	public abstract void forwardTo(double t) throws IllegalStateException;
	public abstract double getCurrentTime();
}
