package ru.yourteam.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.model.Event;
import ru.yourteam.filmorate.service.EventFeedService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{id}/feed")
public class EventFeedController {

    private final EventFeedService eventFeedService;

    @GetMapping
    public List<Event> getFeed(@PathVariable("id") int userId) {
        return eventFeedService.getFeed(userId);
    }
}
