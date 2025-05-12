package com.larkin.defcode.service;

import com.larkin.defcode.dao.OtpConfigDao;
import com.larkin.defcode.dto.request.OtpConfigRequest;
import com.larkin.defcode.util.DurationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OtpConfigService {

    private final OtpConfigDao otpConfigDao;

    public void changeOtpConfig(OtpConfigRequest request) {
        Duration duration = DurationUtil.parseInputDuration(request.getLifetime());
        otpConfigDao.updateOtpConfig(duration, request.getLength());
    }
}
