package com.homework.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.homework.model.Vacancy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportService implements IExportService {

    private final ObjectMapper objectMapper;

    public ExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void exportCsv(List<Vacancy> vacancies, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Название,Компания,Локация,Удалённо,Зарплата от,Зарплата до,Период,Ссылка\n");
            for (Vacancy v : vacancies) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                        escapeCsv(v.getJobTitle()),
                        escapeCsv(v.getEmployerName()),
                        escapeCsv(v.getJobLocation()),
                        v.isJobIsRemote() ? "Да" : "Нет",
                        v.getMinSalary() != null ? v.getMinSalary().toString() : "",
                        v.getMaxSalary() != null ? v.getMaxSalary().toString() : "",
                        escapeCsv(v.getJobSalaryPeriod()),
                        escapeCsv(v.getJobApplyLink())
                ));
            }
        }
    }

    public void exportJson(List<Vacancy> vacancies, String filePath) throws IOException {
        objectMapper.writeValue(new java.io.File(filePath), vacancies);
    }

    public void exportHtml(List<Vacancy> vacancies, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("""
                    <!DOCTYPE html>
                    <html lang="ru">
                    <head>
                        <meta charset="UTF-8">
                        <title>Вакансии</title>
                        <style>
                            body { font-family: Arial, sans-serif; margin: 20px; }
                            h1 { color: #333; }
                            table { width: 100%%; border-collapse: collapse; }
                            th { background-color: #4CAF50; color: white; padding: 10px; text-align: left; }
                            td { padding: 8px; border-bottom: 1px solid #ddd; }
                            tr:hover { background-color: #f5f5f5; }
                            a { color: #4CAF50; }
                        </style>
                    </head>
                    <body>
                    <h1>Агрегатор вакансий</h1>
                    <p>Всего вакансий: %d</p>
                    <table>
                        <tr>
                            <th>#</th>
                            <th>Название</th>
                            <th>Компания</th>
                            <th>Локация</th>
                            <th>Зарплата</th>
                            <th>Ссылка</th>
                        </tr>
                    """.formatted(vacancies.size()));

            for (int i = 0; i < vacancies.size(); i++) {
                Vacancy v = vacancies.get(i);
                writer.write(String.format("""
                        <tr>
                            <td>%d</td>
                            <td>%s</td>
                            <td>%s</td>
                            <td>%s %s</td>
                            <td>%s</td>
                            <td><a href="%s" target="_blank">Открыть</a></td>
                        </tr>
                        """,
                        i + 1,
                        escapeHtml(v.getJobTitle()),
                        escapeHtml(v.getEmployerName()),
                        escapeHtml(v.getJobLocation()),
                        v.isJobIsRemote() ? "🌐" : "",
                        formatSalary(v),
                        v.getJobApplyLink() != null ? v.getJobApplyLink() : "#"
                ));
            }

            writer.write("</table></body></html>");
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
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
}
