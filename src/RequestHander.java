

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

public class RequestHander extends Thread {
	Socket clientsocket;
	Mode curstate = Mode.NONE;
	int portNumber=4138;
	SQL mysql;
	public RequestHander(Socket client,String database,String username,String password) throws SQLException {
		super();
		clientsocket=client;
		mysql = new SQL(database,username,password);

	}
	public enum Mode {
		NONE,LOGIN,REGISTER,ORDER,UPDATE,REMOVE
	}
	public Mode HASHIT(String str){
		String hashstr=str.toUpperCase();
		if(hashstr.equals("LOGIN"))
			return Mode.LOGIN;
		if(hashstr.equals("REGISTER"))
			return Mode.REGISTER;
		if(hashstr.equals("ORDER"))
			return Mode.ORDER;
		return Mode.NONE;
	}

	public void run () {
		try (  
				ObjectOutputStream out =
				new ObjectOutputStream(clientsocket.getOutputStream());   

				BufferedReader in = new BufferedReader(
						new InputStreamReader(clientsocket.getInputStream()));
				) {
			System.out.println("connection established with:"+clientsocket.toString());
			out.flush();
			String results = null;
			String inputLine;
			//     out.println("Press 1 to insert, 2 to select");
			String response = "";
			out.writeObject("Connection Established");
			while ((inputLine = in.readLine()) != null) {

				try{
					String id = inputLine;
					System.out.println("recieved "+id);
					
					String[] splited = id.split(" ");
					Mode requestmode = HASHIT(splited[0]);
					switch(requestmode){
					case LOGIN:
						if(splited.length!=3)
						{
							results="user failed to login";
							out.writeObject("invalidlogin");
							break;
						}
						if(!mysql.containsUserLogin(splited[1], splited[2]))
						{
							results="user failed to login";
							out.writeObject("invalidlogin");
							break;
						}
						curstate=Mode.LOGIN;
					//	String query = "SELECT * FROM users where UserName = '" + splited[1] + "' and PassWord = '" + splited[2] + "'";
						out.writeObject("acceptedlogin"); //user received message for attempting to log in
						out.flush();
						try{
							//   out.writeObject("PASSWORD=");
							// String to=in.readLine(); 
							//  mysql.insert(id, from, to, distance, price);
							results="user successfuly logged in.";
						}
						//       catch (SQLException e) {
						//        results="Error fulfilling insert request.";
						//       }
						catch (NumberFormatException e) {
							results="please enter a number next time.1";
						}finally {
							//out.writeObject(results +"\n"+response); worthless for now
						}
						break;
					case REGISTER:
						if(splited.length!=3)
						{
							results="user failed to register";
							out.writeObject("invalidregistery");
							break;
						}
						if(mysql.containsUser(splited[1]))
						{
							results="user failed to register";
							out.writeObject("invalidregistery");
							break;
						}
						curstate=Mode.REGISTER;
						 mysql.insert(splited[1],splited[2]);
						 out.writeObject("acceptedregistery");
						out.flush();


						break;
					case ORDER:
						System.out.println("ORDER:");
						for(int i =0;i<splited.length;i++)
						{
							System.out.print(splited[i]+" ");
						}
						if(splited[1].equals("2"))
						{
							mysql.insertOrder2(splited[1], splited[2], Integer.parseInt(splited[3]), Integer.parseInt(splited[4]),splited[5], splited[6], splited[7], splited[8], splited[9], splited[10]);
							out.writeObject("acceptedorder");
						}
						break;
					case UPDATE:
						curstate=Mode.NONE;
						out.writeObject("not yet implemented");
						break;
					case REMOVE:
						curstate=Mode.NONE;
						out.writeObject("not yet implemented");
						break;
					default:
						out.writeObject("SERVER:\nDEFAULT\nINVALID CHOICE\n");
						break;
					}
				}catch(SQLException e){
					out.writeObject("SERVER EXCEPTION\n"+e.getMessage());
				}
				catch (Exception e) {
					out.writeObject("SERVER EXCEPTION\nINVALID CHOICE\n");
					//      break;
				}

				//      System.out.println(inputLine);
				//      out.println(inputLine+" selected mode is:"+curstate.name());
				out.flush();
			}
			System.out.println(results);
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
		finally {
			try { clientsocket.close(); }
			catch (Exception e ){ ; }
		}
	} 
}