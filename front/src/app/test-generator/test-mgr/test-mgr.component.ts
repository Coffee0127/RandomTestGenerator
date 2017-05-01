import { Component, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { Observable } from 'rxjs/Rx';

import { BaseMgrComponent } from '../../shared/base-mgr-component';
import { DataPage } from '../../shared/model/data-page.model';
import { DEFAULT_ERROR_MSG } from '../../shared/constant';
import { QueryCond } from '../../shared/model/query-cond.model';
import { showErrorMsg } from '../../shared/showMsg';

import { TestGeneratorService } from '../service/test-generator.service';

import { QuestLevel } from '../model/quest-level.model';
import { Category } from '../model/category.model';
import { Question } from '../model/question.model';
import { QuestionQueryCond } from '../model/question-query-cond.model';

@Component({
  selector: 'app-test-mgr',
  templateUrl: './test-mgr.component.html',
  styleUrls: ['./test-mgr.component.css']
})
export class TestMgrComponent implements OnInit {

  categories: Category[];
  questLevels: QuestLevel[];

  /** 當前頁數 */
  private _activePage: number = 0;

  /** 每頁筆數 */
  private _pageSize: number = 10;

  private queryCondHolder: QuestionQueryCond;

  /** 查詢結果 */
  questions: Question[] = [];

  /** SpringData 查詢結果 */
  dataPage: DataPage<Question>;

  constructor(private testGenService: TestGeneratorService) { }

  ngOnInit() {
    this.testGenService.findCategories()
      .subscribe(v => this.categories = v);
    this.testGenService.findQuestLevels()
      .subscribe(v => this.questLevels = v);
  }

  isPaginable() {
    return this.dataPage != null && this.dataPage.totalElements != 0;
  }

  query(queryCondForm: FormGroup) {
    this._activePage = 0;
    this.queryCondHolder = JSON.parse(JSON.stringify(queryCondForm.value));
    this.queryCondHolder.sort = '';
    this.queryCondHolder.asc = false;
    this.executeFind();
  }

  catOidToDesc(catOid: string) {
    return this.categories.filter(v => v.oid === catOid).map(v => v.desc);
  }

  executeFind() {
    this.queryCondHolder.activePage = this.activePage;
    this.queryCondHolder.limit = this.pageSize;
    this.testGenService.findQuestions(this.queryCondHolder)
      .subscribe(v => {
        this.questions = v.content;
        this.dataPage = v;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }

  keyinActivePage(value: number, activePageInput: HTMLInputElement) {
    let localActivePage = value - 1;
    if (this.isOverFirstPage(localActivePage) || this.isOverLastPage(localActivePage)) {
      activePageInput.value = '' + (this.dataPage.number + 1);
      activePageInput.blur();
      showErrorMsg(`Page ${value} is not an available page.`);
      return;
    }
    this.activePage = localActivePage;
  }

  private isOverFirstPage(value: number) {
    return value < 0;
  }

  private isOverLastPage(value: number) {
    return value + 1 > this.dataPage.totalPages;
  }

	public get pageSize(): number  {
		return this._pageSize;
	}

	public set pageSize(value: number) {
    if (!this.isPaginable()) {
      console.warn('Change page size is only avaialbe after querying database');
      return;
    }
    this._pageSize = value;
    // 回到第 1 頁
    this._activePage = -1;
    this.activePage = 0;
	}

	public get activePage(): number  {
		return this._activePage;
	}

	public set activePage(value: number) {
    // 檢查超出第一頁或最後一頁
    if (this.isOverFirstPage(value) || this.isOverLastPage(value)) {
      console.warn(`The active page ${value} is over the first page or last page.`);
      return;
    }
    if (value === this._activePage) {
      console.warn(`The active page is already ${value}`);
      return;
    }
		this._activePage = value;
    this.executeFind();
	}

}
