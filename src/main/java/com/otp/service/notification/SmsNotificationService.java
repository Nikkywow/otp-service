package com.otp.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.smpp.*;
import org.smpp.pdu.*;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class SmsNotificationService {
    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddress;

    public SmsNotificationService() {
        Properties config = loadConfig();
        this.host = config.getProperty("smpp.host");
        this.port = Integer.parseInt(config.getProperty("smpp.port"));
        this.systemId = config.getProperty("smpp.system_id");
        this.password = config.getProperty("smpp.password");
        this.systemType = config.getProperty("smpp.system_type");
        this.sourceAddress = config.getProperty("smpp.source_addr");
    }

    public void sendCode(String destination, String code) {
        Connection connection = null;
        Session session = null;

        try {
            // 1. Установка соединения
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);

            // 2. Настройка и выполнение bind
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34);
            bindRequest.setAddressRange(sourceAddress);

            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new PDUException("Bind failed with status: " + bindResponse.getCommandStatus());
            }

            // 3. Подготовка и отправка сообщения
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddress);
            submitSM.setDestAddr(destination);
            submitSM.setShortMessage("Your code: " + code);

            // 4. Отправка
            session.submit(submitSM);
            log.info("SMS sent to {}", destination);

        } catch (ValueNotSetException e) {
            log.error("Required SMPP value not set: {}", e.getMessage());
            throw new RuntimeException("SMPP configuration error", e);
        } catch (PDUException e) {
            log.error("SMPP protocol error: {}", e.getMessage());
            throw new RuntimeException("SMPP communication failure", e);
        } catch (Exception e) {
            log.error("General SMS sending error: {}", e.getMessage());
            throw new RuntimeException("Failed to send SMS", e);
        } finally {
            // 5. Освобождение ресурсов
            closeResources(session, connection);
        }
    }

    private void closeResources(Session session, Connection connection) {
        try {
            if (session != null) {
                session.unbind();
                session.close();
            }
        } catch (Exception e) {
            log.warn("Error closing SMPP session: {}", e.getMessage());
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            log.warn("Error closing SMPP connection: {}", e.getMessage());
        }
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("sms.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load SMS configuration", e);
        }
    }
}