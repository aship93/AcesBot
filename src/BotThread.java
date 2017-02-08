import java.sql.* ;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class BotThread extends PircBot implements Runnable {
	public String threadname;
	public Thread t;
	public User[] names; 
	AcesBot aBot;
	public BotThread(String name, AcesBot bot){
		this.threadname = name;
		this.aBot = bot;
		System.out.println("Creating thread " + threadname);
	}
	
	@Override
	public void run() {
		System.out.println("Running " + threadname);

		try {
			Connection conn = DBConn.dbConnect();
			if(threadname.equals("Points Thread")){
				Statement stmt = conn.createStatement();
				while(true){
					names = aBot.getUsers("#4acesgaming");
					for(int i = 0; i < names.length; i++){
						ResultSet rs = stmt.executeQuery("SELECT * from users WHERE name = '" + names[i] + "'");
						if(rs.isBeforeFirst()){
							stmt.executeUpdate("UPDATE users " + "SET points = points + 10 " + "WHERE name = '" + names[i] + "'");
							System.out.println("Points added to database");
						}else if(!rs.isBeforeFirst()){
							stmt.executeUpdate("INSERT INTO users VALUES ('" + names[i] + "', 0)");
							System.out.println(names[i] + " was added to the database");
						}
					}
					Thread.sleep(10000);
				}
			}else if(threadname.equals("Data Thread")){
				Statement stmt = conn.createStatement();
				while(true){
					ResultSet rs = stmt.executeQuery("SELECT * FROM user_points.quotes ORDER BY RAND() LIMIT 1");
					if(rs.next()){
						System.out.println("\"" +rs.getString("quote") + "\" - " + rs.getString("speaker"));
					}
					Thread.sleep(3000);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Thread "+ threadname + " exiting");
	}
	
	public void start(){
		System.out.println("Starting thread " + threadname);
		if(t == null){
			t = new Thread(this,threadname);
			t.start();
		}
	}
}
