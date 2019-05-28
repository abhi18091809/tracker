import { Component, OnInit } from '@angular/core';
import { GithubService } from '../github.service';
import { Config } from 'protractor';

@Component({
  selector: 'app-issues',
  templateUrl: './issues.component.html',
  styleUrls: ['./issues.component.css']
})
export class IssuesComponent implements OnInit {

  public url = "";
  public showError = false
  public searching = false;
  public results = {
    total:null,
    yesterday:null,
    week:null,
    beforeWeek:null
  };

  constructor(private _githubService: GithubService) { }

  ngOnInit() {
  }

  getResults(url) {
    if (url.length == 0) {
      this.showError = true;
    }
    else {
      this.showError = false;
      this.searching = true;
      this._githubService.getTotalIssues(url)
      .subscribe( data => this.results.total = data.total_count);
      this._githubService.getOpenIssuesYesterday(url)
      .subscribe( data => this.results.yesterday = data.total_count);
      this._githubService.getOpenIssuesInWeek(url)
      .subscribe(data => this.results.week = data.total_count);
      this._githubService.getOpenIssuesBeforeWeek(url)
      .subscribe( 
        data => this.results.beforeWeek = data.total_count
        ).add(()=>{
          this.searching = false;
        })
        
      

    }
    

  }

}
