// Контроллер для работы с фильмами и их режиссёрами.
package ru.yourteam.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yourteam.filmorate.dto.FilmDto;
import ru.yourteam.filmorate.model.Director;
import ru.yourteam.filmorate.service.DirectorService;
import ru.yourteam.filmorate.service.SortMode;

@Validated
@RestController
@RequestMapping("/directors")
public class DirectorFilmsController {

    private final DirectorService directorService;

    @Autowired
    public DirectorFilmsController(DirectorService directorService) {
        this.directorService = directorService;
    }

    // получение всех режиссеров
    @GetMapping
    public ResponseEntity<Collection<Director>> getAll() {
        return ResponseEntity.ok(directorService.getAll());
    }

    // получение режиссера по id
    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirector(@PathVariable @Min(0) long id) {
        return ResponseEntity.ok(directorService.getById(id));
    }

    // создание режиссера
    @PostMapping
    public ResponseEntity<Director> create(@Valid @RequestBody Director director) {
        directorService.create(director);
        return new ResponseEntity<>(director, HttpStatus.CREATED); // возвращаем 201
    }


    // изменение режиссера
    @PutMapping
    public ResponseEntity<Director> update(@RequestBody Director director) {
        return ResponseEntity.ok(directorService.update(director));
    }

    // удаление режиссера
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable @Min(0) long id) { // 204 — удалён; 404 — не найден; 409 — если ON DELETE RESTRICT и есть связи.
        directorService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204
    }

    @GetMapping("/{id}/films")
    public List<FilmDto> getFilms(
        @PathVariable long id,
        @RequestParam(value = "sort", defaultValue = "likes") String sort,
        @RequestParam(value = "limit", defaultValue = "50") @Min(1) int limit,
        @RequestParam(value = "offset", defaultValue = "0") @Min(0) int offset) {
        // Передаем в сервис уже разобранные параметры пагинации и сортировки
        return directorService.getFilmsByDirector(id, SortMode.from(sort), limit, offset);
    }
}
