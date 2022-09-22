package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exeption.GenreNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreControllerTest {
    private final GenreController controller;

    @Test
    void genreFindAll() {
        assertEquals(6, controller.findAll().size(), "Жанры не получены");
    }

    @Test
    void genreGetUnknown() {
        assertThrows(GenreNotFoundException.class, () -> controller.getGenre(-1));
    }

    @Test
    void genreGetId() {
        assertEquals("Комедия", controller.getGenre(1).getName(), "Жанр не совпадает с ожидаемым");
    }

}

