// timlead: добавлен NotFoundException в общий пакет исключений
// причина: ранее класс лежал в отдельном пакете exceptions и не обрабатывался централизованно
package ru.yourteam.filmorate.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
