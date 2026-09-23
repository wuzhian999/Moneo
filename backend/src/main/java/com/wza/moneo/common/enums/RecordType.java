package com.wza.moneo.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecordType implements IEnum<String> {
    EXPENSE("expense"),
    INCOME("income");

    @EnumValue
    @JsonValue
    private final String value;

    RecordType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RecordType fromValue(String value) {
        for (RecordType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("不支持的收支类型: " + value);
    }
}
