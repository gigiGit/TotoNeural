package it.gg.neural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NN_Neuron {
	Double[] weigth;
	double[] delta;
	// Double bias;
	String nome;
	static int cnt = 0;

	public NN_Neuron(int n1) {
		nome = "Neurone " + (++cnt);
		weigth = new Double[n1 + 1];
		delta = new double[n1 + 1];

		for (int i = 0; i <= n1; i++)
			weigth[i] = Math.random();
	}

	@Override
	public String toString() {
		return nome;
	}

	public void setInput(int n1) {
		n1++;
		Double[] ww = weigth;
		delta = new double[n1];
		if (n1 < weigth.length) {
			ww = new Double[n1];
			for (int i = 0; i < n1; i++) {
				ww[i] = weigth[i];
			}
			weigth = ww;
		} else if (n1 > weigth.length) {
			ww = new Double[n1];
			for (int i = 0; i < weigth.length; i++) {
				ww[i] = weigth[i];
			}
			for (int i = weigth.length; i < n1; i++) {
				ww[i] = Math.random();
			}
			weigth = ww;
		}
	}

	public NN_Neuron(int n1, DataInputStream ois) throws IOException {
		nome = "Neurone " + (++cnt);
		weigth = new Double[n1 + 1];
		delta = new double[n1 + 1];
		for (int j = 0; j < weigth.length; j++) {
			weigth[j] = ois.readDouble();
		}

	}

	public void save(DataOutputStream oos) {
		try {
			for (int j = 0; j < weigth.length; j++) {
				oos.writeDouble(weigth[j]);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	double _activation;

	public double feed(double[] ds) {
		_activation = weigth[weigth.length - 1];
		for (int i = 0; i < weigth.length - 1; i++) {
			_activation += weigth[i] * ds[i];
		}
		return _activation;
	}

	public void sberla(double d) {
		for (int k = 0; k < weigth.length; k++) {
			weigth[k] += (Math.random() - 0.5);
		}
	}

	public void save(String netName, String layerName, String nome,
			Connection conn) {
		PreparedStatement stmt;
		try {
			stmt = conn
					.prepareStatement("create table if not exists NN_Neurons ( " //
							+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," //
							+ "net  TEXT NOT NULL," //
							+ "layer  TEXT NOT NULL," //
							+ "nome TEXT NOT NULL," //
							+ "indice INTEGER NOT NULL," //
							+ "weigth number NOT NULL," //
							+ "UNIQUE (nome,net,layer,indice)); ");
			stmt.execute();
			conn.setAutoCommit(false);

			stmt = conn
					.prepareStatement("delete from NN_Neurons where nome=? and net=? and layer=?;");
			stmt.setString(1, nome);
			stmt.setString(2, netName);
			stmt.setString(3, layerName);
			stmt.execute();

			stmt = conn.prepareStatement("insert into NN_Neurons ("
					+ "nome,net,layer,indice,weigth)" + "values(?,?,?,?,?);");
			for (int j = 0; j < weigth.length; j++) {
				stmt.setString(1, nome);
				stmt.setString(2, netName);
				stmt.setString(3, layerName);
				stmt.setInt(4, j);
				stmt.setDouble(5, weigth[j]);
				stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
