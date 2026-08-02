package com.chc.ai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.List;
import java.util.Map;

/**
 * 使用指定语言进行回答-基于模板参数实现
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Slf4j
public class UseLanguageAdvisor2 implements BaseAdvisor {
    private static final String ENHANCE_PROMPT_TPL = """
            使用{languageType}回答
            """;
    private String language;
    public UseLanguageAdvisor2(String language) {
        this.language = language;
    }

    /**
     * 提示词类型 {@link org.springframework.ai.chat.messages.MessageType}
     */
    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        // 获取所有类型的消息
        List<Message> instructions = chatClientRequest.prompt().getInstructions();
        // 提示词模板
        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(ENHANCE_PROMPT_TPL)
                .build();
        // 替换变量,比如获取用户权限后进行填充就可以对ai进行权限控制
        String finalPrompt = promptTemplate.render(Map.of("languageType", language));
        log.info("替换后的提示词模板内容:{}", finalPrompt);
        instructions.add(new UserMessage(finalPrompt));
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
