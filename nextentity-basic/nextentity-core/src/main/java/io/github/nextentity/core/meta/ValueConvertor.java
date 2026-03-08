package io.github.nextentity.core.meta;

public interface ValueConvertor<X, Y> {

    Y convertToDatabaseColumn(X attributeValue);

    X convertToEntityAttribute(Y databaseValue);

    default Class<? extends Y> getDatabaseColumnType() {
        return null;
    }

}
