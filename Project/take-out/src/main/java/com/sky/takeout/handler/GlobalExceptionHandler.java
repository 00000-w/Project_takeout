package com.sky.takeout.handler;


import com.sky.takeout.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一捕获异常，返回标准 Result 格式，而不是500错误页
 */

@Slf4j //在当前类里，自动生成一个 log 对象，让我能直接写 log.info(...)
@RestControllerAdvice //全局捕获(捕获所有controller抛出的异常)异常，统一给前端返回格式
public class GlobalExceptionHandler {
    //捕获业务异常（我自己主动抛出的）
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("业务异常:{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    //捕获其他所有未知异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage());
        return Result.error("系统繁忙，请稍后再试");
    }
}

