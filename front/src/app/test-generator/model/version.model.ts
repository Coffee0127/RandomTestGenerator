import { Question } from './question.model';

export interface Version {
  oid?: string
  createDatetime?: Date
  creator?: string
  passingScore?: number
  questions?: Question[]
}
