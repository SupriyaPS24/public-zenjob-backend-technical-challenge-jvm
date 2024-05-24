package com.zenjob.challenge.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.repository.JobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;
    private UUID companyId;
    private LocalDate validStartDate;
    private LocalDate validEndDate;
    private LocalDate pastDate;
    private LocalDate invalidEndDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        companyId = UUID.randomUUID();
        validStartDate = LocalDate.now();
        validEndDate = validStartDate.plusDays(10);
        pastDate = validStartDate.minusDays(1);
       // futureStartDate = LocalDate.of(9999, 12, 30);
        invalidEndDate = LocalDate.of(10000, 1, 1); // Invalid end date for the max date condition
    }

    //@Test
    void testCreateJob() {
        Job job = mock(Job.class);
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(3);
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(job));
        when(jobService.createJob(jobId, startDate,endDate, companyId))
                .thenReturn(job);
        when(job.getStartTime()).thenReturn(startDate.atTime(8, 0, 0).toInstant(ZoneOffset.UTC));
        when(job.getEndTime()).thenReturn(endDate.atTime(17, 0, 0).toInstant(ZoneOffset.UTC));
        when(job.getCreatedAt()).thenReturn(Instant.now());
        when(job.getUpdatedAt()).thenReturn(Instant.now());
        Job createdJob = jobService.createJob(jobId, startDate, endDate, companyId);
        assertEquals(job,createdJob);
    }


    private IllegalArgumentException assertInvalidDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            UUID jobId = UUID.randomUUID();
            jobService.createJob(jobId, startDate, endDate, companyId);
            // If no exception is thrown, the test should fail
            fail("Expected IllegalArgumentException to be thrown");
            return null; // This line is just to satisfy the compiler, as the fail method always throws an exception
        } catch (IllegalArgumentException e) {
            return e;
        }
    }

    @Test
    public void testCreateJobWithPastStartDate() {
        IllegalArgumentException exception = assertInvalidDateRange(pastDate, validEndDate);
        assert exception != null;
        assertEquals("Invalid date range", exception.getMessage());
    }

    @Test
    public void testCreateJobWithEndDateBeforeStartDate() {
        IllegalArgumentException exception = assertInvalidDateRange(validStartDate, validStartDate.minusDays(1));
        assert exception != null;
        assertEquals("Invalid date range", exception.getMessage());
    }

    @Test
    public void testCreateJobWithStartDateAfterMaxDate() {
        IllegalArgumentException exception = assertInvalidDateRange(invalidEndDate, validEndDate);
        assert exception != null;
        assertEquals("Invalid date range", exception.getMessage());
    }

    @Test
    public void testCreateJobWithEndDateAfterMaxDate() {
        IllegalArgumentException exception = assertInvalidDateRange(validStartDate, invalidEndDate);
        assert exception != null;
        assertEquals("Invalid date range", exception.getMessage());
    }

    //@Test
    void testCancelJob_WhenJobExists() {
        UUID jobId = UUID.randomUUID();
        Job existingJob = new Job();
        existingJob.setId(jobId);
        when(jobRepository.findById(jobId)).thenReturn(Optional.of(existingJob));
        boolean canceled = jobService.cancelJob(jobId);
        verify(jobRepository, times(1)).findById(jobId);
        verify(jobRepository, times(1)).delete(existingJob);
        assertTrue(canceled);
    }

    //@Test
    void testCancelJobWhenJobDoesNotExist() {
        UUID jobId = UUID.randomUUID();
        when(jobRepository.findById(jobId)).thenReturn(Optional.empty());
        boolean result = jobService.cancelJob(jobId);
        assertFalse(result);
        verify(jobRepository, never()).delete(any());
    }
}

