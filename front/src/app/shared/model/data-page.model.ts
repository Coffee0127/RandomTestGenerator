import { DataSort } from './data-sort.model';

export interface DataPage<T> {
  // 資料
  content: T[];
  // 總頁數
  totalPages: number;
  // 總筆數
  totalElements: number;
  // 是否為第一頁
  first: boolean;
  // 是否為最後一頁
  last: boolean;
  // active page (zero-based)
  number: number;
  // 頁面資料限制
  size: number;
  // content 資料筆數
  numberOfElements: number;
  // 排序
  sort: DataSort[];
}
