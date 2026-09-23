package com.wza.moneo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.wza.moneo.common.enums.RecordType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("category")
public class Category {

    @TableId
    private Long id;
    private String name;
    private RecordType type;
    private String icon;
    private String color;
    private Integer sortOrder;
    @TableLogic
    private Integer deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
