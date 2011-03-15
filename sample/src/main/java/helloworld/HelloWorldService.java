
package helloworld;

import org.springframework.transaction.annotation.Transactional;

public interface HelloWorldService {
    @Transactional
    String getMessage(String greeting, String subject);
}