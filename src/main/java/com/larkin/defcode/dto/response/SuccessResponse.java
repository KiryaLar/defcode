package com.larkin.defcode.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuccessResponse {

    private String message;
    private String timestamp;
}
