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

#### MICROSERVICE: <name of the microservice>
- TYPE: backend
- DESCRIPTION: <description of the microservice>
- PORTS: <ports to be published by the microservice>
- TECHNOLOGICAL SPECIFICATION:
<description of the technological aspect of the microservice>
- SERVICE ARCHITECTURE: 
<description of the architecture of the microservice>

- ENDPOINTS: <put this bullet point only in the case of backend and fill the following table>
		
	| HTTP METHOD | URL | Description | User Stories |
	| ----------- | --- | ----------- | ------------ |
    | ... | ... | ... | ... |

- PAGES: <put this bullet point only in the case of frontend and fill the following table>

	| Name | Description | Related Microservice | User Stories |
	| ---- | ----------- | -------------------- | ------------ |
	| ... | ... | ... | ... |

- DB STRUCTURE: <put this bullet point only in the case a DB is used in the microservice and specify the structure of the tables and columns>

	**_<name of the table>_** :	| **_id_** | <other columns>

#### <other microservices>

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

#### MICROSERVICE: <name of the microservice>
- TYPE: backend
- DESCRIPTION: <description of the microservice>
- PORTS: <ports to be published by the microservice>
- TECHNOLOGICAL SPECIFICATION:
<description of the technological aspect of the microservice>
- SERVICE ARCHITECTURE: 
<description of the architecture of the microservice>

- ENDPOINTS: <put this bullet point only in the case of backend and fill the following table>
		
	| HTTP METHOD | URL | Description | User Stories |
	| ----------- | --- | ----------- | ------------ |
    | ... | ... | ... | ... |

- PAGES: <put this bullet point only in the case of frontend and fill the following table>

	| Name | Description | Related Microservice | User Stories |
	| ---- | ----------- | -------------------- | ------------ |
	| ... | ... | ... | ... |

- DB STRUCTURE: <put this bullet point only in the case a DB is used in the microservice and specify the structure of the tables and columns>

	**_<name of the table>_** :	| **_id_** | <other columns>

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

#### MICROSERVICE: <name of the microservice>
- TYPE: backend
- DESCRIPTION: <description of the microservice>
- PORTS: <ports to be published by the microservice>
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

