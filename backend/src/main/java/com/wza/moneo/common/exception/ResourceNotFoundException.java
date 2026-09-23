package com.wza.moneo.common.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource) {
        super(404, resource + "不存在");
    }
}
