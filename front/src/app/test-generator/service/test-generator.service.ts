import { Observable } from 'rxjs/Rx';

import { GenerateCond } from '../model/generate-cond.model';
import { Version } from '../model/version.model';

// 因為 JavaScript 沒有 interface，只能拿 abstract class 當 interface 用
export abstract class TestGeneratorService {
  abstract findCategories(): Observable<any>;
  abstract findQuestLevels(): Observable<any>;
  abstract generateTestPreview(cond: GenerateCond): Observable<any>;
  abstract generate(version: Version): Observable<string>;
}
