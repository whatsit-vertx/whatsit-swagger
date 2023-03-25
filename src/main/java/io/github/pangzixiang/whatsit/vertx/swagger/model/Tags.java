package io.github.pangzixiang.whatsit.vertx.swagger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tags {
    private String name;
    private String description;
    private ExternalDocs externalDocs;
}
