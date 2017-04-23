import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule, RequestOptions } from '@angular/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { TestPreviewComponent } from './test-generator/test-preview/test-preview.component';

import { TestGeneratorService } from './test-generator/test-generator.service';

import { CustomRequestOptions } from './shared/config/custom-request-options';

@NgModule({
  declarations: [
    AppComponent,
    IndexComponent,
    TestPreviewComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpModule,
    AppRoutingModule
  ],
  providers: [TestGeneratorService, {
    provide: RequestOptions, useClass: CustomRequestOptions
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
