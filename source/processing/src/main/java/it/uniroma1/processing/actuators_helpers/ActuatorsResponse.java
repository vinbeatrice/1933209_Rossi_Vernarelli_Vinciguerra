package it.uniroma1.processing.actuators_helpers;

import java.util.Map;

public class ActuatorsResponse {

    private Map<String, String> actuators;

    public ActuatorsResponse() {
    }

    public Map<String, String> getActuators() {
        return actuators;
    }

    public void setActuators(Map<String, String> actuators) {
        this.actuators = actuators;
    }
}