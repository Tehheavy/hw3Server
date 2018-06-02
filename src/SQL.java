
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
	public synchronized  void insert(int num,String from,String to,int distance,String price) throws SQLException {
		// TODO Auto-generated method stub
//		String result ="";
//		ResultSet rs =stmt.executeQuery("SELECT * FROM flights");
		PreparedStatement pstmt = con.prepareStatement("INSERT INTO Employees VALUES (?,?,?,?,?);");
		pstmt.setInt(1, num);
		pstmt.setString(2, from);
		pstmt.setString(3, to);
		pstmt.setInt(4, distance);
		pstmt.setString(5, price);
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
}
