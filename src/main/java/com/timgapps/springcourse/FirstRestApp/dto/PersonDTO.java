package com.timgapps.springcourse.FirstRestApp.dto;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;


// класс DTO (Data Transfer Object)
// специальный класс, который будет служить для общения с клиентом
public class PersonDTO {

    // здесь будем описывать те поля, которые будут приходить от клиента, и которые мы будем клиенту отправлять
    // добавим аннотации для проверки соответствия полей модели данным из запроса приходящего от клиента
    // DTO вообще никак не связан с базой данных, не помечен аннотацией @Entity, не помечен аннотацией @Table и
    // нет аннотаций @Column
    // оставляем валидацию полей
    // нет поля id, это поле не приходит от клиента, и клиенту в принципе его знать не надо,
    // id назначается только на сервере

    // DTO используется на уровне контроллера и мы не должны глубже заходить с DTO
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name;


    @Min(value = 0, message = "Age should be greater than 0")
    private int age;

    @Email
    @NotEmpty(message = "Name should not be empty")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
