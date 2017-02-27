import { Answer } from './answer.model';
import { QuestLevel } from './quest-level.model';

export interface Question {
  oid?: String
  desc?: String
  catId?: String
  singleAnswer?: boolean
  questLevel?: QuestLevel
  answers?: Set<Answer>;
}
