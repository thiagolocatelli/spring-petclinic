package org.springframework.samples.petclinic.config.logdna;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogDnaConfiguration {

    private String apiKey;
    private String appName;
    private String url = "https://logs.logdna.com/logs/ingest";
    private String tags;
    private String profiles;
    private List<String> mdcFields = new ArrayList<String>();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    public List<String> getMdcFields() {
        return mdcFields;
    }

    public void setMdcFields(String mdcFields) {
        this.mdcFields = Arrays.asList(mdcFields.split(","));
    }

}
