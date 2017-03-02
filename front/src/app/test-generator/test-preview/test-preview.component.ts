import { Component, OnInit } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormArray } from '@angular/forms';
import { Http, Headers, RequestOptions, Response } from '@angular/http';

import 'rxjs/Rx';

import { showErrorMsg } from './../../shared/showMsg';
import { DEFAULT_ERROR_MSG } from './../../shared/constant';
import { Category } from './../../model/testgen/category.model';
import { Question } from './../../model/testgen/question.model';
import { QuestLevel } from './../../model/testgen/quest-level.model';

@Component({
  selector: 'app-test-preview',
  templateUrl: './test-preview.component.html',
  styleUrls: ['./test-preview.component.css']
})
export class TestPreviewComponent implements OnInit {

  public static readonly TOTAL_QUESTS = 25;
  public static readonly TOTAL_SCORE = 100;

  categories: Category[];
  questions: Question[];
  questLevels: QuestLevel[];

  condForm: FormGroup;

  constructor(private http: Http, private fb: FormBuilder) { }

  ngOnInit() {
    this.http.get('/api/cate/find')
      .map((response: Response) => response.json())
      .subscribe((value: Category[]) => {
        this.categories = value.map(cate => {
          cate.checked = true;
          return cate;
        });
      }, (error) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });

    this.http.get('/api/quest/findQL')
      .map((response: Response) => response.json())
      .subscribe((value: QuestLevel[]) => {
        this.questLevels = value;
      }, (error) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });

    let cateValidator = (formArray: FormArray) => {
      let isCateChecked = false;
      for (let i = 0; i < formArray.controls.length; i++) {
        if (formArray.controls[i].value) {
          isCateChecked = true;
          break;
        }
      }
      return !isCateChecked ? { invalidCate: true } : null;
    };

    let levelValidator = (formArray: FormArray) => {
      //
      /*let totalQuests = formArray.controls.reduce((prevLevelGroup, currLevelGroup) => {
        let tmpTotalQuests = 0;
        if (prevLevelGroup.get('level_checked').value) {
          tmpTotalQuests += prevLevelGroup.get('level_number').value;
        }
        if (currLevelGroup.get('level_checked').value) {
          tmpTotalQuests += currLevelGroup.get('level_number').value;
        }
        return this.fb.group({
          level_checked: true,
          level_number: tmpTotalQuests
        });
      }).get('level_number').value;*/

      // easy way to calcuate total quest :)
      let totalQuests = formArray.controls.filter(level => !!level.get('level_checked').value)
        .reduce((tmpTotalQuests, currLevel) => {
          return tmpTotalQuests + currLevel.get('level_number').value;
        }, 0);
      if (totalQuests !== TestPreviewComponent.TOTAL_QUESTS) {
        return { invalidLevel: true };
      }
      return null;
    };

    this.condForm = this.fb.group({
      totalQuests: this.fb.control(TestPreviewComponent.TOTAL_QUESTS, Validators.required),
      totalScore: this.fb.control(TestPreviewComponent.TOTAL_SCORE, Validators.required),
      cate_checked: this.fb.array(this.categories.map((cate) => {
        return this.fb.control(cate.checked);
      }), cateValidator),
      questLevelForm: this.fb.array(this.questLevels.map((level) => {
        return this.fb.group({
          level_checked: !!level.checked,
          level_number: level.number && level.number || 0
        });
      }), levelValidator)
    });
  }

  getFieldInvalid(fieldName, prefix = '') {
    let field = this.condForm.get(prefix + fieldName);
    return field && field.dirty && field.invalid;
  }

  generateTest() {
    let values = this.condForm.value;
    // total questions
    let totalQuests = values['totalQuests'];
    // total score
    let totalScore = values['totalScore'];
    // collect Category oid
    let catIds = (<FormArray> this.condForm.get('cate_checked')).controls.map((checkedCate, index) => {
      if (checkedCate.value) {
        return this.categories[index].oid;
      }
    }).filter(v => v);
    // collect level
    let questLevels = [...this.questLevels];
    (<Array<any>> values.questLevelForm).map((formLevel, index) => {
      if (formLevel.level_checked) {
        questLevels[index].number = formLevel.level_number;
      }
    });

    let body = JSON.stringify({
      totalQuests: totalQuests,
      totalScore: totalScore,
      catIds: catIds,
      questLevels: questLevels,
      isSingleAnswer: false
    });
    let headers = new Headers({ 'Content-Type': 'application/json' });
    let options = new RequestOptions({ headers: headers });
    this.http.post('/api/quest/generate', body, options)
      .map((response: Response) => response.json())
      .subscribe((value: Question[]) => {
        this.questions = value;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }

}
