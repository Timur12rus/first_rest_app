package com.timgapps.springcourse.FirstRestApp.controllers;

import com.timgapps.springcourse.FirstRestApp.dto.PersonDTO;
import com.timgapps.springcourse.FirstRestApp.models.Person;
import com.timgapps.springcourse.FirstRestApp.services.PeopleService;
import com.timgapps.springcourse.FirstRestApp.util.PersonErrorResponse;
import com.timgapps.springcourse.FirstRestApp.util.PersonNotCreatedException;
import com.timgapps.springcourse.FirstRestApp.util.PersonNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


// в контроллере должно быть минимальное кол-во логики, логика должна быть перенесена в Service
@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    private final ModelMapper modelMapper;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
    }


    @GetMapping()
    public List<PersonDTO> getPeople() {
//    public List<Person> getPeople() {
//        return peopleService.findAll();  // Jackson конвертирует эти объекты в JSON
        // и этот список мы пересылаем по сети в виде JSON'а
        // и на другом конце, какой-нибудь сервис может принять наш список из наших
        // объектов и что-нибудь с ними сделать

        // -----------------------------------------------------------------------------------------------//
        // исправим и тоже будем отдавать клиенту DTO вместо модели, т.к. в модели существуют поля, которые
        // клиенту знать не нужно и не нужны
        return peopleService.findAll()
                .stream() // используем Stream
                .map(this::convertToPersonDTO) // смапим все сущности в DTO, т.е. вызовем convertToPersonDTO()
                // на каждом из этих объектов Person, которые получили из сервиса

                .collect(Collectors.toList());  // и построим список из этих DTO
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) { // с помощью аннотации @PathVariable получаем доступ
//    public Person getPerson(@PathVariable("id") int id) { // с помощью аннотации @PathVariable получаем доступ
        // к id, который пришел в адресе запроса ("id" помещаем в аргумент метода int id)
        // статус - 200
//        return peopleService.findOne(id);  // Jackson автоматически сконвертирует в объект JSON
        // вызывающему человеку мы можем отправить специальный JSON с сообщением об ошибке
        // чтобы это делать в spring'е есть специальная аннтонация @ExceptionHandler
        // этой аннотацией мы отмечаем метод, который в себя ловит исключение и который возвращает необходимый
        // JSON объект

        // ---------------------------------------------------------------------------//
        // исправим и тоже будем отдавать клиенту DTO вместо модели, т.к. в модели существуют поля, которые
        // клиенту знать не нужно и не нужны
        return convertToPersonDTO(peopleService.findOne(id));
    }

    // метод для создания нового человека
    @PostMapping
    // здесь мы могли возвращать любой объект, jackson его конвертирует в JSON,
    // и клиент его получит на другом конце
    // мы просто будем возвращать специальный объект, который будет представлять http-ответ
    // здесь от клиента мы должны принять JSON и его сконвертировать в объект класса Person

    // раньше принимали от клиента модель Person
    // public ResponseEntity<HttpStatus> create(@RequestBody @Valid Person person,

    // теперь принимает DTO
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
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
        // раньше сохраняли модель
//        peopleService.save(person); // сохраняем человека

        // теперь должны сконвертировать в модель нашей сущности "Person"
        peopleService.save(convertToPerson(personDTO)); // сохраняем человека

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

    private Person convertToPerson(PersonDTO personDTO) {
        // создаем сущность Person
//        Person person = new Person();

        // есть недостаток кода: если полей много получается много строк здесь
        // для этого есть решение - зависимость, которая называется ModelMapper
        // она специальн используется чтобы мапить DTO в модель и наоборот, мапить модель в DTO (когда мы
        // отдаем response для клиента
//        person.setName(personDTO.getName());
//        person.setAge(personDTO.getAge());
//        person.setEmail(personDTO.getEmail());

        // объект будет недоделланный, не доконструированный с null'ами
        // и в СЕРВИСЕ, перед тем как сохранить человека в базу данных в методе save(), мы дополним его

        // положим в объект Person (положим в человека) дополнительные данные,
        // которые назначаются уже на самом сервере

        // для этого создадим отельный метод
//        enrichPerson(person); // (обогати этот объект) (этот метод будет принадлежать СЕРВИСУ)

        //----------------------------------------------------------------------------
        // есть недостаток кода: если полей много получается много строк здесь
        // для этого есть решение - зависимость, которая называется ModelMapper
        // используем ModelMapper
        // здесь мы создаем объект modelMapper вручную, но можем делегировать это самому Spring'у
        // В большом проекте мы часто используем ModelMapper и повсеместно, в большом кол-ве контроллеров
        // поэтому чтобы каждый раз нам не создавать новый объект этого класса мы испльзуем Spring,
        // который нам создаст этот объект (Singleton) ModelMapper и использовать его везде и мапить DTP в модель
        // и обратно
        // здесь закоментируем создание объекта вручную
//        ModelMapper modelMapper = new ModelMapper();

        // modelMapper найдет все поля, которые совпадают по названию (например поле "name" оно совпадет с
        // полем "name" модели)

        return modelMapper.map(personDTO, Person.class);   // маппим PersonDTO в модель Person
        // ModelMapper берет на себя полностью маппинг между DTO и моделью
    }

    // принимает модель Person и отдает DTO
    private PersonDTO convertToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);    // смаппится модель в DTO
    }
}
