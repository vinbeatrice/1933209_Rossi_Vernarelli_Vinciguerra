# SYSTEM DESCRIPTION
A distributed automation platform capable of ingesting heterogeneous sensor data, normalizing it into a unified internal representation, evaluating simple automation rules dynamically to allow both manual and automatic activation of actuators, and providing a real-time dashboard for habitat monitoring and plots showing recent history of data.

# USER STORIES
1) As a Mars operator, I want to visualize current data coming from REST sensors so that I can monitor the situation.
2) As a Mars operator, I want to visualize telemetries coming from sensors so that I can monitor the situation.
3) As a Mars operator, I want to be able to select the specific sensor data I want to plot so that I can analyze them in detail.
4) As a Mars operator, I want to be able to manually refresh values captured by the sensors so that I can get the most up to date information.
5) As a Mars operator, I want to set the value of the refreshing interval  so that I can decide the frequency.
6) As a Mars operator, I want to add an automation rule to automate my reaction to the environment.
7) As a Mars operator, I want to update an automation rule so that I can customize my rules.
8) As a Mars operator, I want to delete an automation rule so that I can get rid of it if I don’t need it anymore.
9) As a Mars operator, I want to be able to visualize the actual state of all the actuators so that I can monitor the situation.
10) As a Mars operator, I want to manually change the state of the actuators so that I can have more control.
11) As a Mars operator, I want the values over the threshold to be highlighted so that I can easily understand what is happening.
12) As a Mars operator, I want to see some metrics (average, max, min) about received data so that I can analyse them.
13) As a Mars operator, I want to receive a notification when an automation rule is triggered so that I know when the system reacts automatically.
14) As a Mars operator, I want to be able to export useful data into pdf files so that I can process them offline.
15) As a Mars operator, I want to be able to activate/disable an automation rule so that I can have more flexibility and handle critic situations.
16) As a Mars operator, I want to be able to see the list of automation rules, so that I can manage them.

# NORMALIZED EVENT STRUCTURE:
NormalizedEvent
- sourceId : String
- sourceType: SourceType
- schema : String
- timestamp : String
- status : String
- measurements : List\<Measurmentes\>

SourceType: enum {REST, STREAM}

Measurement: 
- metric : String
- value : Double
- unit : String

# RULE MODEL:
AutomationRule:
- id : Long
- sensorName : String
- metric : String
- operator : String
- thresholdValue : Double 
- unit: String
- actuatorName : String
- targetState : String
- enabled : boolean 
- description : String 