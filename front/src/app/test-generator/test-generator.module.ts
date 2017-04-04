import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TestGeneratorRoutingModule } from './test-generator-routing.module';

import { TestGeneratorService } from './test-generator.service';

import { TestPreviewComponent } from './test-preview/test-preview.component';

@NgModule({
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
    TestGeneratorRoutingModule
  ],
  declarations: [
    TestPreviewComponent
  ],
  providers: [
    { provide: TestGeneratorService, useClass: TestGeneratorService }
  ]
})
export class TestGeneratorModule { }
