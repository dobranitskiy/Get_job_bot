package com.homework.model;

import java.util.List;

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

    private Vacancy() {}

    public String getJobTitle()                  { return jobTitle; }
    public String getEmployerName()              { return employerName; }
    public String getEmployerWebsite()           { return employerWebsite; }
    public String getJobPublisher()              { return jobPublisher; }
    public List<String> getJobEmploymentTypes()  { return jobEmploymentTypes; }
    public String getJobApplyLink()              { return jobApplyLink; }
    public String getJobDescription()            { return jobDescription; }
    public boolean isJobIsRemote()               { return jobIsRemote; }
    public String getJobPostedAt()               { return jobPostedAt; }
    public String getJobLocation()               { return jobLocation; }
    public Double getMinSalary()                 { return minSalary; }
    public Double getMaxSalary()                 { return maxSalary; }
    public String getJobSalaryPeriod()           { return jobSalaryPeriod; }

    public void setJobTitle(String jobTitle)                      { this.jobTitle = jobTitle; }
    public void setEmployerName(String employerName)              { this.employerName = employerName; }
    public void setEmployerWebsite(String employerWebsite)        { this.employerWebsite = employerWebsite; }
    public void setJobPublisher(String jobPublisher)              { this.jobPublisher = jobPublisher; }
    public void setJobEmploymentTypes(List<String> types)         { this.jobEmploymentTypes = types; }
    public void setJobApplyLink(String jobApplyLink)              { this.jobApplyLink = jobApplyLink; }
    public void setJobDescription(String jobDescription)          { this.jobDescription = jobDescription; }
    public void setJobIsRemote(boolean jobIsRemote)               { this.jobIsRemote = jobIsRemote; }
    public void setJobPostedAt(String jobPostedAt)                { this.jobPostedAt = jobPostedAt; }
    public void setJobLocation(String jobLocation)                { this.jobLocation = jobLocation; }
    public void setMinSalary(Double minSalary)                    { this.minSalary = minSalary; }
    public void setMaxSalary(Double maxSalary)                    { this.maxSalary = maxSalary; }
    public void setJobSalaryPeriod(String jobSalaryPeriod)        { this.jobSalaryPeriod = jobSalaryPeriod; }

    @Override
    public String toString() {
        return "Vacancy{jobTitle='" + jobTitle + "', employerName='" + employerName + "'}";
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Vacancy vacancy = new Vacancy();

        public Builder jobTitle(String val)                 { vacancy.jobTitle = val; return this; }
        public Builder employerName(String val)             { vacancy.employerName = val; return this; }
        public Builder employerWebsite(String val)          { vacancy.employerWebsite = val; return this; }
        public Builder jobPublisher(String val)             { vacancy.jobPublisher = val; return this; }
        public Builder jobEmploymentTypes(List<String> val) { vacancy.jobEmploymentTypes = val; return this; }
        public Builder jobApplyLink(String val)             { vacancy.jobApplyLink = val; return this; }
        public Builder jobDescription(String val)           { vacancy.jobDescription = val; return this; }
        public Builder jobIsRemote(boolean val)             { vacancy.jobIsRemote = val; return this; }
        public Builder jobPostedAt(String val)              { vacancy.jobPostedAt = val; return this; }
        public Builder jobLocation(String val)              { vacancy.jobLocation = val; return this; }
        public Builder minSalary(Double val)                { vacancy.minSalary = val; return this; }
        public Builder maxSalary(Double val)                { vacancy.maxSalary = val; return this; }
        public Builder jobSalaryPeriod(String val)          { vacancy.jobSalaryPeriod = val; return this; }

        public Vacancy build() { return vacancy; }
    }
}
