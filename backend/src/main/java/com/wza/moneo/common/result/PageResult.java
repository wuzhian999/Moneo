package com.wza.moneo.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageResult<T> {

    private final List<T> items;
    private final long page;
    private final long pageSize;
    private final long total;
}
