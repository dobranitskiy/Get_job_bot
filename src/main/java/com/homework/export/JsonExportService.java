package com.homework.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.homework.model.Vacancy;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonExportService {

    private final ObjectMapper objectMapper;

    public JsonExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void export(List<Vacancy> vacancies, String filePath) throws IOException {
        objectMapper.writeValue(new File(filePath), vacancies);
    }
}
