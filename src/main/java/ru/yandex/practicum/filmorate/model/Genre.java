package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {
    private int id;
    private String name;

    public Genre() {
    }

    public Genre(int id) {
        this.id = id;
    }
}
