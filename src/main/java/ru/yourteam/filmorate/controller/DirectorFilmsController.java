// Контроллер для работы с фильмами и их режиссёрами.
package ru.yourteam.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yourteam.filmorate.dto.DirectorDto;
import ru.yourteam.filmorate.repository.DirectorRepository;
import ru.yourteam.filmorate.service.DirectorServiceImpl;
import ru.yourteam.filmorate.service.SortMode;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/directors")
public class DirectorFilmsController {

    private final DirectorRepository directorRepository;
    private final DirectorServiceImpl directorServiceImpl;

    @Autowired
    public DirectorFilmsController(DirectorRepository directorRepository, DirectorServiceImpl directorServiceImpl) {
        this.directorRepository = directorRepository;
        this.directorServiceImpl = directorServiceImpl;
    }

    // получение всех режиссеров
    @GetMapping
    public ResponseEntity<Collection<DirectorDto>> getAll() {
        return ResponseEntity.ok(directorServiceImpl.getAll());
    }

    // получение режиссера по id
    @GetMapping("/{id}")
    public ResponseEntity<DirectorDto> getDirector(@PathVariable @Min(0) long id) {
        return ResponseEntity.ok(directorServiceImpl.getById(id));
    }

    // создание режиссера
    @PostMapping
    public ResponseEntity<DirectorDto> create(@Valid @RequestBody DirectorDto director) {
        directorServiceImpl.create(director);
        return new ResponseEntity<>(director, HttpStatus.CREATED); // возвращаем 201
    }


    // изменение режиссера
    @PutMapping
    public ResponseEntity<DirectorDto> update(@RequestBody DirectorDto director) {
        return ResponseEntity.ok(directorServiceImpl.update(director));
    }

    // удаление режиссера
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable @Min(0) long id) { // 204 — удалён; 404 — не найден; 409 — если ON DELETE RESTRICT и есть связи.
        directorServiceImpl.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/{id}/films")
    public List<FilmDto> getFilms(
        @PathVariable long id,
        @RequestParam("sort") String sort) {
        return directorServiceImpl.getFilmsByDirector(id, SortMode.from(sort)); // SortMode.parse/validate бросит 400
    }
}
