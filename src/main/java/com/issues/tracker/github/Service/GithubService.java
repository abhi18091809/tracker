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

        List<String> words = Arrays.asList(url.split("/"));
        int n = words.size();
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("owner", words.get(n - 2));
        urlParams.put("repo", words.get(n - 1));
        return urlParams;

    }

    public RepositoryResponse getOpenIssues(String publicUrl) {
        try {
            logger.info("Github service layer invoked to get all open issues");

            Map<String, String> params = getParamsFromUrl(publicUrl);
            String url = "https://api.github.com/repos/"+params.get("owner")+
                    "/"+params.get("repo");

            ResponseEntity<RepositoryResponse> response =
                    restTemplate.exchange(url,
                            HttpMethod.GET,null,
                            RepositoryResponse.class);

            if (response.getBody() != null) {
                return response.getBody();
            }

        } catch (Exception e) {
            logger.error("Error in fetching details of repository");
        }
        return null;
    }

    public SearchResponse getOpenIssuesYesterday(String publicUrl) {
        try {

            logger.info("Github service layer invoked to get all open issues " +
                    "opened 24 hours before now");

            Date dMinusOne = getDifferenceWithCurrent(-24);

            SimpleDateFormat dtf = new SimpleDateFormat(DATE_FORMAT);

            Map<String, String> params = getParamsFromUrl(publicUrl);
            String searchQuery =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+created:>"+dtf.format(dMinusOne);


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

        } catch (Exception e) {
            logger.error("Error in fetching issues opened in last 24 hours", e);
        }

        return null;
    }

    public SearchResponse getOpenIssuesInWeek(String publicUrl) {
        try {

            logger.info("Github service layer invoked to get all open issues " +
                    "opened after 7 days from now");

            Date d1 = getDifferenceWithCurrent(-24);
            Date d2 = getDifferenceWithCurrent(-168);

            SimpleDateFormat dtf = new SimpleDateFormat(DATE_FORMAT);

            Map<String, String> params = getParamsFromUrl(publicUrl);

            String searchQuery1 =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+created:>"+dtf.format(d1);
            String searchQuery2 = "repo:"+params.get("owner")+"/"+params.get("repo")+
                    "+created:>"+dtf.format(d2);

            Map<String,String> map1 = new HashMap<>();
            map1.put("q",searchQuery1);

            Map<String,String> map2 = new HashMap<>();
            map2.put("q",searchQuery2);

            UriComponentsBuilder builder1 =
                    UriComponentsBuilder.fromUriString(SEARCH_ISSUE_URL)
                    .queryParam("q", searchQuery1);

            UriComponentsBuilder builder2 =
                    UriComponentsBuilder.fromUriString(SEARCH_ISSUE_URL)
                            .queryParam("q", searchQuery2);

            ResponseEntity<SearchResponse> response1 =
                    restTemplate.exchange(builder1.buildAndExpand(map1).toUri(),
                            HttpMethod.GET,null,
                            SearchResponse.class);

            ResponseEntity<SearchResponse> response2 =
                    restTemplate.exchange(builder2.buildAndExpand(map2).toUri(),
                            HttpMethod.GET,null,
                            SearchResponse.class);

            SearchResponse response = new SearchResponse();
            response.setTotalCount(response2.getBody().getTotalCount() - response1.getBody().getTotalCount());

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

            ResponseEntity<RepositoryResponse> responseAll =
                    restTemplate.exchange(url,
                            HttpMethod.GET,null,
                            RepositoryResponse.class);

            Date dMinusSeven = getDifferenceWithCurrent(-168);

            SimpleDateFormat dtf = new SimpleDateFormat(DATE_FORMAT);

            String searchQuery =
                    "repo:"+params.get("owner")+"/"+params.get("repo")+
                            "+created:>"+dtf.format(dMinusSeven);


            UriComponentsBuilder builder =
                    UriComponentsBuilder.fromHttpUrl(SEARCH_ISSUE_URL)
                            .queryParam("q", searchQuery);

            Map<String,String> map = new HashMap<>();
            map.put("q",searchQuery);

            ResponseEntity<SearchResponse> responseMinusSeven =
                    restTemplate.exchange(builder.buildAndExpand(map).toUri(),
                            HttpMethod.GET,null,
                            SearchResponse.class);

            SearchResponse response = new SearchResponse();
            response.setTotalCount(responseAll.getBody().getOpenIssues() - responseMinusSeven.getBody().getTotalCount());

            return response;



        } catch (Exception e) {
            logger.error("Error in fetching issues before a week");
        }

        return null;
    }
}
