import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class StreamMyTest {
    List<Person> listPersons;

    @BeforeEach
    public  void prepare(){
        listPersons = new ArrayList<>(){{
            add(new Person("Ivanov", "Ivan", 29, "Russia"));
            add(new Person("Ivanov", "Vladislav", 79, "Russia"));
            add(new Person("Ivanov", "Andrey", 25, "Russia"));
            add(new Person("Vasiliev", "Victor", 23, "Russia"));
            add(new Person("Nikolayev", "Nikolay", 21, "Russia"));
            add(new Person("Pavlov", "Pavel", 20, "Russia"));
            add(new Person("Ivanov", "Vladimir", 24, "Russia"));
            add(new Person("Yan", "YYY", 25, "Russia"));
        }};

    }
    @Test
    public void test1() {
        System.out.println("Stream ");
        StreamMy<Person> streamPersons = StreamMy.of(listPersons);
       // Stream str = listPersons.stream();
       // str.forEach(System.out::println);
        //str.forEach(System.out::println);


       // streamPersons.forEach(System.out::println);
        streamPersons.filter(p -> p.getSurname().equals("Ivanov")).forEach(System.out::println);
    }

    @Test
    public void test2() {

        System.out.println("Stream ");
        StreamMy<Person> streamPersons = StreamMy.of(listPersons);
        streamPersons.filter(p ->  p.getSurname()
                        .equals("Ivanov")).
                transform(Person::getName).
                forEach(System.out::println);
    }

    @Test
    public void testMap()  {

        System.out.println("Stream & Map ");
        StreamMy<Person> streamPersons = StreamMy.of(listPersons);
        streamPersons.filter(p ->  p.getSurname()
                        .equals("Ivanov")).
                toMap(p->p.getAge(),p->p.getName() + " " + p.getSurname()).entrySet().
                forEach(System.out::println);

        //  streamPersons.forEach(System.out::println);
        //streamPersons.forEach(x->System.out.println(x.getClass()));
    }
}

