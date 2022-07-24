const { Kafka, logLevel } = require('kafkajs')
const http = require('http');
//  , fs = require('fs');
const WebSocket = require('ws');

var express = require('express');
var app = express();
app.use(express.static(__dirname+'/public'));
app.listen(8080)

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

const wss = new WebSocket.Server({ port: 7071 });
const clients = new Map();
wss.on('connection', (ws) => {
  consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
      console.log(`- ${prefix} ${message.key}#${message.value}`)
      ws.send(JSON.stringify({topic: `${topic}`, message: `${message.value}`}));
    },
  })
}
});
wss.on("close", () => {
  clients.delete(ws);
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