package com.buguagaoshu.tiktube.model;

import lombok.Data;

/**
 * @create 2025-05-05
 */
@Data
public class MailConfigData {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private String protocol;
}
