package com.management.ManagementInventaris.gender;

import com.management.ManagementInventaris.handler.PagingResponse;
import com.management.ManagementInventaris.handler.WebResponse;
import com.management.ManagementInventaris.utils.CalculatePages;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenderService {

    @Autowired
    private GenderRepository genderRepository;

    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    @CachePut(value = "user", key = "'genderUser' + #result.id")
    public GenderResponse create(GenderRequest request) {
        Gender gender = new Gender();
        gender.setName(request.getName());
        genderRepository.save(gender);

        return toGenderResponse(gender);
    }

    @Transactional
    @CacheEvict(value = "user", allEntries = true)
    @CachePut(value = "user", key = "'genderUser' + #result.id")
    public GenderResponse update(Integer id, GenderRequest request) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender With ID : " + id + " Not Found!"));
        gender.setName(request.getName());
        genderRepository.save(gender);

        return toGenderResponse(gender);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'genderUser:' + #id")
    public GenderResponse get(Integer id) {
        Gender gender = genderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,  "Gender With ID : " + id + " Not Found!"));
        return toGenderResponse(gender);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "'findAllGenderForUser' + #page + '-' + #size")
    public WebResponse<List<GenderResponse>> findAll(int page, int size) {
        int offset = page * size;

        List<Gender> genders = genderRepository.findAllWithPagination(offset, size);

        List<GenderResponse> genderResponses = genders.stream()
                .map(this::toGenderResponse)
                .toList();

        CalculatePages calculatePages = new CalculatePages(genderResponses.size(), size);
        PagingResponse pagingResponse = PagingResponse.builder()
                .currentPage(page)
                .totalPage(calculatePages.calculateTotalPages())
                .size(genders.size())
                .build();

        return WebResponse.<List<GenderResponse>>builder()
                .data(genderResponses)
                .paging(pagingResponse)
                .build();
    }

    private GenderResponse toGenderResponse(Gender gender) {
        return GenderResponse.builder()
                .id(gender.getId())
                .name(gender.getName())
                .build();
    }
}