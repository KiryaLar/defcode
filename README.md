# Defcode - OTP Code Management System
## Описание проекта
**Defcode** - это система управления одноразовыми паролями (OTP), разработанная на Spring Boot. Проект предоставляет REST API для регистрации пользователей, аутентификации, генерации и валидации OTP-кодов через различные каналы (например, SMS, Email, Telegram), а также административные функции для управления пользователями и настройками OTP.

Установка и запуск

Склонируйте репозиторий:
git clone https://github.com/yourusername/defcode.git
cd defcode


Настройте базу данных PostgreSQL:

Создайте базу данных defcode.
Выполните SQL-скрипты для создания таблиц и начальных данных:create type role as enum ('ADMIN', 'USER');
create type otp_status as enum ('ACTIVE', 'EXPIRED', 'USED');

create table users (
    id serial primary key,
    username varchar(64) unique not null,
    password varchar(32) not null,
    role role not null
);

create table token (
    id serial primary key,
    user_id int references users (id) not null,
    token varchar(124) unique not null,
    expiration_date timestamp not null,
    revoked bool default false
);

create table otp_config (
    id serial primary key,
    code_length int not null default 6,
    lifetime interval not null default '1 minutes'
);
insert into otp_config values (1, 4);

create table otp_codes (
    id bigserial primary key,
    code int not null,
    user_id int references users (id) on delete cascade not null,
    status otp_status default 'ACTIVE',
    expiration_time timestamp not null,
    operation_type int references operation (operation_type) on delete set NULL not null
);

create table operation (
    operation_type int primary key,
    description varchar(128) not null
);

insert into operation values (1, 'Login Verification');
insert into operation values (2, 'Account Registration');
insert into operation values (3, 'Password Reset');
insert into operation values (4, 'Transaction Confirmation');
insert into operation values (5, 'Update Contact Information');
insert into operation values (6, 'Account Deletion');




Настройте конфигурацию:

Отредактируйте файл src/main/resources/application.properties:spring.datasource.url=jdbc:postgresql://localhost:5432/defcode
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update




Запустите приложение:
mvn clean install
mvn spring-boot:run


Приложение будет доступно по адресу http://localhost:8080.


Использование
Сервис предоставляет REST API для управления пользователями, аутентификации и OTP-кодами. Все запросы, кроме /auth/register и /auth/login, требуют JWT-токен в заголовке Authorization: Bearer <access_token>.
Поддерживаемые команды (эндпоинты)
Аутентификация

POST /auth/register - Регистрация нового пользователя.
POST /auth/login - Вход и получение JWT-токенов.
POST /auth/logout - Выход (отзыв refresh-токена).
POST /auth/refresh-token - Обновление access-токена.

Управление OTP

POST /user/otp/generate - Генерация и отправка OTP-кода.
POST /user/otp/validate - Валидация OTP-кода.

Административные функции

PUT /admin/otp-config - Изменение конфигурации OTP.
GET /admin/users - Получение списка пользователей (только не-админов).
DELETE /admin/users/{id} - Удаление пользователя по ID.

Примеры запросов
Регистрация пользователя
curl -X POST http://localhost:8080/auth/register \
-H "Content-Type: application/json" \
-d '{
    "username": "admin",
    "password": "Password1",
    "role": "admin"
}'

Вход
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{
    "username": "admin",
    "password": "Password1"
}'

Генерация OTP-кода
curl -X POST http://localhost:8080/user/otp/generate \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <access_token>" \
-d '{
    "method": "email",
    "contact": "user@example.com",
    "operationType": "1"
}'

Валидация OTP-кода
curl -X POST http://localhost:8080/user/otp/validate \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <access_token>" \
-d '{
    "code": "1234"
}'

Изменение конфигурации OTP (админ)
curl -X PUT http://localhost:8080/admin/otp-config \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <access_token>" \
-d '{
    "length": 6,
    "lifetime": "2 minutes"
}'

Тестирование
Для тестирования используйте Postman или curl:

Зарегистрируйте пользователя через /auth/register.
Выполните вход через /auth/login и сохраните полученные accessToken и refreshToken.
Используйте accessToken для вызова защищенных эндпоинтов (например, генерация и валидация OTP).
Проверьте административные функции с учетной записью роли ADMIN.

Юнит-тесты находятся в src/test/java и могут быть запущены командой:
mvn test

Структура проекта

src/main/java/com/larkin/defcode:

config - Конфигурационные классы (Spring, безопасность).
controller - REST-контроллеры (AuthController, UserController, AdminController).
dao - Интерфейсы для работы с базой данных.
dto - Объекты передачи данных:
request - Входные DTO (например, RegisterUserRequest, GenerateOtpRequestDto).
response - Выходные DTO (например, AuthenticationResponse, SuccessResponse).


entity - Сущности базы данных (например, User, OtpCode).
exception - Кастомные исключения.
mapper - Преобразование между сущностями и DTO.
security - Настройки безопасности (JWT, фильтры).
service - Бизнес-логика (например, UserService, OtpCodeService).
util - Утилитные классы.


src/main/resources:

application.properties - Конфигурация приложения.


logs - Директория для логов.


Инструкции по установке внешних библиотек

Проект использует стандартные зависимости Spring Boot, указанные в pom.xml.
Для эмуляции SMS (если используется SMPP):
Скачайте и запустите SMPPsim.
Настройте подключение в application.properties.


Для Telegram Bot:
Создайте бота через BotFather и получите токен.
Добавьте токен в application.properties.



Лицензия
Проект распространяется под лицензией MIT.
