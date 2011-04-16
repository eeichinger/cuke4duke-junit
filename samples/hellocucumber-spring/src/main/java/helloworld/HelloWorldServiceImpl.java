package helloworld;

import org.springframework.stereotype.Component;

@Component
public class HelloWorldServiceImpl implements HelloWorldService {

    private String messagePrefix;

    public String getMessagePrefix() {
        return messagePrefix;
    }

    public void setMessagePrefix(String messagePrefix) {
        this.messagePrefix = messagePrefix;
    }

    public String getMessage(String greeting, String subject) {
        return messagePrefix + greeting + ", " + subject;
    }
}
