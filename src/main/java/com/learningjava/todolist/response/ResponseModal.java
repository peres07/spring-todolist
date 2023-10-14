package com.learningjava.todolist.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponseModal {
    private Object data;
    private String message;
    private Integer statusCode;
}
