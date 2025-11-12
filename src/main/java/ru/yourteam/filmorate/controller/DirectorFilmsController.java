// Контроллер для работы с фильмами и их режиссёрами.
package ru.yourteam.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.repository.DirectorRepository;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
public class DirectorFilmsController {

    private final DirectorRepository directorRepository;

    @Autowired
    public DirectorFilmsController(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @GetMapping
    public Collection<Director> findAll() {
        return directorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Director getDirector(@PathVariable long id) {
        return directorRepository.getById(id);
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        return directorRepository.create(director);
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        return directorRepository.update(director);
    }

    @DeleteMapping("/{id}")
    public Director delete(@RequestBody Director director) {
        return directorRepository.delete(director);
    }
}
