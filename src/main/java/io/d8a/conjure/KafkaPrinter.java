package io.d8a.conjure;

import kafka.javaapi.producer.Producer;
import kafka.javaapi.producer.ProducerData;
import kafka.producer.ProducerConfig;

import java.util.Properties;

class KafkaPrinter implements Printer {
    private final Producer<String, String> producer;
    private final String topic;

    public KafkaPrinter(String zkString, String topic){
        this.topic = topic;
        Properties props = new Properties();
        props.put("zk.connect", zkString);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);
    }

    @Override
    public void print(Object message){
        ProducerData<String, String> data = new ProducerData<String, String>(topic, (String) message);
        producer.send(data);
    }

    @Override
    public String toString(){
        return "Kafka topic '"+topic+"'";
    }
}
