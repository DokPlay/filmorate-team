package ru.yourteam.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yourteam.filmorate.dal.repositories.deleteUserRepository.DeleteUserRepository;
import ru.yourteam.filmorate.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

    // Репозиторий храним как финальное поле для гарантированной инъекции зависимостей
    private final DeleteUserRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public void deleteUserById(int userId) {
        // Проверяем, что пользователь существует, до обращения к базе.
        userRepository.ensureUserExists(userId);
        // Передаем удаление в слой хранилища, здесь можно навесить дополнительную бизнес-валидацию
        repository.deleteUserById(userId);
    }


}
