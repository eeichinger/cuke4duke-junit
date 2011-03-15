package org.oaky.cuke4duke.spring;

import org.springframework.transaction.annotation.SpringTransactionAnnotationParser;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;

import java.lang.reflect.Method;

public class SimpleAnnotationTransactionAttributeSource implements TransactionAttributeSource {
    SpringTransactionAnnotationParser parser = new SpringTransactionAnnotationParser();

    public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        return parser.parseTransactionAnnotation(targetClass);
    }
}
