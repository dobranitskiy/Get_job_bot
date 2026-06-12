package com.homework.export;

import com.homework.model.Vacancy;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExportService {

    public void export(List<Vacancy> vacancies, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Название,Компания,Локация,Удалённо,Зарплата от,Зарплата до,Период,Ссылка\n");
            for (Vacancy v : vacancies) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                        escape(v.getJobTitle()),
                        escape(v.getEmployerName()),
                        escape(v.getJobLocation()),
                        v.isJobIsRemote() ? "Да" : "Нет",
                        v.getMinSalary() != null ? v.getMinSalary().toString() : "",
                        v.getMaxSalary() != null ? v.getMaxSalary().toString() : "",
                        escape(v.getJobSalaryPeriod()),
                        escape(v.getJobApplyLink())
                ));
            }
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
