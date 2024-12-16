# **Virtual Data Center Log Generate & ksqlDB Consumer**

 
이 프로젝트는 **Java**와 **Confluent Kafka**를 활용하여 가상 데이터 센터(판교, 부산)의 온도 센서 데이터를 수집, 처리, 그리고 분석하는 과정을 다룹니다.  


1. **온도 센서 데이터 로그 생성 및 MySQL 적재**  
   - Java를 사용하여 가상 데이터 센터(판교, 부산)의 온도 센서 데이터를 시뮬레이션하고 MySQL 데이터베이스에 적재.  

2. **MySQL 데이터를 Kafka 토픽으로 전송**  
   - Confluent의 **JDBC Source Connector**를 활용하여 MySQL에 저장된 데이터를 Kafka 토픽으로 Produce.  

3. **ksqlDB를 통한 데이터 분석**  
   - Confluent의 ksqlDB를 사용해 Kafka 토픽 데이터를 처리.  
   - **Tumbling Window**를 통해 1분 단위로 UPS 평균 온도를 계산하고, 결과를 새로운 Kafka 토픽에 적재.  

4. **Kafka Consumer로 데이터 처리**  
   - 새로운 Kafka 토픽의 데이터를 Java Consumer를 통해 수신 및 처리.  
   - 처리된 데이터를 추가 분석하거나 필요한 곳에 활용.  

