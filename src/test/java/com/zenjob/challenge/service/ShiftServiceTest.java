package com.zenjob.challenge.service;

import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.repository.JobRepository;
import com.zenjob.challenge.repository.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private ShiftService shiftService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGetShifts() {
            UUID jobId = UUID.randomUUID();
            Shift shift1 = mock(Shift.class);
            Shift shift2 = mock(Shift.class);
            List<Shift> expectedShifts = Arrays.asList(shift1, shift2);
            when(shiftRepository.findAllByJobId(jobId)).thenReturn(expectedShifts);
            List<Shift> actualShifts = shiftService.getShifts(jobId);
            assertEquals(expectedShifts.size(), actualShifts.size());
            assertTrue(actualShifts.containsAll(expectedShifts));
    }

    @Test
    void testBookTalent() {
        UUID shiftId = UUID.randomUUID();
        UUID talentId = UUID.randomUUID();
        Shift shift = mock(Shift.class);
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        shiftService.bookTalent(shiftId, talentId);
        verify(shift).setTalentId(talentId);
    }

    @Test
    public void testCancelShiftWhenShiftExists() {
        UUID shiftId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        Shift shift = mock(Shift.class);
        Job job = mock(Job.class);
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shift.getJob()).thenReturn(job);
        when(job.getId()).thenReturn(jobId);
        when(shiftRepository.countByJobId(jobId)).thenReturn(1L);
        doNothing().when(shiftRepository).delete(shift);
        boolean result = shiftService.cancelShift(shiftId);
        assertTrue(result);
        verify(shiftRepository, times(1)).delete(shift);
        verify(jobRepository, never()).delete(job);
    }
    @Test
    void testCancelShiftWhenShiftDoesNotExist() {
        UUID shiftId = UUID.randomUUID();
        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());
        boolean result = shiftService.cancelShift(shiftId);
        assertFalse(result);
        verify(shiftRepository, never()).delete(any());
        verify(jobRepository, never()).delete(any());
    }

    @Test
    void testCancelTalentAndReplaceShiftsWhenNoShiftsFound() {
        UUID talentId = UUID.randomUUID();
        when(shiftRepository.findByTalentId(talentId)).thenReturn(Collections.emptyList());
        boolean result = shiftService.cancelTalentAndReplaceShifts(talentId);
        assertFalse(result);
        verify(shiftRepository, never()).deleteAll(any());
        verify(shiftRepository, never()).saveAll(any());
    }

}
