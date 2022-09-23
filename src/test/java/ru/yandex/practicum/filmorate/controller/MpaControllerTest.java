package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exeption.MpaNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MpaControllerTest {
    private final MpaController controller;

    @Test
    void mpaFindAll() {
        controller.findAll();
        assertEquals(5, controller.findAll().size(), "Рейтинги не получены");
    }

    @Test
    void mpaGetUnknown() {
        assertThrows(MpaNotFoundException.class, () -> controller.getMpa(-1));
    }

    @Test
    void mpaGetId() {
        assertEquals("G", controller.getMpa(1).getName(), "Рейтинг не совпадает с ожидаемым");
    }
}