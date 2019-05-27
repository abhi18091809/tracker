package com.issues.tracker.github.Config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GithubConfig {

   @Bean
    RestTemplate restTemplate(){
       return new RestTemplate();
   }

}
