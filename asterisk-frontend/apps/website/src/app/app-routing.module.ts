import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/common/home/home.component";
import {AboutComponent} from "./components/common/about/about.component";
import {AuthenticationGuard} from "./guards/authentication.guard";
import {LoginComponent} from "./components/authentication/login/login.component";
import {RegisterComponent} from "./components/authentication/register/register.component";
import {NgModule} from "@angular/core";
import {NotFoundComponent} from "./components/common/not-found/not-found.component";
import {ConfirmRegistrationComponent} from "./components/authentication/confirm-registration/confirm-registration.component";
import {ForgotPasswordResetComponent} from "./components/authentication/forgot-password-reset/forgot-password-reset.component";
import {ProfileComponent} from "./components/user/profile/profile.component";
import {ForgotPasswordComponent} from "./components/authentication/forgot-password/forgot-password.component";
import {EditProfileComponent} from "./components/user/edit-profile/edit-profile.component";
import {ChangePasswordComponent} from "./components/user/change-password/change-password.component";
import {ErrorComponent} from "./components/common/error/error.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'about', component: AboutComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'forgot-password/reset', component: ForgotPasswordResetComponent},
  {path: 'confirm-registration', component: ConfirmRegistrationComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {
    path: 'profile', canActivate: [AuthenticationGuard],
    children: [
      {
        path: '',
        component: ProfileComponent,
      }, {
        path: "edit",

        children: [
          {
            path: '',
            component: EditProfileComponent,
          }, {path: 'change-password', component: ChangePasswordComponent}
        ]
      }
    ]
  },
  {path: 'error', component: ErrorComponent},
  {path: '**', component: NotFoundComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
