package org.example;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;


public class ConsumerTest {
    private static final String BOOTSTRAP_SERVERS = "ec2-18-118-73-174.us-east-2.compute.amazonaws.com:9092, ec2-18-216-146-190.us-east-2.compute.amazonaws.com:9092, ec2-18-224-218-221.us-east-2.compute.amazonaws.com:9092";
    private static final String topicName = "HA_test_bosung";
    public static void main(String[] args) {
        // consumer properties 설정
        Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "HA_test_1102");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
//    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // offset auto commit 되지 않게 설정
        consumerProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // Kafka consumer instance 생성
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        printCommittedOffsets(consumer);
        try {
            String uuidKey = null;
            // topic(s) 구독
            consumer.subscribe(Collections.singletonList(topicName));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
//          uuidKey = UUID.randomUUID().toString();
                    System.out.println("처리해야 하는 데이터 partition : " + record.partition());
                    System.out.println("처리해야 하는 데이터 offset : " + record.offset());
                    System.out.println("처리해야 하는 데이터 value : " + record.value());
                    System.out.println("처리해야 하는 데이터 timestamp : " + record.timestamp());

                    consumer.commitSync();
                }

            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            // consumer.close();
        }
    }

    private static void printCommittedOffsets(KafkaConsumer<String, String> consumer) {
        Set<TopicPartition> partitions = new HashSet<>();
        partitions.add(new TopicPartition(topicName, 0));
        partitions.add(new TopicPartition(topicName, 1));
        partitions.add(new TopicPartition(topicName, 2));

        // 커밋된 오프셋과 메타데이터를 가져옵니다.
        Map<TopicPartition, OffsetAndMetadata> committedOffsets = consumer.committed(partitions);
        // 가져온 결과를 출력합니다.

        for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : committedOffsets.entrySet()) {
            TopicPartition topicPartition = entry.getKey();
            OffsetAndMetadata offsetAndMetadata = entry.getValue();
            System.out.println(">>>>offsetAndMetadata: " + offsetAndMetadata);
//      System.out.println("==========:"+committedOffsets.entrySet().size());

            System.out.println(">>>>Topic: " + topicPartition.topic());
            System.out.println(">>>>Partition: " + topicPartition.partition());
//      System.out.println(">>>>Last Committed Offset: " + offsetAndMetadata.offset());
        }
    }
}




