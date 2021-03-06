package com.projecta.eleven.justclassbackend.utils;

import java.util.HashMap;
import java.util.Objects;

public interface MapSerializable {

    default HashMap<String, Object> toMap() {
        return toMap(false);
    }

    HashMap<String, Object> toMap(boolean isTimestampInMilliseconds);

    default void ifFieldNotNullThenPutToMap(String field, Object value, HashMap<String, Object> map) {
        if (Objects.nonNull(value)) {
            map.put(field, value);
        }
    }
}
