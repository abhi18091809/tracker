package com.issues.tracker.github.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@ConfigurationProperties("github")
@Configuration
public class GithubPropertyManager {

    private Map<String,String> issueUrl;

}
