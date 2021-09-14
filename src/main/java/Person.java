import java.util.Objects;

public class Person {


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    public String getCountry() {
        return country;
    }

    public Person(String surname, String name, int age, String country) {
        this.surname = surname;
        this.name = name;
        this.age = age;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", country='" + country + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name) && Objects.equals(surname, person.surname) && Objects.equals(country, person.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, age, country);
    }

    private String name;
    private String surname;
    private int age;
    private String country;
}
