package com.homework.db;

import com.homework.model.Vacancy;

import java.sql.SQLException;
import java.util.List;

public interface IVacancyRepository {
    void createTableIfNotExists() throws SQLException;
    void save(Vacancy vacancy) throws SQLException;
    void saveAll(List<Vacancy> vacancies) throws SQLException;
    List<Vacancy> findAll() throws SQLException;
    List<Vacancy> search(String keyword) throws SQLException;
    List<Vacancy> findRemote() throws SQLException;
    List<Vacancy> findBySalary(double minSalary) throws SQLException;
    List<Vacancy> findAllSortedBySalary() throws SQLException;
    List<Vacancy> findAllSortedByDate() throws SQLException;
    List<Vacancy> findAllSortedByCompany() throws SQLException;
    int countAll() throws SQLException;
    int countRemote() throws SQLException;
    double avgSalary() throws SQLException;
    List<String> topLocations() throws SQLException;
    List<String> topCompanies() throws SQLException;
    List<String> getHistory() throws SQLException;
}
