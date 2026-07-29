package com.chc.ai.ex.handler;



import com.chc.ai.enums.CodeEnum;
import com.chc.ai.ex.BizEx;
import com.chc.ai.res.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 统一异常处理
 * @author ${authorName}
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestControllerAdvice
@Slf4j
public class ExHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException e) {
        // 直接返回 404，不记录错误日志
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(BizEx.class)
    public Result<String> handleEx(BizEx e) {
        log.error("业务异常",e);
        return Result.buildFailure(e.getCode(),e.getMessage());
    }

    /**
     * 处理未精准匹配的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleEx(Exception e) {
        log.error("处理未精准匹配的异常",e);
        return Result.buildFailure(String.valueOf(CodeEnum.SYS_ERR));
    }
}
