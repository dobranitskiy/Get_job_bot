package com.homework.cli;

import com.homework.db.IVacancyRepository;
import com.homework.export.IExportService;
import com.homework.export.ExportService;
import com.homework.model.Vacancy;
import com.homework.parser.JSearchClient;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {

    private final IVacancyRepository repository;
    private final JSearchClient client;
    private final IExportService exportService;
    private final Scanner scanner;

    public ConsoleApp(IVacancyRepository repository, JSearchClient client) {
        this.repository = repository;
        this.client = client;
        this.exportService = new ExportService();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        printBanner();
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] parts = input.split(" ", 2);
            String command = parts[0].toLowerCase();
            String args = parts.length > 1 ? parts[1] : "";

            switch (command) {
                case "help"    -> printHelp();
                case "list"    -> listVacancies();
                case "search"  -> searchVacancies(args);
                case "filter"  -> filterVacancies(args);
                case "update"  -> updateVacancies();
                case "stats"   -> printStats();
                case "history" -> printHistory();
                case "sort"    -> sortVacancies(args);
                case "export"  -> exportVacancies(args);
                case "exit"    -> { System.out.println("Пока!"); return; }
                default        -> System.out.println("Неизвестная команда. Введите help.");
            }
        }
    }

    private void listVacancies() {
        try {
            List<Vacancy> vacancies = repository.findAll();
            if (vacancies.isEmpty()) {
                System.out.println("Вакансий нет. Используйте команду update.");
                return;
            }
            printVacancies(vacancies);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void searchVacancies(String keyword) {
        if (keyword.isEmpty()) {
            System.out.println("Укажите ключевое слово. Пример: search java");
            return;
        }
        try {
            List<Vacancy> results = repository.search(keyword);
            if (results.isEmpty()) {
                System.out.println("Ничего не найдено по запросу: " + keyword);
            } else {
                printVacancies(results);
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void filterVacancies(String args) {
        try {
            List<Vacancy> vacancies;

            if (args.contains("--remote")) {
                vacancies = repository.findRemote();
                System.out.println("Фильтр: только удалённые");
            } else if (args.contains("--salary")) {
                String[] parts = args.split(" ");
                double minSalary = Double.parseDouble(parts[parts.length - 1]);
                vacancies = repository.findBySalary(minSalary);
                System.out.println("Фильтр: зарплата от " + minSalary);
            } else {
                System.out.println("Доступные фильтры:");
                System.out.println("  filter --remote          — только удалённые вакансии");
                System.out.println("  filter --salary 50000    — зарплата от 50000");
                return;
            }

            if (vacancies.isEmpty()) {
                System.out.println("Ничего не найдено.");
            } else {
                printVacancies(vacancies);
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void printHistory() {
        try {
            List<String> history = repository.getHistory();
            if (history.isEmpty()) {
                System.out.println("История пуста.");
                return;
            }
            System.out.println("\nПоследние запросы:");
            history.forEach(h -> System.out.println("  " + h));
            System.out.println();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void printStats() {
        try {
            System.out.println("\nСтатистика:");
            System.out.println("  Всего вакансий: " + repository.countAll());
            System.out.println("  Удалённых: " + repository.countRemote());
            System.out.printf("  Средняя зарплата: %.0f%n", repository.avgSalary());
            System.out.println("\n  Топ локации:");
            repository.topLocations().forEach(l -> System.out.println("    " + l));
            System.out.println("\n  Топ компании:");
            repository.topCompanies().forEach(c -> System.out.println("    " + c));
            System.out.println();
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void sortVacancies(String by) {
        try {
            List<Vacancy> vacancies = switch (by.toLowerCase()) {
                case "--salary"  -> repository.findAllSortedBySalary();
                case "--date"    -> repository.findAllSortedByDate();
                case "--company" -> repository.findAllSortedByCompany();
                default -> {
                    System.out.println("Доступные варианты сортировки:");
                    System.out.println("  sort --salary   — по зарплате (убывание)");
                    System.out.println("  sort --date     — по дате (новые первые)");
                    System.out.println("  sort --company  — по компании (A-Z)");
                    yield null;
                }
            };
            if (vacancies != null) printVacancies(vacancies);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void exportVacancies(String format) {
        if (format.isEmpty()) {
            System.out.println("Укажите формат. Пример: export csv");
            return;
        }
        try {
            List<Vacancy> vacancies = repository.findAll();
            String filePath = "vacancies." + format.toLowerCase();

            switch (format.toLowerCase()) {
                case "csv"  -> exportService.exportCsv(vacancies, filePath);
                case "json" -> exportService.exportJson(vacancies, filePath);
                case "html" -> exportService.exportHtml(vacancies, filePath);
                default     -> { System.out.println("Неизвестный формат. Доступны: csv, json, html"); return; }
            }

            System.out.println("Готово! Файл сохранён: " + filePath);
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте: " + e.getMessage());
        }
    }

    private void updateVacancies() {
        System.out.print("Введите поисковый запрос (например: java developer): ");
        String query = scanner.nextLine().trim();
        try {
            System.out.println("Загружаю вакансии...");
            List<Vacancy> vacancies = client.search(query, 1);
            repository.saveAll(vacancies);
            System.out.println("Готово! Загружено: " + vacancies.size());
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке: " + e.getMessage());
        }
    }

    private void printVacancies(List<Vacancy> vacancies) {
        System.out.println("\nНайдено: " + vacancies.size() + "\n");
        for (int i = 0; i < vacancies.size(); i++) {
            Vacancy v = vacancies.get(i);
            System.out.println((i + 1) + ". " + v.getJobTitle());
            System.out.println("   " + v.getEmployerName() + ", " + v.getJobLocation() + (v.isJobIsRemote() ? " (удалённо)" : ""));
            System.out.println("   Зарплата: " + formatSalary(v));
            System.out.println("   " + v.getJobApplyLink());
            System.out.println();
        }
    }

    private String formatSalary(Vacancy v) {
        if (v.getMinSalary() == null && v.getMaxSalary() == null) return "не указана";
        if (v.getMinSalary() != null && v.getMaxSalary() != null)
            return String.format("%.0f - %.0f %s", v.getMinSalary(), v.getMaxSalary(),
                    v.getJobSalaryPeriod() != null ? v.getJobSalaryPeriod() : "");
        if (v.getMinSalary() != null)
            return String.format("от %.0f %s", v.getMinSalary(),
                    v.getJobSalaryPeriod() != null ? v.getJobSalaryPeriod() : "");
        return String.format("до %.0f %s", v.getMaxSalary(),
                v.getJobSalaryPeriod() != null ? v.getJobSalaryPeriod() : "");
    }

    private void printBanner() {
        System.out.println("\nАгрегатор вакансий");
        System.out.println("Введите help для списка команд.\n");
    }

    private void printHelp() {
        System.out.println("\nКоманды:");
        System.out.println("  list                    показать все вакансии");
        System.out.println("  search <текст>          поиск по названию");
        System.out.println("  filter --remote         только удалённые");
        System.out.println("  filter --salary 50000   зарплата от 50000");
        System.out.println("  sort --salary/--date/--company");
        System.out.println("  stats                   статистика");
        System.out.println("  history                 история запросов");
        System.out.println("  update                  загрузить новые вакансии");
        System.out.println("  export csv/json/html");
        System.out.println("  exit\n");
    }
}
