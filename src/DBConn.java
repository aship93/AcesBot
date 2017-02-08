import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {

	 public static Connection dbConnect(){
			try {
				
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				
			System.out.println("Driver Class loaded!");
			String URL = "jdbc:mysql://localhost/user_points";
			String USER = "root";
			String PASS = "";
			
			return DriverManager.getConnection(URL, USER, PASS);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch(ClassNotFoundException ex) {
				   System.out.println("Error: unable to load driver class!");
				   System.exit(1);
			}
			return null;
	 }
}
