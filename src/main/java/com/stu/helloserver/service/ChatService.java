package com.stu.helloserver.service;

import com.stu.helloserver.model.dto.ChatRequestDTO;
import com.stu.helloserver.model.vo.ChatResponseVO;

public interface ChatService {

    ChatResponseVO chat(ChatRequestDTO requestDTO);
}
