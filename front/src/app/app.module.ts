import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { IndexComponent } from './index/index.component';
import { TestPreviewComponent } from './test-generator/test-preview/test-preview.component';

import { TestGeneratorService } from './test-generator/test-generator.service';

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
  providers: [TestGeneratorService],
  bootstrap: [AppComponent]
})
export class AppModule { }
