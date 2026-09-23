package com.wza.moneo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wza.moneo.common.enums.RecordType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("bill")
public class Bill {

    @TableId
    private Long id;
    private RecordType type;
    private Long categoryId;
    private Long accountId;
    private BigDecimal amount;
    private LocalDateTime occurredAt;
    private String note;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
