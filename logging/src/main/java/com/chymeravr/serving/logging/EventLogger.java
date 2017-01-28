package com.chymeravr.serving.logging;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * Created by rubbal on 19/1/17.
 */
public class EventLogger {
    private final Producer<String, String> producer;

    public EventLogger(Properties properties) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "23.101.198.81:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        this.producer = new KafkaProducer<>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // TODO : Find out if close method flushes before close
            producer.flush();
            producer.close();
        }));
    }

    public void sendMessage(String requestId, String event) {
        ProducerRecord<String, String> data = new ProducerRecord<>("serving", requestId, event);
        producer.send(data);
    }
}
