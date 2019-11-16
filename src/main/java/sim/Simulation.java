package sim;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
	static class ModelController {
		ModelController(StateModel m, Controller c) {
			if (m == null || c == null)
				throw new NullPointerException();
			this.m = m;
			this.c = c;
		}
		private StateModel m;
		private Controller c;
		
		StateModel getModel() {
			return m;
		}
		Controller getController() {
			return c;
		}
	}

	List<ModelController> mc;
	double samples[];
	double controls[];
	double errors[];
	double plans[];

	public Simulation(int n, double factorP, double factorI, double factorD, double m, double f, double velocity, MotionPlan plan) {
		mc = new ArrayList<ModelController>();
		samples = new double[n];
		controls = new double[n];
		errors = new double[n];
		plans = new double[n];
	
		StateModel model = new SimpleMass(m, f, velocity, 0);
		PIDController controller = new PIDController(factorP, factorI, factorD, 500, 0);
		model.setController(controller);
		controller.setStateModel(model);
		controller.setDesiredValue(10);
		controller.setMotionPlan(plan);
		ModelController v = new ModelController(model, controller);
		addModelController(v);
	}
	
	public void addModelController(ModelController item) {
		if (item == null)
			throw new NullPointerException();
		mc.add(item);
	}
	
	public void run() {
		int time_index;
		for (time_index = 1; time_index < samples.length; time_index++) {
			double t = (double)time_index / 100.0;
			for (ModelController modelController : mc) {
				Controller c = modelController.getController();
				c.forwardTo(t);
			}
			for (ModelController modelController : mc) {
				StateModel m = modelController.getModel();
				m.forwardTo(t);
			}
			for (ModelController modelController : mc) {
				StateModel m = modelController.getModel();
				Controller c = modelController.getController();
				//System.out.printf("%5.02f: %6.03f\n", t, m.getCurrentValue());
				samples[time_index] = m.getCurrentValue();
				controls[time_index] = c.getControlValue();
				errors[time_index] = c.getErrorValue();
				plans[time_index] = c.getPlanValue();
			}
		}

	}
	
}
