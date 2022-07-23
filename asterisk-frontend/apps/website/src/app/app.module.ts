import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { HomeComponent } from './components/common/home/home.component';
import { AboutComponent } from './components/common/about/about.component';
import { LoginComponent } from './components/authentication/login/login.component';
import { RegisterComponent } from './components/authentication/register/register.component';
import { NotFoundComponent } from './components/common/not-found/not-found.component';
import { ProfileComponent } from './components/user/profile/profile.component';
import { ConfirmRegistrationComponent } from './components/authentication/confirm-registration/confirm-registration.component';
import { ForgotPasswordResetComponent } from './components/authentication/forgot-password-reset/forgot-password-reset.component';
import { ForgotPasswordComponent } from './components/authentication/forgot-password/forgot-password.component';
import { EditProfileComponent } from './components/user/edit-profile/edit-profile.component';
import { ChangePasswordComponent } from './components/user/change-password/change-password.component';
import { NavbarComponent } from './components/layout/navbar/navbar.component';
import { AppRoutingModule } from './app-routing.module';
import { JwtModule } from '@auth0/angular-jwt';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { ErrorComponent } from './components/common/error/error.component';

export function tokenGetter() {
  return localStorage.getItem('_uid');
}

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AboutComponent,
    LoginComponent,
    RegisterComponent,
    NotFoundComponent,
    ProfileComponent,
    ConfirmRegistrationComponent,
    ForgotPasswordResetComponent,
    ForgotPasswordComponent,
    EditProfileComponent,
    ChangePasswordComponent,
    NavbarComponent,
    ErrorComponent,
  ],
  imports: [
    CommonModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    BrowserModule,
    HttpClientModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
      },
    }),
    ReactiveFormsModule,
    AppRoutingModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
