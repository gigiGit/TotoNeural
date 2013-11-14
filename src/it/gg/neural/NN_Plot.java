package it.gg.neural;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class NN_Plot extends JPanel {
	final static DecimalFormat dff = new DecimalFormat(" 0.0000;-0.0000");
	JButton btnLearn;
	Double currentw = null;
	private JButton btnSalva;
	int curr = 0;
	int wp = 150, hp = 150, maxWp = 1000;
	double[] yValue;

	private JLabel lblErrore;

	private JLabel lblErroreQm;

	private JLabel lblIterazioni;

	private JLabel lblNewLabel;
	private JLabel lblPattern;
	private JLabel lblPatternInUso;
	double miny = 0, maxy = 0;
	JPanel panPlot;
	int cicli = 1;
	Color plotColor = Color.red;
	NN_Network rete;
	JSpinner spinIter, spinTemp;
	private JTextField textField;
	private JLabel lblWMin;
	private JLabel lblWeightRange;
	Hashtable<String, JPanel> ht = new Hashtable<String, JPanel>();

	public NN_Plot(NN_Network _rete) {
		yValue = new double[1000];
		rete = _rete;
		setBorder(new TitledBorder(null, rete.getNome(), TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		setLayout(new BorderLayout(0, 0));

		JPanel plotters = new JPanel();
		add(new JScrollPane(plotters));
		plotters.setLayout(null);

		int x = 0, y = 0;

		for (NN_Layer l : rete.getNet()) {
			for (NN_Neuron n : l.neurons) {
				x = 0;
				for (int i = 0; i < n.weigth.length; i++) {
					JPanel panPlotDummy = new JPanel() {
						public void paint(Graphics g) {
							String text = (String) getClientProperty("txt");
							if (text == null)
								return;
							double[] yValue = (double[]) getClientProperty("err");
							Dimension size;
							size = getSize();
							int w = size.width;
							int h = size.height;
							g.clearRect(0, 0, w, h);
							int y, x, x0, y0;
							miny = 0;
							maxy = 0;
							for (int i = 0; i < yValue.length; i++) {
								if (maxy < yValue[i])
									maxy = yValue[i];
							}
							h -= 16;
							w -= 16;
							double delta = h / (maxy - miny);
							double alfa = (1.0 * (w) / yValue.length);
							x0 = 0;
							y0 = (int) ((maxy - yValue[0]) * delta);
							g.setColor(Color.GRAY);
							for (int ix = 0; ix < w; ix += w / 10) {
								g.drawLine(ix, 0, ix, h);
							}
							for (int iy = 0; iy < h; iy += h / 10) {
								g.drawLine(0, iy, w, iy);
							}
							g.setColor(plotColor);
							
							for (int i = 0; i < yValue.length; i++) {
								x = (int) (alfa * i);
								y = (int) ((maxy - yValue[i]) * delta);
								
								g.drawLine(x0, y0, x, y);
								x0 = x;
								y0 = y;
							
							//g.drawRect(x-2, y-2, x+2,y+2);
							}
							g.setColor(Color.black);
							g.drawString(dff.format(maxy), 0, 10);
							g.drawString(text, 0, h - 10);
						}
					};
					ht.put(l.nome + n.nome + "w" + i, panPlotDummy);
					panPlotDummy.setBounds(x, y, wp, hp);
					x += wp + 2;
					plotters.add(panPlotDummy);
					if (x > maxWp) {
						x = 0;
						y += hp + 2;
						// maxWp = x;
					}
				}
				y += hp + 2;
			}
		}
		plotters.setPreferredSize(new Dimension(maxWp, y + hp));
		textField = new JTextField();
		add(textField, BorderLayout.SOUTH);
		textField.setColumns(10);

		JPanel pnlControllo = new JPanel();
		pnlControllo.setSize(200, 300);
		add(new JScrollPane(pnlControllo), BorderLayout.WEST);

		pnlControllo.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.MIN_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC,},
			new RowSpec[] {
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.MIN_ROWSPEC,}));

		lblIterazioni = new JLabel("Iterazioni");
		spinIter = new JSpinner();
		spinIter.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {

				cicli = (Integer) spinIter.getValue();
			}
		});
		spinIter.setModel(new SpinnerNumberModel((int) cicli, 1, 2000,
				1));

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
		rete.setPanPlot(this);

		btnCalcola = new JButton("Calcola");
		btnCalcola.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Runnable r = new Runnable() {
					public void run() {
						setRun();
						for (int i = 0; i < cicli; i++) {
							double err = rete.cicloDeltaUpdate();
							lblErrore.setText(dff.format(err));
							if(err<rete.getMinErr())
								break;
						}
						setStop("");
					}
				};
				Thread t = new Thread(r);
				t.start();

			}
		});
		pnlControllo.add(btnCalcola, "1, 1");

		lblPatternInUso = new JLabel("Pattern in uso");
		pnlControllo.add(lblPatternInUso, "1, 2, left, center");

		lblPattern = new JLabel("New label");
		pnlControllo.add(lblPattern, "3, 2, fill, center");
		pnlControllo.add(lblIterazioni, "1, 3, left, center");
		pnlControllo.add(spinIter, "3, 3, default, center");
		pnlControllo.add(lblNewLabel, "1, 4, left, center");
		pnlControllo.add(spinTemp, "3, 4, default, center");
		pnlControllo.add(lblErroreQm, "1, 5, left, center");
		pnlControllo.add(lblErrore, "3, 5, left, center");
		lblErrore.setText("");

		lblWMin = new JLabel("<html>&omega;(intervallo)</html>");
		pnlControllo.add(lblWMin, "1, 6");

		lblWeightRange = new JLabel("");

		pnlControllo.add(lblWeightRange, "3, 6");

		btnSalva = new JButton("Salva");
		btnSalva.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rete.save();
			}
		});
		pnlControllo.add(btnSalva, "1, 7, left, center");
		lblPattern.setText("" + rete.getPatterns().size());
		setStop("");
	}

	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
	}

	public void setInfo(double[] sqm) {
		yValue = sqm;
		curr++;
		lblErrore.setText("" + sqm);
		lblPattern.setText("" + rete.getPatterns().size());
		lblWeightRange.setText("(" + df.format(rete.getMinWeigth()) + ","
				+ df.format(rete.getMaxWeigth()) + ")");
		panPlot.updateUI();
	}

	public void setRun() {

		btnSalva.setEnabled(false);

	}

	public void setStop(String msg) {

		btnSalva.setEnabled(true);
		textField.setText(msg);
		lblPattern.setText("" + rete.getPatterns().size());
		lblWeightRange.setText("(" + df.format(rete.getMinWeigth()) + ","
				+ df.format(rete.getMaxWeigth()) + ")");
	}

	final static DecimalFormat df = new DecimalFormat(" 0.0000;-0.0000");
	private JButton btnCalcola;
	private JPanel panel;

	public void set(NN_Layer l, NN_Neuron n, int k, double[] errori) {
		if (errori == null)
			return;
		JPanel p = ht.get(l.nome + n.nome + "w" + k);
		Graphics g = p.getGraphics();
		p.putClientProperty("err", errori);
		p.putClientProperty("key", l.nome + n.nome + "w" + k);
		p.putClientProperty("txt", l.nome + n.nome + "(" + k + ")");
		p.updateUI();
	}
}
