import { QuestLevel } from './quest-level.model';

export interface GenerateCond {
  totalQuests?: number
  totalScore?: number
  catIds?: string[]
  questLevels?: QuestLevel[]
  isSingleAnswer?: boolean
}
