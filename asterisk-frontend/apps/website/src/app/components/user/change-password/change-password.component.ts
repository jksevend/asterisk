import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../../services/user.service";
import {AuthenticationService} from "../../../services/authentication.service";
import {HttpResponse} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Component({
  selector: 'asterisk-frontend-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css'],
})
export class ChangePasswordComponent {

  public errorMessage: string | null | undefined = null;

  public readonly changePasswordForm: FormGroup = new FormGroup({
    password: new FormControl('', [Validators.required]),
    passwordConfirmation: new FormControl('', [Validators.required]),

  });

  constructor(private readonly _userService: UserService,
              private readonly _authenticationService: AuthenticationService,
              private readonly _router: Router,
              private readonly _toastr: ToastrService) {
  }

  public performPasswordChange(): void {
    this._userService.changePassword(this._authenticationService.getSubject(),
      this.changePasswordForm.get('password')?.value,
      this.changePasswordForm.get('passwordConfirmation')?.value
    ).subscribe({
      next: (response: HttpResponse<never>) => {
        if (response.status === 200) {
          // Perform a logout
          this._authenticationService.logout().subscribe({
            next: (response: HttpResponse<never>) => {
              if (response.status === 200) {
                localStorage.removeItem('_uid');
                this._router.navigate(['login']);
                this._toastr.success('Please sign in again.','Password change complete')
              }
            }
          })
        }
      }
    });
  }

}
