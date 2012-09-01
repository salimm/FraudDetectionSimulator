import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.h2.Driver;
import org.h2.command.Prepared;

import simulator.data.SerializableInstance;


public class TestMain {
	public static void main(String[] args) throws SQLException {
		Driver d = new Driver();
		Connection c= d.connect("jdbc:h2:fds", null);
//		c.prepareStatement("CREATE TABLE TEST(ID INT PRIMARY KEY, VALUE OTHER, MONTH INT)").execute();
		SerializableInstance si = new SerializableInstance(new Integer[]{1,2,3}, 1);
//		PreparedStatement ps = c.prepareStatement("INSERT INTO TEST VALUES(1,?,1);");
//		ps.setObject(1, si);
//		ps.execute();
		ResultSet executeQuery = c.prepareStatement("Select * from TEST").executeQuery();
		while(executeQuery.next()){
			System.out.println(executeQuery.getObject("VALUE"));
		}
		
	}
}
