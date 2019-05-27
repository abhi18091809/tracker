package com.issues.tracker.github.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SearchResponse {

    @JsonProperty("total_count")
    private Integer totalCount;

}
