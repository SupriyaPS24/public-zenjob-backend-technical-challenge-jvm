package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.RequestJobRequestDto;
import com.zenjob.challenge.dto.RequestJobResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.service.JobService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping(path = "/job")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseDto<Object>> createJob(@RequestBody @Valid RequestJobRequestDto dto) {

        LocalDate maxDate = LocalDate.of(9999, 12, 31);

        if (dto.getStart().isBefore(LocalDate.now()) || dto.getEnd().isBefore(dto.getStart()) || dto.getStart().isAfter(maxDate) || dto.getEnd().isAfter(maxDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.builder()
                            .error("Invalid date range")
                            .build());
        }
            Job job = jobService.createJob(UUID.randomUUID(), dto.getStart(), dto.getEnd(), dto.getCompanyId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body((ResponseDto<Object>) ResponseDto.builder()
                            .data(RequestJobResponseDto.builder()
                                    .jobId(job.getId())
                                    .build())
                            .build());
    }

    @DeleteMapping("/cancelJob/{jobId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> cancelJob(@PathVariable UUID jobId) {
        boolean isDeleted = jobService.cancelJob(jobId);
        return isDeleted ? ResponseEntity.ok("Job: "+jobId+ " is cancelled")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found for given JobId: "+jobId);
    }
}
