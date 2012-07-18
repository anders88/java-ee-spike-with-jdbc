package no.steria.kata.javaee;

import java.util.List;

public interface PersonRepository {

    void createPerson(Person person);

    Transaction startTransaction();

    List<Person> findPeople(String nameQuery);

}
