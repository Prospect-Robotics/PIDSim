package sim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SimUI implements Runnable {
	double data[];
	DefaultBoundedRangeModel pModel;
	DefaultBoundedRangeModel iModel;
	DefaultBoundedRangeModel dModel;
	DefaultBoundedRangeModel massModel;
	DefaultBoundedRangeModel frictionModel;
	SimCanvas simCanvas;

	private static final class TxtToModel implements KeyListener, FocusListener {
		private final JTextField txt;
		private final BoundedRangeModel model;

		private TxtToModel(JTextField t, BoundedRangeModel m) {
			this.txt = t;
			this.model = m;
		}

		@Override
		public void keyPressed(KeyEvent ke) {
			// Ignore			
		}

		@Override
		public void keyReleased(KeyEvent ke) {
			// Ignore			
		}

		private void updateModel() {
			try {
				double value = Double.parseDouble(txt.getText());
				int slider_position = (int)(value * 10);
				model.setValue(slider_position);
			} catch (NumberFormatException nfe) {
				System.out.println("Bad Value!!");
				txt.setText(val2String(model.getValue()));
			}
		}
		
		@Override
		public void keyTyped(KeyEvent ke) {
			if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
				updateModel();
			}
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			// Ignore
			
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			updateModel();
		}
	}
	
	@SuppressWarnings("serial")
	static final class SliderInput extends JPanel {
		
		private BoundedRangeModel model;
		
		SliderInput(String label, BoundedRangeModel m) {
			super(new GridBagLayout());
			model = m;

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints gbc1;
			
			JLabel lab = new JLabel(label);
			gbc1 = (GridBagConstraints)gbc.clone();
			gbc1.gridx = 0;
			gbc1.gridy = 0;
			this.add(lab, gbc1);

			JSlider slider = new JSlider(model);
			gbc1 = (GridBagConstraints)gbc.clone();
			gbc1.gridx = 1;
			gbc1.gridy = 0;
			gbc1.weightx = 1.0;
			this.add(slider, gbc1);
			
			
			JTextField txt = new JTextField("", 4);
			TxtToModel t2m = new TxtToModel(txt, model);
			txt.addKeyListener(t2m);
			txt.addFocusListener(t2m);
			ModelToTxt m2t = new ModelToTxt(txt, model);
			m2t.stateChanged(null); // dummy event to set the initial text.
			model.addChangeListener(m2t);
			gbc1 = (GridBagConstraints)gbc.clone();
			gbc1.gridx = 2;
			gbc1.gridy = 0;
			this.add(txt, gbc1);
		}
	}

	private static final class ModelToTxt implements ChangeListener {
		private final JTextField txt;
		private final BoundedRangeModel model;

		private ModelToTxt(JTextField t, BoundedRangeModel m) {
			this.txt = t;
			this.model = m;
		}

		public void stateChanged(ChangeEvent e) {
			txt.setText(val2String(model.getValue()));
		}
	}

	@SuppressWarnings("serial")
	static class SimCanvas extends JPanel {
		double data[];
		double maxData;
		double minData;
		double control[];
		double maxControl;
		double minControl;
		double plan[];
		double maxPlan;
		double minPlan;

		SimCanvas(double d[]) {
			setData(d);
		}
		
		void setData(double d[]) {
			data = d;
			if (data == null)
				return;
			if (data.length > 0) {
				maxData = data[0];
				minData = data[0];
			} else {
				maxData = 0;
				minData = 0;
			}
			for(double v : data) {
				if (v > maxData)
					maxData = v;
				if (v < minData)
					minData = v;
			}
			repaint();
		}

		void setControl(double c[]) {
			control = c;
			if (control == null)
				return;
			if (control.length > 0) {
				maxControl = control[0];
				minControl = control[0];
			} else {
				maxControl = 0;
				minControl = 0;
			}
			for(double v : control) {
				if (v > maxControl)
					maxControl = v;
				if (v < minControl)
					minControl = v;
			}
			repaint();
		}

		void setPlan(double p[]) {
			plan = p;
			if (plan == null)
				return;
			if (plan.length > 0) {
				maxPlan = plan[0];
				minPlan = plan[0];
			} else {
				maxControl = 0;
				minControl = 0;
			}
			for(double v : plan) {
				if (v > maxPlan)
					maxPlan = v;
				if (v < minPlan)
					minPlan = v;
			}
			repaint();
		}

		@Override
		public void paintComponent(Graphics g) {
			final int margin = 7;
			int width = getWidth();
			int height = getHeight();
			g.setColor(Color.yellow);
			g.fillRect(0, 0, width, height);
			if (data == null)
				return;
			g.setColor(Color.gray);
			double rangeMax = maxPlan > maxData ? maxPlan : maxData;
			double rangeMin = minPlan < minData ? minPlan : minData;
			double scale = (height - 2.0 * margin) / (rangeMax - rangeMin);
			int zero = height - 1 - margin - (int)(scale * (0.0 - rangeMin));
			int ten = height - 1 - margin - (int)(scale * (10.0 - rangeMin));
			g.drawString( "0.0", 5,zero - 5);
			g.drawLine(0, zero, width, zero);
			g.drawString( "10.0", 5,ten + 15);
			g.drawLine(0, ten, width, ten);
			g.setColor(Color.blue);
			int prevY = 0;
			for(int i = 0; i < width && i < data.length; i++) {
				int y = height - 1 - margin - (int)(scale * (data[i] - rangeMin));
				if (i == 0)
					g.drawLine(i, y,  i,  y);
				else
					g.drawLine(i - 1, prevY,  i,  y);
				prevY = y;
			}
			g.setColor(Color.green);
			prevY = 0;
			for(int i = 0; i < width && i < plan.length; i++) {
				int y = height - 1 - margin - (int)(scale * (plan[i] - rangeMin));
				if (i == 0)
					g.drawLine(i, y,  i,  y);
				else
					g.drawLine(i - 1, prevY,  i,  y);
				prevY = y;
			}
			
			scale = (height - 2.0 * margin) / (maxControl - minControl);
			g.setColor(Color.red);
			prevY = 0;
			for(int i = 0; i < width && i < control.length; i++) {
				int y = height - 1 - margin - (int)(scale * (control[i] - minControl));
				if (i == 0)
					g.drawLine(i, y,  i,  y);
				else
					g.drawLine(i - 1, prevY,  i,  y);
				prevY = y;
			}

		}
	}
	public SimUI(double d[]) {
		pModel = new DefaultBoundedRangeModel(100, 0, 0, 999);
		iModel = new DefaultBoundedRangeModel(0, 0, 0, 999);
		dModel = new DefaultBoundedRangeModel(80, 0, 0, 999);
		massModel = new DefaultBoundedRangeModel(20, 0, 1, 999); // Must be non-zero to avoid division by zero problems.
		frictionModel = new DefaultBoundedRangeModel(0, 0, 0, 999);
		
		data = d;
	}
	
	static String val2String(int v) {
		StringWriter sw = new StringWriter(10);
		PrintWriter pw = new PrintWriter(sw);
		pw.printf("%2.01f", (double)v / 10.0);
		pw.flush();
		return sw.toString();
	}
	
	@Override
	public void run() {
		JFrame mainFrame = new JFrame("Main UI");
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.33;
		GridBagConstraints gbc1;
		mainFrame.setLayout(gb);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		SliderInput inputP = new SliderInput(" P: ", pModel);
		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 0;
		gbc1.gridy = 0;
		mainFrame.add(inputP, gbc1);


		SliderInput inputI = new SliderInput(" I: ", iModel);
		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 1;
		gbc1.gridy = 0;
		mainFrame.add(inputI, gbc1);

		SliderInput inputD = new SliderInput(" D: ", dModel);
		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 2;
		gbc1.gridy = 0;
		mainFrame.add(inputD, gbc1);

		SliderInput inputMass = new SliderInput(" M: ", massModel);
		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 0;
		gbc1.gridy = 1;
		mainFrame.add(inputMass, gbc1);

		SliderInput inputFriction = new SliderInput(" F: ", frictionModel);
		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 1;
		gbc1.gridy = 1;
		mainFrame.add(inputFriction, gbc1);

		
		JButton button = new JButton("Regenerate");
		ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Button Pressed!");
				
				double p = (double)pModel.getValue() / 10.0;
				double i = (double)iModel.getValue() / 10.0;
				double d = (double)dModel.getValue() / 10.0;
				double mass = (double)massModel.getValue() / 10.0;
				double friction = (double)frictionModel.getValue() / 10.0;
				Simulation sim = new Simulation(1000, p, i, d, mass, friction, 0.0);
				sim.run();
				simCanvas.setData(sim.samples);
				simCanvas.setControl(sim.controls);
				simCanvas.setPlan(sim.plans);
			}
		};
		button.addActionListener(al);
		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 3;
		gbc1.gridy = 0;
		gbc1.weightx = 0;
		mainFrame.add(button, gbc1);

		gbc1 = (GridBagConstraints)gbc.clone();
		gbc1.gridx = 0;
		gbc1.gridy = 2;
		gbc1.gridwidth = 4;
		gbc1.weightx = 1.0;
		gbc1.weighty = 1.0;
		gbc1.fill = GridBagConstraints.BOTH;
		simCanvas = new SimCanvas(data);
		mainFrame.add(simCanvas, gbc1);
		mainFrame.setSize(1010, 500);
		al.actionPerformed(null);
		mainFrame.setVisible(true);

	}

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new SimUI(null));
		
	}


}
