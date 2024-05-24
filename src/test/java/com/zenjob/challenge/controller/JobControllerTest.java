package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.RequestJobRequestDto;
import com.zenjob.challenge.dto.RequestJobResponseDto;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JobControllerTest {

    @Mock
    private JobService jobService;

    @Mock
    private Job job;

    @InjectMocks
    private JobController jobController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Initialize mocks
    }

    @Test
    void testCreateJob_ValidRequest() {
        RequestJobRequestDto dto = new RequestJobRequestDto();
        dto.setStart(LocalDate.now().plusDays(1));
        dto.setEnd(LocalDate.now().plusDays(3));
        UUID companyId = UUID.randomUUID();
        dto.setCompanyId(companyId);
        Job job = mock(Job.class);
        when(jobService.createJob(any(UUID.class), any(LocalDate.class), any(LocalDate.class), any(UUID.class)))
                .thenReturn(job);
        ResponseEntity<ResponseDto<Object>> responseEntity = jobController.createJob(dto);
        verify(jobService, times(1)).createJob(any(UUID.class), eq(dto.getStart()), eq(dto.getEnd()), eq(companyId));
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }


    @Test
    void testCancelJob_ExistingJob() {
        UUID jobId = UUID.randomUUID();
        when(jobService.cancelJob(jobId)).thenReturn(true);
        ResponseEntity<String> responseEntity = jobController.cancelJob(jobId);
        verify(jobService, times(1)).cancelJob(jobId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Job: " + jobId + " is cancelled", responseEntity.getBody());
    }

    @Test
    void testCancelJob_NonExistingJob() {
        UUID jobId = UUID.randomUUID();
        when(jobService.cancelJob(jobId)).thenReturn(false);
        ResponseEntity<String> responseEntity = jobController.cancelJob(jobId);
        verify(jobService, times(1)).cancelJob(jobId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Job not found for given JobId: " + jobId, responseEntity.getBody());
    }
}
