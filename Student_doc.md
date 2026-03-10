# SYSTEM DESCRIPTION:

<system of the system>

# USER STORIES:

<list of user stories>

# NORMALIZED EVENT STRUCTURE:
NormalizedEvent
- metricType : string
- value : number
- unit : string
- timestamp : string
- source : string
- schemaVersion : string

# CONTAINERS:

## CONTAINER_NAME: ingestion-1

### DESCRIPTION: 
<description of the container>

### USER STORIES:
1. As a Mars operator, I want to visualize current data coming from REST sensors so that I can monitor the situation.
2. As a Mars operator, I want to visualize telemetries coming from sensors so that I can monitor the situation.
As a Mars operator, I want to be able to manually refresh values captured by the sensors so that I can get the most up to date information.
As a Mars operator, I want to set the value of the refreshing interval (intervallo di aggiornamento del valore) so that I can decide the frequency.


### PORTS: 
<used ports>

### DESCRIPTION:
<description of the container>

### PERSISTENCE EVALUATION
<description on the persistence of data>

### EXTERNAL SERVICES CONNECTIONS
<description on the connections to external services>

### MICROSERVICES:

#### MICROSERVICE: Ingestion
- TYPE: backend
- DESCRIPTION: This microservice is responsable of collecting at regular intervals of time data from the REST sensors and receive telemetry streams. Once received, it normalizes the collected event in a unique standard format and forwards them to both the other services.
- PORTS: 8081
- TECHNOLOGICAL SPECIFICATION:
It is realized as a Spring-boot application. It relies on RabbitMQ to handle the exchange of events with presentation and processing containers. CORS is enabled to let presentation microservice do HTTP requests.
- SERVICE ARCHITECTURE: 
The service is realized as follows:
    - a unified event schema is defined to normalize different data in a single way, created starting from the specific sensor schemas and a list of their metrics (normalization_helpers, rest_normalization, telemetry_normalization).
    - it has a controller (DiscoveryClient) which creates the mappings needed to retrieve the list of sensors and of telemetries streams, to get the normalized versions of the measured values, sends those data to processing and presentation services, allows to update the interval of time used to poll the telemetries.
    - RabbitMQ is set up to allow the controller to send data to other services through exchanges.
    - SSE is used to send stream data to browser in order to show the flow live on the dashboard.

- ENDPOINTS:
		
	| HTTP METHOD | URL | Description | User Stories |
    | ----------- | --- | ----------- | ------------ |
	| GET | http://localhost:8081/discovery/rest_sensors | Returns the list of Rest Sensors. | 1 |
    | GET | http://localhost:8081/discovery/telemetry_streams | Returns the list of telemetry topics. | 2 |
    | GET | http://localhost:8081/discovery/rest_sensors/{sensor_id} | Returns the normalized version of the event retrieved from the Rest Sensor sensor_id. | 1 |
    | POST | http://localhost:8081/discovery/rest_sensors/{sensor_id}/refresh | Asks to the Rest Sensor sensor_id the last event, normalizes it and forwards it to the presentation microservice. | 4 |
    | GET | http://localhost:8081/discovery/rest_sensors/polling-interval | Returns the current polling interval. | 5 |
    | PUT | http://localhost:8081/discovery/rest_sensors/polling-interval | Changes the polling interval. | 5 |
    | GET | http://localhost:8081/discovery/telemetry/mars/telemetry/{topic_id} | Returns the stream of information. | 2 |


#### <other microservices>

## CONTAINER_NAME: processing-1

### DESCRIPTION: 
<description of the container>

### USER STORIES:
<list of user stories satisfied>

### PORTS: 
<used ports>

### DESCRIPTION:
<description of the container>

### PERSISTENCE EVALUATION
<description on the persistence of data>

### EXTERNAL SERVICES CONNECTIONS
<description on the connections to external services>

### MICROSERVICES:

#### MICROSERVICE: Processing
- TYPE: backend
- DESCRIPTION: This microservice handles CRUD operation on automation rule and the manual/automatic activation of actuators through the reception of explicit requests from the presentation service and the evaluation of automation rules on the events received from the ingestion service.
- PORTS: 8082
- TECHNOLOGICAL SPECIFICATION:

    The microservice utilizes the Java programming language, specifically targeting Java 17. The service is built using the Spring Boot framework.
    Key components:
    - Spring-boot (4.0.3)
        - spring-boot-starter-web
        - spring-boot-starter-amqp
        - spring-boot-starter-data-jpa
    - PostgreSQL
    - RabbitMQ
    - Maven
    - CORS
- SERVICE ARCHITECTURE: 
    The service is realized with:
    - controllers for the automation rules and actuators endpoints
    - service classes to implement the business logic for rule CRUD operations, rule evaluation, actuator management and notification publishing
    - an entity class representing automation rules in the db
    - a repository for the interaction with the database, where the automation rules are persistently stored
    - a RabbitMQ consumer to receive normalized events from the ingestion service
    - a RabbitMQ publisher to notify the presentation service when an automation rule is triggered
    - a RabbitMQ publisher to send the actuators states to the presentation service


- ENDPOINTS: <put this bullet point only in the case of backend and fill the following table>
		
	| HTTP METHOD | URL | Description | User Stories |
	| ----------- | --- | ----------- | ------------ |
    | GET | http://localhost:8082/rules | Returns the list of automation rules. | 16 |
    | GET | http://localhost:8082/rules/{id} | Returns the single automation rule {id}. | 16 |
    | POST | http://localhost:8082/rules | Adds an automation rule | 6 |
    | PUT | http://localhost:8082/rules/{id} | Updates an automation rule. | 7 |
    | DELETE | http://localhost:8082/rules/{id} | Deletes the automation rule {id}. | 8 |
    | PATCH | http://localhost:8082/rules/{id}/enable | Enables the automation rule {id}. | 15 |
    | PATCH | http://localhost:8082/rules/{id}/disable | Disables the automation rule {id}. | 15 |
    | GET | http://localhost:8082/actuators | Returns the list of actuators. | 9 |
    | POST | http://localhost:8082/actuators/{actuatorName} | Changes the state of the actuator {actuatorName}. | 10 |


- DB STRUCTURE: <put this bullet point only in the case a DB is used in the microservice and specify the structure of the tables and columns>

	**_automation_rules_** :	| **_id_** | **_sensorName_** | **_metric_** | **_operator_** | **_thresholdValue_** | **_unit_** | **_actuatorName_** | **_targetState_** | **_enabled_** | **_description_**

#### <other microservices>

## CONTAINER_NAME: presentation-1

### DESCRIPTION: 
<description of the container>

### USER STORIES:
<list of user stories satisfied>

### PORTS: 
<used ports>

### DESCRIPTION:
<description of the container>

### PERSISTENCE EVALUATION
<description on the persistence of data>

### EXTERNAL SERVICES CONNECTIONS
<description on the connections to external services>

### MICROSERVICES:

#### MICROSERVICE: Presentation
- TYPE: frontend
- DESCRIPTION: <description of the microservice>
- PORTS: 8083
- TECHNOLOGICAL SPECIFICATION:
<description of the technological aspect of the microservice>
- SERVICE ARCHITECTURE: 
<description of the architecture of the microservice>


- PAGES: <put this bullet point only in the case of frontend and fill the following table>

	| Name | Description | Related Microservice | User Stories |
	| ---- | ----------- | -------------------- | ------------ |
	| ... | ... | ... | ... |


#### <other microservices>