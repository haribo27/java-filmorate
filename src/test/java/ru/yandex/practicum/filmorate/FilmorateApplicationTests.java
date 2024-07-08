package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;

@SpringBootTest
class FilmorateApplicationTests {

    @Autowired
    FilmController filmController;
    @Autowired
    UserController userController;

    @BeforeEach
    void createControllers() {

    }

    /*@Test
    void contextUserLoads() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setName("test");
        user.setLogin("login");
        user.setBirthday(LocalDate.now());
        userController.createUser(user);
        Assertions.assertEquals(1, userController.getAllUsers().size());
        User user1 = new User();
        user1.setId(2L);
        // incorrect email
        user1.setEmail("testtest.com");
        user1.setName("test");
        user1.setLogin("login");
        user1.setBirthday(LocalDate.now());
        userController.createUser(user1);
        Assertions.assertEquals(1, userController.getAllUsers().size());
        User user2 = new User();
        user2.setId(3L);
        // incorrect email
        user2.setEmail("");
        user2.setName("test");
        user2.setLogin("login");
        user2.setBirthday(LocalDate.now());
        userController.createUser(user2);
        Assertions.assertEquals(1, userController.getAllUsers().size());
        User user3 = new User();
        user3.setId(4L);
        user3.setEmail("test@test.com");
        user3.setName("test");
        // incorrect login
        user3.setLogin("");
        user3.setBirthday(LocalDate.now());
        userController.createUser(user3);
        Assertions.assertEquals(1, userController.getAllUsers().size());
        User user4 = new User();
        user4.setId(5L);
        user4.setEmail("test@test.com");
        user4.setName("test");
        // incorrect login
        user4.setLogin("log in");
        user4.setBirthday(LocalDate.now());
        userController.createUser(user4);
        Assertions.assertEquals(1, userController.getAllUsers().size());
        User user5 = new User();
        user5.setId(6L);
        user5.setEmail("test@test.com");
        // set login as name if name is blank
        user5.setName("");
        user5.setLogin("haribol");
        user5.setBirthday(LocalDate.now());
        userController.createUser(user5);
        Assertions.assertEquals(1, userController.getAllUsers().size());
        User user6 = new User();
        user6.setId(7L);
        user6.setEmail("test@test.com");
        user6.setName("test");
        user6.setLogin("");
        // incorrect date
        user6.setBirthday(LocalDate.now().plusDays(2));
        userController.createUser(user6);
        Assertions.assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    void contextFilmLoads() {
        Film film = new Film();
        film.setId(1L);
        film.setName("test");
        film.setReleaseDate(LocalDate.now());
        film.setDescription("test description");
        film.setDuration(30);
        filmController.createFilm(film);
        Assertions.assertEquals(1, filmController.getAllFilms().size());
        Film film1 = new Film();
        film1.setId(2L);
        //incorrect name
        film1.setName("");
        film1.setReleaseDate(LocalDate.now());
        film1.setDescription("test description");
        film1.setDuration(30);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film1),
                "Название фильма не может быть пустым");
        Film film2 = new Film();
        film2.setId(3L);
        film2.setName("test");
        film2.setReleaseDate(LocalDate.now());
        // incorrect description
        film2.setDescription("0000000000000000000000000000000000000000000000000000000000" +
                "00000000000000000000000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000000000000000000000000000000000000");
        film2.setDuration(30);
        Assertions.assertThrows(ValidationException.class, () -> filmController.createFilm(film2),
                "Описание не может быть длиннее 200 символов");
        Film film3 = new Film();
        film3.setId(4L);
        //incorrect name
        film3.setName("");
        film3.setReleaseDate(LocalDate.of(1890,12,28));
        film3.setDescription("test description");
        film3.setDuration(30);
        Assertions.assertThrows(ValidationException.class,() -> filmController.createFilm(film3));
        Film film4 = new Film();
        film4.setId(5L);
        //incorrect name
        film4.setName("");
        film4.setReleaseDate(LocalDate.now());
        film4.setDescription("test description");
        film4.setDuration(-30);
        Assertions.assertThrows(ValidationException.class,() -> filmController.createFilm(film4));
    }*/

}
