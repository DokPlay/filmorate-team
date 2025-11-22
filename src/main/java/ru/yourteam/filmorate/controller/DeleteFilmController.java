package ru.yourteam.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.service.DeleteFilmService;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class DeleteFilmController {

    private final DeleteFilmService service;


    @DeleteMapping("/{id}")
    public void deleteFilmByID(@PathVariable int id) {
        service.deleteFilmById(id);
    }
}
