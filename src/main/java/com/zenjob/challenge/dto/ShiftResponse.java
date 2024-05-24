package com.zenjob.challenge.dto;


import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class ShiftResponse {
    UUID id;
    UUID talentId;
    UUID jobId;
    Instant start;
    Instant end;
}
