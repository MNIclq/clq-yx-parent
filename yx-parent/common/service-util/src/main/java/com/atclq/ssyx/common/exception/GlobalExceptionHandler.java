package com.atclq.ssyx.common.exception;

import com.atclq.ssyx.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//AOP 面向切面
@ControllerAdvice
//@ControllerAdvice是Spring框架中的一个注解，用于全局处理控制器(Controller)抛出的异常。它可以定义一个类，并在该类上添加@ControllerAdvice注解，然后在这个类中定义方法来处理特定类型的异常或者全局的异常。
//使用@ControllerAdvice的一个典型场景是在处理多个控制器中的相同异常时，避免在每个控制器中都重复编写相同的异常处理代码。通过将异常处理逻辑放在一个统一的地方，可以提高代码的可维护性和可读性。
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class) //异常处理器
    @ResponseBody  //返回json数据   将对象转换成Json数据返回
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail(null);
    }

    //自定义异常处理
    @ExceptionHandler(SsyxException.class)
    @ResponseBody
    public Result error(SsyxException exception) {
        return Result.build(null,exception.getCode(),exception.getMessage());
    }
}
