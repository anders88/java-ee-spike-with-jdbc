package no.steria.kata.javaee;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class PersonJdbcRepository extends AbstractJdbcRepository implements PersonRepository {

    private static final String SELECT_ALL = "select full_name from person";
    private static final String INSERT = "insert into person (id, full_name) values (next value for person_seq, ?)";
    private static final String SELECT_BY_NAME_LIKE = "select full_name from person where upper(full_name) like ?";

    public PersonJdbcRepository(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void createPerson(Person person) {
        try (PreparedStatement statement = prepareStatement(INSERT)) {
            statement.setString(1, person.getFullName());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Person> findPeople(String nameQuery) {
        return nameQuery != null ? findPeopleByName(nameQuery) : findAllPeople();
    }

    private List<Person> findPeopleByName(String nameQuery) {
        try (PreparedStatement statement = prepareStatement(SELECT_BY_NAME_LIKE)) {
            statement.setString(1, "%" +  nameQuery.toUpperCase() + "%");
            return mapPeople(statement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Person> findAllPeople() {
        try (ResultSet resultSet = executeQuery(SELECT_ALL)) {
            return mapPeople(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Person> mapPeople(ResultSet resultSet) throws SQLException {
        ArrayList<Person> people = new ArrayList<>();
        while (resultSet.next()) {
            people.add(mapPerson(resultSet));
        }
        return people;
    }

    private Person mapPerson(ResultSet resultSet) throws SQLException {
        return Person.withFullName(resultSet.getString("full_name"));
    }

}
