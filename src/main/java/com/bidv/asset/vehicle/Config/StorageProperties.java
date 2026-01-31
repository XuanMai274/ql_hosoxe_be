package com.bidv.asset.vehicle.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class StorageProperties {

    @Value("${storage.root-path}")
    private String rootPath;
}