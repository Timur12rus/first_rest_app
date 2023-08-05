package com.timgapps.springcourse.FirstRestApp.repositories;

import com.timgapps.springcourse.FirstRestApp.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// репозиторий, который нам дает доступ к базе данных, работает с базой данных
@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

}
