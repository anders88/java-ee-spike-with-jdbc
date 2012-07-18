package no.steria.kata.javaee;

import com.google.common.base.Objects;

public class Person {

    private final String fullName;

    public Person(String fullName) {
        this.fullName = fullName;
    }

    public static Person withFullName(String fullName) {
        return new Person(fullName);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return Objects.equal(fullName, ((Person) obj).fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fullName);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("fullName", fullName)
                .toString();
    }

    public String getFullName() {
        return fullName;
    }

}
