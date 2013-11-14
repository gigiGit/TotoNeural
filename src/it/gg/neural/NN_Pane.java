package it.gg.neural;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.TextField;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.text.DecimalFormat;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.border.TitledBorder;

public class NN_Pane extends JPanel {
	JButton btnLearn;
	private JButton btnSalva;
	private JButton btnSberla;
	JButton btnStop;
	int curr = 0;

	double[] Err;

	private JLabel lblErrore;

	private JLabel lblErroreQm;
	private JLabel lblIter;

	private JLabel lblIterazione;

	private JLabel lblIterazioni;

	private JLabel lblLearningRate;

	private JLabel lblNewLabel;
	private JLabel lblPattern;
	private JLabel lblPatternInUso;
	private JLabel lblScriviOgni;
	double miny = 0, maxy = 0;
	JPanel panPlot;
	int cicli = 100;
	Color plotColor = Color.red;
	NN_Network rete;
	JSpinner spinIter, spinLearn, spinTemp;
	private JSpinner spinScriviOgni;
	private JTextField textField;
	private JLabel lblWMin;
	private JButton lblWeightRange;

	public NN_Pane(NN_Network _rete) {
		Err = new double[1000];
		rete = _rete;
		setBorder(new TitledBorder(null, rete.getNome(), TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));
		panPlot = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics graphics) {
				super.paint(graphics);
				Dimension size;
				size = this.getSize();
				disegna(size.width, size.height, graphics);
			}
		};
		add(panPlot);

		textField = new JTextField();
		add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);

		JPanel pnlControllo = new JPanel();
		pnlControllo.setSize(200, 300);
		add(new JScrollPane(pnlControllo), BorderLayout.WEST);

		btnLearn = new JButton("Addestra");
		btnLearn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runnable r = new Runnable() {
					public void run() {
						curr = 0;
						setRun();
						for (int i = 0; i < cicli; i++) {
							double err = rete.train();
							setInfo(i, err);
						}
						setStop("Fine apprendimento ");
					}
				};

				new Thread(r).start();

			}
		});
		pnlControllo.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.DEFAULT_COLSPEC, FormFactory.DEFAULT_COLSPEC, },
				new RowSpec[] { FormFactory.MIN_ROWSPEC,
						FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
						FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
						FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
						FormFactory.MIN_ROWSPEC, FormFactory.MIN_ROWSPEC,
						FormFactory.MIN_ROWSPEC, }));

		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rete.running = false;
				setStop("Interroto dall'utente.");
			}
		});

		lblLearningRate = new JLabel("Learning Rate");

		spinLearn = new JSpinner();
		spinLearn.setModel(new SpinnerNumberModel((double) rete
				.getLearningRate(), 0.05d, 2.0d, 0.05d));
		spinLearn.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rete.setLearningRate((Double) spinLearn.getValue());
			}
		});

		lblIterazioni = new JLabel("Iterazioni");
		spinIter = new JSpinner();
		spinIter.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				cicli = (Integer) spinIter.getValue();
			}
		});
		spinIter.setModel(new SpinnerNumberModel((int) cicli, 0, 1000000000,
				100));

		lblNewLabel = new JLabel("Temperatura");
		spinTemp = new JSpinner();
		spinTemp.setModel(new SpinnerNumberModel(
				(double) rete.getTemperatura(), 0.05, 2.0, 0.05));
		spinTemp.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rete.setTemperatura((Double) spinTemp.getValue());
			}
		});

		lblErroreQm = new JLabel("Errore qm");

		lblErrore = new JLabel("New label");

		lblIterazione = new JLabel("Iterazione");

		lblIter = new JLabel("New label");

		lblScriviOgni = new JLabel("scrivi ogni");

		spinScriviOgni = new JSpinner();
		spinScriviOgni.setModel(new SpinnerNumberModel((int) rete.scriviOgni,
				1, 100000, 100));
		spinScriviOgni.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				rete.scriviOgni = (Integer) spinScriviOgni.getValue();
			}
		});

		lblPatternInUso = new JLabel("Pattern in uso");

		lblPattern = new JLabel("New label");
		pnlControllo.add(btnLearn, "1, 1, left, center");
		pnlControllo.add(btnStop, "2, 1, left, center");
		pnlControllo.add(lblLearningRate, "1, 2, left, center");
		pnlControllo.add(spinLearn, "2, 2, fill, center");
		pnlControllo.add(lblIterazioni, "1, 3, left, center");
		pnlControllo.add(spinIter, "2, 3, fill, center");
		pnlControllo.add(lblNewLabel, "1, 4, left, center");
		pnlControllo.add(spinTemp, "2, 4, fill, center");
		pnlControllo.add(lblErroreQm, "1, 5, left, center");
		pnlControllo.add(lblErrore, "2, 5, left, center");
		pnlControllo.add(lblIterazione, "1, 6, left, center");
		pnlControllo.add(lblIter, "2, 6, fill, center");
		pnlControllo.add(lblScriviOgni, "1, 7, left, center");
		pnlControllo.add(spinScriviOgni, "2, 7, fill, center");
		pnlControllo.add(lblPatternInUso, "1, 8,left, center");
		pnlControllo.add(lblPattern, "2, 8, fill, center");
		lblErrore.setText("");
		lblIter.setText("");

		btnSalva = new JButton("Salva");
		btnSalva.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rete.save();
			}
		});

		lblWMin = new JLabel("<html>&omega;(intervallo)</html>");
		pnlControllo.add(lblWMin, "1, 9");

		lblWeightRange = new JButton("");
		lblWeightRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rete.normalizza();
			}
		});
		pnlControllo.add(lblWeightRange, "2, 9");
		pnlControllo.add(btnSalva, "1, 10, left, center");

		btnSberla = new JButton("Sberla");
		btnSberla.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rete.sberla(0.1);
			}
		});
		pnlControllo.add(btnSberla, "2, 10, fill, center");
		setStop("");
	}

	public void disegna(int w, int h, Graphics g) {
		// g.drawLine(0, h / 2, w, h / 2);
		// g.drawLine(w / 2, 0, w / 2, h);
		if (Err == null)
			return;
		int y, x, x0, y0;
		miny = 0;
		maxy = 0;
		for (int i = 0; i < Err.length; i++) {
			if (maxy < Err[i])
				maxy = Err[i];
		}
		h -= 16;
		w -= 16;
		double delta = h / (maxy - miny);
		double alfa = (1.0 * (w) / Err.length);
		x0 = 0;
		y0 = (int) ((maxy - Err[0]) * delta);
		g.setColor(Color.GRAY);
		for (int ix = 0; ix < w; ix += w / 10) {
			g.drawLine(ix, 0, ix, h);
		}
		for (int iy = 0; iy < h; iy += h / 10) {
			g.drawLine(0, iy, w, iy);
		}
		g.setColor(plotColor);
		for (int i = 0; i < Err.length; i++) {
			x = (int) (alfa * i);
			y = (int) ((maxy - Err[i]) * delta);
			g.drawLine(x0, y0, x, y);
			x0 = x;
			y0 = y;
		}
	}

	public void setInfo(int iter, double sqm) {
		Err[curr % Err.length] = sqm;
		curr++;
		textField.setText("Errore qm : " + sqm + " iterazione = " + iter);
		lblErrore.setText("" + sqm);
		lblIter.setText("" + iter);
		lblPattern.setText("" + rete.getPatterns().size());
		lblWeightRange.setText("(" + df.format(rete.getMinWeigth()) + ","
				+ df.format(rete.getMaxWeigth()) + ")");
		spinLearn.setValue(rete.getLearningRate());
		panPlot.updateUI();
	}

	public void setRun() {
		btnLearn.setEnabled(false);
		btnStop.setEnabled(true);
		btnSalva.setEnabled(false);
		btnSberla.setEnabled(true);
	}

	public void setStop(String msg) {
		btnLearn.setEnabled(true);
		btnStop.setEnabled(false);
		btnSalva.setEnabled(true);
		btnSberla.setEnabled(false);
		textField.setText(msg);
		lblPattern.setText("" + rete.getPatterns().size());
		lblIter.setText("0");
		lblWeightRange.setText("(" + df.format(rete.getMinWeigth()) + ","
				+ df.format(rete.getMaxWeigth()) + ")");
	}

	final static DecimalFormat df = new DecimalFormat(" 0.0000;-0.0000");
}
