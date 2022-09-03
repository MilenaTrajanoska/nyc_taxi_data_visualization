const { Kafka, logLevel } = require('kafkajs')
const http = require('http');
//  , fs = require('fs');
const WebSocket = require('ws');

var express = require('express');
var app = express();
app.use(express.static(__dirname+'/public'));
app.listen(8080)

var client_sockets = []

const kafka = new Kafka({
  logLevel: logLevel.INFO,
  brokers: ["tricycle-01.srvs.cloudkafka.com:9094",
            "tricycle-02.srvs.cloudkafka.com:9094",
            "tricycle-03.srvs.cloudkafka.com:9094"],
  ssl: {
    rejectUnauthorized: true
  },
  sasl: {
    mechanism: 'scram-sha-256',
    username: 'uwgbzh37',
    password: 'O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7',
  },
})

const topic_popular_destinations = 'uwgbzh37-popular-destinations'
const topic_trip_durations = 'uwgbzh37-trip-durations'
const topic_trip_hour_minute = 'uwgbzh37-trip-hour-minute'

const consumer = kafka.consumer({groupId: 'test-group'})
const run = async () => {
  await consumer.connect()
  await consumer.subscribe({topic: topic_popular_destinations, fromBeginning: true })
  await consumer.subscribe({topic: topic_trip_durations, fromBeginning: true })
  await consumer.subscribe({topic: topic_trip_hour_minute, fromBeginning: true})
}
run().catch(e => console.error(`[example/consumer] ${e.message}`, e))

let last_offset_topic_popular_destinations = null;
let last_offset_topic_trip_durations = null;
let last_offset_topic_trip_counts = null;

const wss = new WebSocket.Server({ port: 7071 });

wss.on('connection', (ws) => {

  client_sockets.push(ws);
  
  let partitions = [0, 1, 2, 3, 4];

  if (last_offset_topic_popular_destinations !== null) {
    for (partition in partitions) {
      consumer.seek({ topic: topic_popular_destinations, partition: partition, offset: last_offset_topic_popular_destinations})
    }
  }

  if (last_offset_topic_trip_counts !== null) {
    for (partition in partitions) {
      consumer.seek({ topic: topic_trip_hour_minute, partition: partition, offset: last_offset_topic_trip_counts})
    }
  }

  if (last_offset_topic_trip_durations !== null) {
    for (partition in partitions) {
      consumer.seek({ topic: topic_trip_durations, partition: partition, offset: last_offset_topic_trip_durations})
    }
  }
  try {
  consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      
      if (topic == topic_popular_destinations) {
        last_offset_topic_popular_destinations = message.offset > 50 ? message.offset - 50 : message.offset - 1;
      }
      else if (topic == topic_trip_durations) {
        last_offset_topic_trip_durations = message.offset > 24 ? message.offset - 24 : message.offset - 1;
      }
      else if (topic == topic_trip_hour_minute) {
        last_offset_topic_trip_counts = message.offset > 30 ? message.offset - 30 : message.offset - 1;
      }

      const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
      console.log(`- ${prefix} ${message.key}#${message.value}`)
      client_sockets.forEach(client => {
        client.send(JSON.stringify({topic: `${topic}`, message: `${message.value}`}));
      });
    },
  })
}catch(error) {
  console.log(error);
}
}

);

wss.on("close", (ws) => {
  for(var i = 0; i < client_sockets.length; i++){ 
    
    if ( client_sockets[i] === ws) { 

        client_sockets.splice(i, 1); 
    }
}
});


const errorTypes = ['unhandledRejection', 'uncaughtException']
const signalTraps = ['SIGTERM', 'SIGINT', 'SIGUSR2']

errorTypes.forEach(type => {
  process.on(type, async e => {
    try {
      console.log(`process.on ${type}`)
      console.error(e)
      await consumer.disconnect()
      process.exit(0)
    } catch (_) {
      process.exit(1)
    }
  })
})

signalTraps.forEach(type => {
  process.once(type, async () => {
    try {
      await consumer.disconnect()
    } finally {
      process.kill(process.pid, type)
    }
  })
})

app.get('/', function(req, res) {
  res.sendFile(__dirname+'/index.html');
});