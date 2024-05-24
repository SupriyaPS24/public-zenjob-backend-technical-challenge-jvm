package com.zenjob.challenge.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

    @Builder
    @Data
    public class GetShiftsResponse {
        List<ShiftResponse> shifts;
}
