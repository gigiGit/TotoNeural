package it.gg.toto;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class SquadraPane extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class SquadreModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Vector<SquadraFile> localPartite;
		public TableRowSorter<SquadreModel> sorterPartite;

		public SquadreModel(Collection<SquadraFile> values) {
			localPartite = new Vector<SquadraFile>();
			localPartite.addAll(values);
			sorterPartite = new TableRowSorter<SquadreModel>(this);
			sorterPartite.setSortsOnUpdates(true);
		}

		public int getColumnCount() {
			return 4;
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
				return String.class;
			case 2:
				return Boolean.class;
			case 3:
				return Integer.class;
			}
			return String.class;
		}

		@Override
		public String getColumnName(int i) {
			switch (i) {
			case 0:
				return "#";
			case 1:
				return "Nome";
			case 2:
				return "Cancella";
			case 3:
				return "Utilizzata";
			}
			return null;
		}

		public int getRowCount() {
			return localPartite.size();
		}

		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			SquadraFile p = localPartite.get(rowIndex);
			switch (columnIndex) {
			case 2: p.setCancella((boolean) aValue);
			}
		}

		public Object getValueAt(int i, int i1) {
			SquadraFile p = localPartite.get(i);
			switch (i1) {
			case 0:
				return p.getId();
			case 1:
				return p.nome;
			case 2: return p.isCancella();
			case 3:return p.getUtilizzata();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int i, int i2) {
			switch (i2) {
			case 2:
				return true;
			}
			return false;
		}
	}

	private JTable table;

	public SquadraPane() {

		table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane);

		table.setAutoCreateRowSorter(true);

		table.setModel(new SquadreModel(SquadraFile.getSquadre().values()));
	}

}
