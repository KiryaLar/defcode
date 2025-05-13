# Defcode - сервис одноразовых временных кодов (OTP)

## Содержание
- [Описание](#описание)
- [Технологии](#технологии)
- [Структура проекта](#структура-проекта)
- [Сборка и запуск](#сборка-и-запуск)
- [Использование API](#использование-api)
  - [Аутентификация](#аутентификация)
  - [Пользовательские OTP-операции](#пользовательские-otp-операции)
  - [Администрирование](#администрирование)

## Описание

**Defcode** - это система управления одноразовыми паролями (OTP), разработанная на Spring Boot. Проект предоставляет REST API для регистрации пользователей, аутентификации, генерации и валидации OTP-кодов через различные каналы (SMS, Email, Telegram), а также административные функции для управления пользователями и настройками OTP.

Приложение позволяет
✔️ Создавать «операции под защитой» 🔐
✔️ Генерировать и хранить OTP‑коды ⏱️
✔️ Доставлять коды по SMS, E‑mail и Telegram ✉️🤖
✔️ Проверять введённый пользователем код ✅
✔️ Администрировать длину и время жизни кода ⚙️

- Клиент инициирует операцию (например, «сброс пароля») и указывает способ доставки кода (SMS, EMAIL, TELEGRAM) и контакт.
   
- Сервис генерирует числовой код (по умолчанию — 4 цифры, TTL — 60 сек).
- Код сохраняется в базе со статусом ACTIVE и отправляется выбранным каналом.
- Пользователь вводит код — DefCode проверяет его статус, срок жизни и соответствие операции.
- Под капотом используется собственная реализация TOTP: код зависит от операции, контакта и текущего времени, что исключает повторное использование и повышает стойкость.

## Технологии
| Стек                    | Версия / Назначение                           |
|-------------------------|-----------------------------------------------|
| Java                   | 17 LTS                                        |
| Spring Boot            | 3.2.x                                         |
| Spring Security + JWT  | Авторизация / распределение ролей             |
| Spring Data JPA, JDBC | Работа с PostgreSQL                        |
| MapStruct              | Маппинг DTO ↔ Entity                         |
| Lombok                 | Уменьшение шаблонного кода (boilerplate)      |
| Maven                  | Сборка проекта                                |
| PostgreSQL             | Хранение пользователей и OTP-кодов            |
| SLF4J + Logback        | Логирование                                   |

## Структура проекта
```bash
src
├───main
│   ├───java
│   │   └───com
│   │       └───larkin
│   │           └───defcode
│   │               ├───config
│   │               ├───controller
│   │               ├───dao
│   │               ├───dto
│   │               │   ├───request
│   │               │   └───response
│   │               ├───entity
│   │               ├───exception
│   │               ├───mapper
│   │               ├───security
│   │               │   ├───config
│   │               │   ├───filter
│   │               │   └───service
│   │               ├───service
│   │               └───util
│   └───resources
└───test

```
Схема БД и скрипты находятся в /src/main/resources/db.

## Сборка и запуск

### 1️⃣ Клонировать репозиторий

```bash
git clone https://github.com/username/defcode.git
cd defcode
```

### 2️⃣ Настроить окружение

- Установите PostgreSQL 15+ и создайте базу defcode_db.

- Создайти application.yaml на основе application.yaml.origin из проекта
### 3️⃣ Собрать и запустить
```bash
mvn clean install      # сборка и юнит‑тесты
mvn spring-boot:run    # запуск
```
После старта API доступно по адресу http://localhost:8080.

## Использование API

    Во всех примерах HOST = http://localhost:8080.
    Формат даты времени — ISO 8601.

## Аутентификация
| Метод | URL                  | Тело запроса                  | Описание                                |
|-------|-----------------------|-------------------------------|-----------------------------------------|
| POST  | `/auth/register`      | `{username, password, role}`  | Регистрация (роль: `ADMIN` или `USER`)  |
| POST  | `/auth/login`         | `{username, password}`        | Получить `accessToken` и `refreshToken` |
| POST  | `/auth/refresh-token` | `{refreshToken}`              | Обновить JWT‑пару                        |
| POST  | `/auth/logout`        | `{refreshToken}`              | Отозвать refresh‑токен                  |

### Пример запроса POST `/auth/register`
```http
POST /auth/register
Content-Type: application/json

{
  "username": "user",
  "password": "Secret123",
  "role": "user",
}
```
**Ответ 201 Created**
```json
{
  "message": "User alice has successfully registered as an USER",
  "timestamp": "Tue May 01 12:45:01 GMT+03:00 2025"
}
```

### Пример запроса POST `/auth/login`
```http
POST /auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "Secret123"
}
```
**Ответ 200 OK**
```json
{
  "role": "USER",
  "accessToken": "<JWT>",
  "refreshToken": "<JWT>"
}
```
### Пример запроса POST `/auth/logout`
```http
POST /auth/logout
Content-Type: application/json

{
  "refreshToken": "<ваш_refresh_token>"
}
```
**Ответ 200 OK**
```json
{
  "message": "Success logged out",
  "timestamp": "Tue May 01 12:45:01 GMT+03:00 2025"
}
```
### Пример запроса POST `/auth/refresh-token`
```http
POST /auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "<ваш_refresh_token>"
}
```
**Ответ 200 OK**
```json
{
  "role": "USER",
  "accessToken": "<новый_access_token>",
  "refreshToken": "<новый_refresh_token>"
}
```

Добавляйте заголовок Authorization: Bearer <accessToken> ко всем защищённым эндпоинтам.

## Пользовательские OTP-операции

| Метод | URL                   | Тело запроса                         | Назначение                     |
|-------|------------------------|--------------------------------------|--------------------------------|
| POST  | `/user/otp/generate`   | `{method, contact, operationType}`   | Сгенерировать и отправить код |
| POST  | `/user/otp/validate`   | `{code}`                             | Проверить введённый код       |

### Значения `operationType`:
1. Login Verification
2. Account Registration
3. Password Reset
4. Transaction Confirmation
5. Update Contact Information
6. Account Deletion

### Пример генерации кода по SMS
```http
POST /user/otp/generate
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "method": "sms",
  "contact": "+79179997799",
  "operationType": "4"
}
```
### Пример генерации кода по email
```http
POST /user/otp/generate
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "method": "email",
  "contact": "user@yande.ru",
  "operationType": "2"
}
```
### Пример генерации кода по telegram
```http
POST /user/otp/generate
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "method": "telegram",
  "contact": "<telegram bot chat id>",
  "operationType": "4"
}
```
**Ответ 200 OK**
```json
{
  "message": "Code was successfully sent",
  "timestamp": "Tue May 01 12:45:01 GMT+03:00 2025"
}
```

### Пример валидации кода
```http
POST /user/otp/validate
Content-Type: application/json

{
  "code": "123456"
}
```
**Ответ 200 OK**
```json
{
  "message": "Code is correct",
  "timestamp": "Tue May 01 12:45:01 GMT+03:00 2025"
}
```

## Администрирование
| Метод | URL                  | Тело запроса               | Функция                             |
|-------|-----------------------|----------------------------|-------------------------------------|
| PUT   | `/admin/otp-config`   | `{length, lifetime}`       | Изменить длину и TTL кода           |
| GET   | `/admin/users`        | –                          | Получить список пользователей (USER)|
| DELETE| `/admin/users/{id}`   | –                          | Удалить пользователя                |
### Значения `lifetime` в виде число + один символ из (smhd):
1. 30s
2. 2m
3. 3h
4. 1d

### Пример изменения конфигурации
```http
PUT /admin/otp-config
Authorization: Bearer <accessToken>
Content-Type: application/json

{
  "length": 8,
  "lifetime": "2m"   // 2 минуты 
}
```
**Ответ 200 OK**
```json
{
  "message": "OTP config was successfully changed: New duration: 2m New code length: 8",
  "timestamp": "Tue May 01 12:45:01 GMT+03:00 2025"
}
```

### Пример получения списка пользователей
```http
GET /admin/users
Authorization: Bearer <access_token>
```
**Ответ 200 OK**
```json
[
  {
    "id": 1,
    "username": "user1"
  },
  {
    "id": 2,
    "username": "user2"
  }
]
```

### Пример удаления пользователя
```http
GET /admin/users/{id}
Authorization: Bearer <access_token>
```
**Ответ 200 OK**
```json
{
  "message": "User with id 1 deleted",
  "timestamp": "Tue May 01 12:55:01 GMT+03:00 2025"
}
```
