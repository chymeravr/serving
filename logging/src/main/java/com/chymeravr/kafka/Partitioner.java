package com.chymeravr.kafka;

import kafka.utils.VerifiableProperties;

/**
 * Created by rubbal on 19/1/17.
 */
public class Partitioner implements kafka.producer.Partitioner {
    public Partitioner(VerifiableProperties props) {

    }

    public int partition(Object key, int a_numPartitions) {
        int partition = 0;
        String stringKey = (String) key;
        int offset = stringKey.lastIndexOf('.');
        if (offset > 0) {
            partition = Integer.parseInt(stringKey.substring(offset + 1)) % a_numPartitions;
        }
        return partition;
    }
}
