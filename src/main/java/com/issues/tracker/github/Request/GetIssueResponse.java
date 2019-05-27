package com.issues.tracker.github.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class GetIssueResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("state")
    private String state;

    @JsonProperty("number")
    private Integer number;

    @JsonProperty("title")
    private String title;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("closed_at")
    private String closedAt;

    private Date created;
    private Date updated;
    private Date closed;





}
