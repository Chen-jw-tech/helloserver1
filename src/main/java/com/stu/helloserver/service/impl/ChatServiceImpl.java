package com.stu.helloserver.service.impl;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.stu.helloserver.model.dto.ChatRequestDTO;
import com.stu.helloserver.model.vo.ChatResponseVO;
import com.stu.helloserver.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder chatClientBuilder, StringRedisTemplate stringRedisTemplate) {
        this.chatClient = chatClientBuilder
                .defaultSystem("你是一名专业、友好、简洁的中文智能助手，请结合历史上下文回答问题。")
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public ChatResponseVO chat(ChatRequestDTO requestDTO) {
        String sessionId = requestDTO.getSessionId();
        String message = requestDTO.getMessage();
        String redisKey = "chat:session:" + sessionId;

        List<String> records = stringRedisTemplate.opsForList().range(redisKey, 0, -1);
        String historyText = "";
        if (records != null && !records.isEmpty()) {
            historyText = String.join("\n", records);
        }

        String finalPrompt = """
                以下是历史对话:
                %s
                当前用户问题:
                %s
                """.formatted(historyText, message);

        String answer = chatClient.prompt(finalPrompt)
                .call()
                .content();

        String recordText = "用户:" + message + "\n助手:" + answer;
        stringRedisTemplate.opsForList().rightPush(redisKey, recordText);

        Long size = stringRedisTemplate.opsForList().size(redisKey);
        if (size != null && size > 3) {
            stringRedisTemplate.opsForList().trim(redisKey, size - 3, size - 1);
        }

        return new ChatResponseVO(message, answer);
    }
}
