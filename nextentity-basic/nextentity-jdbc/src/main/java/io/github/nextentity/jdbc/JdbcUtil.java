package io.github.nextentity.jdbc;

import io.github.nextentity.core.TypeCastUtil;
import io.github.nextentity.core.reflect.ReflectUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class JdbcUtil {

    private static final Map<Class<?>, ResultSetGetter<?>> GETTER_MAPS = new HashMap<>();

    static {

        put(Byte.class, ResultSet::getByte);
        put(byte.class, ResultSet::getByte);
        put(Short.class, ResultSet::getShort);
        put(short.class, ResultSet::getShort);
        put(Integer.class, ResultSet::getInt);
        put(int.class, ResultSet::getInt);
        put(Float.class, ResultSet::getFloat);
        put(float.class, ResultSet::getFloat);
        put(Long.class, ResultSet::getLong);
        put(long.class, ResultSet::getLong);
        put(Double.class, ResultSet::getDouble);
        ResultSetGetter<Character> getChar = (resultSet, index) -> {
            String string = resultSet.getString(index);
            if (string == null || string.length() != 1) {
                throw new IllegalStateException(string + " is not a character");
            }
            return string.charAt(0);
        };
        put(char.class, getChar);
        put(Character.class, getChar);
        put(double.class, ResultSet::getDouble);
        put(Boolean.class, ResultSet::getBoolean);
        put(boolean.class, ResultSet::getBoolean);
        put(BigDecimal.class, ResultSet::getBigDecimal);
        put(Date.class, ResultSet::getTimestamp);
        put(String.class, ResultSet::getString);
        put(Time.class, ResultSet::getTime);

        put(java.sql.Date.class, ResultSet::getDate);
        put(Blob.class, ResultSet::getBlob);
        put(Clob.class, ResultSet::getClob);
        put(java.sql.Array.class, ResultSet::getArray);
        put(java.io.InputStream.class, ResultSet::getBinaryStream);
        put(byte[].class, ResultSet::getBytes);
        put(Timestamp.class, ResultSet::getTimestamp);
        put(Instant.class, (resultSet, columnIndex) -> {
            Timestamp timestamp = resultSet.getTimestamp(columnIndex);
            return timestamp.toInstant();
        });
        put(LocalDate.class, (resultSet, columnIndex) -> resultSet.getDate(columnIndex).toLocalDate());
        put(LocalDateTime.class, (resultSet, columnIndex) -> resultSet.getTimestamp(columnIndex).toLocalDateTime());
        put(LocalTime.class, (resultSet, columnIndex) -> resultSet.getTime(columnIndex).toLocalTime());

    }

    public static Object getValue(ResultSet resultSet, int column, Class<?> targetType) throws SQLException {
        Object result = resultSet.getObject(column);
        if (result == null) {
            return null;
        }
        if (!targetType.isInstance(result)) {
            ResultSetGetter<?> getter = GETTER_MAPS.get(targetType);
            if (getter == null) {
                if (Enum.class.isAssignableFrom(targetType)) {
                    result = ReflectUtil.getEnum(targetType, resultSet.getInt(column));
                }
            } else {
                result = getter.getValue(resultSet, column);
            }
        }
        return TypeCastUtil.unsafeCast(result);
    }

    public static void setParameters(PreparedStatement pst, Iterable<?> args) throws SQLException {
        int i = 0;
        for (Object arg : args) {
            i++;
            if (arg instanceof Enum) {
                arg = ((Enum<?>) arg).ordinal();
            }
            if (arg instanceof TypedParameter) {
                setNullParam(pst, i, (TypedParameter) arg);
            } else {
                pst.setObject(i, arg);
            }
        }
    }

    private static void setNullParam(PreparedStatement pst, int parameterIndex, TypedParameter parameter) throws SQLException {
        Class<?> type = parameter.type();
        Object value = parameter.value();
        if (type == SQLXML.class) {
            pst.setObject(parameterIndex, value, Types.SQLXML);
        } else if (type == String.class) {
            pst.setObject(parameterIndex, value, Types.VARCHAR);
        } else if (type == BigDecimal.class) {
            pst.setObject(parameterIndex, value, Types.DECIMAL);
        } else if (type == Short.class) {
            pst.setObject(parameterIndex, value, Types.SMALLINT);
        } else if (type == Integer.class) {
            pst.setObject(parameterIndex, value, Types.INTEGER);
        } else if (type == Long.class) {
            pst.setObject(parameterIndex, value, Types.BIGINT);
        } else if (type == Float.class) {
            pst.setObject(parameterIndex, value, Types.FLOAT);
        } else if (type == Double.class) {
            pst.setObject(parameterIndex, value, Types.DOUBLE);
        } else if (type == byte[].class) {
            pst.setObject(parameterIndex, value, Types.BLOB);
        } else if (type == java.sql.Date.class) {
            pst.setObject(parameterIndex, value, Types.DATE);
        } else if (type == Time.class) {
            pst.setObject(parameterIndex, value, Types.TIME);
        } else if (type == Timestamp.class) {
            pst.setObject(parameterIndex, value, Types.TIMESTAMP);
        } else if (type == Boolean.class) {
            pst.setObject(parameterIndex, value, Types.BOOLEAN);
        } else if (type == Byte.class) {
            pst.setObject(parameterIndex, value, Types.TINYINT);
        } else if (type == Blob.class) {
            pst.setObject(parameterIndex, value, Types.BLOB);
        } else if (type == Clob.class) {
            pst.setObject(parameterIndex, value, Types.CLOB);
        } else if (type == Array.class) {
            pst.setObject(parameterIndex, value, Types.ARRAY);
        } else if (type == Character.class) {
            pst.setObject(parameterIndex, value, Types.CHAR);
        } else if (type == LocalDate.class) {
            pst.setObject(parameterIndex, value, Types.DATE);
        } else if (type == LocalTime.class) {
            pst.setObject(parameterIndex, value, Types.TIME);
        } else if (type == OffsetTime.class) {
            pst.setObject(parameterIndex, value, Types.TIMESTAMP);
        } else if (type == LocalDateTime.class) {
            pst.setObject(parameterIndex, value, Types.TIMESTAMP);
        } else if (type == OffsetDateTime.class) {
            pst.setObject(parameterIndex, value, Types.TIMESTAMP);
        } else if (type == Date.class) {
            pst.setObject(parameterIndex, value, Types.TIMESTAMP);
        } else if (type.isAssignableFrom(Date.class)) {
            pst.setObject(parameterIndex, value, Types.OTHER);
        } else {
            pst.setObject(parameterIndex, value);
        }
    }

    private static <T> void put(Class<T> type, ResultSetGetter<T> getter) {
        GETTER_MAPS.put(type, getter);
    }

    @FunctionalInterface
    interface ResultSetGetter<T> {
        T getValue(ResultSet resultSet, int index) throws SQLException;
    }
}

