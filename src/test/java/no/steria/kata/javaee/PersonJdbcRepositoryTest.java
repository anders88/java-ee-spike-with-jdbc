package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;


public class PersonJdbcRepositoryTest {

    private static PersonRepository personRepository;

    @Test
    public void shouldSaveAndRetrivePerson() {
        Person darth = Person.withFullName("Darth");
        try (Transaction tx = personRepository.startTransaction()) {
            personRepository.createPerson(darth);
            assertThat(personRepository.findPeople(null)).contains(darth);
        }
    }

    @Test
    public void shouldOnlySaveOnSuccessfulTransaction() {
        Person saved = Person.withFullName("Darth");
        Person unsaved = Person.withFullName("Jar-Jar");
        try (Transaction tx = personRepository.startTransaction()) {
            personRepository.createPerson(saved);
            tx.setCommit();
        }
        try (Transaction tx = personRepository.startTransaction()) {
            personRepository.createPerson(unsaved);
        }
        try (Transaction tx = personRepository.startTransaction()) {
            assertThat(personRepository.findPeople(null)).contains(saved).excludes(unsaved);
        }
    }
    
    @Test
    public void shouldFindPeopleOnName() {
        Person luke = Person.withFullName("Luke Skywalker");
        Person ani = Person.withFullName("Anakin Skywalker");
        Person jarjar = Person.withFullName("JarJar Binks");
        try (Transaction tx = personRepository.startTransaction()) {
            personRepository.createPerson(luke);
            personRepository.createPerson(ani);
            personRepository.createPerson(jarjar);
            assertThat(personRepository.findPeople("sky")).contains(luke,ani).excludes(jarjar);
        }
    }

    @BeforeClass
    public static void createRepository() throws Exception {
        personRepository = createPersonRepository();
    }

    private static PersonRepository createPersonRepository() throws SQLException, IOException {
        DataSource dataSource = DataSources.inMemoryDataSource("foo");
        DataSources.initDatabaseSchema(dataSource);
        return new PersonJdbcRepository(dataSource);
    }
}
