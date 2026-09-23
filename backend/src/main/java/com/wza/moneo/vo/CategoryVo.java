package com.wza.moneo.vo;

import com.wza.moneo.common.enums.RecordType;

public record CategoryVo(Long id, String name, RecordType type, String icon, String color, Integer sortOrder) {
}
