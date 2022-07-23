import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {CONFIG} from "@asterisk-frontend/config";
import {AsteriskCommonModule} from "@asterisk-frontend/asterisk-common";
import {environment} from "../environments/environment";
import {AuthenticationModule} from "@asterisk-frontend/authentication";


@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AsteriskCommonModule,
    AuthenticationModule
  ],
  providers: [
    {provide: CONFIG, useValue: environment}
  ],
  bootstrap: [AppComponent],
})
export class AppModule {
}
