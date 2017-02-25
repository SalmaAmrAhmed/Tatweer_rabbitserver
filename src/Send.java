//import com.rabbitmq.client.ConnectionFactory;
//import com.rabbitmq.client.Connection;
//
//import java.util.Scanner;
//import java.util.concurrent.TimeoutException;
//
//import com.rabbitmq.client.Channel;
//
//
//
//public class Send {
//	 private final static String QUEUE_NAME = "hello";
//	 
//	 
//	 private static String getMessage(String[] strings){
//		    if (strings.length < 1)
//		        return "Hello World!";
//		    return joinStrings(strings, " ");
//		}
//
//		private static String joinStrings(String[] strings, String delimiter) {
//		    int length = strings.length;
//		    if (length == 0) return "";
//		    StringBuilder words = new StringBuilder(strings[0]);
//		    for (int i = 1; i < length; i++) {
//		        words.append(delimiter).append(strings[i]);
//		    }
//		    return words.toString();
//		}
//
//	  public static void main(String[] argv)
//	      throws java.io.IOException, TimeoutException {
//		  
//		  	//creates connection 
//		  	ConnectionFactory factory = new ConnectionFactory();
//		  	factory.setHost("localhost");
//		  	// factory.setHost("http://trafficcounts.tatweer-co.ae:15672/#/exchanges");
//		    Connection connection = factory.newConnection();
//		    Channel channel = connection.createChannel();
//		    
//		    //declaring queue
//		    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//		    Scanner sc = new Scanner(System.in);
//		    while (true) {
//		    	String message = sc.nextLine();
//		    	channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//		    	System.out.println(" [x] Sent '" + message + "'");
//		    }
//		    
//		    
//		   // close connection
//		   // channel.close();
//		   // connection.close();
//	  }
//}
