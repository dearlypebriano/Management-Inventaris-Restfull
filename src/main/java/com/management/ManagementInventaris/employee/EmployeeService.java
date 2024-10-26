package com.management.ManagementInventaris.employee;

import com.management.ManagementInventaris.exception.AuthorizationException;
import com.management.ManagementInventaris.gender.Gender;
import com.management.ManagementInventaris.gender.GenderRepository;
import com.management.ManagementInventaris.job.Job;
import com.management.ManagementInventaris.job.JobRepository;
import com.management.ManagementInventaris.location.district.District;
import com.management.ManagementInventaris.location.district.DistrictService;
import com.management.ManagementInventaris.location.province.Province;
import com.management.ManagementInventaris.location.province.ProvinceService;
import com.management.ManagementInventaris.location.regency.Regency;
import com.management.ManagementInventaris.location.regency.RegencyService;
import com.management.ManagementInventaris.location.village.Village;
import com.management.ManagementInventaris.location.village.VillageService;
import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.user.User;
import com.management.ManagementInventaris.user.UserRepository;
import com.management.ManagementInventaris.utils.Cryptographic;
import com.management.ManagementInventaris.utils.CurrencyFormatter;
import com.management.ManagementInventaris.utils.ImageCompressor;
import com.management.ManagementInventaris.utils.UserDetailToken;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private RegencyService regencyService;

    @Autowired
    private DistrictService districtService;

    @Autowired
    private VillageService villageService;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailToken userDetailToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Value("${server.host}")
    private String serverHost;

    @Value("${server.port}")
    private String serverPort;

    private final String server = serverHost + ":" + serverPort;

    private final AtomicInteger counter = new AtomicInteger(110);
    public int generateSequentialNip() {
        return counter.getAndIncrement();
    }

    /**
     * Creates a new employee based on the provided EmployeeRequest object.
     *
     * @param request the EmployeeRequest object containing the details of the new employee
     * @return a PrivateEmployeeResponse object containing the details of the newly created employee
     */
    @CacheEvict(value = "employees", allEntries = true)
    @CachePut(value = "employees", key = "#result.id")
    @Transactional
    public PrivateEmployeeResponse createNewEmployee(EmployeeRequest request, MultipartFile file) {
        Employee employee = new Employee();
        employee.setId(UUID.randomUUID().toString());
        employee.setEmployeeName(request.getEmployeeName());
        employee.setNip(generateSequentialNip());
        employee.setPhone(Long.valueOf(request.getPhone()));
        employee.setSalary(request.getSalary());

        Gender gender = genderRepository.findByName(request.getGender())
                .orElseThrow(() -> new IllegalStateException("Couldn't find gender"));
        employee.setGender(gender);

        Optional<Job> job = jobRepository.findByJobName(request.getJob());
        if (job.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job With Name : " + request.getJob() + " Not Found!");
        }
        employee.setJob(job.get());

        Province province = provinceService.findByName(request.getProvinceName());
        Regency regency = regencyService.findByNames(province.getName(), request.getRegencyName());
        District district = districtService.findDistrictByNames(province.getName(), regency.getName(), request.getDistrictName());
        Village village = villageService.getVillageByDistrictAndNames(province.getName(), regency.getName(), district.getName(), request.getVillageName());
        employee.setProvince(province);
        employee.setRegency(regency);
        employee.setDistrict(district);
        employee.setVillage(village);
        employee.setPostalCode(request.getPostalCode());

        if (file != null && !file.isEmpty()) {
            try {
                String hashedFileName = ImageCompressor.hashFileName(file.getOriginalFilename(), file.getBytes());
                String objectName = "uploaded/employee/" + hashedFileName;
                InputStream inputStream = file.getInputStream();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
                employee.setImageUrl(hashedFileName);
            } catch (IOException e) {
                log.error("Failed to upload image: {}", e.getMessage());
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            } catch (Exception e) {
                log.error("Error while uploading image: {}", e.getMessage());
                throw new RuntimeException("Error while uploading image: " + e.getMessage());
            }
        }

        employeeRepository.save(employee);

        EmployeeDTO employeeDTO = EmployeeDTO.fromEntity(employee);
        redisTemplate.opsForValue().set("employee:" + employeeDTO.getId(), employeeDTO);
        return toPrivateEmployeeResponse(employee);
    }

    /**
     * Updates the details of an existing employee.
     *
     * @param employeeId the ID of the employee to be updated
     * @param request    the EmployeeRequest object containing the new details of the employee
     * @return a PrivateEmployeeResponse object containing the updated details of the employee
     */
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    @CachePut(value = "employees", key = "#result.id")
    public PrivateEmployeeResponse updateDataEmployee(String employeeId, EmployeeRequest request, MultipartFile file) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Employee With ID : " + employeeId + " Not Found!"));

        if (request.getEmployeeName() != null) employee.setEmployeeName(request.getEmployeeName());
        if (request.getGender() != null) {
            Gender gender = genderRepository.findByName(request.getGender())
                    .orElseThrow(() -> new IllegalStateException("Couldn't find gender'"));
            employee.setGender(gender);
        }

        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getJob() != null) {
            Optional<Job> job = jobRepository.findByJobName(request.getJob());
            if (job.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job With Name : " + request.getJob() + " Not Found!");
            }
            employee.setJob(job.get());
        }

        if (request.getProvinceName() != null || request.getRegencyName() != null || request.getDistrictName() != null || request.getVillageName() != null) {
            Province province = provinceService.findByName(request.getProvinceName());
            employee.setProvince(province);
            Regency regency = regencyService.findByNames(province.getName(), request.getRegencyName());
            employee.setRegency(regency);
            District district = districtService.findDistrictByNames(province.getName(), regency.getName(), request.getDistrictName());
            employee.setDistrict(district);
            Village village = villageService.getVillageByDistrictAndNames(province.getName(), regency.getName(), district.getName(), request.getVillageName());
            employee.setVillage(village);
        }

        if (request.getPostalCode() != null) {
            employee.setPostalCode(request.getPostalCode());
        }

        if (file != null) {
            try {
                String hashedFileName = ImageCompressor.hashFileName(file.getOriginalFilename(), file.getBytes());
                String objectName = "uploaded/employee/" + hashedFileName;
                InputStream inputStream = file.getInputStream();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, inputStream.available(), -1)
                        .build());
                employee.setImageUrl(hashedFileName);
            } catch (Exception e) {
                log.error(String.valueOf(e));
                e.printStackTrace();
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        employeeRepository.save(employee);

        EmployeeDTO employeeDTO = EmployeeDTO.fromEntity(employee);
        redisTemplate.opsForValue().set("employee:" + employeeDTO.getId(), employeeDTO);

        return toPrivateEmployeeResponse(employee);
    }

    /**
     * Deletes an employee based on the provided ID.
     *
     * @param id the ID of the employee to be deleted
     */
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data Employee With ID : " + id + " Not Found!"));
        employeeRepository.delete(employee);
        redisTemplate.delete("employee:" + id);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "#page + '-' + #size")
    public WebResponse<List<PrivateEmployeeResponse>> findAll(int page, int size) {
        int offset = page * size;
        List<Employee> employees = employeeRepository.findAllWithPagination(offset, size);

        List<PrivateEmployeeResponse> employeeResponses = employees.stream()
                .map(this::toPrivateEmployeeResponse)
                .toList();

        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculateTotalPages(employeeRepository.count(), size))
                .size(employeeResponses.size())
                .build();

        log.info("Storing result in cache with key: page:{}-size:{}", page, size);

        return WebResponse.<List<PrivateEmployeeResponse>>builder()
                .data(employeeResponses)
                .paging(pagingResponse)
                .build();
    }

    // Method to calculate total pages
    private int calculateTotalPages(long totalItems, int size) {
        return (int) Math.ceil((double) totalItems / size);
    }

    /**
     * Retrieves an employee by NIP (Employee Identification Number) for private view.
     *
     * @param nip the NIP of the employee to retrieve
     * @return a PrivateEmployeeResponse object containing the details of the employee
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "'findByNameAndNip:' + #categoryName")
    public PrivateEmployeeResponse findByNameAndNipForPrivate(Integer nip) {
        Employee employeeList = employeeRepository.findByNip(nip);
        return toPrivateEmployeeResponse(employeeList);
    }

    /**
     * Retrieves an employee by ID for private view.
     *
     * @param id the ID of the employee to retrieve
     * @return a PrivateEmployeeResponse object containing the details of the employee
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "'getEmployeePrivateById:' + #id")
    public PrivateEmployeeResponse findByIdPrivate(String id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        assert employee != null;
        return toPrivateEmployeeResponse(employee);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "employees", key = "'getEmployeeByJob:' + #jobName")
    public List<PrivateEmployeeResponse> findAllEmployeeByJob(String jobName) {
        User user = userDetailToken.dataUserEmail();

        if (!user.getRole().name().equals("ADMIN") && !user.getRole().name().equals("MANAGER")) throw new AuthorizationException("User role not authorized to view this data");

        List<Job> jobList = jobRepository.findByJobNameContaining(jobName);
        if (jobList.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job with name: " + jobName + " not found!");
        }

        List<Employee> employees = jobList.stream()
                .flatMap(job -> employeeRepository.findAllByJobNameContaining(jobName).stream())
                .toList();

        return employees.stream()
                .map(this::toPrivateEmployeeResponse)
                .toList();
    }

    /**
     * Converts an Employee object to a PrivateEmployeeResponse object.
     *
     * @param employee the Employee object to convert
     * @return a PrivateEmployeeResponse object containing the details of the employee
     */
    private PrivateEmployeeResponse toPrivateEmployeeResponse(Employee employee) {
        String imageUrl = "http://localhost/api/minio/download/uploaded/employee/" + employee.getImageUrl();

        String encryptIdEmployee = "";
        try {
            encryptIdEmployee = Cryptographic.encrypt(employee.getId());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }

        return PrivateEmployeeResponse.builder()
                .id(encryptIdEmployee)
                .employeeName(employee.getEmployeeName())
                .nip(employee.getNip())
                .phone(employee.getPhone())
                .gender(employee.getGender().getName())
                .jobName(employee.getJob().getJobName())
                .salary(CurrencyFormatter.formatIDR(employee.getSalary()))
                .province(employee.getProvince().getName())
                .regency(employee.getRegency().getName())
                .district(employee.getDistrict().getName())
                .village(employee.getVillage().getName())
                .postalCode(employee.getPostalCode())
                .profile(imageUrl)
                .build();
    }
}