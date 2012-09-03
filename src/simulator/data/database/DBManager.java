package simulator.data.database;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.h2.Driver;

import simulator.conf.SOptions;
import simulator.data.AbstractInstance;
import simulator.policy.ScoreBoard;

public class DBManager {
	private Connection con;

	public DBManager() throws SQLException {
		Driver d = new Driver();
		try {
			con = d.connect("jdbc:h2:fds", null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		createTables();
		con.prepareStatement("DELETE FROM TRANSACTION").execute();
	}

	private void createTables() throws SQLException {
		if (!tableExists("TRANSACTION")) {
			con.prepareStatement(
					"CREATE TABLE TRANSACTION(ID INT PRIMARY KEY AUTO_INCREMENT, VALUE OTHER, MONTH INT,CORRECTED BOOLEAN)")
					.execute();
		}
		if (!tableExists("SIMULATION")) {
			con.prepareStatement(
					"CREATE TABLE SIMULATION(ID INT PRIMARY KEY AUTO_INCREMENT, TIME TIMESTAMP,CONF OTHER,SCOREBOARD OTHER)")
					.execute();
		}

	}

	private boolean tableExists(String name) {
		try {
			DatabaseMetaData dbm = con.getMetaData();
			ResultSet rs = dbm.getTables(null, null, name, null);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<AbstractInstance> getAllTransactions() {
		ArrayList<AbstractInstance> out = new ArrayList<AbstractInstance>();
		try {
			ResultSet rs = con.prepareStatement("SELECT * FROM TRANSACTION")
					.executeQuery();
			while (rs.next()) {
				out.add((AbstractInstance) rs.getObject("VALUE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	public List<AbstractInstance> getAllCorrectedTransactions() {
		ArrayList<AbstractInstance> out = new ArrayList<AbstractInstance>();
		try {
			ResultSet rs = con.prepareStatement(
					"SELECT * FROM TRANSACTION WHERE CORRECTED=true")
					.executeQuery();
			while (rs.next()) {
				out.add((AbstractInstance) rs.getObject("VALUE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	public List<AbstractInstance> getCorrectedTransactions(int time) {
		ArrayList<AbstractInstance> out = new ArrayList<AbstractInstance>();
		try {
			ResultSet rs = con.prepareStatement(
					"SELECT * FROM TRANSACTION WHERE CORRECTED=true and time="
							+ time).executeQuery();
			while (rs.next()) {
				out.add((AbstractInstance) rs.getObject("VALUE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	public List<AbstractInstance> getTransactions(int time) {
		ArrayList<AbstractInstance> out = new ArrayList<AbstractInstance>();
		try {
			ResultSet rs = con.prepareStatement(
					"SELECT * FROM TRANSACTION WHERE time=" + time)
					.executeQuery();
			while (rs.next()) {
				out.add((AbstractInstance) rs.getObject("VALUE"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return out;
	}

	public void insertTransaction(AbstractInstance ins, int month) {
		try {
			PreparedStatement ps = con
					.prepareStatement("INSERT INTO SIMULATION (VALUE,MONTH,CORRECTED) VALUES(?,"
							+ month + "," + ins.hasTrueLabel() + ")");
			ps.setObject(1, ins);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertSim(ScoreBoard board, SOptions opts) {
		try {
			PreparedStatement ps = con
					.prepareStatement("INSERT INTO SIMULATION (TIME,CONF,SCOREBOARD) VALUES(CURRENT_TIMESTAMP,?,?)");
			ps.setObject(1, opts);
			ps.setObject(2, board);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
