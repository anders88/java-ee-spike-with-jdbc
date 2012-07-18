package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class PersonTest {
    @Test
    public void peopleWithSameNameShouldBeEqual() {
        assertThat(Person.withFullName("Darth")) //
            .isEqualTo(Person.withFullName("Darth")) //
            .isNotEqualTo(Person.withFullName("Anakin")) //
            ;
    }
}
