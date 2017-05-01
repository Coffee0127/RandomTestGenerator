import { Observable } from 'rxjs/Rx';

import { DataPage } from '../../shared/model/data-page.model';
import { GenerateCond } from '../model/generate-cond.model';
import { QuestionQueryCond } from '../model/question-query-cond.model';
import { Question } from '../model/question.model';
import { Version } from '../model/version.model';

// 因為 JavaScript 沒有 interface，只能拿 abstract class 當 interface 用
export abstract class TestGeneratorService {
  // 查詢題目種類
  abstract findCategories(): Observable<any>;
  // 查詢題目難易度
  abstract findQuestLevels(): Observable<any>;
  // 產生試卷預覽
  abstract generateTestPreview(cond: GenerateCond): Observable<any>;
  // 產生試卷
  abstract generate(version: Version): Observable<string>;
  // 查詢題目
  abstract findQuestions(cond: QuestionQueryCond): Observable<DataPage<Question>>;
}
