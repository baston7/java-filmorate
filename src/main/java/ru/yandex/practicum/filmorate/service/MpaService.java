package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Service
public class MpaService {
    private final MpaDao mpaDao;

    @Autowired
    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Mpa getMpaByID(int id) {
        return mpaDao.getMpaByID(id);
    }

    public List<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }
}
