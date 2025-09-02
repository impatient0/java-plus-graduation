# Explore With Me (Исследуй со мной)

Приложение-афиша, позволяющее пользователям делиться информацией об интересных событиях и находить компанию для участия в них. Проект разрабатывается в рамках обучения в Яндекс Практикуме.

## Оглавление

- [Технологии](#технологии)
- [Структура проекта](#структура-проекта)
- [API Спецификации](#api-спецификации)
- [Начало работы](#начало-работы)
    - [Предварительные требования](#предварительные-требования)
    - [Сборка проекта](#сборка-проекта)
    - [Запуск с использованием Docker Compose](#запуск-с-использованием-docker-compose)
    - [Локальный запуск для разработки (IntelliJ IDEA)](#локальный-запуск-для-разработки-intellij-idea)
        - [Локальный запуск Stats Service](#локальный-запуск-stats-service-stats-server)
        - [Локальный запуск Main Service](#локальный-запуск-main-service-main-service)
- [Примеры использования API](#примеры-использования-api)
    - [Публичные эндпоинты Событий, Категорий, Подборок](#публичные-эндпоинты-событий-категорий-подборок)
    - [Публичные эндпоинты Комментариев](#публичные-эндпоинты-комментариев)
- [Тестирование](#тестирование)
    - [Юнит и Интеграционные тесты](#юнит-и-интеграционные-тесты)
    - [Postman-тесты для Дополнительной Функциональности](#postman-тесты-для-дополнительной-функциональности)
- [Реализованная Дополнительная Функциональность: Комментарии](#реализованная-дополнительная-функциональность-комментарии)
- [Планы по использованию OpenAPI Generator](#планы-по-использованию-openapi-generator)
- [Команда](#команда)

## Технологии

- Java 21
- Spring Boot 3.4.5
- Spring Data JPA, QueryDSL
- Spring MVC, Spring AOP (для интеграции со StatsClient)
- PostgreSQL 16.1
- Maven
- Docker / Docker Compose
- Lombok
- MapStruct (для маппинга DTO)
- JUnit 5, Mockito
- Testcontainers
- Checkstyle, Spotbugs, Jacoco (для контроля качества кода)

## Структура проекта

Проект является многомодульным Maven-проектом и состоит из следующих основных частей:

- `explore-with-me` (корневой POM)
    - `ewm-common`: Общий модуль, содержащий классы, используемые как основным сервисом, так и сервисом статистики (например, `ApiError.java`, общие константы).
    - `main-service`: Основной сервис приложения. Отвечает за бизнес-логику, управление пользователями, событиями, категориями, подборками и запросами на участие. Взаимодействует с `stats-client` для сбора статистики.
        - `Dockerfile`
        - `schema.sql` (для инициализации схемы БД `ewm_main_db`)
    - `stats-service` (родительский POM для модулей статистики)
        - `stats-dto`: Data Transfer Objects (DTO) для сервиса статистики.
        *   `stats-client`: HTTP-клиент для взаимодействия с API сервиса статистики (используется `main-service`).
        *   `stats-server`: Сервис статистики (сбор и предоставление данных о запросах к эндпоинтам).
            *   `Dockerfile`
            *   `schema.sql` (для инициализации схемы БД `ewm_stats_db`)

## API Спецификации

Актуальные спецификации API, включая эндпоинты для реализованной дополнительной функциональности "Комментарии", можно найти в репозитории:

-   **Основной сервис:** [`ewm-main-service-spec.json`](https://github.com/impatient0/java-explore-with-me-plus/blob/main/ewm-main-service-spec.json)
    *   *Примечание: Оригинальная спецификация от Яндекс Практикума [здесь](https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json) не включает эндпоинты для комментариев. Описание реализованных эндпоинтов для комментариев см. в разделе [Реализованная Дополнительная Функциональность: Комментарии](#реализованная-дополнительная-функциональность-комментарии).*
-   **Сервис статистики:** [`ewm-stats-service.json`](https://github.com/impatient0/java-explore-with-me-plus/blob/main/ewm-stats-service-spec.json)

*Рекомендуется просматривать через Swagger Editor или аналогичный инструмент.*

## Начало работы

### Предварительные требования

Для работы с проектом вам понадобятся:

- JDK 21
- Apache Maven 3.6+
- Docker и Docker Compose
- IntelliJ IDEA (рекомендуется)

### Сборка проекта

Для сборки всех модулей проекта (включая генерацию Q-типов QueryDSL и реализаций MapStruct) выполните:
```bash
mvn clean install
```
Эта команда также запустит статические анализаторы кода и юнит-тесты.

### Запуск с использованием Docker Compose

Это основной способ запуска всего приложения для проверки взаимодействия сервисов.

1.  **Соберите проект:** `mvn clean install`
2.  **Запустите сервисы:**
    В корневой директории проекта выполните:
    ```bash
    docker-compose up --build -d
    ```
    - Сервис статистики (`stats-server`): `http://localhost:9090`
    - Основной сервис (`main-service`): `http://localhost:8080`

3.  **Просмотр логов:**
    ```bash
    docker-compose logs -f main-service
    docker-compose logs -f stats-server
    ```
4.  **Остановка сервисов:**
    ```bash
    docker-compose down
    ```
    Для удаления volumes (данных БД):
    ```bash
    docker-compose down -v
    ```
    *Примечание: При первом запуске `docker-compose up` скрипты `schema.sql` из каждого сервиса будут выполнены для создания таблиц в соответствующих базах данных.*

### Локальный запуск для разработки (IntelliJ IDEA)

#### Локальный запуск Stats Service (`stats-server`)

Предусмотрен профиль запуска `stat-local` в IntelliJ IDEA.

1.  **База данных для `stats-server`:** Настройте локальный PostgreSQL согласно `stats-service/stats-server/src/main/resources/application-local.yml` (порт, имя БД, пользователь, пароль).
    ```yaml
    # stats-service/stats-server/src/main/resources/application-local.yml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:6543/ewm_stats_db # Пример
        username: stats_user
        password: stats_password
      jpa:
        hibernate:
          ddl-auto: validate # Используется schema.sql из classpath (src/main/resources)
      sql:
        init:
          mode: always # Для выполнения schema.sql при локальном запуске
    ```
2.  **Запуск `StatsServerApplication`:** Используйте Run Configuration "stat-local" (VM options: `-Dspring.profiles.active=local`).

#### Локальный запуск Main Service (`main-service`)

Предусмотрен профиль запуска `main-local` в IntelliJ IDEA.

1.  **База данных для `main-service`:** Настройте локальный PostgreSQL согласно `main-service/src/main/resources/application-local.yml`.
    ```yaml
    # main-service/src/main/resources/application-local.yml
    stats-server:
      url: http://localhost:9090 # Если stats-server тоже запущен локально

    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/ewm_main_db # Пример
        username: ewm_user
        password: ewm_password
      jpa:
        hibernate:
          ddl-auto: validate # Используется schema.sql из classpath
      sql:
        init:
          mode: always # Для выполнения schema.sql при локальном запуске
    ```
2.  **Запуск `MainServiceApplication`:** Используйте Run Configuration "main-local" (VM options: `-Dspring.profiles.active=local`). Убедитесь, что `stats-server` уже запущен (локально или в Docker), так как `main-service` от него зависит.

## Примеры использования API

### Публичные эндпоинты Событий, Категорий, Подборок

-   **Получение списка событий с фильтрацией:**
    `GET http://localhost:8080/events?text=концерт&categories=1,2&paid=true&rangeStart=2025-06-01 00:00:00&rangeEnd=2025-06-30 23:59:59&onlyAvailable=true&sort=VIEWS&from=0&size=10`
    *(Предполагается, что даты и время URL-кодированы)*

-   **Получение подробной информации о событии:**
    `GET http://localhost:8080/events/{eventId}`

-   **Получение списка категорий:**
    `GET http://localhost:8080/categories?from=0&size=10`

-   **Получение категории по ID:**
    `GET http://localhost:8080/categories/{catId}`

-   **Получение списка подборок:**
    `GET http://localhost:8080/compilations?pinned=true&from=0&size=10`

-   **Получение подборки по ID:**
    `GET http://localhost:8080/compilations/{compId}`

### Публичные эндпоинты Комментариев

-   **Получение списка комментариев к событию:**
    `GET http://localhost:8080/events/{eventId}/comments?from=0&size=10&sort=createdOn,DESC`

## Тестирование

### Юнит и Интеграционные тесты

Для запуска всех тестов в проекте выполните:

```bash
mvn test
```
Проект использует JUnit 5, Mockito и Testcontainers для различных уровней тестирования (юнит-тесты, интеграционные тесты с БД). Отчеты о покрытии кода (Jacoco) генерируются в `target/site/jacoco/`.

### Postman-тесты для Дополнительной Функциональности

Для проверки работоспособности эндпоинтов реализованной дополнительной функциональности "Комментарии" подготовлена Postman-коллекция.

-   **Расположение:** `postman/feature.json` в корне репозитория.
-   **Проверка:** Тесты в коллекции проверяют основные сценарии использования API комментариев, включая коды ответов, базовый формат JSON и значения полей.

## Реализованная Дополнительная Функциональность: Комментарии

В рамках Этапа 3 командой была выбрана и реализована дополнительная функциональность: **"Комментарии к событиям"**.

### Обзор функционала:

Реализована возможность для пользователей оставлять, редактировать и удалять свои комментарии к опубликованным событиям, а также для администраторов модерировать (удалять, восстанавливать) любые комментарии.

**Ключевые возможности:**

*   **Пользователи (Private API):**
    *   Создание комментария к событию (`POST /users/{userId}/comments?eventId={eventId}`).
        *   Комментарии можно оставлять только к опубликованным событиям, у которых включена опция комментирования.
    *   Редактирование своего комментария (`PATCH /users/{userId}/comments/{commentId}`).
        *   Возможно только в течение 6 часов после создания.
        *   Устанавливается флаг `isEdited`.
    *   "Мягкое" удаление своего комментария (`DELETE /users/{userId}/comments/{commentId}`).
        *   Комментарий помечается как удаленный (`isDeleted = true`), но не удаляется физически.
    *   Получение списка своих комментариев (`GET /users/{userId}/comments`).

*   **Администраторы (Admin API):**
    *   "Мягкое" удаление любого комментария (`DELETE /admin/comments/{commentId}`).
    *   Восстановление "мягко" удаленного комментария (`PATCH /admin/comments/{commentId}/restore`).
    *   Получение списка всех комментариев с фильтрацией (`GET /admin/comments`) по автору, событию, статусу удаления. В ответе (`CommentAdminDto`) передается флаг `isDeleted`.

*   **Все пользователи (Public API):**
    *   Получение списка комментариев для конкретного события (`GET /events/{eventId}/comments`).
        *   Возвращаются только не удаленные комментарии.
        *   Если комментарии к событию отключены (`Event.commentsEnabled = false`), возвращается пустой список.
        *   Поддерживается пагинация и сортировка (по умолчанию по дате создания, сначала новые).

*   **Интеграция с Событиями (`Event`):**
    *   В сущность `Event` добавлено поле `commentsEnabled` (boolean, default `true`), позволяющее инициатору или администратору включать/отключать возможность комментирования для события. Это поле управляется через эндпоинты создания/обновления событий.
    *   Настроено каскадное удаление комментариев при удалении связанного события или автора.

**Детальное описание новых эндпоинтов и DTO для комментариев представлено в обновленной спецификации API `ewm-main-service-spec.json`** (см. раздел [API Спецификации](#api-спецификации)).

## Планы по использованию OpenAPI Generator

Команда планировала исследовать `openapi-generator-maven-plugin` для автоматической генерации DTO и, возможно, интерфейсов контроллеров на основе OpenAPI спецификаций. По результатам [исследования](https://docs.google.com/document/d/1nIq45x_X2r8AuA9qDvVPE3X1FWv2NcLJ77NiXnGOJc4/edit?tab=t.0#heading=h.gjdgxs) было принято решение на текущем этапе **отказаться от автоматической генерации DTO** в пользу ручного создания. Это связано с лучшим контролем над кодом, интеграцией с Lombok и Jackson, а также более точной настройкой валидации, что на данном этапе более эффективно для команды. Вопрос может быть пересмотрен в будущем при значительном увеличении количества DTO или частоты изменения API.

## Команда

- Иван Петровский (Team Lead) - [@impatient0](https://github.com/impatient0)
- Андрей Гагарский - [@Gagarskiy-Andrey](https://github.com/Gagarskiy-Andrey)
- Валерия Бутько - [@progingir](https://github.com/progingir)
- Сергей Филипповских - [@SergikF](https://github.com/SergikF)
