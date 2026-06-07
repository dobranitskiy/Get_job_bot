package com.homework.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.model.Vacancy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class JSearchClient {

    private static final String API_URL = "https://jsearch.p.rapidapi.com/search";
    private static final String API_HOST = "jsearch.p.rapidapi.com";

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JSearchClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public List<Vacancy> searchFromFile(String filePath) throws Exception {
        String json = new String(java.nio.file.Files.readAllBytes(java.nio.file.Path.of(filePath)));
        return parseResponse(json);
    }

    public List<Vacancy> search(String query, int numPages) throws Exception {
        String url = API_URL + "?query=" + query.replace(" ", "%20") + "&num_pages=" + numPages;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-rapidapi-host", API_HOST)
                .header("x-rapidapi-key", apiKey)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return parseResponse(response.body());
    }

    private List<Vacancy> parseResponse(String json) throws Exception {
        List<Vacancy> vacancies = new ArrayList<>();

        JsonNode root = objectMapper.readTree(json);
        JsonNode data = root.path("data");
        JsonNode jobs = data.isArray() ? data : data.path("jobs");

        for (JsonNode job : jobs) {
            List<String> types = new ArrayList<>();
            for (JsonNode type : job.path("job_employment_types")) {
                types.add(type.asText());
            }

            Vacancy vacancy = Vacancy.builder()
                    .jobTitle(getText(job, "job_title"))
                    .employerName(getText(job, "employer_name"))
                    .employerWebsite(getText(job, "employer_website"))
                    .jobPublisher(getText(job, "job_publisher"))
                    .jobEmploymentTypes(types)
                    .jobApplyLink(getText(job, "job_apply_link"))
                    .jobDescription(getText(job, "job_description"))
                    .jobIsRemote(job.path("job_is_remote").asBoolean(false))
                    .jobPostedAt(getText(job, "job_posted_at_datetime_utc"))
                    .jobLocation(getText(job, "job_location"))
                    .minSalary(getDouble(job, "job_min_salary"))
                    .maxSalary(getDouble(job, "job_max_salary"))
                    .jobSalaryPeriod(getText(job, "job_salary_period"))
                    .build();

            vacancies.add(vacancy);
        }

        return vacancies;
    }

    private String getText(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isNull() || value.isMissingNode()) return null;
        return value.asText();
    }

    private Double getDouble(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isNull() || value.isMissingNode()) return null;
        return value.asDouble();
    }
}
