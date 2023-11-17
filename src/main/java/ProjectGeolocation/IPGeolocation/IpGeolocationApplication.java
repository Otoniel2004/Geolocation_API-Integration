package ProjectGeolocation.IPGeolocation;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IpGeolocationApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpGeolocationApplication.class, args);

	String api_key = "7024783793bc4863bb64274dd602c482";
        String inputFileName = "ips";
        String outputFileName = "resultado";

        Map<String, String> ipToCity = new HashMap<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String ipAddress = line.trim();
                String apiUrl = "https://api.ipgeolocation.io/ipgeo?apiKey=" + api_key + "&ip=" + ipAddress;

                HttpGet httpGet = new HttpGet(apiUrl);
                HttpResponse response = httpClient.execute(httpGet);

                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    String city = responseBody.split("\"city\":\"")[1].split("\"")[0];
                    ipToCity.put(ipAddress, city);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (Map.Entry<String, String> entry : ipToCity.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Result written to " + outputFileName);
	}

}
