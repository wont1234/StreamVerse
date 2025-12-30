package com.buguagaoshu.tiktube.vo;

import dev.samstevens.totp.qr.QrData;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @create 2025-04-25
 */
@Data
public class TwoFactorData {
    String secret;

    @Size(min = 2, max = 10)
    String code;

    Long userId;

    @Size(max = 100)
    String recoveryCode;

    QrData qrData;
}
