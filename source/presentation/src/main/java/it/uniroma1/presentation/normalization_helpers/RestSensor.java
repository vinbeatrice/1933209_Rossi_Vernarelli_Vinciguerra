package it.uniroma1.presentation.normalization_helpers;


public class RestSensor {

    private String sensor_id;
    private String path;
    private String schema_id;

    public RestSensor(){}

    public RestSensor(String sensor_id, String path, String schema_id){
        this.sensor_id = sensor_id;
        this.path = path;
        this.schema_id = schema_id;
    }

    public String getSensor_id() { 
        return sensor_id; 
    }

    public void setSensor_id(String sensor_id) { 
        this.sensor_id = sensor_id;
    }

    public String getPath() { 
        return path; 
    }
    public void setPath(String path) { 
        this.path = path; 
    }

    public String getSchema_id() { 
        return schema_id; 
    }
    public void setSchema_id(String schema_id) { 
        this.schema_id = schema_id; 
    }

    @Override
    public String toString(){
        return "Rest Sensor{sensor_id= "+ this.sensor_id + ", path= " + this.path + ", schema_id= "+ this.schema_id +"}";
    }


}