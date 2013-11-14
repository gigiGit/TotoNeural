package it.gg.toto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class SchedinaFile implements Comparable {
	public static Hashtable<String, SchedinaFile> schedine = new Hashtable<String, SchedinaFile>();
	static {
		schedine.put("Tutte", new SchedinaFile(""));
	}
	String nome;
	public Calendar realdata;
	double peso;
	private Vector<PartitaFile> partite = new Vector<PartitaFile>();
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public SchedinaFile(String _data) {
		realdata = Calendar.getInstance();
		nome = "tutte";
		try {

			realdata.setTimeInMillis(df.parse(_data).getTime());
			nome = "Partite del " + _data;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		ricalcolaPeso();
	}

	double alfa = 0.03;

	public void ricalcolaPeso() {
		Calendar data = Calendar.getInstance();
		data.add(Calendar.MONTH, -6);
		double x = realdata.getTimeInMillis() / 3600000 / 24;
		double d = data.getTimeInMillis() / 3600000 / 24;
		peso = 1.0 / (1.0 + Math.exp(-alfa * (x - d)));

	}

	@Override
	public String toString() {
		return nome;
	}

	public static SchedinaFile getInstance(String _data) {
		SchedinaFile s = schedine.get(_data);
		if (s == null) {
			s = new SchedinaFile(_data);
			schedine.put(_data, s);
		}
		return s;
	}

	public void add(PartitaFile partitaFile) {
		getPartite().add(partitaFile);
		schedine.get("Tutte").getPartite().add(partitaFile);
	}

	public void dump() {
		System.out.println(nome);
		for (PartitaFile p : getPartite()) {
			System.out.println(p);
		}

	}

	public static Collection<SchedinaFile> values() {
		return schedine.values();
	}

	public static int size() {
		return schedine.size();
	}

	public Vector<PartitaFile> getPartite() {
		return partite;
	}

	public void setPartite(Vector<PartitaFile> partite) {
		this.partite = partite;
	}

	@Override
	public int compareTo(Object arg0) {
		SchedinaFile sf = (SchedinaFile) arg0;
		return -realdata.compareTo(sf.realdata);
	}

}