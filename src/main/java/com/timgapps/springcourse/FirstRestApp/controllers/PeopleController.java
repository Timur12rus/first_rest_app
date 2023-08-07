package com.timgapps.springcourse.FirstRestApp.controllers;

import com.timgapps.springcourse.FirstRestApp.models.Person;
import com.timgapps.springcourse.FirstRestApp.services.PeopleService;
import com.timgapps.springcourse.FirstRestApp.util.PersonErrorResponse;
import com.timgapps.springcourse.FirstRestApp.util.PersonNotCreatedException;
import com.timgapps.springcourse.FirstRestApp.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Persistence;
import javax.validation.Valid;
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

    // метод для создания нового человека
    @PostMapping
    // здесь мы могли возвращать любой объект, jackson его конвертирует в JSON,
    // и клиент его получит на другом конце
    // мы просто будем возвращать специальный объект, который будет представлять http-ответ
    // здесь от клиента мы должны принять JSON и его сконвертировать в объект класса Person
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Person person,
                                             BindingResult bindingResult) { // помечаем параметр с помощью
        // аннотации @RequestBody, когда мы пришлем JSON в этот метод контроллера, RequestBody автоматически
        // сконвертирует его в объект класса Person
        // аннотация @Valid будет проверять на валидность нашего человека,
        // по аннотациям проверки в модели(Person)

        // если в bindingResult есть какие-то ошибки, значит клиент прислал нам какого-то невалидного человека,
        // то мы выкинем какое-то исключение
        if (bindingResult.hasErrors()) {
            // здесь ошибки валидации мы совместим в одну большую строку,
            // эту строку мы хотим отправить обратно клиенту, чтобы он посмотрел и смог исправить данные о человеке
            StringBuilder errorMessage = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors(); // добавим ошибки в List
            for (FieldError error : errors) {
                errorMessage.append(error.getField()) // на каком поле была совершена ошибка
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            // теперь когда мы подготовили сообщение об ошибке
            // мы должны выбросить исключение и должны клиенту отправить сообщение с этой ошибкой
            throw new PersonNotCreatedException(errorMessage.toString());

        }
        peopleService.save(person); // сохраняем человека

        return ResponseEntity.ok(HttpStatus.OK); // это такой стандартный способ ответить чем-нибудь клиенту
        // здесь вернется самый простой объект с сообщением, что все прошло успешно
        // это мы используем, когда не хотим создавать какой-то отдельный объект об успехе
        // и отправляем HTTP ответ с пустым телом и со статусом 200
    }

    @ExceptionHandler
    // метод обрабатывает исключение
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        // создаем наш response(объект, который мы хотим вернуть человеку
        PersonErrorResponse personErrorResponse = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );

        // в HTTP ответе тело ответа(response) и статус в заголовке
        return new ResponseEntity<>(personErrorResponse, HttpStatus.NOT_FOUND); // NOT_FOUND - 404 статус
    }

    @ExceptionHandler
    ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        // в HTTP ответе тело ответа (response) и статус в заголовке
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
