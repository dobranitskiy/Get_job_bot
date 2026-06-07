package com.homework.parser;

import com.homework.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JSearchClientTest {

    private JSearchClient client;

    @BeforeEach
    void setUp() {
        client = new JSearchClient("test-key");
    }

    @Test
    void searchFromFile_возвращаетСписокВакансий() throws Exception {
        List<Vacancy> vacancies = client.searchFromFile("/Users/kirilldobranitskiy/Downloads/example.json");

        assertNotNull(vacancies);
        assertFalse(vacancies.isEmpty());
    }

    @Test
    void searchFromFile_вакансияИмеетНазвание() throws Exception {
        List<Vacancy> vacancies = client.searchFromFile("/Users/kirilldobranitskiy/Downloads/example.json");

        Vacancy first = vacancies.get(0);
        assertNotNull(first.getJobTitle());
        assertFalse(first.getJobTitle().isBlank());
    }

    @Test
    void searchFromFile_вакансияИмеетСсылку() throws Exception {
        List<Vacancy> vacancies = client.searchFromFile("/Users/kirilldobranitskiy/Downloads/example.json");

        vacancies.forEach(v -> assertNotNull(v.getJobApplyLink()));
    }

    @Test
    void searchFromFile_зарплатаНеОтрицательная() throws Exception {
        List<Vacancy> vacancies = client.searchFromFile("/Users/kirilldobranitskiy/Downloads/example.json");

        vacancies.stream()
                .filter(v -> v.getMinSalary() != null)
                .forEach(v -> assertTrue(v.getMinSalary() >= 0));
    }
}
