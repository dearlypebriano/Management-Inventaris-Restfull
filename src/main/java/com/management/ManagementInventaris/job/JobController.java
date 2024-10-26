package com.management.ManagementInventaris.job;

import com.management.ManagementInventaris.handler.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponse> createNewJob(@RequestBody JobRequest request) {
        JobResponse response = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping(path = "/update/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable String jobId,
            @RequestBody JobRequest request)
    {
        try {
            JobResponse response = jobService.updateJob(jobId, request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping(path = "/delete/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteJob(@PathVariable String jobId) {
        try {
            jobService.deleteJob(jobId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/findById/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<JobResponse> findJobById(@PathVariable String jobId) {
        try {
            JobResponse response = jobService.getJob(jobId);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<JobResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<JobResponse>> response = jobService.findAll(page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = "/findByName/{jobName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<JobResponse>> findJobByName(@PathVariable String jobName) {
        try {
            List<JobResponse> response = jobService.getJobByName(jobName);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}