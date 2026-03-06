package it.uniroma1.ingestion;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class DiscoveryClient {
    private static final String BASE_URL = "http://localhost:8080/api";

    @GetMapping("/discovery/rest_sensors")
    public List<RestSensor> getDiscoveryRest_sensors() throws Exception {
        
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL(BASE_URL + "/discovery");
        
        try(InputStream input = url.openStream()){
        
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);

            return dr.getRest_sensors();
        }
    }
    
    @GetMapping("/discovery/pubsub_streams")
    public List<PubsubStream> getDiscoveryPubsub_streams() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL(BASE_URL + "/discovery");

        try(InputStream input = url.openStream()){
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);
            return dr.getPubsub_streams();
        }
    }

    @GetMapping("/discovery/rest_sensors/{sensor_id}")
	public String getRest_sensor(@PathVariable String sensor_id) {
        ObjectMapper mapper = new ObjectMapper();
        URL url = new URL(BASE_URL + "/sensors/" + sensor_id);

        try(InputStream input = url.openStream()){
            /*
            DiscoveryResponse dr = mapper.readValue(input, DiscoveryResponse.class);
            return dr.getPubsub_streams();
            */
        }
	}
}
