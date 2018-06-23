

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;


public class RequestHander extends Thread {
	Socket clientsocket;
	Mode curstate = Mode.NONE;
	int portNumber=4138;
	SQL mysql;
	List<String> l;
	String loggeduser=null;
	public RequestHander(Socket client,String database,String username,String password,List<String> l ) throws SQLException {
		super();
		clientsocket=client;
		mysql = new SQL(database,username,password);
		this.l=l;

	}
	/**
	 * Enum containing:NONE,LOGIN,REGISTER,ORDER,REQUEST,UPDATE,REMOVE,COMPLAINT
	 * @author gogop
	 *
	 */
	public enum Mode {
		NONE,LOGIN,REGISTER,ORDER,REQUEST,UPDATE,REMOVE,COMPLAINT
	}
	public Mode HASHIT(String str){
		String hashstr=str.toUpperCase();
		if(hashstr.equals("LOGIN"))
			return Mode.LOGIN;
		if(hashstr.equals("REGISTER"))
			return Mode.REGISTER;
		if(hashstr.equals("ORDER"))
			return Mode.ORDER;
		if(hashstr.equals("REQUEST"))
			return Mode.REQUEST;
		if(hashstr.equals("COMPLAINT"))
			return Mode.COMPLAINT;
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
						try{
							System.out.println(l);
							if(splited.length==3)
							{
								String res=mysql.containsUserLogin(splited[1], splited[2]);
								if(!res.equals("0")){
									synchronized (l) {
//										l.add(splited[1]);
										if(l.contains(splited[1])){
											out.writeObject("-1");
											break;
										}
										else{
											l.add(splited[1]);
											loggeduser=splited[1];
										}
									}
								}
								out.writeObject(res);
								out.flush();
								break;
							}
							else out.writeObject("0");
							out.flush();
							break;
						}catch (Exception e) {
							out.writeObject("0");
							out.flush();
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
						if(splited[1].equals("1"))
						{
							{
								int newid=mysql.insertCasualOrder(splited[1], splited[2], Integer.parseInt(splited[3]), Integer.parseInt(splited[4]),splited[5]
										, splited[6], splited[7], splited[8], splited[9], splited[10],splited[11]);
								if(newid!=0)
								out.writeObject("acceptedorder " + newid);
								else {
									out.writeObject("nospaceorder");
								}
							}
						}
						if(splited[1].equals("2"))
						{
							if(mysql.insertOrder(splited[1], splited[2], Integer.parseInt(splited[3]), Integer.parseInt(splited[4]),splited[5]
									, splited[6], splited[7], splited[8], splited[9], splited[10],splited[11])==true)
							out.writeObject("acceptedorder");
							else {
								out.writeObject("nospaceorder");
							}
						}
						if(splited[1].equals("4")||splited[1].equals("3"))
						{
							if(mysql.insertOrder(splited[1], splited[2], Integer.parseInt(splited[3]), Integer.parseInt(splited[4]),null
									, splited[5], splited[6], splited[7], splited[8], splited[9],splited[10])==true)
							out.writeObject("acceptedorder");
							else {
								out.writeObject("nospaceorder");
							}
						}
						break;
					case UPDATE:
						curstate=Mode.NONE;
						out.writeObject("not yet implemented");
						break;
					case REQUEST:
						String items=null;
						if(splited[1].equals("malls"))
						{
							items=mysql.GetMalls();
							out.writeObject("acceptedrequest"+" "+items);
						}
						else if(splited[1].equals("price"))
						{
							items=mysql.GetPrice(Integer.parseInt(splited[2]));
							out.writeObject("acceptedrequest"+" "+items);
						}
						else if(splited[1].equals("complaints"))
						{
							String[][] sets=mysql.GetComplaints(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("allcomplaints"))
						{
							String[][] sets=mysql.GetAllComplaints();
							out.writeObject(sets);
						}
						else if(splited[1].equals("parking"))
						{
							String[][] sets=mysql.getAvailableParking(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("parkedcars"))
						{
							String[][] sets=mysql.getparkedcars(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("parkmyvehicle"))
						{
							if(splited.length==4){
								out.writeObject(mysql.parksubscribervehicle(splited[2],splited[3]));
							}
							else out.writeObject(mysql.parkvehicle(splited[2]));
						}
						else if(splited[1].equals("unparkmyvehicle"))
						{
							out.writeObject(mysql.unparkvehicle(splited[2], splited[3]));
						}
						else if(splited[1].equals("getmyvehicle"))
						{
//							String[][] sets=mysql.getAvailableParking(splited[2]);
							out.writeObject("no implemento");
						}
						else if(splited[1].equals("cancelableorder"))
						{
							String[][] sets=mysql.getAvailableCancels(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("cancelorder"))
						{
							String sets=mysql.CancelParking(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("changeprice")) // send accept/decline of price
						{
							String sets=mysql.changePrice(splited[2],splited[3],splited[4]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("pricedelete")) // send accept/decline of price
						{
							String sets=mysql.deleteprice(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("pricerequests"))
						{
							String sets=mysql.getAvailableRequests();
							out.writeObject(sets);
						}
						else if(splited[1].equals("myorders"))
						{
							String[][] sets=mysql.getUserOrders(splited[2]);
							out.writeObject(sets);
						}
						else if(splited[1].equals("pricechange"))
						{
							if(mysql.insertPriceChangeRequest(splited[2], splited[3])==true){
							out.writeObject("requestaccepted");
							}
							else{
								out.writeObject("requestfailed");
							}
							break;
						}
						else if(splited[1].equals("parkingspots"))
						{
							if(splited.length!=3)
								out.writeObject(null);
							else{
//								out.writeObject(mysql.getparkingspots(splited[2]));
								String[][] rs = mysql.getparkingspots(splited[2]);
								if(rs==null)
									System.out.println("rs null");
								else out.writeObject(rs);
							}
						}
						
						break;
					case REMOVE:
						curstate=Mode.NONE;
						out.writeObject("not yet implemented");
						break;
					case COMPLAINT:
						String complaint="";// is also a response 
						int i=2;
						do{
							complaint+=splited[i]+' ';
							i++;
						}while(i!=splited.length);
						if(!splited[1].equals("resolve"))
							mysql.addcomplaint(splited[1], complaint);
						else{
							String complaintid=splited[2];
							mysql.addresponse(complaintid,complaint.substring(complaintid.length()+1));
							
						}
						out.writeObject("acceptedcomplaint");
						break;
					default:
						out.writeObject("SERVER:\nDEFAULT\nINVALID CHOICE\n");
						break;
					}
				}catch(SQLException e){
					e.printStackTrace();
					out.writeObject("SERVER EXCEPTION\n"+e.getMessage());
				}
				catch (Exception e) {
					e.printStackTrace();
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
			try {
				System.out.println("in finally, logged user was:"+loggeduser);
				clientsocket.close(); 
				if(loggeduser!=null)
					synchronized (l) {						
						l.remove(loggeduser);
					}
				}
			catch (Exception e ){ ; }
		}
	} 
}