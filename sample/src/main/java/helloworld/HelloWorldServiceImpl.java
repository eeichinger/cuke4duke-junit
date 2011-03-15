package helloworld;

import org.springframework.stereotype.Component;

@Component
public class HelloWorldServiceImpl implements HelloWorldService {
    public String getMessage(String greeting, String subject) {
        return greeting + ", " + subject;
    }
}
