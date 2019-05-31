package com.issues.tracker.github.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issues.tracker.github.Config.GithubPropertyManager;
import com.issues.tracker.github.Request.RepositoryResponse;
import com.issues.tracker.github.Request.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GithubService {

    private RestTemplate restTemplate;
    private GithubPropertyManager manager;
    private ObjectMapper objectMapper;
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String SEARCH_ISSUE_URL = "https://api.github" +
            ".com/search/issues";

    @Autowired public GithubService(RestTemplate restTemplate,
                                    GithubPropertyManager manager,
                                    ObjectMapper objectMapper){
        this.restTemplate = restTemplate;
        this.manager = manager;
        this.objectMapper = objectMapper;
    }

    private static final Logger logger =
            LoggerFactory.getLogger(GithubService.class);

    private Map<String, String> getParamsFromUrl(String url) {

//        This method helps in getting the owner and repo name from the input
//        url

        List<String> words = Arrays.asList(url.split("/"));
        int n = words.size();
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("owner", words.get(n - 2));
        urlParams.put("repo", words.get(n - 1));
        return urlParams;

    }

    public SearchResponse getOpenIssues(String publicUrl) {
        try {
            logger.info("Github service layer invoked to get all open issues");

            Map<String, String> params = getParamsFromUrl(publicUrl);
            String url = "https://api.github.com/repos/"+params.get("owner")+
                    "/"+params.get("repo");

            //Getting open pull requests
            String searchQuery = "repo:"+params.get("owner")+"/"+params.get("repo")+"+is" +
                    ":pr+is:open";

            SearchResponse pullRequests = githubSearchHttpExchange(searchQuery);

            RepositoryResponse issues = restTemplate.exchange(url,
                    HttpMethod.GET,null,
                    RepositoryResponse.class).getBody();

            SearchResponse result = new SearchResponse();
            if (issues != null && pullRequests != null) {
                    result.setTotalCount(issues.getOpenIssues()-pullRequests.getTotalCount());
            }

            return result;



        } catch (Exception e) {
            logger.error("Error in fetching details of repository");
        }
        return null;
    }

    private SearchResponse githubSearchHttpExchange(String searchQuery) {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(SEARCH_ISSUE_URL)
                        .queryParam("q", searchQuery);

        Map<String,String> map = new HashMap<>();
        map.put("q",searchQuery);

        ResponseEntity<SearchResponse> response =
                restTemplate.exchange(builder.buildAndExpand(map).toUri(),
                        HttpMethod.GET,null,
                        SearchResponse.class);

        if (response.getBody() != null) {
            return response.getBody();
        }

        return null;
    }

    public SearchResponse getOpenIssuesYesterday(String publicUrl) {
        try {

            logger.info("Github service layer invoked to get all open issues " +
                    "opened 24 hours before now");

//            Getting the time 24 hours before now

            Date dMinusOne = getDifferenceWithCurrent(-24);

            SimpleDateFormat dtf = new SimpleDateFormat(DATE_FORMAT);

//            Creating search query for 'q' param in Github search API

            Map<String, String> params = getParamsFromUrl(publicUrl);
            String searchQuery =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+created:>"+dtf.format(dMinusOne);
            String pullRequestQuery = "repo:"+params.get("owner")+"/"+params.get("repo")+
                    "+is:pr+is:open+created:>"+dtf.format(dMinusOne);

            SearchResponse issuesInDay = githubSearchHttpExchange(searchQuery);
            SearchResponse pullInDay = githubSearchHttpExchange(pullRequestQuery);

            SearchResponse result = new SearchResponse();

            if (pullInDay != null && issuesInDay != null) {
                result.setTotalCount(issuesInDay.getTotalCount()-pullInDay.getTotalCount());
            }

            return result;

        } catch (Exception e) {
            logger.error("Error in fetching issues opened in last 24 hours", e);
        }

        return null;
    }

    public SearchResponse getOpenIssuesInWeek(String publicUrl) {
        try {

            logger.info("Github service layer invoked to get all open issues " +
                    "opened after 7 days from now");

//          Lets assume a timeline
//            ---------------------------|------------------------|------>|now()

//            ---------------------------|----------------------->|-now()-24hrs

//            -------------------------->|-now()-7days

//                                       |<---------------------->| issues to
//                                         be counted

//            We calculate the difference using two http calls which queries
//            for number of issues at different point of times.
            


            Date d1 = getDifferenceWithCurrent(-24);
            Date d2 = getDifferenceWithCurrent(-168);

            SimpleDateFormat dtf = new SimpleDateFormat(DATE_FORMAT);

            Map<String, String> params = getParamsFromUrl(publicUrl);

            String searchQuery1 =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+created:>"+dtf.format(d1);
            String searchQuery2 = "repo:"+params.get("owner")+"/"+params.get(
                    "repo")+"+created:>"+dtf.format(d2);

            SearchResponse issues1 = githubSearchHttpExchange(searchQuery1);
            SearchResponse issues2 = githubSearchHttpExchange(searchQuery2);
            SearchResponse totalIssues = new SearchResponse();
            if (issues1 != null && issues2 != null) {
                totalIssues.setTotalCount(issues2.getTotalCount()-issues1.getTotalCount());
            }

            String pullQuery1 =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+is:pr+is:open+created:>"+dtf.format(d1);
            String pullQuery2 = "repo:"+params.get("owner")+"/"+params.get(
                    "repo")+"+is:pr+is:open+created:>"+dtf.format(d2);

            SearchResponse pulls1 = githubSearchHttpExchange(pullQuery1);
            SearchResponse pulls2 = githubSearchHttpExchange(pullQuery2);

            SearchResponse totalPulls = new SearchResponse();

            if (pulls2 != null && pulls1 != null) {
                totalPulls.setTotalCount(pulls2.getTotalCount()-pulls1.getTotalCount());
            }


            SearchResponse response = new SearchResponse();
            response.setTotalCount(totalIssues.getTotalCount()-totalPulls.getTotalCount());

            return response;

        } catch (Exception e) {
            logger.error("Error in fetching issues opened in last 24 hours", e);
        }

        return null;
    }

    private Date getDifferenceWithCurrent(int i) {
        Date d = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.HOUR, i);
        return c.getTime();
    }

    public SearchResponse getOpenIssuesBeforeWeek(String publicUrl) {
        try {

            logger.info("Github service layer invoked to get all open issues " +
                    "opened before seven days from now");
            Map<String, String> params = getParamsFromUrl(publicUrl);
            String url = "https://api.github.com/repos/"+params.get("owner")+
                    "/"+params.get("repo");

            Date dMinusSeven = getDifferenceWithCurrent(-168);
            SimpleDateFormat dtf = new SimpleDateFormat(DATE_FORMAT);

            String searchQuery1 = "repo:"+params.get("owner")+"/"+params.get("repo")+
                    "+created:>"+dtf.format(dMinusSeven);
            SearchResponse issues1 = githubSearchHttpExchange(searchQuery1);
            RepositoryResponse issues2 = restTemplate.exchange(url,
                    HttpMethod.GET,null,
                    RepositoryResponse.class).getBody();

            SearchResponse totalIssues = new SearchResponse();

            if (issues1 != null && issues2 != null) {
                totalIssues.setTotalCount(issues2.getOpenIssues()-issues1.getTotalCount());
            }





            String pullQuery1 =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+is:pr+is:open+created:>"+dtf.format(dMinusSeven);
            String pullQuery2 = "repo:"+params.get("owner")+"/"+params.get(
                    "repo")+"+is:pr+is:open";

            SearchResponse pulls1 = githubSearchHttpExchange(pullQuery1);
            SearchResponse pulls2 = githubSearchHttpExchange(pullQuery2);

            SearchResponse totalPulls = new SearchResponse();

            if (pulls2 != null && pulls1 != null) {
                totalPulls.setTotalCount(pulls2.getTotalCount()-pulls1.getTotalCount());
            }

            SearchResponse response = new SearchResponse();
            response.setTotalCount(totalIssues.getTotalCount()-totalPulls.getTotalCount());

            return response;



        } catch (Exception e) {
            logger.error("Error in fetching issues before a week");
        }

        return null;
    }
}
