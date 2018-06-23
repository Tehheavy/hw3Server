import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public class RoutineJobs  extends Thread  {
	EmailNotifications EN;
	final long minute = 60000; 
	SQL sql;
	public RoutineJobs(EmailNotifications eN) {
		super();
		EN = eN;
	}
	public RoutineJobs() {
		EN = new EmailNotifications();
	}
	/**
	 * Constructor for Routinejobs class,connects to sql server to send emails
	 * @param database the URL of the sql server
	 * @param username
	 * @param password
	 * @throws SQLException
	 */
	public RoutineJobs(String database, String username, String password) throws SQLException {
		sql= new SQL(database, username, password);
		EN = new EmailNotifications();

	}
	public void run () {
//		System.out.println("sending msg");
//	EN.sendmail("gogosergey@gmail.com","server started");
		while(true)
		{
			try {
				sleep(minute/30);
//				EN.sendmail("gogosergey@gmail.com", "ONCE PER MINUTE TEST");
				ResultSet rs = sql.getorders();
				while(rs.next()){
					Timestamp arrive = rs.getTimestamp("ArriveTime");
					String wasnotified = rs.getString("werenotified");
					Timestamp leavetime = rs.getTimestamp("LeaveTime");
					String ordertype = rs.getString("Type");
					LocalDateTime date = arrive.toLocalDateTime();
					date = date.plusMinutes(5);
					if(LocalDateTime.now().isAfter(date)&&
							rs.getString("werenotified").equals("0")&&
							rs.getString("parked").equals("0")&&
							Integer.parseInt(rs.getString("Type"))==2){
						System.out.println(rs.getString("username")+"is late!!!, sending email");
						Float price=rs.getFloat("Price"); //alex added
						price=(float) (price*1.2);
						System.out.println("new price after 20% is "+price);
						String id=rs.getString("ID"); // alex added
						sql.setnotified(id); // changed to id variable from rs.getstring id
						sql.setprice(id, price);
						EN.sendmail(rs.getString("Email"), "You are late for your spot, 20% price increase.");
					}
					date = date.plusMinutes(25);
					if(LocalDateTime.now().isAfter(date)&&
							rs.getString("Parked").equals("0")&&
							Integer.parseInt(rs.getString("Type"))==2){
						System.out.println("deleting order: "+rs.getString("ID"));
						sql.deleteorder(rs.getString("ID"));
					}
					
					if(Integer.parseInt(ordertype)>2){
						LocalDateTime subscriberdate = leavetime.toLocalDateTime();
						subscriberdate.minusDays(7);
						if(LocalDateTime.now().isAfter(subscriberdate)){
							EN.sendmail(rs.getString("Email"),"Your parkinglib subscription ends in 7 days, remember to re-subscribe.");
						}
					}
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
