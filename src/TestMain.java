import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.plaf.SliderUI;

import org.h2.Driver;
import org.h2.command.Prepared;

import simulator.data.SerializableInstance;

public class TestMain {
	// public static void main(String[] args) throws SQLException {
	// Driver d = new Driver();
	// Connection c= d.connect("jdbc:h2:fds", null);
	// //
	// c.prepareStatement("CREATE TABLE TEST(ID INT PRIMARY KEY, VALUE OTHER, MONTH INT)").execute();
	// SerializableInstance si = new SerializableInstance(new Integer[]{1,2,3},
	// 1);
	// // PreparedStatement ps =
	// c.prepareStatement("INSERT INTO TEST VALUES(1,?,1);");
	// // ps.setObject(1, si);
	// // ps.execute();
	// ResultSet executeQuery =
	// c.prepareStatement("Select * from TEST").executeQuery();
	// while(executeQuery.next()){
	// System.out.println(executeQuery.getObject("VALUE"));
	// }
	//
	// }
	public static void main(String[] args) {
		Callable<Integer> run = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				Thread.sleep(600);
				return 2;
			}
		};

		RunnableFuture future = new FutureTask(run);
		ExecutorService service = Executors.newSingleThreadExecutor();
		service.execute(future);
		Integer result = null;
		try {
			result = (Integer) future.get(500, TimeUnit.MILLISECONDS); // wait 1 second
			System.out.println(result);
		} catch (TimeoutException ex) {
			// timed out. Try to stop the code if possible.
			future.cancel(true);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		service.shutdown();
	}
}
