package org.example;

import java.util.Random;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import java.util.Properties;

public class AvroProducerT1 {
    public static void main(String[] args) {
        // producer 설정
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ec2-18-118-73-174.us-east-2.compute.amazonaws.com:9092, ec2-18-216-146-190.us-east-2.compute.amazonaws.com:9092, ec2-18-224-218-221.us-east-2.compute.amazonaws.com:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://ec2-3-136-225-98.us-east-2.compute.amazonaws.com:8081, ec2-3-19-40-90.us-east-2.compute.amazonaws.com:8081");

        // avro key schema
        Schema keySchema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Key\",\"fields\":[{\"name\":\"keyField\",\"type\":\"string\"}]}");
        // avro value schema
        Schema valueSchema = new Schema.Parser().parse("{\n" +
                "  \"type\": \"record\",\n" +
                "  \"name\": \"Record\",\n" +
                "  \"fields\": [\n" +
                "    {\n" +
                "      \"name\": \"Name\",\n" +
                "      \"type\": \"string\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"Age\",\n" +
                "      \"type\": \"long\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"City\",\n" +
                "      \"type\": \"string\"\n" +
                "    }\n" +
                "  ]\n" +
                "}");

        KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);

        try {
            for (int i = 0; i < 10000; i++) {
                // UUID key 생성
                String randomKey = java.util.UUID.randomUUID().toString();
                String genkey = genKey();
                // 랜덤 value 문자열 생성
                String randomName = generateRandomName();
                String randomCity = generateRandomCity();
                Integer randomAge = generateRandomAge();
                // key avro 레코드 생성
                GenericRecord keyRecord = new GenericData.Record(keySchema);
//        keyRecord.put("keyField", randomKey);
                keyRecord.put("keyField", genkey);

                // value avro 레코드 생성
                GenericRecord valueRecord = new GenericData.Record(valueSchema);
                valueRecord.put("Name", randomName);
                valueRecord.put("City", randomCity);
                valueRecord.put("Age", randomAge);
        if(genkey.equals("apple")){
                ProducerRecord<Object, Object> record = new ProducerRecord<>("HA_test_bosung", 0, keyRecord, valueRecord);
                producer.send(record).get();
                System.out.println("Record sent successfully: " + record);
        }
        else if(genkey.equals("banana")){
                ProducerRecord<Object, Object> record = new ProducerRecord<>("HA_test_bosung", 1, keyRecord, valueRecord);
                producer.send(record).get();
                System.out.println("Record sent successfully: " + record);
        }
        else if(genkey.equals("orange")){
                ProducerRecord<Object, Object> record = new ProducerRecord<>("HA_test_bosung", 2, keyRecord, valueRecord);
                producer.send(record).get();
                System.out.println("Record sent successfully: " + record);
        }
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            producer.close();
        }
    }

    // 랜덤 키 생성 메소드
    private static String genKey() {
        // 랜덤으로 들어갈 단어
        String[] words = {"apple", "banana", "orange"};
        Random rand = new Random();
        StringBuilder sentence = new StringBuilder();

        // 랜덤으로 최대 1 단어 생성
        int sentenceLength = rand.nextInt(1) + 1;
        for (int i = 0; i < sentenceLength; i++) {
            sentence.append(words[rand.nextInt(words.length)]);
            sentence.append(" ");
        }
        return sentence.toString().trim();
    }

    // 랜덤 이름 생성 메소드
    private static String generateRandomName() {
        // 랜덤으로 들어갈 단어
        String[] words = {"John", "Dex", "Seulgi", "Max", "Peter", "James", "Kim", "Tailor"};
        Random rand = new Random();
        return words[rand.nextInt(words.length)];
    }

    // 랜덤 도시 생성 메소드
    private static String generateRandomCity() {
        // 랜덤으로 들어갈 단어
        String[] cities = {"Seoul", "London", "LA", "New York", "Tokyo", "Beijing", "Madrid"};
        Random rand = new Random();
        return cities[rand.nextInt(cities.length)];
    }

    // 랜덤 나이 생성 메소드
    private static Integer generateRandomAge() {
        // 랜덤으로 들어갈 단어
        Integer[] ages = {32, 21, 23, 34, 57, 11, 23, 54};
        Random rand = new Random();
        return ages[rand.nextInt(ages.length)];

    }

}

