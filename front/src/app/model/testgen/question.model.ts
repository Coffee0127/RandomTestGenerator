import { Answer } from './answer.model';

export interface Question {
  oid?: String
  desc?: String
  catId?: String
  singleAnswer?: boolean
  answers?: Set<Answer>;
}
