package com.issues.tracker.github.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

@Getter
public class GetIssueRequest {


    @JsonProperty("publicUrl")
    private String publicUrl;

}
