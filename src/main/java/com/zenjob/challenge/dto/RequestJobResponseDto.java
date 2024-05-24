package com.zenjob.challenge.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Builder
@Data
public class RequestJobResponseDto {
        UUID jobId;
}

