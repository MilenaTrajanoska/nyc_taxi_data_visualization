# from flask import Flask
# from threading import Event
# import signal
#
# from flask_kafka import FlaskKafka
# app = Flask(__name__)
#
# INTERRUPT_EVENT = Event()
#
# bus = FlaskKafka(
#     INTERRUPT_EVENT,
#     bootstrap_servers=",".join([
#         "tricycle-01.srvs.cloudkafka.com:9094",
#         "tricycle-02.srvs.cloudkafka.com:9094",
#         "tricycle-03.srvs.cloudkafka.com:9094"]
#     ),
#     group_id="test-group",
#     security_protocol="SSL",
#     sasl_plain_username="uwgbzh37",
#     sasl_mechanism="SCRAM-SHA-256",
#     sasl_plain_password="O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7",
# )
#
#
# def listen_kill_server():
#     signal.signal(signal.SIGTERM, bus.interrupted_process)
#     signal.signal(signal.SIGINT, bus.interrupted_process)
#     signal.signal(signal.SIGHUP, bus.interrupted_process)
#
#
# @bus.handle('uwgbzh37-popular-destinations')
# def test_topic_handler(msg):
#     print("consumed {} from test-topic".format(msg))
#
#
# if __name__ == '__main__':
#     bus.run()
#     listen_kill_server()
#     app.run(debug=True, port=5000)
import time
from threading import Thread

from flask_sock import Sock
from flask import Flask, send_from_directory
from flask_cors import CORS, cross_origin
from flask_socketio import SocketIO, emit
from kafka import KafkaConsumer, TopicPartition

app = Flask(__name__)
socketio = SocketIO(app, cors_allowed_origins="*")
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

sock = Sock(app)
sock.init_app(app)

BOOTSTRAP_SERVERS = ",".join(
    [
        "tricycle-01.srvs.cloudkafka.com:9094",
        "tricycle-02.srvs.cloudkafka.com:9094",
        "tricycle-03.srvs.cloudkafka.com:9094"
    ]
)

TOPIC_NAME = 'uwgbzh37-popular-destinations'

@app.route('/')
@cross_origin()
def home():
    return send_from_directory('./web', "index.html")

def kafkaconsumer():
    consumer = KafkaConsumer(
        group_id='test-group',
        bootstrap_servers=BOOTSTRAP_SERVERS,
        security_protocol="SSL",
        sasl_plain_username="uwgbzh37",
        sasl_mechanism="SCRAM-SHA-256",
        sasl_plain_password="O4n_4-ui5DDzfORdAbqNBlYhl7gwJdd7",
        consumer_timeout_ms=10000
    )
    tp = [TopicPartition(TOPIC_NAME, partition) for partition in range(5)]
    # register to the topic
    consumer.assign(tp)

    consumer.seek_to_end(tp[0])
    lastOffset = consumer.position(tp[0])
    consumer.seek_to_beginning(tp[0])

    for message in consumer:
        emit('kafkaconsumer',
             {'data': message.value.decode('utf8')})
        if message.offset == lastOffset - 1:
            break
    consumer.close()

if __name__ == '__main__':
    run_thread = Thread(target=kafkaconsumer())
    run_thread.daemon = True
    run_thread.start()
    run_thread.join()