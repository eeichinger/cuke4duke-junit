
package helloworld;

import org.springframework.transaction.annotation.Transactional;

public interface HelloWorldService {

    void setMessagePrefix(String prefix);
    String getMessagePrefix();
    String getMessage(String greeting, String subject);
}