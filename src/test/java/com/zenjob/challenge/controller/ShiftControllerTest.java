package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.BookTalentRequestDto;
import com.zenjob.challenge.dto.GetShiftsResponse;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ShiftResponse;
import com.zenjob.challenge.entity.Job;
import com.zenjob.challenge.entity.Shift;
import com.zenjob.challenge.service.ShiftService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ShiftControllerTest {

    @Mock
    private ShiftService shiftService;

    @Mock
    private Job job;

    @Mock
    private Shift shift;

    @InjectMocks
    private ShiftController shiftController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        shift = createShift(UUID.randomUUID());
    }

    @Test
    void testGetShifts() {
        UUID jobId = UUID.randomUUID();
        List<Shift> shifts = Collections.singletonList(createShift(jobId));
        when(shiftService.getShifts(jobId)).thenReturn(shifts);
        ResponseDto<GetShiftsResponse> response = shiftController.getShifts(jobId);
        List<ShiftResponse> expectedShiftResponses = shifts.stream()
                .map(shift -> ShiftResponse.builder()
                        .id(shift.getId()) // Use shift.getId() directly
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getCreatedAt())
                        .end(shift.getEndTime())
                        .build())
                .toList();
        assertEquals(expectedShiftResponses.size(), response.getData().getShifts().size());
    }



    @Test
    void testBookTalent() {
        UUID shiftId = UUID.randomUUID();
        BookTalentRequestDto dto = new BookTalentRequestDto();
        dto.setTalent(UUID.randomUUID());
        shiftController.bookTalent(shiftId, dto);
        verify(shiftService, times(1)).bookTalent(shiftId, dto.getTalent());
    }

    @Test
    void testCancelShift() {
        UUID shiftId = UUID.randomUUID();
        when(shiftService.cancelShift(shiftId)).thenReturn(true);
        ResponseEntity<String> response = shiftController.cancelShift(shiftId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Shift: " + shiftId + " is cancelled"));
    }

    @Test
    void testCancelShiftsForTalent() {
        UUID talentId = UUID.randomUUID();
        when(shiftService.cancelTalentAndReplaceShifts(talentId)).thenReturn(true);
        ResponseEntity<String> response = shiftController.cancelShiftsForTalent(talentId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Shifts cancelled for talent " + talentId + " and replacements shifts are created"));
    }

    private Shift createShift(UUID jobId) {
        Shift shift = new Shift();
        shift.setId(UUID.randomUUID());
        shift.setTalentId(UUID.randomUUID());
        shift.setJob(job);
        shift.setCreatedAt(Instant.now());
        shift.setEndTime(Instant.now().plusSeconds(3600)); // Example end time
        return shift;
    }
}
