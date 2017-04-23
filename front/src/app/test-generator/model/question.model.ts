import { Answer } from './answer.model';
import { QuestLevel } from './quest-level.model';

export interface Question {
  oid? : string
  versionOid? : string
  questionNo? : number
  desc? : string
  correctAnswer? : string
  answers? : Answer[]
}
