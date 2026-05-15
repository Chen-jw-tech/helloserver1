package com.stu.helloserver.model.entity;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChatRecord {

    private String sessionId;
    private String userMessage;
    private String assistantMessage;
    private LocalDateTime createTime;
}
