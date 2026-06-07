# Get Job Bot

Консольное приложение для поиска вакансий. Написано на Java как домашнее задание.

## Что умеет

- искать вакансии по ключевым словам
- фильтровать по удалёнке и зарплате
- сортировать по зарплате / дате / компании
- показывать статистику
- экспортировать в csv, json, html

## Как запустить

нужна java 21 и maven

```bash
mvn clean package -DskipTests
java -cp target/Get_job_bot-1.0-SNAPSHOT.jar com.homework.Main
```

тесты:
```bash
mvn clean test
```

## Команды

```
help                    - список команд
list                    - все вакансии
search <слово>          - поиск
filter --remote         - только удалёнка
filter --salary 50000   - зарплата от 50000
sort --salary           - сортировка по зарплате
sort --date             - по дате
sort --company          - по компании
stats                   - статистика
history                 - история запросов
export csv / json / html
exit
```

## Стек

- Java 21
- SQLite
- Jackson
- JUnit 5 + Mockito

## Структура

```
model/       - класс Vacancy
parser/      - получение вакансий через JSearch API
db/          - работа с базой данных
export/      - экспорт файлов
cli/         - консольный интерфейс
```
