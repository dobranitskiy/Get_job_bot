package com.homework.export;

import com.homework.model.Vacancy;

import java.io.IOException;
import java.util.List;

public class ExportService implements IExportService {

    private final CsvExportService csvExportService;
    private final JsonExportService jsonExportService;
    private final HtmlExportService htmlExportService;

    public ExportService() {
        this.csvExportService = new CsvExportService();
        this.jsonExportService = new JsonExportService();
        this.htmlExportService = new HtmlExportService();
    }

    public void exportCsv(List<Vacancy> vacancies, String filePath) throws IOException {
        csvExportService.export(vacancies, filePath);
    }

    public void exportJson(List<Vacancy> vacancies, String filePath) throws IOException {
        jsonExportService.export(vacancies, filePath);
    }

    public void exportHtml(List<Vacancy> vacancies, String filePath) throws IOException {
        htmlExportService.export(vacancies, filePath);
    }
}
