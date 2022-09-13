package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private long id;
    @Email
    private final String email;
    @NotBlank
    private final String login;
    private String name;
    @PastOrPresent
    private final LocalDate birthday;
    private Set<Long> friendsId = new HashSet<>();
}
