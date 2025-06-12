package com.example;

import com.example.dao.UserDao;
import com.example.dao.UserDaoImpl;
import com.example.entity.User;
import com.example.util.HibernateUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private static final Logger logger = LogManager.getLogger(App.class);
    private static final UserDao userDao = new UserDaoImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            logger.info("Запуск приложения User Service");

            boolean running = true;
            while (running) {
                printMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // очистка буфера

                switch (choice) {
                    case 1:
                        createUser();
                        break;
                    case 2:
                        getUserById();
                        break;
                    case 3:
                        getAllUsers();
                        break;
                    case 4:
                        updateUser();
                        break;
                    case 5:
                        deleteUser();
                        break;
                    case 6:
                        running = false;
                        break;
                    default:
                        System.out.println("Неверный выбор. Пожалуйста, попробуйте снова.");
                }
            }
        } catch (Exception e) {
            logger.error("Произошла ошибка: ", e);
        } finally {
            try {
                HibernateUtil.shutdown();
            } catch (Exception e) {
                logger.error("Ошибка при закрытии Hibernate", e);
            }
            try {
                scanner.close();
            } catch (Exception e) {
                logger.error("Ошибка при закрытии Scanner", e);
            }

            logger.info("Приложение остановлено");
        }
    }

    private static void printMenu() {
        System.out.println("\nМеню:");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("6. Выход");
        System.out.print("Введите номер выбранной операции: ");
    }

    private static void createUser() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        User user = new User(name, email, age);
        userDao.save(user);
        System.out.println("Пользователь успешно создан: " + user);
    }

    private static void getUserById() {
        System.out.print("Введите ID пользователя: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            System.out.println("Пользователь найден: " + user);
        } else {
            System.out.println("Пользователь с ID " + id + " не найден");
        }
    }

    private static void getAllUsers() {
        List<User> users = userDao.findAll();
        if (users != null && !users.isEmpty()) {
            System.out.println("Список пользователей:");
            users.forEach(System.out::println);
        } else {
            System.out.println("Пользователи не найдены.");
        }
    }

    private static void updateUser() {
        System.out.print("Введите ID пользователя для обновления: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Optional<User> user = userDao.findById(id);
        if (user.isEmpty()) {
            System.out.println("Пользователь с ID " + id + " не найден");
            return;
        }

        System.out.print("Введите новое имя (текущее: " + user.get().getName() + "): ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            user.get().setName(name);
        }

        System.out.print("Введите новый email (текущее: " + user.get().getEmail() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            user.get().setEmail(email);
        }

        System.out.print("Введите новый возраст (текущий: " + user.get().getAge() + "): ");
        String ageInput = scanner.nextLine();
        if (!ageInput.isEmpty()) {
            user.get().setAge(Integer.parseInt(ageInput));
        }

        userDao.update(user.orElse(null));
        System.out.println("Пользователь успешно обновлен: " + user);
    }

    private static void deleteUser() {
        System.out.print("Введите ID пользователя для удаления: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Optional<User> user = userDao.findById(id);
        if (user.isPresent()) {
            userDao.delete(user.orElse(null));
            System.out.println("Пользователь с ID " + id + " успешно удален");
        } else {
            System.out.println("Пользователь с ID " + id + " не найден");
        }
    }
}