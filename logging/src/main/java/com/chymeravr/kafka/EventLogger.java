package com.chymeravr.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;

/**
 * Created by rubbal on 19/1/17.
 */
public class EventLogger {
    public void sendMessage(String requestId, String event) {
        Properties props = new Properties();
        props.put("metadata.broker.list", "localhost:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("partitioner.class", "com.chymeravr.kafka.Partitioner");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(config);
        KeyedMessage<String, String> data = new KeyedMessage<>("test", requestId, event);
        producer.send(data);
        producer.close();
    }
}
