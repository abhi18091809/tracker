import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ITotalCount } from './totalCount';
import { Observable } from 'rxjs';
import { ISearchCount } from './searchCount';

@Injectable({
  providedIn: 'root'
})
export class GithubService {

  private _url = "src/results.json";

  constructor(private http: HttpClient) { }


  getTotalIssues(url): Observable<ISearchCount> {
    return this.http.get<ISearchCount>(
      '/v1/getOpenIssues', {
        params: {
          publicUrl:url
        }
      }
    );
  }

  getOpenIssuesYesterday(url): Observable<ISearchCount> {
    return this.http.get<ISearchCount>(
      '/v1/getOpenIssuesYesterday', {
        params: {
          publicUrl:url
        }
      }
    );
  }

  getOpenIssuesInWeek(url): Observable<ISearchCount> {
    return this.http.get<ISearchCount>(
      '/v1/getOpenIssuesInWeek', {
        params: {
          publicUrl:url
        }
      }
    );
  }

  getOpenIssuesBeforeWeek(url): Observable<ISearchCount> {
    return this.http.get<ISearchCount>(
      '/v1/getOpenIssuesBeforeWeek', {
        params: {
          publicUrl:url
        }
      }
    );
  }


}
