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
	public RoutineJobs(String database, String username, String password) throws SQLException {
		sql= new SQL(database, username, password);
		EN = new EmailNotifications();

	}
	public void run () {
	
		while(true)
		{
			try {
				sleep(minute/60);
//				EN.sendmail("gogosergey@gmail.com", "ONCE PER MINUTE TEST");
				ResultSet rs = sql.getorders();
				while(rs.next()){
					Timestamp arrive = rs.getTimestamp("ArriveTime");
					String wasnotified = rs.getString("werenotified");
//					System.out.println("arrive time is:"+arrive);
					LocalDateTime date = arrive.toLocalDateTime();
					date = date.plusMinutes(5);
//					System.out.println("after 5 min is:"+ date + "cur time is:"+LocalDateTime.now()+" "+ LocalDateTime.now().isAfter(date));
					if(LocalDateTime.now().isAfter(date)&&
							rs.getString("werenotified").equals("0")&&
							rs.getString("parked").equals("0")&&
							Integer.parseInt(rs.getString("Type"))==2){
						System.out.println(rs.getString("username")+"is late!!!, sending email");
						sql.setnotified(rs.getString("ID"));
						EN.sendmail(rs.getString("Email"), "You are late for your spot, 20% price increase.");
					}
					date = date.plusMinutes(25);
					if(LocalDateTime.now().isAfter(date)&&
							rs.getString("Parked").equals("0")&&
							Integer.parseInt(rs.getString("Type"))==2){
						System.out.println("deleting order:"+rs.getString("ID"));
						sql.deleteorder(rs.getString("ID"));
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
