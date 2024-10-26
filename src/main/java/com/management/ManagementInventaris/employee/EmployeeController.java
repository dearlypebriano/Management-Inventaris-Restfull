package com.management.ManagementInventaris.employee;

import com.management.ManagementInventaris.handler.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping(path = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PrivateEmployeeResponse> createNewEmployee(
            @ModelAttribute EmployeeRequest request,
            @RequestParam("file")MultipartFile file
    ) {
        PrivateEmployeeResponse response = employeeService.createNewEmployee(request, file);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping(path = "/update/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PrivateEmployeeResponse> updateEmployee(
            @PathVariable String employeeId,
            @ModelAttribute EmployeeRequest request,
            @RequestParam("file") MultipartFile file
    ) {
        PrivateEmployeeResponse response = employeeService.updateDataEmployee(employeeId, request, file);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping(path = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<List<PrivateEmployeeResponse>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebResponse<List<PrivateEmployeeResponse>> response = employeeService.findAll(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/findByNip", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrivateEmployeeResponse> findByNameAndNipForPrivate(
            @PathVariable Integer nip
    ) {
        PrivateEmployeeResponse response = employeeService.findByNameAndNipForPrivate(nip);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable String id) {
        PrivateEmployeeResponse response = employeeService.findByIdPrivate(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findAllByJob/{jobName}")
    public ResponseEntity<List<?>> findAllByJob(@PathVariable String jobName) {
        return ResponseEntity.ok(employeeService.findAllEmployeeByJob(jobName));
    }
}
