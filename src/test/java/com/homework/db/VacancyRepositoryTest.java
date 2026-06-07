package com.homework.db;

import com.homework.model.Vacancy;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VacancyRepositoryTest {

    private VacancyRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        // Используем БД в памяти — не трогаем реальный файл
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        DatabaseConnection.setConnection(conn);

        repository = new VacancyRepository();
        repository.createTableIfNotExists();
    }

    @Test
    void save_иFindAll_работают() throws Exception {
        Vacancy vacancy = buildVacancy("Java Developer", "Google", false, 80000.0, 120000.0);
        repository.save(vacancy);

        List<Vacancy> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Java Developer", all.get(0).getJobTitle());
    }

    @Test
    void search_находитПоОдномуСлову() throws Exception {
        repository.save(buildVacancy("Java Developer", "Google", false, 80000.0, null));
        repository.save(buildVacancy("Python Engineer", "Meta", false, 90000.0, null));

        List<Vacancy> results = repository.search("java");
        assertEquals(1, results.size());
        assertEquals("Java Developer", results.get(0).getJobTitle());
    }

    @Test
    void search_находитПоНесколькимСловам() throws Exception {
        repository.save(buildVacancy("Junior Java Developer", "Google", false, 60000.0, null));
        repository.save(buildVacancy("Senior Python Engineer", "Meta", false, 120000.0, null));

        List<Vacancy> results = repository.search("junior java");
        assertEquals(1, results.size());
    }

    @Test
    void findRemote_возвращаетТолькоУдалённые() throws Exception {
        repository.save(buildVacancy("Remote Job", "Google", true, null, null));
        repository.save(buildVacancy("Office Job", "Meta", false, null, null));

        List<Vacancy> remote = repository.findRemote();
        assertEquals(1, remote.size());
        assertTrue(remote.get(0).isJobIsRemote());
    }

    @Test
    void findBySalary_фильтруетПоМинимальнойЗарплате() throws Exception {
        repository.save(buildVacancy("Low Pay", "Corp1", false, 30000.0, null));
        repository.save(buildVacancy("High Pay", "Corp2", false, 100000.0, null));

        List<Vacancy> results = repository.findBySalary(50000);
        assertEquals(1, results.size());
        assertEquals("High Pay", results.get(0).getJobTitle());
    }

    @Test
    void saveAll_неДублируетВакансии() throws Exception {
        Vacancy vacancy = buildVacancy("Java Dev", "Google", false, null, null);
        repository.save(vacancy);
        repository.save(vacancy); // повторное сохранение

        assertEquals(1, repository.countAll());
    }

    @Test
    void countRemote_считаетПравильно() throws Exception {
        repository.save(buildVacancy("Remote 1", "A", true, null, null));
        repository.save(buildVacancy("Remote 2", "B", true, null, null));
        repository.save(buildVacancy("Office",   "C", false, null, null));

        assertEquals(2, repository.countRemote());
    }

    // Вспомогательный метод для создания тестовой вакансии
    private Vacancy buildVacancy(String title, String company, boolean remote,
                                  Double minSalary, Double maxSalary) {
        return Vacancy.builder()
                .jobTitle(title)
                .employerName(company)
                .jobIsRemote(remote)
                .jobApplyLink("https://example.com/" + title.replace(" ", "-"))
                .minSalary(minSalary)
                .maxSalary(maxSalary)
                .build();
    }
}
