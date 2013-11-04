import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import oracle.jdbc.OracleDriver;
import oracle.jdbc.driver.OracleConnection;


public class DB {
	private static final String URL = "jdbc:oracle:thin:@localhost:1521:hw2";
	private static final String userName = "system";
	private static final String password = "hw2";
	
	private static OracleConnection conn;
	public static Statement statement;
	
	public static void init() {
		try	{
	    	DriverManager.registerDriver(new OracleDriver());
	    	conn = (OracleConnection)DriverManager.getConnection(URL, userName, password);
	    	statement = conn.createStatement();
	    	Geom.init(conn);
   		} catch (Exception e) {
     		System.out.println( "Error while connecting to DB: "+ e.toString() );
     		e.printStackTrace();
     		System.exit(-1);
   		}
	}
	
	public static PreparedStatement prepareStatement(String statement) {
		try {
			return conn.prepareStatement(statement);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
