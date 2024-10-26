package com.management.ManagementInventaris.job;

import com.management.ManagementInventaris.employee.EmployeeRepository;
import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.CalculatePages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    @CachePut(value = "employees", key = "'employeeJob' + #result.id")
    public JobResponse createJob(JobRequest request) {
        if (jobRepository.findByJobName(request.getJobName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job Name : " + request.getJobName() + " Already Exist!");
        }
        Job job = Job.builder()
                .jobName(request.getJobName())
                .description(request.getDescription())
                .build();
        return toJobResponse(jobRepository.save(job));
    }

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    @CachePut(value = "employees", key = "'employeeJob' + #result.id")
    public JobResponse updateJob(String id, JobRequest request) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job With ID : " + id + " Not Found!"));
        if (request.getJobName() != null) job.setJobName(request.getJobName());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        jobRepository.save(job);
        return toJobResponse(job);
    }

    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public void deleteJob(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job With ID : " + id + " Not Found!"));

        employeeRepository.clearJobByJobNameContaining(job.getJobName());

        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "'findAllJobForEmployee' + #page + '-' + #size")
    public WebResponse<List<JobResponse>> findAll(int page, int size) {
        int offset = page * size;
        List<Job> jobs = jobRepository.findAllWithPagination(offset, size);

        List<JobResponse> jobResponses = jobs.stream()
                .map(this::toJobResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(jobRepository.count(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .size(jobResponses.size())
                .build();

        return WebResponse.<List<JobResponse>>builder()
                .data(jobResponses)
                .paging(pagingResponse)
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "'getJobEmployeeById:' + #id")
    public JobResponse getJob(String id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job With ID : " + id + " Not Found!"));
        return toJobResponse(job);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "'getJobEmployeeByName:' + #name")
    public List<JobResponse> getJobByName(String jobName) {
        List<Job> jobList = jobRepository.findByJobNameContaining(jobName);
        if (jobList == null || jobName.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job with name : " + jobName + " Not Found!");
        return jobList.stream().map(this::toJobResponse).collect(Collectors.toList());
    }

    private JobResponse toJobResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .jobname(job.getJobName())
                .description(job.getDescription())
                .build();
    }
}