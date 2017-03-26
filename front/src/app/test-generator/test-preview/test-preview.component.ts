import { Component, OnInit, trigger, state, style, transition, animate } from '@angular/core';
import { Validators, FormGroup, FormBuilder, FormArray } from '@angular/forms';

import 'rxjs/Rx';
import { Observable } from 'rxjs/Observable';

import { showErrorMsg } from './../../shared/showMsg';
import { DEFAULT_ERROR_MSG } from './../../shared/constant';
import { Category } from './../../model/testgen/category.model';
import { Question } from './../../model/testgen/question.model';
import { QuestLevel } from './../../model/testgen/quest-level.model';
import { Version } from './../../model/testgen/version.model';

import { TestGeneratorService } from './../test-generator.service';

@Component({
  selector: 'app-test-preview',
  templateUrl: './test-preview.component.html',
  styleUrls: ['./test-preview.component.css'],
  animations: [
    trigger('btnAnimation', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate(500, style({ opacity: 1 }))
      ]),
      transition(':leave', [
        style({ opacity: 0 }),
      ])
    ])
  ]
})
export class TestPreviewComponent implements OnInit {

  public static readonly TOTAL_QUESTS = 25;
  public static readonly TOTAL_SCORE = 100;

  categories: Category[];
  version: Version;
  questLevels: QuestLevel[];

  condForm: FormGroup;

  // 是否可以下載
  isDownloadAvailable = false;

  constructor(private testGenerator: TestGeneratorService, private fb: FormBuilder) { }

  ngOnInit() {
    let ob$ = Observable.forkJoin(
      this.testGenerator.findCategories(),
      this.testGenerator.findQuestLevels());
    ob$.subscribe(values => {
      this.categories = values[0].map(cate => {
        cate.checked = true;
        return cate;
      });
      this.condForm.addControl('cate_checked',
        this.fb.array(this.categories.map((cate) => {
          return this.fb.control(cate.checked);
        }), cateValidator));

      this.questLevels = values[1];
      this.condForm.addControl('questLevelForm',
        this.fb.array(this.questLevels.map((level) => {
          return this.fb.group({
            level_checked: !!level.checked,
            level_number: level.number && level.number || 0
          });
        }), levelValidator));
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
      answerType: this.fb.control(null)
    });
  }

  getFieldInvalid(fieldName, prefix = '') {
    let field = this.condForm.get(prefix + fieldName);
    return field && field.dirty && field.invalid;
  }

  previewTest() {
    let values = this.condForm.value;
    // total questions
    let totalQuests = values['totalQuests'];
    // total score
    let totalScore = values['totalScore'];
    let isSingleAnswer = values['answerType'];
    // collect Category oid
    let catIds = (<FormArray> this.condForm.get('cate_checked')).controls.map((checkedCate, index) => {
      if (checkedCate.value) {
        return this.categories[index].oid;
      }
    }).filter(v => v);
    // collect level
    let questLevels = [...this.questLevels];
    (<Array<any>> values.questLevelForm).map((formLevel, index) => {
      questLevels[index].checked = formLevel.level_checked;
      if (formLevel.level_checked) {
        questLevels[index].number = formLevel.level_number;
      }
    });
    questLevels = questLevels
      .filter(v => v.checked )
      .map(v => {
        let id = v.id,
          number = v.number;
        return { id, number };
      });

    let cond = { totalQuests, totalScore, catIds, questLevels, isSingleAnswer };

    this.isDownloadAvailable = false;
    this.version = undefined;
    this.testGenerator.generateTestPreview(cond)
      .subscribe((value: Version) => {
        this.version = value;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }

  generateTest() {
    this.testGenerator.generate(this.version)
      .subscribe((value) => {
        this.isDownloadAvailable = true;
        this.version.oid = value;
      }, (error: any) => {
        showErrorMsg(error._body && error._body || DEFAULT_ERROR_MSG);
      });
  }
}
