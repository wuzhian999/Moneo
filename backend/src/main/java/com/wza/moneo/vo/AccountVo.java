package com.wza.moneo.vo;

import java.math.BigDecimal;

public record AccountVo(Long id, String name, String icon, String color, BigDecimal initialBalance, Boolean isDefault) {
}
