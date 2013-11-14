import it.gg.toto.PartitaFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class GetInfo {
	static class Part {
		Date data = new Date() {
			@Override
			public int compareTo(Date date) {
				return -super.compareTo(date);
			}

			@Override
			public String toString() {
				return dffout.format(this);
			}
		};
		Integer ordine;
		Integer res1, res2;
		String squ1, squ2;

		@Override
		public String toString() {
			return dffout.format(data)   + ";" + ordine  + ";" + squ1 + ";" + squ2
					+ ";" + res1 + ";" + res2;
		}
	}

	static SimpleDateFormat dffout = new SimpleDateFormat("dd/MM/yyyy");
	static SimpleDateFormat dffx = new SimpleDateFormat("dd;MMM;yyyy");

	static StringBuffer htmlText;
	static StringBuffer csvText;

	public static void main(String[] args) {
		PrintStream fos;
		try {
			fos = new PrintStream("appo.txt");
			for (int i = 79; i < 120; i++) {
				System.out.println("Recupero di http://www.toto13.com/toto/2013/" + i + "_toto_13.htm");
				getHttp("http://www.toto13.com/toto/2013/" + i + "_toto_13.htm");
				getCSV();
				//System.out.println(htmlText);
				// System.out.println(csvText);
				for (Part p : parts) {
					fos.println(p);
				}
			}
			fos.close();
		} catch (FileNotFoundException e) {
		}
	}

	static Vector<Part> parts = new Vector<Part>();

	static private void getCSV() {
		parts = new Vector<Part>();
		csvText = new StringBuffer();
		convertiSchedina();
		for (Part p : parts) {
			csvText.append(p.toString() + "\n");
		}
	}

	static private void convertiSchedina() {
		String testo = htmlText.toString();
		String reMesi = "(?:gennaio)|(?:febbraio)|(?:marzo)|(?:aprile)|(?:maggio)|(?:giugno)|(?:luglio)|(?:agosto)|(?:settembre)|(?:ottobre)|(?:novembre)|(?:dicembre)";
		testo = testo.replaceAll(" ?([123]?[0-9]) *((?:" + reMesi
				+ ")) *([0-9]{4})", "#d#$1;$2;$3#d#");
		int pos1 = testo.indexOf("#d#");
		if (pos1 >= 0) {
			String data;
			int pos2 = testo.indexOf("#d#", pos1 + 3);
			data = testo.substring(pos1 + 3, pos2);
			// htmlText.setText(testo.substring(pos1, pos2) + "\n");
			String[] fld = data.split(";");
			String giorno = fld[0];
			String mese = fld[1];
			String anno = fld[2];
			System.out.println("data " + giorno + "-" + mese + "-" + anno);

			testo = testo
					.replaceAll(
							"[\\t ]*[X12-]*[\\t ]*.[\\t ]*(1?[0-9])[\\t ]*([A-Za-z0-9]*)[\\t ]*([A-Za-z0-9]*)[\\t ]*([0-9])-([0-9]).*",
							"#tag#$1;$2;$3;$4;$5#end#");
			
			pos1 = testo.indexOf("#tag#", pos2);
			int cnt = 0;
			while (pos1 >= 0 && cnt < 15) {
				pos2 = testo.indexOf("#end#", pos1 + 5);
				String squ1, squ2, ordine, res1, res2;
				String partita = testo.substring(pos1 + 5, pos2);
				PartitaFile pp;
				fld = partita.split(";");
				Part p = new Part();
				try {
					if (fld[1].equals("") || fld[2].equals("")) {
						throw new Exception();
					}
					p.ordine = new Integer(fld[0]);
					p.squ1 = fld[1];
					p.squ2 = fld[2];
					p.data.setTime(dffx.parse(data).getTime());
					try {
						p.res1 = Integer.parseInt(fld[3]);
					} catch (Exception e) {
						p.res1 = null;
					}
					try {
						p.res2 = Integer.parseInt(fld[4]);
					} catch (Exception e) {
						p.res2 = null;
					}
					
					parts.add(p);
					System.out.println(p);
				} catch (NumberFormatException e) {
					System.out.println("'" + partita + "'");
					e.printStackTrace();
					return;
				} catch (ParseException e) {
					System.out.println("'" + partita + "'");
					e.printStackTrace();
					return;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.out.println("'" + partita + "'");
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Errore in '" + partita + "'");
				}
				pos1 = testo.indexOf("#tag#", pos2);
				cnt++;
			}
		}
	}

	static private void getHttp(String url) {
		htmlText = new StringBuffer();
		URLConnection connection;
		String charset = "UTF-8";
		try {
			connection = new URL(url).openConnection();
			connection.setRequestProperty("Accept-Charset", charset);
			InputStream response = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response, charset));
			try {
				for (String line; (line = reader.readLine()) != null;) {
					htmlText.append(line.replaceAll("\\<[pP]\\>", "\n")
							.replaceAll("\\</div\\>", "\n")
							.replaceAll("\\</tr\\>", "\n")
							.replaceAll("\\<.*?\\>", "")
							.replaceAll("&nbsp;", " "));
					// jTextArea2.append(line);
				}
			} catch (MalformedURLException e) {
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException logOrIgnore) {
					}
			}
		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}
	}

}
