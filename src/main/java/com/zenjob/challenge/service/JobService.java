package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Service
@Transactional
public class JobService {

    private final JobRepository jobRepository;
    public Job createJob(UUID uuid, LocalDate date1, LocalDate date2,UUID companyId) {
        Instant now = Instant.now();
        // Validate start and end dates
        LocalDate maxDate = LocalDate.of(9999, 12, 31);
        if (date1.isBefore(LocalDate.now()) || date2.isBefore(date1) || date1.isAfter(maxDate) || date2.isAfter(maxDate)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        Job job = Job.builder()
                .id(uuid)
                .companyId(companyId)
                .startTime(date1.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(date2.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                .createdAt(now)
                .updatedAt(now)
                .build();
        job.setShifts(LongStream.range(0, ChronoUnit.DAYS.between(date1, date2))
                .mapToObj(idx -> date1.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .id(UUID.randomUUID())
                        .job(job)
                        .startTime(date.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(date.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .collect(Collectors.toList()));
        return jobRepository.save(job);
    }

    public boolean cancelJob(UUID jobId) {
        Optional<Job> job = jobRepository.findById(jobId);
        job.ifPresent(jobRepository::delete);
        return job.isPresent();
    }
}
