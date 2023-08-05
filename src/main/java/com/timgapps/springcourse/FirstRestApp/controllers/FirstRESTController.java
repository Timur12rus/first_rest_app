package com.timgapps.springcourse.FirstRestApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// реализуем контроллеры, но в этих контроллерах будем возвращать данные,
// а не адрес представления(ссылку, как делали раньше), это и есть REST приложение
@Controller
//@RestController (можем использовать эту аннотацию, чтобы не писать аннотацию @ResponseBody и
// говорить, что метод возвращает данные, можем просто её использовать, которая делает то-же самое)
@RequestMapping("/api")
public class FirstRESTController {

    @ResponseBody // используем аннотацию, является главной аннотацией для создания REST приложений
    // spring понимает, что мы больше не возвращаем название для представления, мы возвращаем данные
    // только благодаря аннотации @ResponseBody
    // поэтому строка, которую мы вернем, будет возвращена как данные, просто как строка
    // т.е. клиент получит объект класса String
    @GetMapping("/sayHello")
    public String sayHello() {
        return "Hello world!";
    }


}
