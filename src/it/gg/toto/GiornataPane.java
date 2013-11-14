package it.gg.toto;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Calendar;
import java.util.Vector;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GiornataPane extends JPanel {
	private JTextField txtR1;
	private JTextField txtR2;
	private JButton btnAggiungi;
	private JSpinner spinner;
	JComboBox cmbSq1 = new JComboBox();
	JComboBox cmbSq2 = new JComboBox();

	public GiornataPane() {
		setLayout(new FormLayout(new ColumnSpec[] { ColumnSpec.decode("50dlu"),
				ColumnSpec.decode("50dlu"), ColumnSpec.decode("50dlu"),
				ColumnSpec.decode("20dlu"), ColumnSpec.decode("20dlu"),
				FormFactory.DEFAULT_COLSPEC, },
				new RowSpec[] { FormFactory.DEFAULT_ROWSPEC, }));

		spinner = new JSpinner();
		spinner.setModel(new SpinnerDateModel(new Date(1384383600000L), null,
				null, Calendar.DAY_OF_YEAR));
		add(spinner, "1, 1, fill, default");
		Vector<SquadraFile> vector = new Vector<SquadraFile>(SquadraFile
				.getSquadre().values());
		Collections.sort(vector);
		cmbSq1.setModel(new DefaultComboBoxModel(vector));
		add(cmbSq1, "2, 1, fill, top");

		cmbSq2.setModel(new DefaultComboBoxModel(vector));
		add(cmbSq2, "3, 1, fill, top");

		txtR1 = new JTextField();
		add(txtR1, "4, 1, fill, center");

		txtR2 = new JTextField();
		add(txtR2, "5, 1, fill, center");

		btnAggiungi = new JButton("Aggiungi");
		btnAggiungi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SimpleDateFormat dfd = new SimpleDateFormat("dd/MM/yyyy");
				Date data = (Date) spinner.getValue();
				String dataTxt = dfd.format(data);
				try {
					PartitaFile p = new PartitaFile(dataTxt,
							(SquadraFile) cmbSq1.getSelectedItem(),
							(SquadraFile) cmbSq2.getSelectedItem(), txtR1
									.getText(), txtR2.getText());
				} catch (Exception e1) {

					e1.printStackTrace();
				}
			}
		});
		add(btnAggiungi, "6, 1");

	}

}
