package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DisplayMessagesConfiguration {

    @Value("${global.property:This is an global property coming from local}")
    private String globalMessage;

    @Value("${global.property:This is an application property coming from local}")
    private String applicationMessage;

    @Value("${demo.secret:This is a secret coming from local}")
    private String demoSecret;

    public String getGlobalMessage() {
        return globalMessage;
    }

    public void setGlobalMessage(String globalMessage) {
        this.globalMessage = globalMessage;
    }

    public String getApplicationMessage() {
        return applicationMessage;
    }

    public void setApplicationMessage(String applicationMessage) {
        this.applicationMessage = applicationMessage;
    }

    public String getDemoSecret() {
        return demoSecret;
    }

    public void setDemoSecret(String demoSecret) {
        this.demoSecret = demoSecret;
    }
}
