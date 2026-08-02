package com.chc.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

/**
 * 使用指定语言进行回答
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
public class UseLanguageAdvisor implements BaseAdvisor {
    private String language;
    public UseLanguageAdvisor(String language) {
        this.language = language;
    }

    /**
     * 提示词类型 {@link org.springframework.ai.chat.messages.MessageType}
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取所有类型的消息
        List<Message> instructions = chatClientRequest.prompt().getInstructions();
        instructions.add(new UserMessage("使用"+language+"回答"));
        ChatClientRequest newChatClientRequest = chatClientRequest
                // 复制一个新的对象
                .mutate()
                .prompt(Prompt.builder().messages(instructions).build())
                .build();
        return newChatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    // 值越小越先执行
    @Override
    public int getOrder() {
        return 0;
    }
}
