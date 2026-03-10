# SYSTEM DESCRIPTION:

<system of the system>

# USER STORIES:

<list of user stories>


# CONTAINERS:

## CONTAINER_NAME: ingestion

### DESCRIPTION: 
The Ingestion container is responsible for collecting data from the *Mars IoT simulator* by discovering available sensor data using periodic polling for REST sensors and subscription to telemetry streams for Pub/Sub sensors. The incoming data is then converted into a normalized event format and published asychronously to a RabbitMQ message broker.
The container also exposes REST endpoints to manually refresh sensor readings and configuring the polling interval for REST sensors.

### USER STORIES:
1. As a Mars operator, I want to visualize current data coming from REST sensors so that I can monitor the situation. (us #1)
2. As a Mars operator, I want to visualize telemetries coming from sensors so that I can monitor the situation. (us #2)
3. As a Mars operator, I want to be able to manually refresh values captured by the sensors so that I can get the most up to date information. (us #4)
4. As a Mars operator, I want to set the value of the refreshing interval so that I can decide the frequency. (us #5)

### PORTS: 
- simulator port: 8080
- rabbitmq port: 5672

### PERSISTENCE EVALUATION
No persistent data is mantained.

### EXTERNAL SERVICES CONNECTIONS
The Ingestion container does not connect to external services.

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

## CONTAINER_NAME: processing

### DESCRIPTION: 
<description of the container>

### USER STORIES:
1. As a Mars operator, I want to add an automation rule to automate my reaction to the environment. (us #6)
2. As a Mars operator, I want to update the threshold of an automation rule so that I can customize my rules. (us #7)
3. As a Mars operator, I want to delete an automation rule so that I can get rid of it if I don’t need it anymore. (us #8)
4. As a Mars operator, I want to be able to visualize the actual state of all the actuators so that I can monitor the situation. (us #9)
5. As a Mars operator, I want to manually change the state of the actuators so that I can have more control. (us #10)
6. As a Mars operator, I want to see the last triggered automation rule so that I know when the system reacts automatically. (us #13)
7. As a Mars operator, I want to be able to activate/disable an automation rule so that I can have more flexibility and handle critical situations. (us #15)

### PORTS: 
- simulator port: 8080
- rabbitmq port: 5672
- postgres port: 5432

### PERSISTENCE EVALUATION
*Automation Rules* are stored persistently in a relational database managed by the Processing service using *Spring Data JPA*. Each rule is represented as a database entity (*AutomationRule*) and stored in the *automation_rules* table.

### EXTERNAL SERVICES CONNECTIONS
The Processing container does not connect to external services.

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
    | ... | ... | ... | ... |
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

## CONTAINER_NAME: presentation

### DESCRIPTION: 
The Presentation container provides the user interface for the Mars monitoring system. It allows operators to visualize real-time sensor data, monitor actuator states and manage automation rules through a web-based dashboard. It also provides features for visualizing sensor trends, exporting charts as PDF and manually interacting with the system such as refreshing sensor values or toggling actuators.

### USER STORIES:
1. As a Mars operator, I want to visualize current data coming from REST sensors so that I can monitor the situation. (us #1)
2. As a Mars operator, I want to visualize telemetries coming from sensors so that I can monitor the situation. (us #2)
3. As a Mars operator, I want to be able to select the specific sensor data I want to plot so that I can analyze them in detail. (us #3)
4. As a Mars operator, I want to be able to visualize the actual state of all the actuators so that I can monitor the situation. (us #9)
5. As a Mars operator, I want the values over the threshold to be highlighted so that I can easily understand what is happening. (us #11)
6. As a Mars operator, I want to see some metrics (average, max, min) about received data so that I can analyse them. (us #12)
7. As a Mars operator, I want to see the last triggered automation rule so that I know when the system reacts automatically. (us #13)
8. As a Mars operator, I want to be able to export useful data into pdf files so that I can process them offline. (us #14)
9. As a Mars operator, I want to be able to see the list of automation rules, so that I can manage them. (us #16)

### PORTS: 
- rabbitmq port: 5672

### PERSISTENCE EVALUATION
No persistent data is mantained.

### EXTERNAL SERVICES CONNECTIONS
The Presentation container does not connect to external services.

### MICROSERVICES:

#### MICROSERVICE: Presentation
- TYPE: frontend
- DESCRIPTION: <description of the microservice>
- PORTS: 8083
- TECHNOLOGICAL SPECIFICATION:  
The microservices has been developed using Spring-boot version 4.0.3 and it has been written in Java version 17. The dependencies used are:  
    - *spring-boot-starter-amqp* that provides a Spring Boot integration with AMPQ-based message brokers such as RabbitMQ;
    - *spring-boot-starter-thymeleaf* that provides a Spring Boot integration between Spring Boot and the Thymeleaf template engine, enabling the creation of dynamic server-side HTML views of web applications;
    - *spring-boot-starter-webmvc* that provides the Spring MVC framework for building web applications and REST APIs;
    - *jackson-databind* that provides data binding between JSON and Java objects, allowing automatic serialization and deserialization of data in REST APIs and message processing.
 
- SERVICE ARCHITECTURE:  
The service is realized with:
    - a service that offers support for the live update of information;
    - a service that updates the state of actuators based on the events happening during execution;
    - a controller that handles the request of up-to-date information coming from sensors;
    - a service that handles the consumes events of automation rule triggering;
    - a service that handles the in-memory caching and returning of historical data;
    - a controller that manages requests of retrieval of historical data.
    - normalization_helpers?

- PAGES:
 
	| Name | Description | Related Microservice | User Stories |
	| ---- | ----------- | -------------------- | ------------ |
	| index.html | entrypoint of the system | ingestion, processing | 1, 2, 4, 5, 11, 13 |
    | actuators.html| list of all the endpoints | processing | 9, 10|
    | add-rule.html | visualization of the form to create an automation rule | processing | 6 |
    | rules.html | list of all automation rules | processing |7, 8, 15, 16 |
    | sensor-detail.html | charts for each metric of a specific sensor | ingestion | 3, 12, 14|
