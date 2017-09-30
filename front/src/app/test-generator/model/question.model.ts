import { Answer } from './answer.model';
import { QuestLevel } from './quest-level.model';

export interface Question {
  oid? : string;
  catId: string;
  versionOid? : string;
  isSingleAnswer: boolean;
  questLevel: QuestLevel;
  questionNo? : number;
  desc? : string;
  correctAnswer? : string;
  answers? : Answer[];
}
