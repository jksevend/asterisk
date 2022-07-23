import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../services/authentication.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {HttpResponse} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'asterisk-frontend-forgot-password-reset',
  templateUrl: './forgot-password-reset.component.html',
  styleUrls: ['./forgot-password-reset.component.css'],
})
export class ForgotPasswordResetComponent implements OnInit {

  private _fpid = "";

  private readonly uuidRegex: string = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$"

  public errorMessage: string | null | undefined = null;
  public message: string | null = null;

  public readonly forgotPasswordResetForm: FormGroup = new FormGroup({
    password: new FormControl('', [Validators.required]),
    passwordConfirmation: new FormControl('', [Validators.required]),

  });

  constructor(private readonly _authenticationService: AuthenticationService,
              private readonly _activatedRoute: ActivatedRoute,
              private readonly _router: Router,
              private readonly _toastr: ToastrService) {
  }

  ngOnInit(): void {
    this._activatedRoute.queryParams.subscribe((params: Params) => {
      // If query parameter is found assign it
      if (params['fpid']) {
        this._fpid = params['fpid'];
      }

      // Still empty -> error
      if (this._fpid === "") {
        this._router.navigate(['error']);
      }

      // Is no UUID -> error
      const regex = new RegExp(this.uuidRegex);
      if (!regex.test(this._fpid)) {
        this._router.navigate(['error']);
      }
    });
  }

  public performPasswordReset(): void {
    this._authenticationService.resetPassword(this._fpid,
      this.forgotPasswordResetForm.get('password')?.value,
      this.forgotPasswordResetForm.get('passwordConfirmation')?.value).subscribe({
      next: (response: HttpResponse<never>) => {
        if (response.status === 200) {
          this._router.navigate(['login']);
          this._toastr.success('Successfully reset password - You can now login again.','Password reset')
        }
    }
    });
  }

}
