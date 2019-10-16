package org.springframework.samples.petclinic.config.logdna;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LogDnaAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    protected PatternLayoutEncoder encoder;
    protected LogDnaConfiguration config;
    protected LogDnaExecutor executor;

    @Override
    public void start() {
        executor = new LogDnaExecutor(identifyHostname(), config, encoder);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    protected void append(ILoggingEvent event) {

        if (event.getLoggerName().equals(LogDnaAppender.class.getName())) {
            return;
        }
        executor.append(event);
    }

    private String identifyHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    public PatternLayoutEncoder getEncoder() {
        return encoder;
    }

    public void setEncoder(PatternLayoutEncoder encoder) {
        this.encoder = encoder;
    }

    public LogDnaConfiguration getConfig() {
        return config;
    }

    public void setConfig(LogDnaConfiguration config) {
        this.config = config;
    }
}