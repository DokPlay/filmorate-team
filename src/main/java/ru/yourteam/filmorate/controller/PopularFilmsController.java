// Контроллер для выдачи популярных фильмов по жанрам и годам.
package ru.yourteam.filmorate.controller; // Если что, вдруг перед финалом скорректирую.

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.dto.PopularFilmDto; // Если что, вдруг перед финалом скорректирую.
import ru.yourteam.filmorate.service.PopularFilmService;

@Slf4j
@Validated
@RestController
@RequestMapping("/films/popular")
@RequiredArgsConstructor
public class PopularFilmsController {

    private final PopularFilmService popularFilmService;

    @GetMapping
    public List<PopularFilmDto> getMostPopular(
            @RequestParam(name = "count", defaultValue = "10") @Positive int count,
            @RequestParam(name = "genreId", required = false) @Positive Integer genreId,
            @RequestParam(name = "year", required = false) @Min(1895) Integer year) {

        // Метод поддерживает фильтрацию по жанру и году, поэтому сразу логируем все параметры,
        // чтобы упростить разбор проблем при обращении к этому endpoint.
        log.info("Request GET /films/popular: count={}, genreId={}, year={}", count, genreId, year);

        List<PopularFilmDto> popularFilms =
                popularFilmService.getMostPopular(count, genreId, year);

        log.debug("Popular films found: {}", popularFilms.size());

        return popularFilms;
    }
}
