package com.timgapps.springcourse.FirstRestApp.util;

public class PersonNotCreatedException extends RuntimeException {
    // вызвали дефолтный конструктор
    public PersonNotCreatedException(String msg) {
        super(msg);
    }
}
