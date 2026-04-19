package io.github.nextentity.core.configuration;


public interface PostProcessor<T> {

    T process(T image);

    int getOrder();

}
