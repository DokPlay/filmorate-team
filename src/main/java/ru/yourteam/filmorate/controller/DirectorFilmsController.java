// Контроллер для работы с фильмами и их режиссёрами.
package ru.yourteam.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // получение всех режиссеров
    @GetMapping
    public ResponseEntity<Collection<Director>> findAll() {
        return ResponseEntity.ok(directorRepository.findAll());
    }

    // получение режиссера по id
    @GetMapping("/{id}")
    public ResponseEntity getDirector(@PathVariable long id) {
        return ResponseEntity.ok(directorRepository.getById(id));
    }

    // создание режиссера
    @PostMapping
    public ResponseEntity create(@RequestBody Director director) {
        return ResponseEntity.ok(directorRepository.create(director));
    }


    // изменение режиссера
    @PutMapping
    public ResponseEntity update(@RequestBody Director director) {
        return ResponseEntity.ok(directorRepository.update(director));
    }

    // удаление режиссера
    @DeleteMapping("/{id}")
    public Director delete(@PathVariable long id) {
        return directorRepository.delete(id);
    }
}
