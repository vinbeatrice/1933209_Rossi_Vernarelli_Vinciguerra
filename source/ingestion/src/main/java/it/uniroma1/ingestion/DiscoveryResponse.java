package it.uniroma1.ingestion;

import it.uniroma1.ingestion.normalization_helpers.*;
import it.uniroma1.ingestion.rest_normalization.*;
import it.uniroma1.ingestion.telemetry_normalization.*;

import java.util.List;

public class DiscoveryResponse {

    private String schema_version;
    private String schema_policy;
    private List<RestSensor> rest_sensors;
    private List<PubsubStream> pubsub_streams;

    public String getSchema_version() { 
        return schema_version; 
    }
    
    public void setSchema_version(String schema_version) { 
        this.schema_version = schema_version; 
    }

    public String getSchema_policy() { 
        return schema_policy; 
    }
    
    public void setSchema_policy(String schema_policy) { 
        this.schema_policy = schema_policy;
    }

    public List<RestSensor> getRest_sensors() { 
        return rest_sensors;
    }
    
    public void setRest_sensors(List<RestSensor> rest_sensors) { 
        this.rest_sensors = rest_sensors;
    }

    public List<PubsubStream> getPubsub_streams() { 
        return pubsub_streams;
    }
    
    public void setPubsub_streams(List<PubsubStream> pubsub_streams) { 
        this.pubsub_streams = pubsub_streams; 
    }

}