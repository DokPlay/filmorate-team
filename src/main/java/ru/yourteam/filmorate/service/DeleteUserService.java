package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.deleteUserRepository.DeleteUserRepository;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

    DeleteUserRepository repository;

    public void deleteUserVyID(int userId) {
        repository.deleteUserById(userId);
    }


}
