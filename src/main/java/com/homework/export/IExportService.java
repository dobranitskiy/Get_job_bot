package com.homework.export;

import com.homework.model.Vacancy;

import java.io.IOException;
import java.util.List;

public interface IExportService {
    void exportCsv(List<Vacancy> vacancies, String filePath) throws IOException;
    void exportJson(List<Vacancy> vacancies, String filePath) throws IOException;
    void exportHtml(List<Vacancy> vacancies, String filePath) throws IOException;
}
