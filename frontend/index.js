const { Kafka, logLevel } = require('kafkajs')
const ip = require('ip')
const host = ip.address()

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

const topic = 'uwgbzh37-popular-destinations'
const consumer = kafka.consumer({groupId: 'test-group'})

const run = async () => {
  await consumer.connect()
  await consumer.subscribe({ topic, fromBeginning: true })
  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      const prefix = `${topic}[${partition} | ${message.offset}] / ${message.timestamp}`
      console.log(`- ${prefix} ${message.key}#${message.value}`)
    },
  })
}

run().catch(e => console.error(`[example/consumer] ${e.message}`, e))

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