import it.gg.neural.NN_Network;
import it.gg.neural.NN_Pane;
import it.gg.neural.NN_Plot;
import it.gg.toto.PartitaFile;
import it.gg.toto.SquadraPane;
import it.gg.toto.TotoPane;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TotoGui extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Connection db;
	static String fileName = "toto.bin";
	static String fileName_1 = "toto1.bin";
	static String fileName_2 = "toto2.bin";
	static String fileName_X = "totoX.bin";
	static int N_Giocatori = 1;
	static int N_Reti = 3;

	JButton buttAggiornaPattern;
	double[][] Err = new double[N_Reti][1000];
	Hashtable<Integer, JTabbedPane> mappaxAnno = new Hashtable<Integer, JTabbedPane>();
	int maxIter = 1000;
	JMenuBar menuBar = new JMenuBar();
	NN_Network[] rete;

	public TotoGui(NN_Network[] _rete) {
		setFont(new Font("Serif", Font.PLAIN, 12));
		menuBar.setFont(new Font("Serif", Font.PLAIN, 12));
		rete = _rete;
		this.setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		mnNewMenu.setFont(new Font("Serif", Font.PLAIN, 12));
		menuBar.add(mnNewMenu);

		JMenuItem mnNewMenu_1 = new JMenuItem("Exit");
		mnNewMenu_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PartitaFile.saveDB();
				for (NN_Network r : rete) {
					r.save();
				}
				System.exit(0);
			}
		});
		mnNewMenu.add(mnNewMenu_1);
		JTabbedPane tabPanel = new JTabbedPane();
		tabPanel.add("Squadre", new SquadraPane());
		tabPanel.add("Partite", new TotoPane(rete));
		this.getContentPane().add(tabPanel, BorderLayout.CENTER);
		JPanel pannello = new JPanel();
		tabPanel.addTab("Apprendimento rete", null, pannello, null);
		for (NN_Network r : rete) {
			tabPanel.addTab(r.getNome(), null, new NN_Plot(r), null);
		}
		pannello.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		pannello.add(new NN_Pane(rete[0]), gbc);
		if (N_Reti > 1) {
			GridBagConstraints gbc_1 = new GridBagConstraints();
			gbc_1.fill = GridBagConstraints.BOTH;
			gbc_1.insets = new Insets(0, 0, 0, 0);
			gbc_1.gridx = 0;
			gbc_1.gridy = 1;
			gbc_1.weightx = 1;
			gbc_1.weighty = 1;
			pannello.add(new NN_Pane(rete[1]), gbc_1);
			GridBagConstraints gbc_2 = new GridBagConstraints();
			gbc_2.anchor = GridBagConstraints.NORTH;
			gbc_2.fill = GridBagConstraints.BOTH;
			gbc_2.insets = new Insets(0, 0, 0, 0);
			gbc_2.gridx = 0;
			gbc_2.gridy = 2;
			gbc_2.weightx = 1;
			gbc_2.weighty = 1;
			pannello.add(new NN_Pane(rete[2]), gbc_2);
		}
		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.SOUTH);
		buttAggiornaPattern = new JButton("Aggiorna");
		panel.add(buttAggiornaPattern);

		JButton buttSalva = new JButton("Salva Partite");
		buttSalva.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PartitaFile.saveFile("partite.txt");
			}
		});

		panel.add(buttSalva);

		buttAggiornaPattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (N_Reti) {
				case 3:
					for (int i = 0; i < rete.length; i++) {
						rete[i].cleanPattern();
						for (PartitaFile p : PartitaFile.values()) {
							if (p.isValida() && p.get_Peso() > 0.5) {
								rete[i].addPattern(p.toString(),
										p.getInputPattern(N_Giocatori),
										p.getResult(i));
							}
						}
					}
					break;
				case 1:
					rete[0].cleanPattern();
					for (PartitaFile p : PartitaFile.values()) {
						if (p.isValida() && p.get_Peso() > 0.5) {
							rete[0].addPattern(p.toString(),
									p.getInputPattern(N_Giocatori),
									p.getResult());
						}
					}
					break;
				}
			}
		});
		this.setSize(672, 410);
	}

}
