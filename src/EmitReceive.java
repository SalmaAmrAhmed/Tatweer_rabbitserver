import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class EmitReceive {
	
	private static final String U_NAME = "a.madkor";
	private static final String PASSWORD = "P@ssw0rd";
	private static final int PORT = 5672;
	private static final String HOST_NAME = "trafficcounts.tatweer-co.ae";
	
	private static final String ACCIDENTS_EXCHANGE_NAME = "Accidents";
	private static final String COUNTS_EXCHANGE_NAME = "Counts";
	private static final String ALERTS_EXCHANGE_NAME = "Alerts";
	
	private static int allcount = 0;
	private static double allSpeed = 0.0; 
	
	private static ConnectionFactory connectToServer(String uname, String pass, int port, String host) {
		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(uname);
		factory.setPassword(pass);
		factory.setPort(port);
		factory.setHost(host);
		return factory;
	}

	public static void main(String[] argv) throws Exception {
		
		Parser parser = new Parser();
		Map<String, Double> sensors = parser.readFile();
		Set<String> ids = sensors.keySet();

		ConnectionFactory factory = connectToServer(U_NAME, PASSWORD, PORT, HOST_NAME);
		
		Connection connection = factory.newConnection();

		Channel countsChannel = connection.createChannel();
		Channel alertsChannel = connection.createChannel();
		Channel accidentsChannel = connection.createChannel();

		countsChannel.exchangeDeclare(COUNTS_EXCHANGE_NAME, "fanout", true);
		alertsChannel.exchangeDeclare(ALERTS_EXCHANGE_NAME, "fanout", true);
		accidentsChannel.exchangeDeclare(ACCIDENTS_EXCHANGE_NAME, "fanout", true);

		String queueName = countsChannel.queueDeclare().getQueue();
		countsChannel.queueBind(queueName, COUNTS_EXCHANGE_NAME, "");

		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(countsChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				message = message.replaceAll("[\\[\\]]", "");

				System.out.println(" [x] Received '" + message + "'");

				// length > 2 becasue of two square brackets
				if (message.length() > 0) {
					message = message.substring(1, message.length());
					
					if (message.contains("{")) {
						String[] allArray = message.split("\\{");
						int prevCount = allArray.length;
						double prevavgSpeed = 0.0;
						
						for (int i = 0; i < allArray.length; i++) {
							String current = allArray[i];
							current = current.replace("{", "").replace("}", "");
							String[] currentArray = current.split(",");
							
							String sensorID = (currentArray[2].split(":")[1]).replace("\"", "");
							if (ids.contains(sensorID)) {
								
//									for (String j : currentArray) {
//								System.out.println("element: " + j);
//							}
								
							prevavgSpeed += Double.parseDouble(currentArray[8].split(":")[1]);
							
//							System.out.println(sdf.toPattern().toString());
							double speed = Double.parseDouble(currentArray[8].split(":")[1]);
//							System.out.println("speed: " + speed);
							
							if (speed > 145) {
							// edit the alert publish mssg details
								String time = currentArray[3].split(":")[1];
								String vehicleType = currentArray[5].split(":")[1];
								String lane = currentArray[6].split(":")[1];
								String mssg = vehicleType + " in lane number " + lane + " exceed speed 145 at " + time
								+ " on road from dubia to abudhabi";
								alertsChannel.basicPublish(ALERTS_EXCHANGE_NAME, "", null, mssg.getBytes("UTF-8"));
//								System.out.println("alert sent");
							}
							
							prevavgSpeed = prevavgSpeed / prevCount;
							if (Math.abs(prevavgSpeed - allSpeed) > 100 || Math.abs(prevCount - allcount) > 10) {
								String accident = "Accident may have happened";
								accidentsChannel.basicPublish(ACCIDENTS_EXCHANGE_NAME, "", null, accident.getBytes("UTF-8"));
//								System.out.println("Accident sent");
							}
								
							}
						}
						allcount = prevCount;
						allSpeed = prevavgSpeed;
					}
				}

			}
		};
		countsChannel.basicConsume(queueName, true, consumer);

	}
}
