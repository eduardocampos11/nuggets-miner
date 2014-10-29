public class NoName {
String str = "12/9/2010 4:39:38 PM";
DateFormat formatter ;
Date date ;
formatter = new SimpleDateFormat("M/dd/yyyy H:m:s a");
date =(Date)formatter.parse(str);             
System.out.println("date printed"+date);

}