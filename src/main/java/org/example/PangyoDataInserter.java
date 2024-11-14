package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class PangyoDataInserter {
    // 임계값 설정
    private static final float TEMP_THRESHOLD = 30.0f;
    private static final float HUMIDITY_THRESHOLD = 35.0f;
    private static final float POWER_USAGE_THRESHOLD = 315.0f;
    private static final float UPS_TEMP_THRESHOLD = 40.0f;
    private static final float SMOKE_LEVEL_THRESHOLD = 0.01f;

    public static void main(String[] args) {
        String url = "jdbc:mysql://ohiomysql2.c782uy2a401d.us-east-2.rds.amazonaws.com:3306/mysql_test";
        String user = "ohiomysql";
        String password = "ohio1234";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to MySQL.");

            // 테이블 생성 (한 번만 실행하면 됨)
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS pangyo17 (
                    data_id INT PRIMARY KEY,
                    timestamp VARCHAR(50),
                    temperature FLOAT,
                    humidity FLOAT,
                    oxygen_level FLOAT,
                    airflow INT,
                    power_usage FLOAT,
                    ups_temp FLOAT,
                    battery_health VARCHAR(50),
                    voltage_variation VARCHAR(50),
                    smoke_level FLOAT,
                    heat_sensor_trigger VARCHAR(10),
                    sprinkler_status VARCHAR(10),
                    level VARCHAR(20)
                );
            """;
            conn.createStatement().execute(createTableQuery);
            System.out.println("Table checked/created successfully.");

            // 초기 값 설정
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            float temperature = 27.0f;
            float humidity = 40.0f;
            float oxygenLevel = 20.9f;
            int airflow = 3;
            float powerUsage = 275.0f;
            float upsTemp = 33.0f;
            String batteryHealth = "Good";
            String voltageVariation = "None";
            float smokeLevel = 0.0f;
            String heatSensorTrigger = "No";
            String sprinklerStatus = "Active";

            String insertQuery = "INSERT INTO pangyo17 (data_id, timestamp, temperature, humidity, oxygen_level, airflow, power_usage, ups_temp, battery_health, voltage_variation, smoke_level, heat_sensor_trigger, sprinkler_status, level) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);

            for (int i = 1; i <= 3600; i++) {
                // 값 변화를 설정
                temperature += 0.01;
                smokeLevel += 0.001;

                if (i < 900) { // 정상 단계
                    if (powerUsage < POWER_USAGE_THRESHOLD) {
                        powerUsage += 0.01;
                    }
                    if (upsTemp < UPS_TEMP_THRESHOLD) {
                        upsTemp += 0.01;
                    }
                    batteryHealth = "Good";
                    voltageVariation = "None";
                } else if (i < 1800) { // 경고 단계
                    if (powerUsage < POWER_USAGE_THRESHOLD) {
                        powerUsage += 0.1;
                    }
                    if (upsTemp < UPS_TEMP_THRESHOLD) {
                        upsTemp += 0.04;
                    }
                    voltageVariation = (i % 200 < 100) ? "Low" : "Medium";
                    batteryHealth = (i % 200 < 100) ? "Good" : "Fair";
                } else if (i < 2700) { // 위험 단계
                    if (powerUsage < POWER_USAGE_THRESHOLD) {
                        powerUsage += 0.3;
                    }
                    if (upsTemp < UPS_TEMP_THRESHOLD) {
                        upsTemp += 0.06;
                    }
                    voltageVariation = (i % 100 < 50) ? "Medium" : "High";
                    batteryHealth = (i % 100 < 50) ? "Fair" : "Poor";
                } else { // 화재 발생 단계
                    if (powerUsage < POWER_USAGE_THRESHOLD) {
                        powerUsage += 0.5;
                    }
                    if (upsTemp < UPS_TEMP_THRESHOLD) {
                        upsTemp += 0.1;
                    }
                    voltageVariation = "High";
                    batteryHealth = "Poor";
                }

                // 위험 수준 분류
                String level = classifyLevel(temperature, humidity, powerUsage, upsTemp, smokeLevel);

                // 데이터 삽입
                pstmt.setInt(1, i);
                pstmt.setString(2, timestamp.toString());
                pstmt.setFloat(3, temperature);
                pstmt.setFloat(4, humidity);
                pstmt.setFloat(5, oxygenLevel);
                pstmt.setInt(6, airflow);
                pstmt.setFloat(7, powerUsage);
                pstmt.setFloat(8, upsTemp);
                pstmt.setString(9, batteryHealth);
                pstmt.setString(10, voltageVariation);
                pstmt.setFloat(11, smokeLevel);
                pstmt.setString(12, heatSensorTrigger);
                pstmt.setString(13, sprinklerStatus);
                pstmt.setString(14, level);
                pstmt.executeUpdate();

                System.out.println("Inserted data_id " + i);

                // 타임스탬프 갱신 및 딜레이
                timestamp = new Timestamp(timestamp.getTime() + 500);
                TimeUnit.MILLISECONDS.sleep(500);
            }

            System.out.println("MySQL에 로그 데이터가 성공적으로 저장되었습니다.");
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String classifyLevel(float temperature, float humidity, float powerUsage, float upsTemp, float smokeLevel) {
        boolean tempExceed = temperature > TEMP_THRESHOLD;
        boolean humidityExceed = humidity < HUMIDITY_THRESHOLD;
        boolean powerUsageExceed = powerUsage > POWER_USAGE_THRESHOLD;
        boolean upsTempExceed = upsTemp > UPS_TEMP_THRESHOLD;
        boolean smokeLevelExceed = smokeLevel > SMOKE_LEVEL_THRESHOLD;

        if (tempExceed && upsTempExceed && smokeLevelExceed) {
            return "Fire outbreak";
        } else if ((tempExceed && humidityExceed) || (tempExceed && powerUsageExceed) || (humidityExceed && powerUsageExceed)) {
            return "Danger";
        } else if (tempExceed || humidityExceed) {
            return "Warning";
        } else {
            return "Normal";
        }
    }
}
