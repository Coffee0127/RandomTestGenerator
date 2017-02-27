import { Answer } from './answer.model';
import { QuestLevel } from './quest-level.model';

export interface Question {
  oid?: string
  desc?: string
  catId?: string
  singleAnswer?: boolean
  questLevel?: QuestLevel
  answers?: Set<Answer>;
}
