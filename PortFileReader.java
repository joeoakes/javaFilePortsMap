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
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the contents of the map
        for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }

        try {
            Jedis jedis = new Jedis("localhost");

            for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
                 //Create (Set a key-value pair)
                jedis.set(entry.getKey(), entry.getValue().toString());
            }

            for (Map.Entry<String, Integer> entry : portMap.entrySet()) {
                //Read (Get the value of a key)
                String value = jedis.get(entry.getKey());
                System.out.println(value);
            }
            jedis.close();
        } catch (JedisConnectionException e) {
            System.out.println("Could not connect to Redis: " + e.getMessage());
        }

    }
}
