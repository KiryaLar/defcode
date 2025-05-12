package com.larkin.defcode.entity;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OtpCode {

    private Integer id;
    private Integer code;
    private Integer userId;
    private String status;
    private Instant expirationTime;
    private Integer operationType;
}
