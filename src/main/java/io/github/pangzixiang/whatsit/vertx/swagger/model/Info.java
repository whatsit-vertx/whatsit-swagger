package io.github.pangzixiang.whatsit.vertx.swagger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Info {
    private String description;
    private String version;
    private String title;
    private String termsOfService;
    private Contact contact;
    private License license;
}
