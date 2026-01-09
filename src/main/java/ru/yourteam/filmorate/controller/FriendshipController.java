package ru.yourteam.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.service.FriendshipService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{id}/friends/{friendId}")
public class FriendshipController {

    private final FriendshipService friendshipService;

    @PutMapping
    public void addFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        friendshipService.addFriend(userId, friendId);
    }

    @DeleteMapping
    public void removeFriend(@PathVariable("id") int userId, @PathVariable int friendId) {
        friendshipService.removeFriend(userId, friendId);
    }
}
