package helloworld;

public class HelloWorldServiceImpl implements HelloWorldService {
    public String getMessage(String greeting, String subject) {
        return greeting + ", " + subject;
    }
}
