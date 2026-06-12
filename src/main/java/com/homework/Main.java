package com.homework;

import com.homework.cli.ConsoleApp;
import com.homework.db.VacancyRepository;
import com.homework.parser.JSearchClient;

public class Main {
    public static void main(String[] args) throws Exception {
        String apiKey = "65470ec76amsha84127c736d5ad2p150416jsnc820eec2e6e7";

        VacancyRepository repository = new VacancyRepository();
        repository.createTableIfNotExists();

        JSearchClient client = new JSearchClient(apiKey);

        if (repository.findAll().isEmpty()) {
            System.out.println("БД пустая, загружаю данные из API...");
            var vacancies = client.search("java developer", 1);
            repository.saveAll(vacancies);
        }

        new ConsoleApp(repository, client).run();
    }
}
