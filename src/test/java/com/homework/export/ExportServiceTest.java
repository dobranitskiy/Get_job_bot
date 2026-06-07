package com.homework.export;

import com.homework.model.Vacancy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExportServiceTest {

    private ExportService exportService;
    private List<Vacancy> testVacancies;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        exportService = new ExportService();
        testVacancies = List.of(
                Vacancy.builder()
                        .jobTitle("Java Developer")
                        .employerName("Google")
                        .jobLocation("New York")
                        .jobIsRemote(false)
                        .minSalary(80000.0)
                        .maxSalary(120000.0)
                        .jobSalaryPeriod("YEAR")
                        .jobApplyLink("https://google.com/jobs/1")
                        .build(),
                Vacancy.builder()
                        .jobTitle("Python Engineer")
                        .employerName("Meta")
                        .jobLocation("Remote")
                        .jobIsRemote(true)
                        .minSalary(null)
                        .maxSalary(null)
                        .jobApplyLink("https://meta.com/jobs/1")
                        .build()
        );
    }

    @Test
    void exportCsv_создаётФайл() throws Exception {
        String path = tempDir.resolve("test.csv").toString();
        exportService.exportCsv(testVacancies, path);

        assertTrue(new File(path).exists());
    }

    @Test
    void exportCsv_содержитЗаголовок() throws Exception {
        String path = tempDir.resolve("test.csv").toString();
        exportService.exportCsv(testVacancies, path);

        String content = Files.readString(Path.of(path));
        assertTrue(content.contains("Название"));
        assertTrue(content.contains("Компания"));
    }

    @Test
    void exportCsv_содержитДанные() throws Exception {
        String path = tempDir.resolve("test.csv").toString();
        exportService.exportCsv(testVacancies, path);

        String content = Files.readString(Path.of(path));
        assertTrue(content.contains("Java Developer"));
        assertTrue(content.contains("Google"));
    }

    @Test
    void exportJson_создаётФайл() throws Exception {
        String path = tempDir.resolve("test.json").toString();
        exportService.exportJson(testVacancies, path);

        assertTrue(new File(path).exists());
    }

    @Test
    void exportJson_содержитДанные() throws Exception {
        String path = tempDir.resolve("test.json").toString();
        exportService.exportJson(testVacancies, path);

        String content = Files.readString(Path.of(path));
        assertTrue(content.contains("Java Developer"));
        assertTrue(content.contains("Google"));
    }

    @Test
    void exportHtml_создаётФайл() throws Exception {
        String path = tempDir.resolve("test.html").toString();
        exportService.exportHtml(testVacancies, path);

        assertTrue(new File(path).exists());
    }

    @Test
    void exportHtml_содержитТаблицу() throws Exception {
        String path = tempDir.resolve("test.html").toString();
        exportService.exportHtml(testVacancies, path);

        String content = Files.readString(Path.of(path));
        assertTrue(content.contains("<table>"));
        assertTrue(content.contains("Java Developer"));
    }
}
