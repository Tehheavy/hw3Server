
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.sql.Statement;
import java.util.ArrayList;

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
	public synchronized  void insertOrder2(String type,String username,int ID,int CarID,String Mall,String ArrivalDate,
			String ArrivalTime,String LeaveDate,String LeaveTime,String Email) throws SQLException {
		// TODO Auto-generated method stub
//		String result ="";
//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO ParkingOrders (PersonID,CarID,Type,RequestMall,ArrivalDate,ArrivalTime,LeaveDate,LeaveTime,Email,Username) VALUES (?,?,?,?,?,?,?,?,?,?);");
		pstmt.setInt(1, ID);
		pstmt.setInt(2, CarID);
		pstmt.setInt(3, 2);
		pstmt.setString(4, Mall);
		pstmt.setString(5, ArrivalDate);
		pstmt.setString(6, ArrivalTime);
		pstmt.setString(7, LeaveDate);
		pstmt.setString(8, LeaveTime);
		pstmt.setString(9, Email);
		pstmt.setString(10, username);

		pstmt.executeUpdate();
	}
	public synchronized String GetMalls() throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement pstmt;;
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
}
