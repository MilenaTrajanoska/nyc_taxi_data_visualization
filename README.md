# NYC Taxi Rides Data Visualization
## Overview
The application represents real-time streaming of taxi ride events in the area around New York City. 
The architecture of the application contains the following components:
  * Data source - a csv file with taxi rides. Each entry receives a timestamp and is enqueued in a priority queue based on the timestamp. Lower timestamps have higher priority to be served as current events. Once an event is dequeued it receives a new timestamp (derived from the current time + a random latency) and is again enqueued in the priority queue. This process continues infinetly.
  * Flink stream processing jobs - three stream processing jobs are currently available.
      * The first job aggregates the number of passengers arriving at a destination (represented as with its latitude and longitude). The destinations are grouped into blocks, where the width of the block is 0.0014 degrees and the height of the block is 0.00125 degrees. This processing segments the map of New York City into rounghly equally sized blocks. The aggregations are currently computed on a sliding window with a size of 10 minutes and a 1 minute slide.
      * The second job represnts the average durations within a tumbling window of 10 minutes.
      * The third job represents the number of trips taken within a tumbling window of 10 minutes.
  * Kafka sink - for each streaming job, there is one Kafka sink which writes the resulting objects into a separate kafka topic.
  * Kafka cloud server - a cloud managed kafka server where 3 topcis have been created, namely popular-destinations, trip-duration, and trip-count.
  * Kafka consumer (on the Back-End) - a kafka consumer on the Back-End of the Web applicaiton, which constantly listens for new messages on each of the three above-defined topics.
  * Web socket - the Front-End and the Back-End of the Web application communicate via Web Sockets. Each time the Kafka consumer recieves a new message it's sent to the Web socket and the Front-End is updated.
  * Visualization - the Front-End part of the Web application. It visualizes the events on a leaflet.js map, every time a new message is received from the Web socket. Additionally, chart.js is used for representing the charts for the average trip durations and the number of trips taken within a 10 minute time window.
The entire architecture is displayed in the figure in the following section.
The application section is a reference to a video showing the main application functionalities.
## Architecture
![alt text](https://github.com/MilenaTrajanoska/nyc_taxi_data_visualization/blob/main/img/flink-kafka-ws-architecture.jpg?raw=true)
## Application
[![Watch the video](https://img.youtube.com/vi/QfMp5LL0Ec0/0.jpg)](https://youtu.be/QfMp5LL0Ec0)
