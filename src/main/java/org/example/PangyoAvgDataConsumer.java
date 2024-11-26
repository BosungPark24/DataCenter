package org.example;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class PangyoAvgDataConsumer {
    private static final String BOOTSTRAP_SERVERS = "ec2-18-118-73-174.us-east-2.compute.amazonaws.com:9092,ec2-18-216-146-190.us-east-2.compute.amazonaws.com:9092,ec2-18-224-218-221.us-east-2.compute.amazonaws.com:9092";
    private static final String TOPIC_NAME = "pangyo_avg7";
    private static final String SCHEMA_REGISTRY_URL = "http://ec2-3-136-225-98.us-east-2.compute.amazonaws.com:8081";

    public static void main(String[] args) {
        // Kafka Consumer 설정
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "pangyo-avg7-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 처음부터 읽기
        props.put("schema.registry.url", SCHEMA_REGISTRY_URL);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); // 수동 커밋

        // Kafka Consumer 생성
        KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(TOPIC_NAME));

        try {
            while (true) {
                // 메시지 폴링
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, Object> record : records) {
                    System.out.println("Partition: " + record.partition() + ", Offset: " + record.offset());
                    System.out.println("Key: " + record.key());
                    System.out.println("Value: " + record.value().toString());

                    // JSON 데이터에서 평균 온도 추출 (예: {"PANGYO_AVG_UPS_TEMP": 38.5, "COUNT": 88, ...})
                    String jsonValue = record.value().toString();
                    double avgTemp = parseAvgTemperature(jsonValue);

                    // 알림 조건: 평균 온도가 38도 이상
                    if (avgTemp >= 38.0) {
                        System.out.println("ALERT: Temperature exceeded 38°C! Value: " + avgTemp);
                    }
                }

                // 메시지 수동 커밋
                consumer.commitSync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

    // JSON 데이터에서 평균 온도 추출
    private static double parseAvgTemperature(String jsonValue) {
        try {
            String tempString = jsonValue.split("\"PANGYO_AVG_UPS_TEMP\":")[1].split(",")[0];
            return Double.parseDouble(tempString);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
