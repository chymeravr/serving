package com.chymeravr.serving.logging;

import com.simple.metrics.kafka.DropwizardReporter;
import com.simple.metrics.kafka.DropwizardReporterConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.Configuration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by rubbal on 19/1/17.
 */
@Slf4j
public class EventLogger implements ResponseLogger {
    private final Producer<String, String> producer;

    public EventLogger(Configuration configuration) {
        Properties props = new Properties();
        addToKafkaProps(props, ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration);
        addToKafkaProps(props, ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, configuration);
        addToKafkaProps(props, ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, configuration);
        addToKafkaProps(props, ProducerConfig.MAX_BLOCK_MS_CONFIG, configuration);
        addToKafkaProps(props, ProducerConfig.ACKS_CONFIG, configuration);

        props.put(ProducerConfig.METRIC_REPORTER_CLASSES_CONFIG, DropwizardReporter.class.getCanonicalName());
        props.put(DropwizardReporterConfig.REGISTRY_PROPERTY_NAME, "kafka");

        this.producer = new KafkaProducer<>(props);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // TODO : Find out if close method flushes before close
            producer.flush();
            producer.close();
        }));
    }

    private void addToKafkaProps(Properties properties, String key, Configuration configuration) {
        properties.put(key, configuration.getString(key));
    }

    @Override
    public void sendMessage(String requestId, String event, String topic) {
        final ProducerRecord<String, String> data = new ProducerRecord<>(topic, requestId, event);
        producer.send(data, (metadata, exception) -> {
            // TODO : spool locally
            if (exception != null) {
                log.error("Unable to send kafka message : {}", data, exception);
            }
        });
    }
}
