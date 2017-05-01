import { QueryCond } from '../../shared/model/query-cond.model';

export interface QuestionQueryCond extends QueryCond {
  catId: string;
  isSingleAnswer: boolean;
  levelId: number;
}
