package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yourteam.filmorate.dal.repositories.deleteUserRepository.DeleteUserRepository;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

    // Репозиторий храним как финальное поле для гарантированной инъекции зависимостей
    private final DeleteUserRepository repository;

    public void deleteUserById(int userId) {
        // Передаем удаление в слой хранилища, здесь можно навесить дополнительную бизнес-валидацию
        repository.deleteUserById(userId);
    }


}
