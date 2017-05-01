import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { TestPreviewComponent } from './test-preview/test-preview.component';
import { TestMgrComponent } from "app/test-generator/test-mgr/test-mgr.component";

const routes: Routes = [
  { path: 'views', component: TestPreviewComponent },
  { path: 'admin', component: TestMgrComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: []
})
export class TestGeneratorRoutingModule { }
