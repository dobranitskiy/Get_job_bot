package com.homework.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Vacancy {
    private String jobTitle;
    private String employerName;
    private String employerWebsite;
    private String jobPublisher;
    private List<String> jobEmploymentTypes;
    private String jobApplyLink;
    private String jobDescription;
    private boolean jobIsRemote;
    private String jobPostedAt;
    private String jobLocation;
    private Double minSalary;
    private Double maxSalary;
    private String jobSalaryPeriod;
}
