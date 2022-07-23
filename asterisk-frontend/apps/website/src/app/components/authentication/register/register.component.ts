import {Component} from '@angular/core';
import {AuthenticationService} from "../../../services/authentication.service";
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {BaseResponse} from "../../../models/base-response.model";

@Component({
  selector: 'asterisk-frontend-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {

  public errorMessage: string | null | undefined = null;

  public readonly registerForm: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),

  });

  constructor(private readonly _authenticationService: AuthenticationService, private readonly _router: Router) {
  }

  public performRegister(): void {
    this._authenticationService.register(
      this.registerForm.get('firstName')?.value,
      this.registerForm.get('lastName')?.value,
      this.registerForm.get('username')?.value,
      this.registerForm.get('email')?.value,
      this.registerForm.get('password')?.value,
    ).subscribe({
      next: (response: HttpResponse<BaseResponse>) => {
        if (response.status === 200) {
          this._router.navigate(['confirm-registration'],
            {queryParams: {crid: response.body?.payload}})
        }
      }, error: (error: HttpErrorResponse) => {
        this.errorMessage = error.error.hint;
      }
    });
  }

}
