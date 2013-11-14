import it.gg.neural.NN_Network;
import it.gg.toto.PartitaFile;
import it.gg.toto.SchedinaFile;
import it.gg.toto.SquadraFile;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JFrame;

public class TotoApp {
	static int N_Reti = 3;
	static NN_Network[] rete;
	static String fileName = "toto.bin";
	static String[] fileName_n = new String[] { "toto1.bin", "toto2.bin",
			"totoX.bin" };
	static int N_Giocatori = 1, tipo = 3;
	static double learningRate = 0.4, eps = 0.01, temp = 0.8;
	static int layer = 1, cicli = 30,depth=0,steps=5;
	static boolean nuova = false, gui = false, test = false;
	static String helpText = "Utilizzo : \n"
			+ " -test            --> Calcola l'errore della rete ["	+ test	+ "]\n"
			+ " -gui             --> Modalità grafica ["	+ gui		+ "]\n"
			+ " -create          --> rigenera la rete con valori random ["	+ gui	+ "]\n"
			+ " -tipo 1|2|3      --> tipo di train 1=BackProp 2=Reticolo 3=1+2+1 ["	+ tipo	+ "]\n"
			+ " -mode 1|3        --> 1=1 rete con 3 output, 3=3 reti con un output ["+ N_Reti+ "]\n"
			+ " -g numGiocatori  --> Numero giocatori in input ["	+ N_Giocatori	+ "]\n"
			+ " -n numIterazioni --> Numero iterazioni di apprendimento ["	+ cicli	+ "]\n"
			+ " -depth profondità--> Profondità in Reticolo ["	+ depth	+ "]\n"
			+ " -steps divisioni --> Suddivisioni del Reticolo ["	+ steps	+ "]\n"
			+ " -l learningRate  --> Learning rate ["	+ learningRate	+ "]\n"
			+ " -eps minErr      --> Valore minimo su cui fermare l'apprendimento ["+ eps	+ "]\n"
			+ " -t temperatura   --> Temperatura della funzione di Fermi ["	+ temp	+ "]\n"
			+ " -hidden 0|1|2    --> Numero di strati hidden ["	+ layer + "]\n";
	public static void main(String[] args) {

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-h")) {
					System.out.println(helpText);
					System.exit(0);
				}
				if (args[i].equals("-tipo")) {
					if (args[i + 1].equals("1"))
						tipo = 1;
					else if (args[i + 1].equals("2"))
						tipo = 2;
					else if (args[i + 1].equals("3"))
						tipo = 3;
					i++;
					continue;
				}
				if (args[i].equals("-hidden")) {
					if (args[i + 1].equals("0"))
						layer = 0;
					else if (args[i + 1].equals("1"))
						layer = 1;
					else if (args[i + 1].equals("2"))
						layer = 2;
					i++;
					continue;
				}
				if (args[i].equals("-mode")) {
					if (args[i + 1].equals("1"))
						N_Reti = 1;
					else if (args[i + 1].equals("3"))
						N_Reti = 3;
					i++;
					continue;
				}
				if (args[i].equals("-g")) {
					N_Giocatori = new Integer(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-depth")) {
					depth = new Integer(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-steps")) {
					steps = new Integer(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-l")) {
					learningRate = new Double(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-eps")) {
					eps = new Double(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-t")) {
					temp = new Double(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-n")) {
					cicli = new Integer(args[i + 1]);
					i++;
					continue;
				}
				if (args[i].equals("-create")) {
					nuova = true;
					continue;
				}
				if (args[i].equals("-gui")) {
					gui = true;
					continue;
				}
				if (args[i].equals("-test")) {
					test = true;
					continue;
				}
			}
		}
		PartitaFile.loadDB();
		PartitaFile.loadFile("partite.txt");
		File f = new File("appo.txt");
		if (f.exists()) {
			PartitaFile.loadFile("appo.txt");
			PartitaFile.saveFile("partite.txt");
			PartitaFile.saveDB();
		}
		System.out.println("Partite caricate : " + PartitaFile.size());
		System.out.println("Schedine caricate : " + SchedinaFile.size());
		System.out.println("Squadre caricate : " + SquadraFile.size());

		int n1 = SquadraFile.size() * N_Giocatori;
		rete = new NN_Network[N_Reti];

		switch (N_Reti) {
		case 3:
			if (nuova) {
				switch (layer) {
				case 0:
					rete[0] = new NN_Network("Rete per apprendimento 1",
							2 * n1, 1);
					rete[1] = new NN_Network("Rete per apprendimento 2",
							2 * n1, 1);
					rete[2] = new NN_Network("Rete per apprendimento X",
							2 * n1, 1);
					break;
				case 1:
					rete[0] = new NN_Network("Rete per apprendimento 1",
							2 * n1, 4, 1);
					rete[1] = new NN_Network("Rete per apprendimento 2",
							2 * n1, 4, 1);
					rete[2] = new NN_Network("Rete per apprendimento X",
							2 * n1, 4, 1);
					break;
				case 2:
					rete[0] = new NN_Network("Rete per apprendimento 1",
							2 * n1, 22, 4, 1);
					rete[1] = new NN_Network("Rete per apprendimento 2",
							2 * n1, 22, 4, 1);
					rete[2] = new NN_Network("Rete per apprendimento X",
							2 * n1, 22, 4, 1);
					break;
				}
				rete[0].save(fileName_n[0]);
				rete[1].save(fileName_n[1]);
				rete[2].save(fileName_n[2]);
			}
			rete[0] = NN_Network.load(2 * n1, fileName_n[0]);
			rete[1] = NN_Network.load(2 * n1, fileName_n[1]);
			rete[2] = NN_Network.load(2 * n1, fileName_n[2]);
			rete[0].setNome("Rete di apprendimento per \"1\"");
			rete[1].setNome("Rete di apprendimento per \"2\"");
			rete[2].setNome("Rete di apprendimento per \"X\"");
			break;
		case 1:
			if (nuova) {
				switch (layer) {
				case 0:
					rete[0] = new NN_Network("Rete per apprendimento 1X2",
							2 * n1, 3);
					break;
				case 1:
					rete[0] = new NN_Network("Rete per apprendimento 1X2",
							2 * n1, 12, 3);
					break;
				case 2:
					rete[0] = new NN_Network("Rete per apprendimento 1X2",
							2 * n1, 22, 12, 3);
					break;
				}
				rete[0].save(fileName);
			}
			rete[0] = NN_Network.load(2 * n1, fileName);
			rete[0].setNome("Rete di apprendimento per \"1X2\"");
			break;
		}

		switch (N_Reti) {
		case 3:
			for (int i = 0; i < rete.length; i++) {
				rete[i].cleanPattern();
				for (PartitaFile p : PartitaFile.values()) {
					if (p.isValida() && p.get_Peso() > 0.5) {
						rete[i].addPattern(p.toString(),p.getInputPattern(N_Giocatori),
								p.getResult(i));
					}
				}

			}
			break;
		case 1:
			rete[0].cleanPattern();
			for (PartitaFile p : PartitaFile.values()) {
				if (p.isValida() && p.get_Peso() > 0.5) {
					rete[0].addPattern(p.toString(),p.getInputPattern(N_Giocatori),
							p.getResult());
				}
			}
			break;
		}
		for (NN_Network r : rete) {
			r.setLearningRate(learningRate);
			r.setMinErr(eps);
			r.setScriviOgni(cicli / 10);
			r.setTemperatura(temp);
			r.setTipo(tipo);
			r.setDepth(depth);
			r.setSteps(steps);
			r.dump();
		}
		if (test) {
			for (NN_Network r : rete) {
				r.dump();
				r.test();
			}
		} else if (gui) {
			TotoGui r = new TotoGui(rete);
			r.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			r.setVisible(true);
		} else {
			for (NN_Network r : rete) {
				r.setLearningRate(learningRate);
				System.out.println("Cicli di apprendimento per "
						+ r.getPatterns().size() + " esempi.");
				System.out.println(r.getNome() + " : ");
				r.train(cicli);
				r.save();
			}
		}
	}

}
