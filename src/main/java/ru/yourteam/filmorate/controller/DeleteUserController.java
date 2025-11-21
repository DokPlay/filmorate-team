package ru.yourteam.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yourteam.filmorate.service.DeleteUserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class DeleteUserController {

    DeleteUserService service;

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        service.deleteUserVyID(id);
    }
}
