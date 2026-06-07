package com.homework.db;

import com.homework.model.Vacancy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VacancyRepository implements IVacancyRepository {

    public void createTableIfNotExists() throws SQLException {
        String vacanciesTable = """
                CREATE TABLE IF NOT EXISTS vacancies (
                    id               INTEGER PRIMARY KEY AUTOINCREMENT,
                    job_title        TEXT,
                    employer_name    TEXT,
                    employer_website TEXT,
                    job_publisher    TEXT,
                    employment_types TEXT,
                    apply_link       TEXT UNIQUE,
                    job_description  TEXT,
                    is_remote        INTEGER,
                    posted_at        TEXT,
                    location         TEXT,
                    min_salary       REAL,
                    max_salary       REAL,
                    salary_period    TEXT
                )
                """;

        String historyTable = """
                CREATE TABLE IF NOT EXISTS history (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    action     TEXT NOT NULL,
                    count      INTEGER,
                    created_at TEXT DEFAULT (datetime('now'))
                )
                """;

        try (Statement stmt = DatabaseConnection.get().createStatement()) {
            stmt.execute(vacanciesTable);
            stmt.execute(historyTable);
            System.out.println("Таблицы готовы");
        }
    }

    private void logHistory(String action, int count) throws SQLException {
        String sql = "INSERT INTO history (action, count) VALUES (?, ?)";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setString(1, action);
            ps.setInt(2, count);
            ps.executeUpdate();
        }
    }

    public List<String> getHistory() throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = "SELECT action, count, created_at FROM history ORDER BY created_at DESC LIMIT 20";
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(String.format("[%s] %s — %d вак.",
                        rs.getString("created_at"),
                        rs.getString("action"),
                        rs.getInt("count")));
            }
        }
        return result;
    }

    public void save(Vacancy vacancy) throws SQLException {
        String sql = """
                INSERT OR IGNORE INTO vacancies
                (job_title, employer_name, employer_website, job_publisher,
                 employment_types, apply_link, job_description, is_remote,
                 posted_at, location, min_salary, max_salary, salary_period)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setString(1, vacancy.getJobTitle());
            ps.setString(2, vacancy.getEmployerName());
            ps.setString(3, vacancy.getEmployerWebsite());
            ps.setString(4, vacancy.getJobPublisher());
            ps.setString(5, vacancy.getJobEmploymentTypes() != null
                    ? String.join(", ", vacancy.getJobEmploymentTypes()) : null);
            ps.setString(6, vacancy.getJobApplyLink());
            ps.setString(7, vacancy.getJobDescription());
            ps.setInt(8, vacancy.isJobIsRemote() ? 1 : 0);
            ps.setString(9, vacancy.getJobPostedAt());
            ps.setString(10, vacancy.getJobLocation());
            ps.setObject(11, vacancy.getMinSalary());
            ps.setObject(12, vacancy.getMaxSalary());
            ps.setString(13, vacancy.getJobSalaryPeriod());
            ps.executeUpdate();
        }
    }

    public void saveAll(List<Vacancy> vacancies) throws SQLException {
        for (Vacancy v : vacancies) {
            save(v);
        }
        logHistory("Добавлено вакансий", vacancies.size());
        System.out.println("Сохранено вакансий: " + vacancies.size());
    }

    public List<Vacancy> findAll() throws SQLException {
        return executeQuery("SELECT * FROM vacancies");
    }

    public List<Vacancy> search(String keyword) throws SQLException {
        String[] words = keyword.toLowerCase().split(" ");
        StringBuilder sql = new StringBuilder("SELECT * FROM vacancies WHERE 1=1");
        for (String word : words) {
            sql.append(" AND LOWER(job_title) LIKE '%").append(word).append("%'");
        }
        return executeQuery(sql.toString());
    }

    public List<Vacancy> findRemote() throws SQLException {
        return executeQuery("SELECT * FROM vacancies WHERE is_remote = 1");
    }

    public List<Vacancy> findBySalary(double minSalary) throws SQLException {
        return executeQuery("SELECT * FROM vacancies WHERE min_salary >= " + minSalary);
    }

    public int countAll() throws SQLException {
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM vacancies")) {
            return rs.getInt(1);
        }
    }

    public int countRemote() throws SQLException {
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM vacancies WHERE is_remote = 1")) {
            return rs.getInt(1);
        }
    }

    public double avgSalary() throws SQLException {
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery("SELECT AVG(min_salary) FROM vacancies WHERE min_salary IS NOT NULL")) {
            return rs.getDouble(1);
        }
    }

    public List<String> topLocations() throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = """
                SELECT location, COUNT(*) as cnt
                FROM vacancies
                WHERE location IS NOT NULL
                GROUP BY location
                ORDER BY cnt DESC
                LIMIT 5
                """;
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(rs.getString("location") + " — " + rs.getInt("cnt") + " вак.");
            }
        }
        return result;
    }

    public List<String> topCompanies() throws SQLException {
        List<String> result = new ArrayList<>();
        String sql = """
                SELECT employer_name, COUNT(*) as cnt
                FROM vacancies
                WHERE employer_name IS NOT NULL
                GROUP BY employer_name
                ORDER BY cnt DESC
                LIMIT 5
                """;
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(rs.getString("employer_name") + " — " + rs.getInt("cnt") + " вак.");
            }
        }
        return result;
    }

    public List<Vacancy> findAllSortedBySalary() throws SQLException {
        return executeQuery("SELECT * FROM vacancies ORDER BY min_salary DESC NULLS LAST");
    }

    public List<Vacancy> findAllSortedByDate() throws SQLException {
        return executeQuery("SELECT * FROM vacancies ORDER BY posted_at DESC NULLS LAST");
    }

    public List<Vacancy> findAllSortedByCompany() throws SQLException {
        return executeQuery("SELECT * FROM vacancies ORDER BY employer_name ASC NULLS LAST");
    }

    private List<Vacancy> executeQuery(String sql) throws SQLException {
        List<Vacancy> vacancies = new ArrayList<>();
        try (Statement stmt = DatabaseConnection.get().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vacancies.add(mapRow(rs));
            }
        }
        return vacancies;
    }

    private Vacancy mapRow(ResultSet rs) throws SQLException {
        return Vacancy.builder()
                .jobTitle(rs.getString("job_title"))
                .employerName(rs.getString("employer_name"))
                .employerWebsite(rs.getString("employer_website"))
                .jobPublisher(rs.getString("job_publisher"))
                .jobApplyLink(rs.getString("apply_link"))
                .jobDescription(rs.getString("job_description"))
                .jobIsRemote(rs.getInt("is_remote") == 1)
                .jobPostedAt(rs.getString("posted_at"))
                .jobLocation(rs.getString("location"))
                .minSalary(rs.getObject("min_salary") != null ? rs.getDouble("min_salary") : null)
                .maxSalary(rs.getObject("max_salary") != null ? rs.getDouble("max_salary") : null)
                .jobSalaryPeriod(rs.getString("salary_period"))
                .build();
    }
}
