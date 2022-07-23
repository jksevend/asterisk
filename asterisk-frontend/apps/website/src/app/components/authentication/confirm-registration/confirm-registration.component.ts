import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthenticationService} from "../../../services/authentication.service";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {HttpResponse} from "@angular/common/http";
import {BaseResponse} from "../../../models/base-response.model";

@Component({
  selector: 'asterisk-frontend-confirm-registration',
  templateUrl: './confirm-registration.component.html',
  styleUrls: ['./confirm-registration.component.css'],
})
export class ConfirmRegistrationComponent implements OnInit {
  public _crid = "";

  private readonly codeRegex: string = "^[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}-[a-zA-Z0-9]{3}$";
  private readonly uuidRegex: string = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$"

  public confirmationErrorMessage: string | null | undefined = null;

  public resendErrorMessage: string | null | undefined = null;
  public resendMessage: string | null = null;

  public readonly confirmRegistrationForm: FormGroup = new FormGroup({
    code: new FormControl('', [Validators.required, Validators.pattern(this.codeRegex)]),
  });

  constructor(private readonly _authenticationService: AuthenticationService,
              private readonly _activatedRoute: ActivatedRoute,
              private readonly _router: Router) {
  }

  ngOnInit(): void {
    this._activatedRoute.queryParams.subscribe((params: Params) => {
      // If query parameter is found assign it
      if (params['crid']) {
        this._crid = params['crid'];
      }

      // Still empty -> error
      if (this._crid === "") {
        this._router.navigate(['error']);
      }

      // Is no UUID -> error
      const regex = new RegExp(this.uuidRegex);
      if (!regex.test(this._crid)) {
        this._router.navigate(['error']);
      }
    });
  }

  /**
   *
   */
  public performConfirmation(): void {
    this._authenticationService.confirmRegistration(this._crid, this.confirmRegistrationForm.get('code')?.value)
      .subscribe({
        next: (response: HttpResponse<BaseResponse>) => {
          if (response.status === 200) {
            this._router.navigate(['login'])
          }
        }
      })
  }

  /**
   *
   */
  public resendCode(): void {
    this._authenticationService.resendConfirmationCode(this._crid)
      .subscribe({
        next: (response: HttpResponse<BaseResponse>) => {
          if (response.status === 200) {
            this.resendMessage = 'Successfully resend confirmation email.';
          }
        }
      })
  }
}
