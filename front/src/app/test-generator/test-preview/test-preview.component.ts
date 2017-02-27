import { Component, OnInit } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import 'rxjs/Rx';

import { showErrorMsg } from './../../shared/showMsg';
import { DEFAULT_ERROR_MSG } from './../../shared/constant';
import { Category } from './../../model/testgen/category.model';
import { Question } from './../../model/testgen/question.model';
import { QuestLevel } from './../../model/testgen/quest-level.model';

@Component({
  selector: 'app-test-preview',
  templateUrl: './test-preview.component.html',
  styleUrls: ['./test-preview.component.css']
})
export class TestPreviewComponent implements OnInit {

  categories: Category[];
  questions: Question[];
  questLevel: QuestLevel[];

  constructor(private http: Http) { }

  ngOnInit() {
    this.http.get('/api/cate/find')
      .map((response: Response) => response.json())
      .subscribe((value: Category[]) => {
        this.categories = value.map(cate => {
          cate.checked = true;
          return cate;
        });
      }, (error) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });

    this.http.get('/api/quest/findQL')
      .map((response: Response) => response.json())
      .subscribe((value: QuestLevel[]) => {
        this.questLevel = value;;
      }, (error) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }

  generateTest() {
    console.log('invoke');
    let body = JSON.stringify({
      catIds: this.categories.map(cate => cate.oid),
      isSingleAnswer: false
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    this.http.post('/api/quest/find', body, options)
      .map((response: Response) => response.json())
      .subscribe((value: Question[]) => {
        this.questions = value;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }

}
