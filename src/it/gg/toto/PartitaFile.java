package it.gg.toto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

public class PartitaFile implements Comparable{
	public static void main(String[] args) {
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

	}

	private double[] result = null;
	public static Hashtable<String, PartitaFile> partite = new Hashtable<String, PartitaFile>();

	static SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	public static PartitaFile getInstance(String line) {
		PartitaFile p;
		String[] item = line.split(";");
		String data = item[0].trim();
		
		SquadraFile squadra1 = SquadraFile.getInstance(item[2].trim());
		SquadraFile squadra2 = SquadraFile.getInstance(item[3].trim());
		p = partite.get(data + ";" + squadra1.nome + ";" + squadra2.nome);
		if (p == null) {
			p = new PartitaFile(line);
		}
		try {
			p.valida = Boolean.parseBoolean(item[6].trim());
		} catch (Exception e) {
			p.valida = false;
		}
		try {
			p.setRes(Integer.parseInt(item[4].trim()),
					Integer.parseInt(item[5].trim()));
		} catch (Exception e) {
			p.setRes(null, null);
		}

		return p;
	}

	public static void loadDB() {
		Connection c;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:partite.db");
			System.out.println("Database Aperto");
			try {
				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("select * from partite");
				while (rs.next()) {
					String data = rs.getString("data");
					String squadra1 = rs.getString("squadra1");
					String squadra2 = rs.getString("squadra2");
					Integer res1 = rs.getInt("res1");
					Integer res2 = rs.getInt("res2");
					String valida = rs.getString("valida");
					PartitaFile p = partite.get(data + ";" + squadra1 + ";"
							+ squadra2);
					if (p == null) {
						p = new PartitaFile(data, squadra1, squadra2, res1,
								res2, valida);
					} else {
						p.setRes(res1, res2);
						p.valida = Boolean.parseBoolean(valida);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			c.close();
			System.out.println("Database chiuso.");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

	public static void loadFile(String nomeFile) {
		try {
			BufferedReader fr = new BufferedReader(new FileReader(nomeFile));
			String line = fr.readLine();
			while (!(line == null || line.equals(""))) {
				//System.err.println(line);
				PartitaFile p = PartitaFile.getInstance(line);
				line = fr.readLine();
			}
			fr.close();
		} catch (FileNotFoundException fne) {
			System.err.println("Il file " + nomeFile + " non esiste");
			// fne.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		modified = false;
	}

	public static void saveDB() {
		// if (modified == false) return;
		Connection c;
		PreparedStatement stmt;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:partite.db");
			System.out.println("Database Aperto");
			try {
				stmt = c.prepareStatement("drop table if exists partite;");
				stmt.execute();
				stmt = c.prepareStatement("create table partite ( " //
						+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," //
						+ "data TEXT NOT NULL," //
						+ "squadra1 TEXT NOT NULL," //
						+ "squadra2 TEXT NOT NULL," //
						+ "res1 INTEGER ," //
						+ "res2 INTEGER ," //
						+ "valida  TEXT ," //
						+ " UNIQUE (data,squadra1,squadra2) ); ");
				stmt.execute();
				c.setAutoCommit(false);
				stmt = c.prepareStatement("insert into partite (data,squadra1,squadra2,res1,res2,valida) values(?,?,?,?,?,?);");

				for (PartitaFile p : partite.values()) {

					if (p.isCancella() || p.squadra1.isCancella()
							|| p.squadra2.isCancella())
						continue;
					stmt.setString(1, df.format(p.schedina.realdata.getTime()));
					stmt.setString(2, p.squadra1.nome);
					stmt.setString(3, p.squadra2.nome);
					if (p.res1 == null) {
						stmt.setNull(4, java.sql.Types.INTEGER);
					} else {
						stmt.setInt(4, p.res1);
					}
					if (p.res2 == null) {
						stmt.setNull(5, java.sql.Types.INTEGER);
					} else {
						stmt.setInt(5, p.res2);
					}
					stmt.setString(6, p.valida.toString());
					stmt.addBatch();
				}
				stmt.executeBatch();
				c.commit();
				stmt.close();
				System.out.println("Partite salvate nel db.");
				c.close();
				System.out.println("Database chiuso.");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
		}
		modified = false;
	}

	public static void saveFile(String nomeFile) {
		// if (modified == false) return;
		PrintStream out;
		Vector<PartitaFile> vector = new Vector<PartitaFile>(partite.values());
		Collections.sort(vector);
		try {
			out = new PrintStream(new File(nomeFile));
			for (PartitaFile p : vector) {
				if (p.isCancella() || p.squadra1.isCancella()
						|| p.squadra2.isCancella())
					continue;
				p.dump(out);
			}
			out.close();
			System.out.println("Partite salvate in " + nomeFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		modified = false;
	}

	public static int size() {
		return partite.size();
	}

	public static Collection<PartitaFile> values() {
		return partite.values();
	}

	// private Calendar data;

	private String previsione;
	private Integer res1;

	private Integer res2;
	private boolean cancella;

	public boolean isCancella() {
		return cancella;
	}

	public void setCancella(boolean cancella) {
		this.cancella = cancella;
	}

	SchedinaFile schedina;

	private int schedina_pos;

	private SquadraFile squadra1;

	private SquadraFile squadra2;

	Boolean valida;

	static boolean modified = false;

	public PartitaFile(String line) {
		String[] item = line.split(";");
		schedina = SchedinaFile.getInstance(item[0].trim());
		schedina.add(this);
		setSchedina_pos(new Integer(item[1]));
		setSquadra1(SquadraFile.getInstance(item[2].trim()));
		setSquadra2(SquadraFile.getInstance(item[3].trim()));
		try {
			setRes(Integer.parseInt(item[4].trim()),
					Integer.parseInt(item[5].trim()));
		} catch (Exception e) {
			setRes(null, null);
		}
		try {
			valida = Boolean.parseBoolean(item[6].trim());
		} catch (Exception e) {
			valida = false;
		}add();
	}

	public PartitaFile(String _data, SquadraFile _sq1, SquadraFile _sq2,
			String r1, String r2) throws Exception {
		if (partite.get(_data + ";" + _sq1.nome + ";" + _sq2.nome) != null) {
			throw new Exception("Partita già presente");
		}

		setSquadra1(_sq1);
		setSquadra2(_sq2);
		schedina = SchedinaFile.getInstance(_data);
		schedina.add(this);
		setSchedina_pos(schedina.getPartite().size());
		if (r1 == null || r2 == null || r1.equals("") || r2.equals("")) {
			setRes(null, null);
			valida = false;
		} else {
			setRes(new Integer(r1), new Integer(r2));
			valida = true;
		}
		add();
	}

	public PartitaFile(String _data, String _sq1, String _sq2, Integer _r1,
			Integer _r2, String _v) {

		setSquadra1(SquadraFile.getInstance(_sq1));
		setSquadra2(SquadraFile.getInstance(_sq2));
		schedina = SchedinaFile.getInstance(_data);
		schedina.add(this);
		setSchedina_pos(schedina.getPartite().size());
		try {
			this.valida = Boolean.parseBoolean(_v);
		} catch (Exception e) {
			this.valida = false;
		}
		try {
			setRes(_r1, _r2);
		} catch (Exception e) {
			setRes(null, null);
		}
		add();
	}

	private void add() {
		partite.put(df.format(getData().getTime()) + ";" + squadra1.nome + ";"
				+ squadra2.nome, this);
	}

	public void dump(PrintStream out) {
		if (out != null) {
			out.println(df.format(getData().getTime())+";"+schedina_pos + ";" + squadra1.nome
					+ ";" + squadra2.nome + ";" + res1 + ";" + res2 + ";"
					+ valida);
		}
	}

	public Calendar getData() {
		return schedina.realdata;
	}

	public Double get_Peso() {
		return schedina.peso;
	}

	public String getPrevisione() {
		return previsione;
	}

	public Integer getRes1() {
		return res1;
	}

	public Integer getRes2() {
		return res2;
	}

	public double[] getResult(int index) {
		return new double[] { result[index] };
	}

	public String getResultT() {
		if (result == null)
			return "";
		return (result[0] == 1 ? "1" : " ") + (result[1] == 1 ? "2" : " ")
				+ (result[2] == 1 ? "X" : " ");
	}

	public int getSchedina_pos() {
		return schedina_pos;
	}

	public SquadraFile getSquadra1() {
		return squadra1;
	}

	public SquadraFile getSquadra2() {
		return squadra2;
	}

	public Boolean getValida() {
		return valida;
	}

	public Boolean isValida() {
		return valida && !(res1 == null || res2 == null);
	}

	public void setPrevisione(String previsione) {
		this.previsione = previsione;
	}

	public void setRes(Integer r1, Integer r2) {
		if (r2 == null || r1 == null) {
			res1 = null;
			res2 = null;
			setResult(null);
		} else {
			this.res2 = r2;
			this.res1 = r1;
			setResult(new double[] { 0, 0, 0 });
			if (res1 > res2)
				getResult()[0] = 1; // 1
			else if (res1 < res2)
				getResult()[1] = 1; // 2
			else
				getResult()[2] = 1; // X
		}
	}

	public void setSchedina_pos(int schedina_pos) {
		this.schedina_pos = schedina_pos;
	}

	public void setSquadra1(SquadraFile oSquadra1) {
		this.squadra1 = oSquadra1;
	}

	public void setSquadra2(SquadraFile oSquadra2) {
		this.squadra2 = oSquadra2;
	}

	public void setValida(Boolean _valida) {
		this.valida = _valida;
		modified = true;
	}

	public String toString() {
		return getSchedina_pos() + ";" + squadra1 + ";" + squadra2 + ";" + res1
				+ ";" + res2;

	}

	double[] x = null;

	public double[] getInputPattern(int N) {
		if (x == null) {
			double peso = get_Peso();
			int n1 = SquadraFile.size();

			x = new double[n1 * N * 2];
			for (int i = 0; i < N; i++) {
				x[squadra1.getId() - 1 + i] = peso;
				x[n1 * N + squadra2.getId() - 1 + i] = peso;
			}
		}
		return x;
	}

	/**
	 * @return the result
	 */
	public double[] getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(double[] result) {
		this.result = result;
	}

	@Override
	public int compareTo(Object arg0) {
		PartitaFile p2=(PartitaFile) arg0;
		
		return schedina.realdata.compareTo(p2.schedina.realdata)*1000
				+(this.schedina_pos-p2.schedina_pos);
	}

}
