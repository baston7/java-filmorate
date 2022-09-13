package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.lang.NonNull;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    @NonNull
    private final LocalDate releaseDate;
    @Positive
    private final int duration;
    private int likes;
    private Set<Long> likedUsersId = new HashSet<>();

}
