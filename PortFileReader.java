import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class PortFileReader {
    public static void main(String[] args) {


        String filename = "ports.txt"; // Replace with the path to your file
        Map<String, Integer> portMap = new HashMap<>();

        //Read the file contents in and place in a Map data Structure
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into key-value pairs (e.g., "http=80")
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String serviceName = parts[0].trim();
                    int portNumber = Integer.parseInt(parts[1].trim());

                    // Put the key-value pair into the map
                    portMap.put(serviceName, portNumber);
                }
            }
            // Print the contents of the map
            for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Issue" + e.getMessage());
        }

        //Connect to Redis Database and put Map data in key value
        Jedis jedis = null;
        try {
            jedis = new Jedis("localhost");
            //Write the Map contents to the
            for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
                jedis.set(entry.getKey(), entry.getValue().toString());
            }
            //Read the Redis database contents
            for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
                String value = jedis.get(entry.getKey());
                System.out.println(value);
            }
        } catch (JedisConnectionException e) {
            System.out.println("Could not connect to Redis: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Issue" + e.getMessage());
        } finally {
            jedis.close();  //Always close the connection
        }

    }
}
