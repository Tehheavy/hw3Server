

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class RequestHander extends Thread {
 Socket clientsocket;
 Mode curstate = Mode.NONE;
 int portNumber=4568;
 SQL mysql;
 public RequestHander(Socket client,String database,String username,String password) throws SQLException {
  super();
  clientsocket=client;
  mysql = new SQL(database,username,password);

 }
 public enum Mode {
  NONE,LOGIN,SELECT,UPDATE,REMOVE
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
   String response = "Press 1 to insert to insert into database"
     + "\n2 to select from database\n3 to update database\n4 to remove from database";
   out.writeObject("SERVER: WELCOME TO THE EMPLOYEE DATABASE.\nPress 1 to insert to insert into the EMPLOYEES database"
     + "\n2 to select from database\n3 to update database\n4 to remove from database");
   while ((inputLine = in.readLine()) != null) {

     try{

      Mode requestmode = Mode.values()[Integer.parseInt(inputLine)];
      switch(requestmode){
      case LOGIN:
       curstate=Mode.LOGIN;
       out.writeObject("acceptedlogin"); //user received message for attempting to log in
       out.flush();
       try{
       inputLine = in.readLine();
        String id=inputLine;
        out.writeObject("acceptedlogin2");
        inputLine = in.readLine();
        String pass=inputLine;
        out.writeObject("acceptedlogin3");
     //   out.writeObject("PASSWORD=");
       // String to=in.readLine(); 
      //  mysql.insert(id, from, to, distance, price);
        results="successfully inserted the Employee datebase.";
       }
//       catch (SQLException e) {
//        results="Error fulfilling insert request.";
//       }
       catch (NumberFormatException e) {
        results="please enter a number next time.";
       }finally {
        out.writeObject(results +"\n"+response);
       }
       break;
      case SELECT:
       curstate=Mode.NONE;
       out.writeObject("Please enter Employee ID, or * if to see all of them.");
       out.flush();
       inputLine = in.readLine();
       try {
        results=mysql.select(inputLine);
       } catch (SQLException e) {
        // TODO Auto-generated catch block
        //      e.printStackTrace();
        results="Error fulfilling request.";
       }catch (NumberFormatException e) {
        results="please enter a number next time.";
       }finally {

        //       out.println(inputLine+results );
        out.writeObject(results +"\n"+response);
        //         out.flush();
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
       out.writeObject("SERVER:\nDEFAULT\nINVALID CHOICE\nPress 1 to insert to insert into database"
         + "\n2 to select from database\n3 to update database\n4 to remove from database");
       break;
      }
     }catch (Exception e) {
      out.writeObject("SERVER EXCEPTION\nINVALID CHOICE\nPress 1 to insert to insert into database"
        + "\n2 to select from database\n3 to update database\n4 to remove from database");
//      break;
     }
     
    //      System.out.println(inputLine);
    //      out.println(inputLine+" selected mode is:"+curstate.name());
     out.flush();
   }
   System.out.println(inputLine);
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