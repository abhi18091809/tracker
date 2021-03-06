package com.issues.tracker.github.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RepositoryResponse {

    @JsonProperty("open_issues")
    private Integer openIssues;

}
