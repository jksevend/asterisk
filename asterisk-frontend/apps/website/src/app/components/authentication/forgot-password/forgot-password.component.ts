import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../services/authentication.service";
import {HttpResponse} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'asterisk-frontend-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {

  public errorMessage: string | null | undefined = null;
  public message: string | null = null;

  public readonly forgotPasswordForm: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  constructor(private readonly _authenticationService: AuthenticationService,
              private readonly _toastr: ToastrService) {
  }

  public performForgotPassword(): void {
    this._authenticationService.forgotPassword(this.forgotPasswordForm.get('email')?.value)
      .subscribe({
        next: (response: HttpResponse<never>) => {
          if (response.status === 200) {
            this._toastr.success('An email was sent to the address','Email sent')
          }
        }
      })
  }

}
