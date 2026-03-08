package it.uniroma1.presentation.normalization_helpers;


public class PubsubStream {

    private String topic;
    private String sse;
    private String ws;
    private String schema_id;

    public PubsubStream(){}

    public PubsubStream(String topic, String sse, String ws, String schema_id) {
        this.topic = topic;
        this.sse = sse;
        this.ws = ws;
        this.schema_id = schema_id;
    }

    public String getTopic() { 
        return topic; 
    }
    
    public void setTopic(String topic) { 
        this.topic = topic;
    }

    public String getSse() { 
        return sse; 
    }
    
    public void setSse(String sse) { 
        this.sse = sse; 
    }

    public String getWs() { 
        return ws; 
    }

    public void setWs(String ws) { 
        this.ws = ws; 
    }

    public String getSchema_id() { 
        return schema_id; 
    }

    public void setSchema_id(String schema_id) { 
        this.schema_id = schema_id; 
    }

    @Override 
    public String toString(){
        return "PubsubStream{ topic= " + this.topic + ", sse= " + this.sse + ", ws= "+ this.ws + ", schema_id= " + this.schema_id + "}";
    }
}