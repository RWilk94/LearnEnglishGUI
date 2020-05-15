
import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassTest {

  @Test
  void test() {
    Optional<String> s = Optional.of("test.html");
    assertEquals(Optional.of("TEST"), s.map(String::toUpperCase));
  }

  @Test
  void test2() {
    assertEquals(Optional.of(Optional.of("STRING")),
        Optional
            .of("string")
            .map(s -> Optional.of("STRING")));
  }

  @Test
  void test3() {
    assertEquals(Optional.of("STRING"), Optional
        .of("string")
        .flatMap(s -> Optional.of("STRING")));
  }

  @Test
  void test4() {
    Optional<String> test = Optional.ofNullable(null);

    String s = test.flatMap(this::te).orElse("ELSE");
    assertEquals("TEST", s);

//    test.html.map(String::toUpperCase);
  }

  private Optional<String> te(String s) {
    return Optional.of("TEST");
  }

}
