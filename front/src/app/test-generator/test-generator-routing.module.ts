import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TestPreviewComponent } from './test-preview/test-preview.component';

const routes: Routes = [
  {
    path: 'views', component: TestPreviewComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: []
})
export class TestGeneratorRoutingModule { }
