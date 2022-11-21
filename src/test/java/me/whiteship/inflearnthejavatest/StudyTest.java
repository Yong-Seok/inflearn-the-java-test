package me.whiteship.inflearnthejavatest.study;

import me.whiteship.inflearnthejavatest.domain.Study;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest {

    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

    int value = 1;

    @DisplayName("스터디 만들기(fast) \uD83D\uDE31")
    @Order(2)
    @FastTest
    void create_new_study_fast() {
        System.out.println(this);
        System.out.println(value++);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit 0보다 커야한다.", exception.getMessage());

        Study study = new Study(10);
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + " 상태다."),
                () -> assertTrue(study.getLimit() > 0, () -> "스터디 최대 참석 가능 인원은 0보다 커야한다."),
                () -> assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
                    new Study(10);
                    Thread.sleep(10);
                })
        );
    }

    @DisplayName("스터디 만들기(slow) \uD83D\uDE31")
    @Order(1)
    @SlowTest
    @EnabledOnJre({JRE.JAVA_11})
    void create_new_study_slow() throws InterruptedException {
        Thread.sleep(1010L);

        System.out.println(this);
        System.out.println(value++);

        String test_env = "LOCAL";
        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            System.out.println("조건에 맞으면 고고!");
        });
    }

    @DisplayName("스터디 만들기 - RepeatedTest")
    @Order(3)
    @RepeatedTest(value = 4, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatedTest(RepetitionInfo repetitionInfo) {
        System.out.println("test " + repetitionInfo.getCurrentRepetition() + " / " + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("스터디 만들기 - ParameterizedTest")
    @Order(4)
    @ParameterizedTest(name = "{index}. {displayName} message={0}")
//    @ValueSource(strings = {"One", "Two", "Three", "Four"})
//    @ValueSource(ints = {10, 20, 30})
    @CsvSource({"10, '자바 스터디'", "20, '스프링'"})
    void parameterizedTest(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }

    static class StudyConverter extends SimpleArgumentConverter {
        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    static class StudyAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0), argumentsAccessor.getString(0));
        }
    }

    // static 이어야하나, TestInstance인 경우는 static 아니여도 됨
    @BeforeAll
    @Disabled
    static void beforeAll() {
        System.out.println("beforeAll");
    }

    // static 이어야하나, TestInstance인 경우는 static 아니여도 됨
    @AfterAll
    @Disabled
    static void afterAll() {
        System.out.println("afterAll");
    }

    @BeforeEach
    @Disabled
    void beforeEach() {
        System.out.println("beforeEach");
    }

    @AfterEach
    @Disabled
    void afterEach() {
        System.out.println("afterEach");
    }
}