package it.uniroma1.processing.actuators_helpers;

public class ActuatorStateRequest {

    private String state;

    public ActuatorStateRequest() {
    }

    public ActuatorStateRequest(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
