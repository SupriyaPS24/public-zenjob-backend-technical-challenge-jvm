package com.zenjob.challenge.controller;

import com.zenjob.challenge.dto.BookTalentRequestDto;
import com.zenjob.challenge.dto.GetShiftsResponse;
import com.zenjob.challenge.dto.ResponseDto;
import com.zenjob.challenge.dto.ShiftResponse;
import com.zenjob.challenge.service.ShiftService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/shift")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping(path = "/{jobId}")
    @ResponseBody
    public ResponseDto<GetShiftsResponse> getShifts(@PathVariable("jobId") UUID uuid) {
        List<ShiftResponse> shiftResponses = shiftService.getShifts(uuid).stream()
                .map(shift -> ShiftResponse.builder()
                        .id(uuid)
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getCreatedAt())
                        .end(shift.getEndTime())
                        .build())
                .collect(Collectors.toList());
        return ResponseDto.<GetShiftsResponse>builder()
                .data(GetShiftsResponse.builder()
                        .shifts(shiftResponses)
                        .build())
                .build();
    }

    @PatchMapping(path = "/{id}/book")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void bookTalent(@PathVariable("id") UUID shiftId, @RequestBody @Valid BookTalentRequestDto dto) {
        shiftService.bookTalent(shiftId, dto.getTalent());
    }

    @DeleteMapping("/cancelShift/{shiftId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> cancelShift(@PathVariable UUID shiftId) {
        boolean isCancelled = shiftService.cancelShift(shiftId);
        return isCancelled ? ResponseEntity.ok("Shift: " + shiftId + " is cancelled")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Shift found for given ShiftId: " + shiftId);
    }

    @PostMapping("/cancelTalent/{talentId}")
    public ResponseEntity<String> cancelShiftsForTalent(@PathVariable UUID talentId) {
        boolean success = shiftService.cancelTalentAndReplaceShifts(talentId);
        return success ? ResponseEntity.ok("Shifts cancelled for talent " + talentId + " and replacements shifts are created")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("No talent found with given TalentId: " + talentId);
    }
}