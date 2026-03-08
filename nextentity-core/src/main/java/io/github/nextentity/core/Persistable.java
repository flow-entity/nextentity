package io.github.nextentity.core;

import java.io.Serializable;

/**
 * @author HuangChengwei
 * @since 1.0.0
 */
public interface Persistable<ID extends Serializable> extends Serializable {
    ID getId();
}
