package org.springframework.samples.petclinic.config.logdna;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;


@Configuration
public class LogDnaAppenderConfiguration  {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(LogDnaAppenderConfiguration.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String springProfilesActive;

    @Value("${LOGDNA_API_KEY:}")
    private String logdnaApiKey;

    @Value("${LOGDNA_URL:https://logs.logdna.com/logs/ingest}")
    private String logdnaUrl;

    @Value("${LOGDNA_TAGS:}")
    private String tags;

    @Value("${LOGDNA_MDC_FIELDS:}")
    private String logdnaMdcFields;

    @Value("${LOGDNA_MDC_TYPES:}")
    private String logdnaMdcFieldsTypes;

    @PostConstruct
    public void setupLogDnaAppender() {

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        if(!StringUtils.isEmpty(logdnaApiKey) && !StringUtils.isEmpty(applicationName)) {

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(lc);
            encoder.setPattern("%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(%5p) %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n%wEx");
            encoder.start();

            LogDnaConfiguration logDnaConfiguration = new LogDnaConfiguration();
            logDnaConfiguration.setAppName(applicationName);
            logDnaConfiguration.setApiKey(logdnaApiKey);
            logDnaConfiguration.setUrl(logdnaUrl);
            logDnaConfiguration.setProfiles(springProfilesActive);
            logDnaConfiguration.setMdcFields(logdnaMdcFields);
            logDnaConfiguration.setTags(tags);

            LogDnaAppender logDnaAppender = new LogDnaAppender();
            logDnaAppender.setName("LOGDNA");
            logDnaAppender.setEncoder(encoder);
            logDnaAppender.setConfig(logDnaConfiguration);
            logDnaAppender.setContext(lc);
            logDnaAppender.start();

            AsyncAppender asyncAppender = new AsyncAppender();
            asyncAppender.setContext(lc);
            asyncAppender.setName("LOGDNAASYNC");
            asyncAppender.setIncludeCallerData(false);
            asyncAppender.setDiscardingThreshold(0);
            asyncAppender.setQueueSize(500);
            asyncAppender.setNeverBlock(true);
            asyncAppender.addAppender(logDnaAppender);
            asyncAppender.start();

            root.addAppender(asyncAppender);
            logger.info("LogDna Logback Appender added to root appender");
        }
        else {
            logger.warn("LogDna Logback Appender not added to root appender");
        }
    }

}
