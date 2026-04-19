package io.github.nextentity.core.configuration;


public interface PostProcessor<T> {

    T process(T image);

    default int getOrder() {
        return 0;
    }

}
