package com.timgapps.springcourse.FirstRestApp.controllers;

import com.timgapps.springcourse.FirstRestApp.models.Person;
import com.timgapps.springcourse.FirstRestApp.services.PeopleService;
import com.timgapps.springcourse.FirstRestApp.util.PersonErrorResponse;
import com.timgapps.springcourse.FirstRestApp.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        // статус - 200
        return peopleService.findOne(id);  // Jackson автоматически сконвертирует в объект JSON
        // вызывающему человеку мы можем отправить специальный JSON с сообщением об ошибке
        // чтобы это делать в spring'е есть специальная аннтонация @ExceptionHandler
        // этой аннотацией мы отмечаем метод, который в себя ловит исключение и который возвращает необходимый
        // JSON объект
    }

    @ExceptionHandler
    // метод обрабатывае исключение
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        // создаем наш response(объект, который мы хотим вернуть человеку
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );

        // в HTTP
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND); // NOT_FOUND - 404 статус

    }
}
