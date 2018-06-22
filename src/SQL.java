
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
//import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Data;



public class SQL {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static String DB_URL = "JDBC:MYSQL://cs.telhai.ac.il/Group_2";

	//	private Statement stmt;
	private Connection con;

	public SQL(String DB_URL,String username,String password) throws SQLException{
		con = DriverManager.getConnection(DB_URL,username,password);


	}
	public synchronized  void insert(String from,String to) throws SQLException {
		// TODO Auto-generated method stub
		//		String result ="";
		//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO Users VALUES (?,?,?);");
		pstmt.setString(1, from);
		pstmt.setString(2, to);
		pstmt.setInt(3, 1);

		pstmt.executeUpdate();
	}
	public synchronized  String select(String num) throws SQLException {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		PreparedStatement pstmt;
		int number;
		if(num.equals("*"))
			pstmt = con.prepareStatement("SELECT * FROM Employees");
		else {
			number = Integer.parseInt(num);
			pstmt = con.prepareStatement("SELECT * FROM Employees WHERE ID = ?");
			pstmt.setInt(1, number);
		}
		String result ="";
		//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				result+=rsmd.getColumnName(i)+":"+rs.getString(i)+" ";
				//				System.out.println(rs.getString(i));
			}
			result+="\n";

		}
		rs.close();
		//		System.out.println("results is:"+result);
		return result;
	}
	public synchronized  String containsUserLogin(String num,String pass) throws SQLException {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		PreparedStatement pstmt;;
		pstmt = con.prepareStatement("SELECT * FROM Users WHERE Username = ? and Password = ?");
		pstmt.setString(1, num);
		pstmt.setString(2, pass);
		String result ="0";
		//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		ResultSet rs = pstmt.executeQuery();
		if(rs.absolute(1))
		{
			result=Integer.toString(rs.getInt(3));
			rs.close();

		}
		return result;
		//		System.out.println("results is:"+result);

	}
	public synchronized  boolean containsUser(String num) throws SQLException {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		PreparedStatement pstmt;;
		pstmt = con.prepareStatement("SELECT * FROM Users WHERE Username = ?");
		pstmt.setString(1, num);
		String result ="";
		//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		ResultSet rs = pstmt.executeQuery();
		if(rs.absolute(1))
		{
			rs.close();
			return true;

		}
		return false;
		//		System.out.println("results is:"+result);

	}
	public synchronized  boolean insertOrder(String type,String username,int ID,int CarID,String Mall,String ArrivalDate,
			String ArrivalTime,String LeaveDate,String LeaveTime,String Email,String Price) throws SQLException {
		// TODO Auto-generated method stub
		//		String result ="";
		//		ResultSet available =stmt.executeQuery("SELECT * FROM flights");



		LocalDateTime testArrive=LocalDateTime.parse(ArrivalDate.toString()+"T"+ArrivalTime.toString());
		LocalDateTime testLeave=LocalDateTime.parse(LeaveDate.toString()+"T"+LeaveTime.toString());//.toString().substring(11, 16)
		Timestamp time1 = Timestamp.valueOf(testArrive);
		Timestamp time2 = Timestamp.valueOf(testLeave);
		if(type.equals("2")||type.equals("1")){
			String test1="call checkavailability(TIMESTAMP('"+time1.toString().substring(0,10)+"', '"+time1.toString().substring(11, 19)+"'),TIMESTAMP('"+time2.toString().substring(0,9)+"', '"+time2.toString().substring(11, 18)+"'),\""+Mall+"\");";
			System.out.println(test1);
			CallableStatement cs = this.con.prepareCall("{call checkavailability(?,?,?)}");
			cs.setTimestamp(1, time1);
			cs.setTimestamp(2, time2);
			cs.setString(3, Mall);
			ResultSet rs = cs.executeQuery();
			int count=0;
			while(rs.next()){
				count++;
			}

			PreparedStatement pstmt3;
			pstmt3 = con.prepareStatement("SELECT * FROM Malls Where mallname = ?");
			pstmt3.setString(1, Mall);
			ResultSet rs2 = pstmt3.executeQuery();
			int space=0;
			if(rs2.next())
			{
				space=rs2.getInt(2);
			}
			System.out.println("Mall space is:"+space+" and number of conflicts is:"+count);
			if(count>=space)
			{
				System.out.println("ORDER CANNOT BE ACCOMPLISHED , NO SPACE IN: "+Mall);
				return false;
			}
		}
		//    	//call checkavailability(TIMESTAMP('2018-06-16', '01:00:00'),TIMESTAMP('2018-06-16','03:00:00'),"KoKoLand");

		PreparedStatement pstmt = con.prepareStatement("INSERT INTO ParkingOrders (PersonID,CarID,Type,RequestMall,ArriveTime,LeaveTime,Email,Username,Price,Parked) VALUES (?,?,?,?,?,?,?,?,?,?);",Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, ID);
		pstmt.setInt(2, CarID);
		pstmt.setInt(3, Integer.parseInt(type));
		pstmt.setString(4, Mall);
		//		pstmt.setTimestamp(5, Timestamp.valueOf(ArrivalDate + " " + ArrivalTime));
		//		pstmt.setString(5, ArrivalDate);
		pstmt.setTimestamp(5, time1);
		pstmt.setTimestamp(6, time2);
		pstmt.setString(7, Email);
		pstmt.setString(8, username);
		pstmt.setString(9, Price);
		pstmt.setBoolean(10, false);
		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();
		keys.next();
		int id = keys.getInt(1);
		System.out.println("id of order is"+id);
		try{
			PreparedStatement pstmt2 = con.prepareStatement("INSERT INTO Cars (carid,parked,username) VALUES (?,?,?);");
			pstmt2.setInt(1, CarID);
			pstmt2.setBoolean(2, false);
			pstmt2.setString(3, username);
			pstmt2.executeUpdate();
		}
		catch(Exception e){
			System.out.println("Car already in database");
		}
		return true;
	}
	public synchronized  int insertCasualOrder(String type,String username,int ID,int CarID,String Mall,String ArrivalDate,
			String ArrivalTime,String LeaveDate,String LeaveTime,String Email,String Price) throws SQLException {
		// TODO Auto-generated method stub
		//		String result ="";
		//		ResultSet available =stmt.executeQuery("SELECT * FROM flights");



		LocalDateTime testArrive=LocalDateTime.parse(ArrivalDate.toString()+"T"+ArrivalTime.toString());
		LocalDateTime testLeave=LocalDateTime.parse(LeaveDate.toString()+"T"+LeaveTime.toString());//.toString().substring(11, 16)
		Timestamp time1 = Timestamp.valueOf(testArrive);
		Timestamp time2 = Timestamp.valueOf(testLeave);
		if(type.equals("2")||type.equals("1")){
			String test1="call checkavailability(TIMESTAMP('"+time1.toString().substring(0,10)+"', '"+time1.toString().substring(11, 19)+"'),TIMESTAMP('"+time2.toString().substring(0,9)+"', '"+time2.toString().substring(11, 18)+"'),\""+Mall+"\");";
			System.out.println(test1);
			CallableStatement cs = this.con.prepareCall("{call checkavailability(?,?,?)}");
			cs.setTimestamp(1, time1);
			cs.setTimestamp(2, time2);
			cs.setString(3, Mall);
			ResultSet rs = cs.executeQuery();
			int count=0;
			while(rs.next()){
				count++;
			}

			PreparedStatement pstmt3;
			pstmt3 = con.prepareStatement("SELECT * FROM Malls Where mallname = ?");
			pstmt3.setString(1, Mall);
			ResultSet rs2 = pstmt3.executeQuery();
			int space=0;
			if(rs2.next())
			{
				space=rs2.getInt(2);
			}
			System.out.println("Mall space is:"+space+" and number of conflicts is:"+count);
			if(count>=space)
			{
				System.out.println("ORDER CANNOT BE ACCOMPLISHED , NO SPACE IN: "+Mall);
				return 0;
			}
		}
		//    	//call checkavailability(TIMESTAMP('2018-06-16', '01:00:00'),TIMESTAMP('2018-06-16','03:00:00'),"KoKoLand");

		PreparedStatement pstmt = con.prepareStatement("INSERT INTO ParkingOrders (PersonID,CarID,Type,RequestMall,ArriveTime,LeaveTime,Email,Username,Price,Parked) VALUES (?,?,?,?,?,?,?,?,?,?);",Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, ID);
		pstmt.setInt(2, CarID);
		pstmt.setInt(3, Integer.parseInt(type));
		pstmt.setString(4, Mall);
		//		pstmt.setTimestamp(5, Timestamp.valueOf(ArrivalDate + " " + ArrivalTime));
		//		pstmt.setString(5, ArrivalDate);
		pstmt.setTimestamp(5, time1);
		pstmt.setTimestamp(6, time2);
		pstmt.setString(7, Email);
		pstmt.setString(8, username);
		pstmt.setString(9, Price);
		pstmt.setBoolean(10, false);
		pstmt.executeUpdate();
		ResultSet keys = pstmt.getGeneratedKeys();
		keys.next();
		int id = keys.getInt(1);
		System.out.println("id of order is"+id);
		try{
			PreparedStatement pstmt2 = con.prepareStatement("INSERT INTO Cars (carid,parked,username) VALUES (?,?,?);");
			pstmt2.setInt(1, CarID);
			pstmt2.setBoolean(2, false);
			pstmt2.setString(3, username);
			pstmt2.executeUpdate();
		}
		catch(Exception e){
			System.out.println("Car already in database");
		}
		return id;
	}
	
	public synchronized String GetMalls() throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement pstmt;
		pstmt = con.prepareStatement("SELECT * FROM Malls");
		String result ="";
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();

		String value=null;
		ArrayList<String> columns = new ArrayList<String>();
		String columnName = metadata.getColumnName(1);
		while (rs.next()) {
			if(value==null)
				value=rs.getString(columnName);
			else
				value = value+" "+rs.getString(columnName);
			System.out.println(value);
		}


		return value;
		//		System.out.println("results is:"+result);

	}
	public synchronized String GetPrice(int type) throws SQLException {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		PreparedStatement pstmt;;
		pstmt = con.prepareStatement("SELECT * FROM Prices Where parkingtype = ?");
		pstmt.setInt(1, type);
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData metadata = rs.getMetaData();
		int columnCount = metadata.getColumnCount();

		//         String malls = null; 
		//         for (int i = 1; i < columnCount; i++) {
		//             String columnName = metadata.getColumnName(i);
		//             malls=malls+" "+columnName;
		//         }
		float value = 0;
		String columnName = metadata.getColumnName(2);
		while (rs.next()) {
			value=rs.getFloat(columnName);
		}


		return String.valueOf(value);
		//		System.out.println("results is:"+result);

	}

	public synchronized String[][] GetComplaints(String User) {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		try{
			PreparedStatement pstmt;;
			pstmt = con.prepareStatement("SELECT * FROM Complaints Where username = ?");
			pstmt.setString(1, User);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String[]> data=new ArrayList<String[]>();
			while(rs.next())
			{
				System.out.println(rs.getInt(1)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+String.valueOf(rs.getString(6)));
				String[] temp= new String[5];
				temp[0]=String.valueOf(rs.getInt(1));
				temp[1]=rs.getString(3);
				temp[2]=rs.getString(4);
				temp[3]=rs.getString(5);
				temp[4]=String.valueOf(rs.getString(6));
				//data.add(new ComplaintHolder(String.valueOf(rs.getInt(1)),rs.getString(3),rs.getString(4),rs.getString(5),String.valueOf(rs.getString(6))));
				data.add(temp);
			}
			String[][] values =new String[data.size()][5];
			for(int i=0;i<data.size();i++)
			{
				values[i][0]=data.get(i)[0];
				values[i][1]=data.get(i)[1];
				values[i][2]=data.get(i)[2];
				values[i][3]=data.get(i)[3];
				values[i][4]=data.get(i)[4];

			}
			return values;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		//		System.out.println("results is:"+result);

	}
	public synchronized String[][] GetAllComplaints() {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		try{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM Complaints");
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String[]> data=new ArrayList<String[]>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnamount=rsmd.getColumnCount();
			while(rs.next())
			{
				System.out.println(rs.getInt(1)+" "+rs.getString(2)+" "+rs.getString(3)+" "+rs.getString(4)+" "+rs.getString(5)+" "+String.valueOf(rs.getString(6)));
				String[] temp= new String[columnamount];
				for(int i=0;i<columnamount;i++)
					temp[i]=rs.getString(i+1);
				data.add(temp);
			}
			String[][] values =new String[data.size()][columnamount];
			for(int i=0;i<data.size();i++)
			{
				for(int j=0;j<columnamount;j++){
					values[i][j]=data.get(i)[j];
				}
			}
			return values;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		//		System.out.println("results is:"+result);

	}

	public synchronized  void addcomplaint(String username,String complaint) throws SQLException {
		// TODO Auto-generated method stub
		//		String result ="";
		//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO Complaints(username,complaint)"
				+ " VALUES (?,?)");
		pstmt.setString(1, username);
		pstmt.setString(2, complaint);
		//		pstmt.setTimestamp(3,new java.sql.Timestamp( (new java.util.Date()).getTime() ));

		pstmt.executeUpdate();
		System.out.println("added complaint");
	}
	public void addresponse(String complaintid, String complaint) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println(complaint);
		PreparedStatement pstmt;;
		pstmt = con.prepareStatement("UPDATE Complaints"
				+ " SET response=?"
				+ " WHERE ID=?");
		pstmt.setString(2, complaintid);
		pstmt.setString(1, complaint);
		//		System.out.println(pstmt.toString());
		pstmt.executeUpdate();
		System.out.println("added response");

	}




	public synchronized String[][] getAvailableParking(String username) {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		try{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM ParkingOrders WHERE ArriveTime<CURRENT_TIMESTAMP() AND LeaveTime>CURRENT_TIMESTAMP() AND Parked=0 AND Username=?");
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String[]> data=new ArrayList<String[]>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnamount=rsmd.getColumnCount();
			while(rs.next())
			{
				System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+
						rs.getString(7)+" "+rs.getInt(8)+" "+rs.getTimestamp(9).toString()+" "+rs.getTimestamp(10).toString());
				String[] temp= new String[columnamount];
				for(int i=0;i<columnamount;i++)
					temp[i]=rs.getString(i+1);
				data.add(temp);
			}
			String[][] values =new String[data.size()][columnamount];
			for(int i=0;i<data.size();i++)
			{
				for(int j=0;j<columnamount;j++){
					values[i][j]=data.get(i)[j];
				}
			}
			return values;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		//		System.out.println("results is:"+result);

	}
	public synchronized  String parkvehicle(String orderid) throws SQLException {
		// TODO Auto-generated method stub
		//		String result ="";
		PreparedStatement pstmt,pstmt2,pstmt3,pstmt4;
		pstmt = con.prepareStatement("SELECT * FROM ParkingOrders where ID = ? and Parked = 0");
		pstmt.setString(1, orderid);
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		String mallname,personid,carid,username;
		if(rs.next()){
			System.out.println("personid:"+rs.getString(2)+ " cardid:"+rs.getString(3) + " username:"+ rs.getString(7) + " mall is:"+ rs.getString(5));
			personid=rs.getString(2);
			carid=rs.getString(3);
			username=rs.getString(7);
			mallname=rs.getString(5);
			System.out.println("b4 pstmt2");
			System.out.println("b4 pstmt2clos mallnaem is:"+mallname);

			pstmt2 = con.prepareStatement("SELECT * FROM ParkingSpots2 WHERE `mallname` = ? and username IS NULL");
			System.out.println("b4 pstmt22");
			pstmt2.setString(1,mallname);
			ResultSet rs2 = pstmt2.executeQuery();
			if(rs2.next()){
				String id = rs2.getString(1);
				System.out.println("b4 pstmt3 ID IS:"+id);
				pstmt3=con.prepareStatement("UPDATE `ParkingSpots2` SET `Carid` = ?, `username` = ? WHERE `ID` = ?;");
				
				System.out.println("b4 pstmt4");
				pstmt3.setInt(1, Integer.parseInt(carid));
				pstmt3.setString(2, username);
				pstmt3.setInt(3, Integer.parseInt(id));
				System.out.println("b4 pstmt5");
				pstmt3.executeUpdate();
				System.out.println("b4 pstmt6");
				
				pstmt4=con.prepareStatement("UPDATE `ParkingOrders` SET `Parked` = 1 WHERE `ID` = ?;");
				pstmt4.setString(1, orderid);
				pstmt4.executeUpdate();
				return "accepted";
				
			}
			else{
				System.out.println("no kwery");
				return "declined";
			}
		}
		else {
			System.out.println("uhoh");
			return "declined";
		}
		
	}

	public synchronized  String parksubscribervehicle(String orderid,String mall) throws SQLException {
		// TODO Auto-generated method stub
		//		String result ="";
		PreparedStatement pstmt,pstmt2,pstmt3,pstmt4;
		pstmt = con.prepareStatement("SELECT * FROM ParkingOrders where ID = ? and Parked = 0");
		pstmt.setString(1, orderid);
		ResultSet rs = pstmt.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		String mallname,personid,carid,username;
		if(rs.next()){
			System.out.println("personid:"+rs.getString(2)+ " cardid:"+rs.getString(3) + " username:"+ rs.getString(7));
			personid=rs.getString(2);
			carid=rs.getString(3);
			username=rs.getString(7);
			System.out.println("b4 pstmt2");

			pstmt2 = con.prepareStatement("SELECT * FROM ParkingSpots2 WHERE `mallname` = ? and username IS NULL");
			System.out.println("b4 pstmt22");
			pstmt2.setString(1,mall);
			ResultSet rs2 = pstmt2.executeQuery();
			if(rs2.next()){
				String id = rs2.getString(1);
				System.out.println("b4 pstmt3 ID IS:"+id);
				pstmt3=con.prepareStatement("UPDATE `ParkingSpots2` SET `Carid` = ?, `username` = ? WHERE `ID` = ?;");
				
				System.out.println("b4 pstmt4");
				pstmt3.setInt(1, Integer.parseInt(carid));
				pstmt3.setString(2, username);
				pstmt3.setInt(3, Integer.parseInt(id));
				System.out.println("b4 pstmt5");
				pstmt3.executeUpdate();
				System.out.println("b4 pstmt6");
				
				pstmt4=con.prepareStatement("UPDATE `ParkingOrders` SET `Parked` = 1 WHERE `ID` = ?;");
				pstmt4.setString(1, orderid);
				pstmt4.executeUpdate();
				return "accepted";
				
			}
			else{
				System.out.println("no kwery");
				return "declined";
			}
		}
		else {
			System.out.println("uhoh");
			return "declined";
		}
	}
		
	public synchronized  boolean insertPriceChangeRequest(String type,String price){
		// TODO Auto-generated method stub
		//		String result ="";
		//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		try{
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO PriceChange (parkingtype,newprice) VALUES (?,?);");
		pstmt.setInt(1, Integer.parseInt(type));
		pstmt.setFloat(2, Float.parseFloat(price));

		pstmt.executeUpdate();
		return true;
		}
		catch(Exception ex){
			return false;
		}
	}
	public synchronized boolean isparked(String carid) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ParkingSpots2 WHERE Carid = ?");
		pstmt.setString(1, carid);
		ResultSet  rs = pstmt.executeQuery();
		if(rs.next())
			return true;
		else return false;
	}
	public synchronized String[][] getparkedcars(String username) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ParkingOrders WHERE Parked=1 AND Username=?");
		pstmt.setString(1, username);
		ResultSet rs = pstmt.executeQuery();
		ArrayList<String[]> data=new ArrayList<String[]>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnamount=rsmd.getColumnCount();
		while(rs.next())
		{
			System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+
					rs.getString(7)+" "+rs.getInt(8)+" "+rs.getTimestamp(9).toString()+" "+rs.getTimestamp(10).toString());
			String[] temp= new String[columnamount];
			for(int i=0;i<columnamount;i++)
				temp[i]=rs.getString(i+1);
			data.add(temp);
		}
		String[][] values =new String[data.size()][columnamount];
		for(int i=0;i<data.size();i++)
		{
			for(int j=0;j<columnamount;j++){
				values[i][j]=data.get(i)[j];
			}
		}
		return values;
	}
	
	public synchronized String unparkvehicle(String carid,String username) throws SQLException {
		PreparedStatement pstmt,pstmt2,pstmt3;
		pstmt = con.prepareStatement("UPDATE ParkingSpots2"
				+ " SET username=NULL , Carid=NULL"
				+ " WHERE Carid = ? AND username = ?" );
		pstmt.setString(1, carid);
		pstmt.setString(2, username);
		System.out.println(pstmt.toString());
		pstmt.executeUpdate();
		
		pstmt = con.prepareStatement("SELECT * FROM ParkingOrders WHERE CarID='"+carid+"' AND Username='"+username+"'");
		System.out.println(pstmt.toString());
		ResultSet rs= pstmt.executeQuery();
		int type=3;
		String orderid=null;
		float price = 0;
		while(rs.next()) {
			type=Integer.parseInt(rs.getString("Type"));
			orderid=rs.getString("ID");
			System.out.println("we got:~~~~~~~~~~~~~~~~~"+String.valueOf(type));
		}
		//if he is a subscriber do:
		if(type>2) {
			
			pstmt2 = con.prepareStatement("UPDATE ParkingOrders"
					+ " SET Parked = '0'"
					+ " WHERE CarID = ? AND Username = ? AND Parked = '1'");
			pstmt2.setString(1, carid);
			pstmt2.setString(2, username);
			pstmt2.executeUpdate();
				
		}
		else if(orderid!=null) {
			pstmt3 = con.prepareStatement("SELECT * FROM ParkingOrders WHERE ID =?");
			pstmt3.setInt(1, Integer.parseInt(orderid));
			ResultSet rs3 = pstmt3.executeQuery();
			if(rs3.next())
				price = rs3.getFloat(8);
			System.out.println("Price of leaving parking is: "+price);
			pstmt2 = con.prepareStatement("DELETE FROM ParkingOrders "+ 
					"WHERE ID=?");
			pstmt2.setString(1, orderid);
			pstmt2.executeUpdate();
		}
		
		return String.valueOf(price);

	}

	public synchronized String[][] getAvailableCancels(String username) {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		try{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM ParkingOrders WHERE ArriveTime>CURRENT_TIMESTAMP() AND Type=2 AND Username=?");
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String[]> data=new ArrayList<String[]>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnamount=rsmd.getColumnCount();
			while(rs.next())
			{
				System.out.println(rs.getInt(1)+" "+rs.getInt(2)+" "+rs.getInt(3)+" "+rs.getInt(4)+" "+rs.getString(5)+" "+rs.getString(6)+" "+
						rs.getString(7)+" "+rs.getInt(8)+" "+rs.getTimestamp(9).toString()+" "+rs.getTimestamp(10).toString());
				String[] temp= new String[columnamount];
				for(int i=0;i<columnamount;i++)
					temp[i]=rs.getString(i+1);
				data.add(temp);
			}
			String[][] values =new String[data.size()][columnamount];
			for(int i=0;i<data.size();i++)
			{
				for(int j=0;j<columnamount;j++){
					values[i][j]=data.get(i)[j];
				}
			}
			return values;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		//		System.out.println("results is:"+result);

	}
	public synchronized String CancelParking(String id) throws SQLException {
		PreparedStatement pstmt,pstmt2,pstmt3;
		pstmt3 = con.prepareStatement("SELECT * FROM ParkingOrders WHERE ID =?");
		pstmt3.setInt(1, Integer.parseInt(id));
		ResultSet rs3 = pstmt3.executeQuery();
		Float price=(float) 0;
		if(rs3.next())
			price = rs3.getFloat(8);
		pstmt2 = con.prepareStatement("DELETE FROM ParkingOrders "+ 
				"WHERE ID=?");
		pstmt2.setInt(1, Integer.parseInt(id));
		pstmt2.executeUpdate();
		
		return String.valueOf(price);

	}
	
	public synchronized String getAvailableRequests() throws SQLException {
		PreparedStatement pstmt,pstmt2,pstmt3;
		pstmt3 = con.prepareStatement("SELECT * FROM PriceChange");
		ResultSet rs3 = pstmt3.executeQuery();
		String s=null;
		while(rs3.next())
		{
			if(s==null)
				s=rs3.getString(1)+" "+rs3.getString(2)+" "+rs3.getString(3);
			else
			s=s+" "+rs3.getString(1)+" "+rs3.getString(2)+" "+rs3.getString(3);
		}
		
		return s;

	}
	public synchronized ResultSet getorders() throws SQLException{
		PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ParkingOrders");
		return pstmt.executeQuery();
	}
	public void setnotified(String orderid) throws SQLException {
		PreparedStatement pstmt;
		pstmt = con.prepareStatement("UPDATE ParkingOrders"
				+ " SET werenotified='1'"
				+ " WHERE ID = ?" );
		pstmt.setString(1, orderid);
		pstmt.executeUpdate();
	}
	public void setprice(String orderid , float price) throws SQLException {
		PreparedStatement pstmt;
		pstmt = con.prepareStatement("UPDATE ParkingOrders"
				+ " SET Price=?"
				+ " WHERE ID = ?" );
		pstmt.setFloat(1, price);
		pstmt.setString(2, orderid);
		pstmt.executeUpdate();
	}
	public void deleteorder(String orderid) throws SQLException {
		PreparedStatement pstmt = con.prepareStatement("DELETE FROM ParkingOrders "+ 
				"WHERE ID=?");
		pstmt.setString(1, orderid);
		pstmt.executeUpdate();
		
	}
	public synchronized String changePrice(String str1,String str2,String str3) throws SQLException {
		PreparedStatement pstmt,pstmt2;
		pstmt = con.prepareStatement("UPDATE Prices"
				+ " SET parkingtype=? , price=?"
				+ " WHERE parkingtype = ?" );
		pstmt.setInt(1, Integer.parseInt(str2));
		pstmt.setFloat(2, Float.parseFloat(str3));
		pstmt.setInt(3, Integer.parseInt(str2));
		pstmt.executeUpdate();
		pstmt2 = con.prepareStatement("DELETE FROM PriceChange "+ 
				"WHERE ID=?");
		pstmt2.setInt(1, Integer.parseInt(str1));
		pstmt2.executeUpdate();
		return "accepted";
	}
	public synchronized String deleteprice(String str1) throws SQLException {
		PreparedStatement pstmt2;
		pstmt2 = con.prepareStatement("DELETE FROM PriceChange "+ 
				"WHERE ID=?");
		pstmt2.setInt(1, Integer.parseInt(str1));
		pstmt2.executeUpdate();
		return "accepted";
	}
	
	public synchronized String[][] getUserOrders(String str1) {
		// TODO Auto-generated method stub
		//		stmt = con.createStatement();
		try{
			PreparedStatement pstmt;
			pstmt = con.prepareStatement("SELECT * FROM ParkingOrders Where username=?");
			pstmt.setString(1, str1);
			ResultSet rs = pstmt.executeQuery();
			ArrayList<String[]> data=new ArrayList<String[]>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnamount=5;
			while(rs.next())
			{
				System.out.println(rs.getString(1)+" "+rs.getString(9)+" "+rs.getString(10)+" "+rs.getString(5)+" "+rs.getString(8));
				String[] temp= new String[5];
				temp[0]=rs.getString(1);
				temp[1]=rs.getString(9);
				temp[2]=rs.getString(10);
				temp[3]=rs.getString(5);
				temp[4]=rs.getString(8);
				data.add(temp);
			}
			String[][] values =new String[data.size()][columnamount];
			for(int i=0;i<data.size();i++)
			{
				for(int j=0;j<columnamount;j++){
					values[i][j]=data.get(i)[j];
				}
			}
			return values;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
		//		System.out.println("results is:"+result);
	}

	//	7:42 PM - Eric Freeman: call checkavailability(TIMESTAMP('2018-06-16', '01:00:00'),TIMESTAMP('2018-06-16','03:00:00'),"KoKoLand");
	//	7:43 PM - Eric Freeman: select * from ParkingOrders 
	//	where RequestMall = mallname AND
	//	(arrive < LeaveTime AND
	//	leavet > ArriveTime) OR
	//
	//	(arrive < LeaveTime AND
	//	leavet >= LeaveTime) OR
	//
	//	(arrive <= ArriveTime AND
	//	leavet > ArriveTime)




}
