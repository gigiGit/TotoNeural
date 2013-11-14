package it.gg.toto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import it.gg.neural.NN_Network;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import java.awt.Color;

public class TotoPane extends JPanel {
	SchedinaFile schedina;
	JCheckBox chSelezionaTutte;

	// ReteNeurale rete;
	class PartiteModel extends AbstractTableModel {
		Vector<PartitaFile> localPartite;
		public TableRowSorter sorterPartite;

		public PartiteModel(Vector<PartitaFile> _partite) {
			localPartite = _partite;
			sorterPartite = new TableRowSorter(this);
			sorterPartite.setSortsOnUpdates(true);
		}

		public int getColumnCount() {
			return 10;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
		 */
		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Integer.class;
			case 1:
				return Date.class;
			case 2:
				return SquadraFile.class;
			case 3:
				return SquadraFile.class;
			case 4:
				return Integer.class;
			case 5:
				return Integer.class;
			case 6:
				return String.class;
			case 7:
				return Boolean.class;
			case 8:
				return String.class;
			case 9:
				return Boolean.class;
			}
			return String.class;
		}

		@Override
		public String getColumnName(int i) {
			switch (i) {
			case 0:
				return "#";
			case 1:
				return "data";
			case 2:
				return "squadra 1";
			case 3:
				return "squadra 2";
			case 4:
				return "reti 1";
			case 5:
				return "reti 2";
			case 6:
				return "Previsione";
			case 7:
				return "Valida";
			case 8:
				return "Risultato";
			case 9:
				return "Cancella";
			}
			return null;
		}

		public int getRowCount() {
			return localPartite.size();
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			PartitaFile p = localPartite.get(rowIndex);
			switch (columnIndex) {
			case 0:
				p.setSchedina_pos((int) aValue);
				break;
			case 2:
				p.setSquadra1((SquadraFile) aValue);
				break;
			case 3:
				p.setSquadra2((SquadraFile) aValue);
				break;
			case 4:
				p.setRes((Integer) aValue,
						p.getRes2() == null ? 0 : p.getRes2());
				break;
			case 5:
				p.setRes(p.getRes1() == null ? 0 : p.getRes1(),
						(Integer) aValue);
				break;
			case 7:
				p.setValida((Boolean) aValue);
				break;
			case 9:
				p.setCancella((Boolean) aValue);
				break;
			}
		}

		public Object getValueAt(int i, int i1) {
			PartitaFile p = localPartite.get(i);
			switch (i1) {
			case 0:
				return p.getSchedina_pos();
			case 1:
				return p.getData().getTime();
			case 2:
				return p.getSquadra1();
			case 3:
				return p.getSquadra2();
			case 4:
				return p.getRes1();
			case 5:
				return p.getRes2();
			case 6:
				return p.getPrevisione();
			case 7:
				return p.getValida();
			case 8:
				return p.getResultT();
			case 9:
				return p.isCancella();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int i, int i2) {
			switch (i2) {
			case 0:
			case 2:
			case 3:
			case 4:
			case 5:
			case 7:
			case 9:
				return true;
			}
			return false;
		}
	}

	NN_Network[] rete;
	JTable jTable1 = new JTable();

	public TotoPane(NN_Network[] _rete) {
		this();
		rete = _rete;
	}

	public TotoPane(SchedinaFile sf, NN_Network[] rete) {
		this();
		rete = rete;
		set(sf);
	}

	public TotoPane() {
		super();
		setForeground(Color.LIGHT_GRAY);
		rete = null;
		setLayout(new BorderLayout(0, 0));
		jTable1.setAutoCreateRowSorter(true);

		this.add(new JScrollPane(jTable1), BorderLayout.CENTER);
		this.add(new GiornataPane(), BorderLayout.SOUTH);
		JPanel panel = new JPanel();

		chSelezionaTutte = new JCheckBox("Seleziona tutto");
		chSelezionaTutte.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JCheckBox chckbxNewCheckBox = (JCheckBox) e.getSource();
				boolean b = chckbxNewCheckBox.isSelected();
				for (PartitaFile p : schedina.getPartite()) {
					p.setValida(b);
				}
				jTable1.updateUI();
				chckbxNewCheckBox.setText(b ? "Deseleziona tutto"
						: "Seleziona tutto");
			}
		});

		JButton butAggiornaPrev = new JButton("Aggiorna Previsione");
		butAggiornaPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (rete == null)
					return;
				int n1 = SquadraFile.size();
				for (PartitaFile p : schedina.getPartite()) {
					double[] R = new double[] { 0, 0, 0 };
					double[] x = new double[n1 * 2];
					x[p.getSquadra1().getId() - 1] = 1.0;
					x[n1 + p.getSquadra2().getId() - 1] = 1.0;
					String prev = "";
					if (rete.length == 3) {
						double[] res1 = rete[0].feed(x);
						double[] res2 = rete[1].feed(x);
						double[] resX = rete[2].feed(x);
						prev = (res1[0] > 0.6 ? "1" : " ")
								+ (res2[0] > 0.6 ? "2" : " ")
								+ (resX[0] > 0.6 ? "X" : " ");
					} else {
						double[] res = rete[0].feed(x);
						prev = (res[0] > 0.6 ? "1" : " ")
								+ (res[1] > 0.6 ? "2" : " ")
								+ (res[2] > 0.6 ? "X" : " ");
					}
					p.setPrevisione(prev);
				}
				jTable1.updateUI();
			}
		});

		JButton butRicalcolaPesi = new JButton("Ricalcola pesi");
		butRicalcolaPesi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schedina.ricalcolaPeso();
				jTable1.updateUI();
			}
		});
		Vector<SchedinaFile> vettore = new Vector<SchedinaFile>(
				SchedinaFile.schedine.values());

		Collections.sort(vettore);
		JComboBox listaSchedine = new JComboBox(new DefaultComboBoxModel(
				vettore));
		listaSchedine.setToolTipText("Schedine disponibili");
		listaSchedine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JComboBox comboBox = (JComboBox) arg0.getSource();
				set((SchedinaFile) comboBox.getSelectedItem());
			}
		});
		listaSchedine.setSelectedIndex(0);
		add(panel, BorderLayout.NORTH);
		panel.add(listaSchedine);
		panel.add(chSelezionaTutte);
		panel.add(butRicalcolaPesi);
		panel.add(butAggiornaPrev);

	}

	public void set(SchedinaFile sf) {
		this.schedina = sf;
		jTable1.setModel(new PartiteModel(schedina.getPartite()));
		TableColumn sq1Column = jTable1.getColumnModel().getColumn(2);
		TableColumn sq2Column = jTable1.getColumnModel().getColumn(3);
		Vector<SquadraFile> v1 = new Vector<SquadraFile>(
				SquadraFile.squadre.values());
		Collections.sort(v1);
		JComboBox cb1 = new JComboBox(v1);
		Vector<SquadraFile> v2 = new Vector<SquadraFile>(
				SquadraFile.squadre.values());
		Collections.sort(v2);
		JComboBox cb2 = new JComboBox(v2);
		sq1Column.setCellEditor(new DefaultCellEditor(cb1));
		sq2Column.setCellEditor(new DefaultCellEditor(cb2));

		jTable1.updateUI();
		boolean b = false;
		chSelezionaTutte.setText("Deseleziona tutto");
		for (PartitaFile p : schedina.getPartite()) {
			b = p.isValida();
			if (b == false)
				chSelezionaTutte.setText("Seleziona tutto");
		}

	}

}