
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
//import java.sql.Statement;

public class SQL {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static String DB_URL = "JDBC:MYSQL://cs.telhai.ac.il/studentDB_cs313313991";

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
		pstmt.setBoolean(3, false);

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
	public synchronized  boolean containsUserLogin(String num,String pass) throws SQLException {
		// TODO Auto-generated method stub
//		stmt = con.createStatement();
		PreparedStatement pstmt;;
			pstmt = con.prepareStatement("SELECT * FROM Users WHERE Username = ? and Password = ?");
			pstmt.setString(1, num);
			pstmt.setString(2, pass);
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
}
