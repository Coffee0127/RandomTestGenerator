import { showErrorMsg } from './../../shared/showMsg';
import { Component, OnInit } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import 'rxjs/Rx';

import { Question } from './../../model/testgen/question.model';

@Component({
  selector: 'app-test-preview',
  templateUrl: './test-preview.component.html',
  styleUrls: ['./test-preview.component.css']
})
export class TestPreviewComponent implements OnInit {

  questions: Question[];

  constructor(private http: Http) { }

  ngOnInit() {
    let body = JSON.stringify({
      catIds: ['F710EF2D72CA4E3391398E6D5EE4DB4E'],
      isSingleAnswer: false
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    this.http.post('/api/quest/find', body, options)
      .map((response: Response) => response.json())
      .subscribe((value: Question[]) => {
        this.questions = value;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || 'Server Internal Error');
      });
  }

}
