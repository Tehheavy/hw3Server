
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.net.*;

public class Sqlserver {
	enum Mode {
		NONE,INSERT,SELECT
	}
	public static void main(String[] args) throws IOException {
		int portNumber = Integer.parseInt("4138");
		ServerSocket serverSocket =null;
		String database,username,password;
		database="JDBC:MYSQL://cs.telhai.ac.il/Group_2";
		username="cs313313991";
		password="NONE";
		try {
			Thread notifications = new Thread(new RoutineJobs(database,username,password));
			notifications.start();
			serverSocket =
					new ServerSocket(portNumber);
			while(true) {
				System.out.println("Waiting for a client on port "+serverSocket.getLocalPort());
				Socket clientSocket = serverSocket.accept(); 
				System.out.println("Connected with:"+clientSocket.toString());
				//			SQL mysql = new SQL("JDBC:MYSQL://cs.telhai.ac.il/studentDB_cs313313991","cs313313991","NONE");
				Thread handler = new Thread( new RequestHander(clientSocket,database,username,password));
				handler.start();
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
