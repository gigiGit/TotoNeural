package it.gg.neural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NN_Layer {
	NN_Neuron[] neurons;
	String nome;
	static int cnt = 0;
	// double[] inputPattern;

	int nPrevNeurons, nNeurons;
	double[] grad_ex;
	double[] res;

	private void init() {
		res = new double[nNeurons];
		grad_ex = new double[nNeurons];
	}

	public NN_Layer(int _n1, int _n2) {
		nome = "Layer " + (++cnt);
		nPrevNeurons = _n1;
		nNeurons = _n2;
		neurons = new NN_Neuron[nNeurons];
		for (int i = 0; i < nNeurons; i++)
			neurons[i] = new NN_Neuron(nPrevNeurons);
		init();
	}

	public void setInput(int n1) {
		nPrevNeurons=n1;
		for (NN_Neuron n:neurons)
			n.setInput(n1);
	}

	public NN_Layer(DataInputStream ois) throws IOException {
		nome = "Layer " + (++cnt);
		nPrevNeurons = ois.readInt();
		nNeurons = ois.readInt();
		neurons = new NN_Neuron[nNeurons];
		for (int i = 0; i < nNeurons; i++)
			neurons[i] = new NN_Neuron(nPrevNeurons, ois);
		init();
	}

	public void save(DataOutputStream oos) {
		try {
			oos.writeInt(nPrevNeurons);
			oos.writeInt(nNeurons);
			for (int k = 0; k < nNeurons; k++) {
				neurons[k].save(oos);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String toString() {

		return nome;
	}

	public void save(String netName, String layerName, Connection conn) {
		PreparedStatement stmt;
		try {
			stmt = conn
					.prepareStatement("create table if not exists NN_Layer ( " //
							+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT," //
							+ "nome TEXT NOT NULL," //
							+ "net  TEXT NOT NULL," //
							+ "nPrevNeurons INTEGER NOT NULL," //
							+ "nNeurons INTEGER NOT NULL," //
							+ "UNIQUE (nome,net)); ");
			stmt.execute();
			conn.setAutoCommit(false);

			stmt = conn
					.prepareStatement("delete from NN_Layer where nome=? and net=?;");
			stmt.setString(1, layerName);
			stmt.setString(2, netName);
			stmt.execute();

			stmt = conn.prepareStatement("insert into NN_Layer (" + "nome,net,"
					+ "nPrevNeurons," + "nNeurons)" + "values(?,?,?,?);");
			stmt.setString(1, layerName);
			stmt.setString(2, netName);
			stmt.setInt(3, nPrevNeurons);
			stmt.setInt(4, nNeurons);
			stmt.addBatch();
			stmt.executeBatch();
			conn.commit();
			stmt.close();
			for (int k = 0; k < nNeurons; k++) {
				neurons[k].save(netName, layerName, "neuron" + k, conn);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
