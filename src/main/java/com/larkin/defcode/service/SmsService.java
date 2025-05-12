package com.larkin.defcode.service;

import com.larkin.defcode.config.SmsConfig;
import com.larkin.defcode.exception.CodeSendingMethodException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final SmsConfig smsConfig;
    private Session session;

    @PostConstruct
    public void init(){
        log.debug("Setting up smpp connection");
        Connection connection = new TCPIPConnection(smsConfig.getHost(), smsConfig.getPort());
        session = new Session(connection);

        BindTransmitter bindRequest = new BindTransmitter();
        try {
            bindRequest.setSystemId(smsConfig.getSystemId());
            bindRequest.setPassword(smsConfig.getPassword());
            bindRequest.setSystemType(smsConfig.getSystemType());
            bindRequest.setInterfaceVersion((byte) 0x34);
            bindRequest.setAddressRange(smsConfig.getSourceAddr());

            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                CodeSendingMethodException.sms("Bind failed: " + bindResponse.getCommandStatus());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            CodeSendingMethodException.sms(e.getMessage());
        }
        log.debug("SMPP session successfully bound to {}", smsConfig.getHost());
    }

    public void sendCode(String destination, int code) {
        log.info("Sending code by sms");
        try {
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(smsConfig.getSourceAddr());
            submitSM.setDestAddr(destination);
            submitSM.setShortMessage("Your code: " + code);

            session.submit(submitSM);
            log.info("SMS with OTP sent to {}", destination);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", destination, e.getMessage());
            CodeSendingMethodException.sms("Failed to send SMS to " + destination);
        }
    }

    @PreDestroy
    public void cleanup() throws Exception {
        if (session != null) {
            session.unbind();
            log.debug("SMPP session closed");
        }
    }
}
