package com.timgapps.springcourse.FirstRestApp.services;

import com.timgapps.springcourse.FirstRestApp.models.Person;
import com.timgapps.springcourse.FirstRestApp.repositories.PeopleRepository;
import com.timgapps.springcourse.FirstRestApp.util.PersonNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
//        return foundPerson.orElse(null);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    // метод будет принимать объект класса Person из контроллера от клиента и будет сохранять его в базу данных
    @Transactional
    public void save(Person person) {
        enrichPerson(person);

        peopleRepository.save(person);
    }

    // обогащаем объект с помощью метода
    private void enrichPerson(Person person) {

        // на самом сервере добавляются поля
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreatedWho("ADMIN");
    }
}
