import { FormGroup } from '@angular/forms';

import { Observable } from 'rxjs/Rx';

import { DEFAULT_ERROR_MSG } from '../shared/constant';
import { showErrorMsg } from '../shared/showMsg';

import { DataPage } from './model/data-page.model';
import { QueryCond } from './model/query-cond.model';

export abstract class BaseMgrComponent<T> {
  /** 當前頁數 */
  private _activePage: number = 0;

  /** 每頁筆數 */
  private _pageSize: number = 10;

  /** 查詢條件 */
  private _queryCondHolder: QueryCond;

  /** 查詢結果 */
  private _queryResults: T[] = [];

  /** SpringData 查詢結果 */
  private _dataPage: DataPage<T>;

  abstract getQueryCond(): QueryCond;

  abstract doFind(): Observable<DataPage<T>>;

  query(queryCondForm: FormGroup) {
    this.activePage = 0;
    this.queryCondHolder = JSON.parse(JSON.stringify(queryCondForm.value));
    this.queryCondHolder.sort = '';
    this.queryCondHolder.asc = false;
    this.executeFind()
      .subscribe(v => {
        this._queryResults = v.content;
        this.dataPage = v;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }

  executeFind(): Observable<DataPage<T>> {
    this.getQueryCond().activePage = this._activePage;
    this.getQueryCond().limit = this._pageSize;
    return this.doFind();
  }

  public get activePage(): number {
    return this._activePage;
  }

  public set activePage(value: number) {
    this._activePage = value;
  }

  public get pageSize(): number {
    return this._pageSize;
  }

  public set pageSize(value: number) {
    this._pageSize = value;
  }

  public get queryCondHolder(): QueryCond {
    return this._queryCondHolder;
  }

  public set queryCondHolder(value: QueryCond) {
    this._queryCondHolder = value;
  }

  public get queryResults(): T[]  {
    return this._queryResults;
  }

  public set queryResults(value: T[] ) {
    this._queryResults = value;
  }

  public get dataPage(): DataPage<T> {
    return this._dataPage;
  }

  public set dataPage(value: DataPage<T>) {
    this._dataPage = value;
  }
}
