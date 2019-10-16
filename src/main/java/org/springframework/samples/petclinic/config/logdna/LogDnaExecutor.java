package org.springframework.samples.petclinic.config.logdna;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LogDnaExecutor {

    private final static Logger errorLog = LoggerFactory.getLogger(LogDnaExecutor.class);

    private final static String CUSTOM_USER_AGENT = "LogbackLogDnaAppender";
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private PatternLayoutEncoder encoder;
    private LogDnaConfiguration configuration;
    private ObjectMapper mapper;
    private OkHttpClient httpClient;
    private String hostname;

    public LogDnaExecutor(String hostname, LogDnaConfiguration configuration, PatternLayoutEncoder encoder) {
        this.hostname = hostname;
        this.configuration = configuration;
        this.encoder = encoder;

        this.mapper = new ObjectMapper();
        this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    protected void append(ILoggingEvent event) {

        try {
            String jsonData = this.mapper.writeValueAsString(buildPostData(event));

            Request.Builder requestBuilder = getRequestBuilder();
            RequestBody requestBody = RequestBody.create(JSON, jsonData);
            Request httpRequest = requestBuilder.post(requestBody).build();
            Response httpResponse = httpClient.newCall(httpRequest).execute();

            if (!httpResponse.isSuccessful()) {
                errorLog.error("Error calling LogDna : {} ({})", httpResponse.body().string(), httpResponse.code());
            }

            httpResponse.body().close();

        } catch (JsonProcessingException e) {
            errorLog.error("Error processing JSON data : " + e.getMessage(), e);
        } catch (Exception e) {
            errorLog.error("Error calling LogDna : " + e.getMessage(), e);
        }
    }

    protected Map<String, Object> buildPostData(ILoggingEvent event) {
        Map<String, Object> line = new HashMap<String, Object>();
        line.put("timestamp", event.getTimeStamp());
        line.put("level", event.getLevel().toString());
        line.put("app", configuration.getAppName());
        line.put("env", configuration.getProfiles());
        line.put("line", this.encoder != null ? new String(this.encoder.encode(event)) : event.getFormattedMessage());

        Map<String, Object> meta = new HashMap<String, Object>();
        meta.put("logger", event.getLoggerName());

        if (configuration.getMdcFields().size() > 0 && !event.getMDCPropertyMap().isEmpty()) {
            for (Map.Entry<String, String> entry : event.getMDCPropertyMap().entrySet()) {
                if (configuration.getMdcFields().contains(entry.getKey())) {
                    meta.put(entry.getKey(), entry.getValue());
                }
            }
        }
        line.put("meta", meta);

        Map<String, Object> lines = new HashMap<String, Object>();
        lines.put("lines", Arrays.asList(line));
        return lines;
    }

    private Request.Builder getRequestBuilder() {
        return new Request.Builder()
                .url(getQueryUrl())
                .header("User-Agent", CUSTOM_USER_AGENT)
                .header("Accept", JSON.toString())
                .header("Content-Type", JSON.toString())
                .header("apikey", configuration.getApiKey());
    }

    private String getQueryUrl() {
        return configuration.getUrl() + "?hostname=" + hostname +
                "&now=" + System.currentTimeMillis() +
                "&tags=" + configuration.getTags();
    }

}
