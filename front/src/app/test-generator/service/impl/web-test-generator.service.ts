import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';

import { serializeObject } from '../../../shared/utils';

import { DataPage } from '../../../shared/model/data-page.model';
import { QuestionQueryCond } from '../../model/question-query-cond.model';
import { Question } from '../../model/question.model';
import { TestGeneratorService } from '../test-generator.service';

@Injectable()
export class WebTestGeneratorService implements TestGeneratorService {

  constructor(private http: Http) { }

  findCategories() {
    return this.http.get('/api/cate/find')
      .map((response: Response) => response.json());
  }

  findQuestLevels() {
    return this.http.get('/api/quest/findQL')
      .map((response: Response) => response.json());
  }

  generateTestPreview(cond) {
    let body = JSON.stringify({
      totalQuests: cond.totalQuests,
      totalScore: cond.totalScore,
      catIds: cond.catIds,
      questLevels: cond.questLevels,
      isSingleAnswer: cond.isSingleAnswer
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post('/api/quest/preview', body, options)
      .map((response: Response) => response.json());
  }

  generate(version) {
    let body = JSON.stringify(version);
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post('/api/quest/generate', body, options)
      .map((response: Response) => response.text());
  }

  findQuestions(cond: QuestionQueryCond): Observable<DataPage<Question>> {
    let url = `/api/quest/find?${serializeObject(cond)}`;
    return this.http.get(url)
      .map((response: Response) => response.json());
  }
}
