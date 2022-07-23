import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../services/authentication.service";
import {Router} from "@angular/router";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {BaseResponse} from "../../../models/base-response.model";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'asterisk-frontend-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {

  public errorMessage: string | null | undefined = null;

  public readonly loginForm: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8),])
  });

  constructor(private readonly _authenticationService: AuthenticationService,
              private readonly _router: Router,
              private readonly _toastr: ToastrService) {
  }

  /**
   * Perform a login request to the backend
   *
   * If successful store the jwt received in the response inside localStorage
   * and navigate back to home
   *
   * In case of an error display the error for the user
   */
  public performLogin(): void {
    this._authenticationService.login(this.loginForm.get('email')?.value, this.loginForm.get('password')?.value)
      .subscribe({
        next: (response: HttpResponse<BaseResponse>) => {
          if (response.status === 200) {
            localStorage.setItem('_uid', response.body?.payload);
            this._router.navigate([''])
            this._toastr.success(`Welcome back ${this._authenticationService.getUsername()}!`, 'Login successful',
              {positionClass: 'toast-bottom-right'})
          }
        }, error: (error: HttpErrorResponse) => {
          this.errorMessage = error.error.hint;
        }
      });
  }
}
