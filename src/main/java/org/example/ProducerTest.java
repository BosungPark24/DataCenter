package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;


import java.util.Properties;

public class ProducerTest {
    public static void main(String[] args) {

    // 설정을 구성합니다.
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ec2-18-118-73-174.us-east-2.compute.amazonaws.com:9092");
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaJSONProducer");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, "io.confluent.monitoring.clients.interceptor" +
            ".MonitoringProducerInterceptor");

    // Producer 객체를 생성합니다.
    Producer<String, String> producer = new KafkaProducer<>(props);

    // JSON 데이터를 생성합니다.
    JSONObject data = new JSONObject();
    for (long i = 0; i < 1000; i++) {
        data.put("fruit", "바나나");
        data.put("address", "경기도 군포시 산본동");
        data.put("record", "ksd_record_"+i);
        System.out.println("input record : ksd_record_"+i);

        ProducerRecord<String, String> record = new ProducerRecord<>("koreantest", data.toString());
        producer.send(record);
    }
    producer.close();
    }
}