package com.issues.tracker.github.Controller;

import com.issues.tracker.github.Request.RepositoryResponse;
import com.issues.tracker.github.Request.SearchResponse;
import com.issues.tracker.github.Service.GithubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.QueryParam;

@RestController
@RequestMapping("/v1")
public class GithubController {

    private GithubService githubService;

    @Autowired public GithubController(GithubService githubService){
        this.githubService = githubService;
    }

    @RequestMapping(value = "/getOpenIssues" , method = RequestMethod.GET)
    public SearchResponse getOpenIssues(@QueryParam(value = "publicUrl") String publicUrl){
        //TODO:lOGGER
        return githubService.getOpenIssues(publicUrl);
    }

    @RequestMapping(value = "/getOpenIssuesYesterday" , method =
            RequestMethod.GET)
    public SearchResponse getOpenIssuesYesterday(@QueryParam(value =
            "publicUrl") String publicUrl){
        //TODO:lOGGER
        return githubService.getOpenIssuesYesterday(publicUrl);
    }

    @RequestMapping(value = "/getOpenIssuesInWeek" , method =
            RequestMethod.GET)
    public SearchResponse getOpenIssuesInWeek(@QueryParam(value =
            "publicUrl") String publicUrl){
        //TODO:lOGGER
        return githubService.getOpenIssuesInWeek(publicUrl);
    }

    @RequestMapping(value = "/getOpenIssuesBeforeWeek" , method =
            RequestMethod.GET)
    public SearchResponse getOpenIssuesBeforeWeek(@QueryParam(value =
            "publicUrl") String publicUrl){
        //TODO:lOGGER
        return githubService.getOpenIssuesBeforeWeek(publicUrl);
    }



}
