package org.cynic.spring_stuff.function;

import java.util.function.Function;
import org.cynic.spring_stuff.domain.ApplicationException;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {

    static <T, R, E extends Throwable> Function<T, R> withTry(ThrowingFunction<T, R, E> consumer,
        Function<Throwable, ApplicationException> exception) {
        return it -> {
            try {
                return consumer.apply(it);
            } catch (Throwable e) {
                throw exception.apply(e);
            }
        };
    }

    R apply(T t) throws E;
}
