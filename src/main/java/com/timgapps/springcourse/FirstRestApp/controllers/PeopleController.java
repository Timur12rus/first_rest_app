package com.timgapps.springcourse.FirstRestApp.controllers;

import com.timgapps.springcourse.FirstRestApp.models.Person;
import com.timgapps.springcourse.FirstRestApp.services.PeopleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Persistence;
import java.util.List;

@RestController
@RequestMapping("/people")
public class PeopleController {

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    private final PeopleService peopleService;


    @GetMapping()
    public List<Person> getPeople() {
        return peopleService.findAll();  // Jackson конвертирует эти объекты в JSON
        // и этот список мы пересылаем по сети в виде JSON'а
        // и на другом конце, какой-нибудь сервис может принять наш список из наших
        // объектов и что-нибудь с ними сделать
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id") int id) { // с помощью аннотации @PathVariable получаем доступ
        // к id, который пришел в адресе запроса ("id" помещаем в аргумент метода int id)
        return peopleService.findOne(id);  // Jackson автоматически сконвертирует в объект JSON
    }
}
