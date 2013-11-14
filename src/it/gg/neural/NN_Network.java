package it.gg.neural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class NN_Network {

	public class Patt {
		double[] X, Y;
String text="";
		public Patt(String txt,double[] _x, double[] _y) {
			X = _x;
			Y = _y;
			text=txt;
		}
public void setText(String txt){
	text=txt;
}
		public String toString() {
			String res = "";
			String sep = "";
			if (X.length < 10) {
				for (double x : X) {
					res += sep + dff.format(x);
					sep = ",";
				}
			} else {
			res=text;	
			}
				sep = " : ";
				for (double x : Y) {
					res += sep + dff.format(x);
					sep = ",";
				}
			return res;
		}
	}

	static enum TipoApprendimento {
		BackProp, GridUpdate, Mixed
	}

	static int cnt = 0;
	final static DecimalFormat dff = new DecimalFormat(" 0.0000;-0.0000");
	final static DecimalFormat dfi = new DecimalFormat("#####");

	static public NN_Network load(int n1, String _fileName) {
		DataInputStream ois;
		NN_Network r = null;
		try {
			ois = new DataInputStream(new FileInputStream(_fileName));
			r = new NN_Network(ois);
			ois.close();
			r.setInput(n1);
			r.fileName = _fileName;
		} catch (FileNotFoundException e1) {
			r = new NN_Network(_fileName, n1, 1);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return r;
	}

	public static void main(String[] args) throws IOException {
		NN_Network n2;
		DataInputStream ois;
		String fileName = "demo2.bin";
		/*
		 * NN_Main n1; n1 = new NN_Main("demo", 5, 4, 1); n1.setTipo(1);
		 * n1.setLearningRate(0.28); n1.setTemperatura(0.9);
		 * n1.setMinErr(0.00001); // n1.save(fileName);
		 */
		JFrame frame = new JFrame();

		ois = new DataInputStream(new FileInputStream(fileName));
		n2 = new NN_Network(ois);
		ois.close();
		System.out.println("Rete neurale caricata da " + fileName);
		// n2.setMaxIter(10000);
		n2.setLearningRate(0.8);
		n2.setMinErr(0.00001);
		n2.dump();
		n2.cleanPattern();
		n2.addPattern("",new double[] { 0, 1, 0, 0, 1 }, new double[] { 0 });
		n2.addPattern("",new double[] { 0, 1, 0, 1, 0 }, new double[] { 1 });
		n2.addPattern("",new double[] { 1, 0, 0, 0, 1 }, new double[] { 0 });
		n2.addPattern("",new double[] { 0, 1, 1, 0, 0 }, new double[] { 1 });
		n2.addPattern("",new double[] { 0, 0, 1, 1, 0 }, new double[] { 0 });
		/*
		 * n2.cleanPattern(); Random rnd = new Random(12345L); for (int i = 0; i
		 * < 20; i++) { n2.addPattern("", new double[] {
		 * n2.transfer(rnd.nextDouble() * 10 - 5), n2.transfer(rnd.nextDouble()
		 * * 10 - 5), n2.transfer(rnd.nextDouble() * 10 - 5),
		 * n2.transfer(rnd.nextDouble() * 10 - 5), n2.transfer(rnd.nextDouble()
		 * * 10 - 5), }, new double[] { n2.transfer(rnd.nextDouble() * 10 - 5),
		 * });
		 * 
		 * }
		 */
		n2.setLearningRate(1.0);
		n2.setTipo(2);
		n2.setDepth(0);
		n2.setScriviOgni(1);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new NN_Plot(n2));
		frame.setSize(300, 300);
		/*
		 * n2.train(1); n2.test(); n2.save(fileName);
		 */
		frame.setVisible(true);
	}

	private boolean debug = false;
	private NN_Plot panPlot = null;

	int depth = 0;

	String fileName = null;

	private Double learningRate;;

	private double minErr;

	double minW, maxW;

	private boolean modified = false;

	private NN_Layer[] net;
	private String nome = "";
	private Vector<Patt> patterns = new Vector<Patt>();

	public boolean running = false;

	public Integer scriviOgni;

	DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.FULL,
			DateFormat.FULL, Locale.ITALY);

	private int steps = 5;

	private Double temperatura;

	private long time;

	// private int tipo = 1;

	private TipoApprendimento tipoApprendimento = TipoApprendimento.BackProp;

	public NN_Network(DataInputStream ois) {
		this("rete" + (++cnt));
		try {
			int nLayer;
			int tipo;
			time = ois.readLong();
			nLayer = ois.readInt();
			tipo = ois.readInt();
			setNet(new NN_Layer[nLayer]);
			setLearningRate(ois.readDouble());
			setTemperatura(ois.readDouble());
			setMinErr(ois.readDouble());
			scriviOgni = ois.readInt();
			setTipo(tipo);
			for (int l = 0; l < nLayer; l++) {
				net[l] = new NN_Layer(ois);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private NN_Network(String _nome) {
		setNome(_nome);
		tipoApprendimento = TipoApprendimento.BackProp;
		setLearningRate(0.8);
		setTemperatura(1.0);
		scriviOgni = 10000;
		setMinErr(0.01);
		time = Calendar.getInstance().getTimeInMillis();
	}

	public NN_Network(String _nome, int _n1, int _n2) {
		this(_nome);
		setNet(new NN_Layer[] { new NN_Layer(_n1, _n2) });
	}

	public NN_Network(String _nome, int _n1, int _n2, int _n3) {
		this(_nome);
		setNet(new NN_Layer[] { new NN_Layer(_n1, _n2), new NN_Layer(_n2, _n3) });
	}

	public NN_Network(String _nome, int _n1, int _n2, int _n3, int _n4) {
		this(_nome);
		setNet(new NN_Layer[] { new NN_Layer(_n1, _n2), new NN_Layer(_n2, _n3),
				new NN_Layer(_n3, _n4) });
	}

	public void addPattern(String txt,double[] X, double[] Y) {
		getPatterns().add(new Patt(txt,X, Y));
	}

	public double cicloBackProp() {
		modified = true;
		resetWeightsDelta();
		double e = 0;
		int nn = 0;
		for (Patt p : getPatterns()) {
			double[] res = feed(p.X);
			for (int j = 0; j < res.length; ++j)
				e += (res[j] - p.Y[j]) * (res[j] - p.Y[j]);
			evaluateGradients(p.Y);
			evaluateWeightsDelta();
			nn++;
		}
		updateWeights();

		e /= nn;
		return e;
	}

	double cicloDeltaUpdate() {
		Double eMin = 0.;
		double delta=1.0;
		for (NN_Layer l : net) {
			for (NN_Neuron n : l.neurons) {
				for (int k = 0; k < n.weigth.length; k++) {
					double[] errori;
					errori = new double[steps];
					eMin = feed(n, k, errori, delta, depth);
					if (debug) {
						for (int i = 0; i < errori.length; i++) {
							System.out.print(dff.format(errori[i]) + " ");
						}
						System.out.println("");
						for (int i = 0; i < errori.length; i++) {
							System.out.print((eMin == errori[i]) ? " # #### "
									: "        ");
						} 
						System.out.println("\n");
					}
					if (panPlot != null) {
						panPlot.set(l, n, k, errori);
					}
				}
			}
		}
		if (eMin < minErr) {
			System.out
					.println("Errore inferiore al valore di riferimento. Fine apprendimento");
		}
		return eMin;
	}

	public void cleanPattern() {
		getPatterns().clear();
	}

	public double dtransfer(double x) {
		double y = transfer(x);
		return temperatura * y * (1 - y);
	}

	public void dump() {
		System.out.println("  Data          : " + sdf.format(new Date(time)));
		System.out.println("  Nome          : " + nome);
		System.out.println("  Tipo appr.    : " + tipoApprendimento);
		System.out.println("  Profondità    : " + depth);
		System.out.println("  ScriviOgni    : " + scriviOgni);
		System.out.println("  Learning Rate : " + learningRate);
		System.out.println("  Temperatura   : " + temperatura);
		System.out.println("  Errore min.   : " + minErr);
		System.out.println("  Net           : " + net.length);
	}

	private void evaluateGradients(double[] results) {
		for (int c = getNet().length - 1; c >= 0; --c) {
			for (int i = 0; i < getNet()[c].neurons.length; ++i) {
				if (c == getNet().length - 1) {
					getNet()[c].grad_ex[i] = 2
							* (getNet()[c].res[i] - results[0])
							* dtransfer(getNet()[c].neurons[i]._activation);
				} else { // if it's neuron of the previous layers
					double sum = 0;
					for (int k = 1; k < getNet()[c + 1].neurons.length; ++k)
						sum += getNet()[c + 1].neurons[k].weigth[i]
								* getNet()[c + 1].grad_ex[k];
					getNet()[c].grad_ex[i] = dtransfer(getNet()[c].neurons[i]._activation)
							* sum;
				}
			}
		}
	}

	private void evaluateWeightsDelta() {
		// evaluate delta values for each weight
		for (int c = 1; c < getNet().length; ++c) {
			for (int i = 0; i < getNet()[c].neurons.length; ++i) {
				Double weights[] = getNet()[c].neurons[i].weigth;
				for (int j = 0; j < weights.length - 1; ++j)
					getNet()[c].neurons[i].delta[j] += getNet()[c].grad_ex[i]
							* getNet()[c - 1].res[j];
			}
		}
	}

	public double[] feed(double[] ds) {
		double[] input = ds;
		for (int i = 0; i < getNet().length; i++) {
			for (int j = 0; j < getNet()[i].neurons.length; j++) {
				getNet()[i].res[j] = transfer(getNet()[i].neurons[j]
						.feed(input));
			}
			input = getNet()[i].res;
		}
		return input;

	}

	private double feed(NN_Neuron n, int k, double[] errs, double delta,
			int depth) {
		double w0 = n.weigth[k];
		int nd = errs.length;
		int index = -1;
		double dw = delta / nd;
		double dw1 = w0 - delta / 2;
		int nn = getPatterns().size();
		double errMin = Double.MAX_VALUE;
		double e;
		modified = true;
		for (int i = 0; i < nd; i++) {
			n.weigth[k] = dw1 + i * dw;
			e = 0;
			for (Patt p : getPatterns()) {
				double[] res = feed(p.X);
				for (int j = 0; j < res.length; ++j)
					e += (res[j] - p.Y[j]) * (res[j] - p.Y[j]);
			}
			errs[i] = e = e / nn;
			if (e < errMin) {
				errMin = e;
				w0 = n.weigth[k];
				index = i;
			}
		}
		n.weigth[k] = w0;
		if (depth > 0 && index > 0 && index < nd) {
			errMin = feed(n, k, errs, dw, depth - 1);
		}
		return errMin;
	}

	public int getDepth() {
		return depth;
	}

	public Double getLearningRate() {
		return learningRate;
	}

	public double getMaxWeigth() {
		return maxW;
	}

	public double getMinErr() {
		return minErr;
	}

	public double getMinWeigth() {
		return minW;
	}

	public NN_Layer[] getNet() {
		return net;
	}

	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}

	public Vector<Patt> getPatterns() {
		return patterns;
	}

	public Integer getScriviOgni() {
		return scriviOgni;
	}

	/**
	 * @return the steps
	 */
	public int getSteps() {
		return steps;
	}

	public Double getTemperatura() {
		return temperatura;
	}

	/**
	 * @return the tipo
	 */
	public int getTipo() {
		return tipoApprendimento.ordinal();
	}

	public synchronized void normalizza() {
		minW = Double.MAX_VALUE;
		maxW = -Double.MAX_VALUE;
		for (int c = 0; c < getNet().length; ++c) {
			for (int i = 0; i < getNet()[c].neurons.length; ++i) {
				for (int j = 0; j < getNet()[c].neurons[i].weigth.length; ++j) {
					if (getNet()[c].neurons[i].weigth[j] > maxW)
						maxW = getNet()[c].neurons[i].weigth[j];
					else if (getNet()[c].neurons[i].weigth[j] < minW)
						minW = getNet()[c].neurons[i].weigth[j];
				}
			}
		}
		for (int c = 0; c < getNet().length; ++c) {
			for (int i = 0; i < getNet()[c].neurons.length; ++i) {
				for (int j = 0; j < getNet()[c].neurons[i].weigth.length; ++j) {
					getNet()[c].neurons[i].weigth[j] = (maxW - getNet()[c].neurons[i].weigth[j])
							/ (maxW - minW);
				}
			}
		}

	}

	private void resetWeightsDelta() {
		// reset delta values for each weight
		for (int c = 0; c < getNet().length; ++c) {
			for (int i = 0; i < getNet()[c].neurons.length; ++i) {
				Double weights[] = getNet()[c].neurons[i].weigth;
				for (int j = 0; j < weights.length; ++j)
					getNet()[c].neurons[i].delta[j] = 0;
			}
		}
	}

	public void save() {
		if (modified) {
			if (fileName == null) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Nome del file della rete");

				int userSelection = fileChooser.showSaveDialog(null);

				if (userSelection == JFileChooser.APPROVE_OPTION) {
					File fileToSave = fileChooser.getSelectedFile();
					fileName = fileToSave.getAbsolutePath();
				} else {
					return;
				}
			}
			save(fileName);
		}
		modified = false;
	}

	public void save(Connection conn) {
		PreparedStatement stmt;
		try {
			stmt = conn
					.prepareStatement("create table if not exists NeuralNetwork ( " //
							+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," //
							+ "nome TEXT NOT NULL," //
							+ "data INTEGER NOT NULL," //
							+ "layer INTEGER NOT NULL," //
							+ "maxIter INTEGER NOT NULL," //
							+ "LearningRate number not null ," //
							+ "Temperature number not null ," //
							+ "minErr number not null ," //
							+ "ScriviOgni Integer not null ," //
							+ "unique(nome)); ");
			stmt.execute();
			conn.setAutoCommit(false);
			stmt = conn
					.prepareStatement("delete from NeuralNetwork where nome=?;");
			stmt.setString(1, getNome());
			stmt.execute();
			stmt = conn.prepareStatement("insert into NeuralNetwork ("
					+ "nome," + "data," + "layer," + "maxIter,"
					+ "LearningRate," + "Temperature," + "minErr,"
					+ "ScriviOgni) " + "values(?,?,?,?,?,?,?,?);");
			stmt.setString(1, getNome());
			stmt.setLong(2, Calendar.getInstance().getTimeInMillis());
			stmt.setInt(3, getNet().length);
			stmt.setInt(4, getTipo());
			stmt.setDouble(5, getLearningRate());
			stmt.setDouble(6, getTemperatura());
			stmt.setDouble(7, getMinErr());
			stmt.setInt(8, scriviOgni);
			stmt.addBatch();
			stmt.executeBatch();
			conn.commit();
			stmt.close();
			for (int l = 0; l < getNet().length; l++) {
				getNet()[l].save(getNome(), "layer_" + l, conn);
			}
			System.out.println("Rete " + getNome() + " salvata");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void save(DataOutputStream oos) {
		try {
			oos.writeLong(Calendar.getInstance().getTimeInMillis());
			oos.writeInt(getNet().length);
			oos.writeInt(getTipo());
			oos.writeDouble(getLearningRate());
			oos.writeDouble(getTemperatura());
			oos.writeDouble(getMinErr());
			oos.writeInt(scriviOgni);
			for (int l = 0; l < getNet().length; l++) {
				getNet()[l].save(oos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void save(String _fileName) {
		DataOutputStream oos;
		try {
			oos = new DataOutputStream(new FileOutputStream(_fileName));
			save(oos);
			oos.close();
			this.fileName = _fileName;
			System.out.println("Rete salvata in " + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sberla(double d) {
		for (NN_Layer l : getNet()) {
			for (NN_Neuron n : l.neurons)
				n.sberla(d);
		}
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	private void setInput(int n1) {
		net[0].setInput(n1);
	}

	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}

	public void setMinErr(double minErr) {
		this.minErr = minErr;
	}

	public void setNet(NN_Layer[] net) {
		this.net = net;
	}

	/**
	 * @param nome
	 *            the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPatterns(Vector<Patt> patterns) {
		this.patterns = patterns;
	}

	public void setScriviOgni(Integer scriviOgni) {
		this.scriviOgni = scriviOgni == 0 ? 1 : scriviOgni;
	}

	/**
	 * @param steps
	 *            the steps to set
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}

	public void setTemperatura(Double _temperatura) {
		this.temperatura = _temperatura;
	}

	/**
	 * @param tipo
	 *            the tipo to set
	 */
	public void setTipo(int tipo) {
		switch (tipo) {
		case 1:
			this.tipoApprendimento = TipoApprendimento.BackProp;
			break;
		case 2:
			this.tipoApprendimento = TipoApprendimento.GridUpdate;
			break;
		case 3:
			this.tipoApprendimento = TipoApprendimento.Mixed;
			break;
		default:
			this.tipoApprendimento = TipoApprendimento.BackProp;
			break;
		}

	}

	public void test() {
		if(patterns.size()==0)return;
		double err=0;
		for (Patt p : patterns) {
			System.out.print(p + " --> ");
			double[] res = feed(p.X);
			
			for (int i=0; i<res.length; i++) {
				err+=(res[i]-p.Y[i])*(res[i]-p.Y[i]);
				System.out.print(" " + dff.format(res[i]));
			}
			System.out.println("");
		}
		System.out.println("Errore : "+err/patterns.size());
	}

	public double train() {
		switch (tipoApprendimento) {
		case BackProp:
			modified = true;
			return cicloBackProp();
		case GridUpdate:
			modified = true;
			return cicloDeltaUpdate();
		case Mixed:
			modified = true;
			cicloBackProp();
			cicloDeltaUpdate();
			return cicloBackProp();
		}
		return Double.MAX_VALUE;
	}

	public double train(int mIter) {
		double err = 1;
		System.out.println("Rete :" + nome);
		dump();
		System.out.println("      Apprendimento su " + patterns.size()
				+ " esempi.");
		for (int i = 1; i <= mIter; i++) {
			err = train();
			if (i % scriviOgni == 0)
				System.out.println("    " + dfi.format(i) + ")  Errore qm :"
						+ dff.format(err));
			if (err < minErr)
				break;
		}
		System.out.println("          Errore qm :" + dff.format(err));
		return err;
	}

	protected double transfer(double x) {
		return 1.0 / (1 + Math.exp(-temperatura * x));
	}

	private void updateWeights() {
		minW = Double.MAX_VALUE;
		maxW = -Double.MAX_VALUE;
		for (int c = 0; c < getNet().length; ++c) {
			for (int i = 0; i < getNet()[c].neurons.length; ++i) {
				for (int j = 0; j < getNet()[c].neurons[i].weigth.length; ++j) {
					getNet()[c].neurons[i].weigth[j] += -(getLearningRate() * getNet()[c].neurons[i].delta[j]);
					if (getNet()[c].neurons[i].weigth[j] > maxW)
						maxW = getNet()[c].neurons[i].weigth[j];
					else if (getNet()[c].neurons[i].weigth[j] < minW)
						minW = getNet()[c].neurons[i].weigth[j];
				}
			}
		}
	}

	/**
	 * @return the panPlot
	 */
	public NN_Plot getPanPlot() {
		return panPlot;
	}

	/**
	 * @param panPlot
	 *            the panPlot to set
	 */
	public void setPanPlot(NN_Plot panPlot) {
		this.panPlot = panPlot;
	}

}
