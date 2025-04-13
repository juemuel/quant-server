package com.juemuel.trend.config;

import lombok.Data;
import java.util.Map;

@Data
public class DataSourceProperties {
    private String name;
    private String type;
    private String url;
    private Map<String, String> properties;
}