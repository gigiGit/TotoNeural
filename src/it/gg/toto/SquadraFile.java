package it.gg.toto;

import java.util.Hashtable;

public class SquadraFile implements Comparable{
	static Hashtable<String, SquadraFile> squadre = new Hashtable<String, SquadraFile>();
	String nome;
	private int id;
	private boolean cancella;
private int utilizzata=0;
	public boolean isCancella() {
		return cancella;
	}

	public void setCancella(boolean cancella) {
		this.cancella = cancella;
	}

	public SquadraFile(String squadra) {
		nome = squadra;
		cancella=false;
		setId(getSquadre().size() + 1);
	}

	public String toString() {
		return nome;
	}

	private static String Correggi(String nome) {
		String res = nome.trim();
		if (res.equals("Dampdoria") || res.equals("Samdoria")
				|| res.equals("Samporia") || res.equals("Sampodria"))
			res = "Sampdoria";
		else if (res.equals("Bologna1"))
			res = "Bologna";
		else if (res.equals("Vhievo") || res.equals("Chieo"))
			res = "Chievo";
		else if (res.equals("palermo"))
			res = "Palermo";
		else if (res.equals("Torno"))
			res = "Torino";
		else if (res.equals("Catani"))
			res = "Catania";
		else if (res.equals("Vicenza Vicenza"))
			res = "Vicenza";
		else if (res.equals("Savona Savona"))
			res = "Savona";
		else if (res.equals("Reggiana Reggiana"))
			res = "Reggiana";
		else if (res.equals("Pavia Pavia"))
			res = "Pavia";
		return res;
	}

	public static SquadraFile getInstance(String squadra) {
		String sq = Correggi(squadra);
		SquadraFile s = getSquadre().get(sq);
		if (s == null) {
			s = new SquadraFile(sq);
			getSquadre().put(sq, s);
		}
		s.setUtilizzata(s.getUtilizzata() + 1);
		return s;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static int size() {
		// TODO Auto-generated method stub
		return getSquadre().size();
	}

	public static Hashtable<String, SquadraFile> getSquadre() {
		return squadre;
	}

	public static void setSquadre(Hashtable<String, SquadraFile> squadre) {
		SquadraFile.squadre = squadre;
	}

	/**
	 * @return the utilizzata
	 */
	public int getUtilizzata() {
		return utilizzata;
	}

	/**
	 * @param utilizzata the utilizzata to set
	 */
	public void setUtilizzata(int utilizzata) {
		this.utilizzata = utilizzata;
	}

	@Override
	public int compareTo(Object o) {
		SquadraFile s2=(SquadraFile) o;
		return nome.compareTo(s2.nome);
	}

}