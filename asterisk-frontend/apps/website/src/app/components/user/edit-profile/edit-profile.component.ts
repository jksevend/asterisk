import {Component, OnInit} from '@angular/core';
import {User} from "../../../models/user.model";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../../services/user.service";
import {HttpResponse} from "@angular/common/http";
import {AuthenticationService} from "../../../services/authentication.service";
import {Router} from "@angular/router";

@Component({
  selector: 'asterisk-frontend-edit-profile',
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.css'],
})
export class EditProfileComponent implements OnInit {

  public user: User | null = null;

  public errorMessage: string | null | undefined = null;

  public readonly editProfileForm: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required])
  });

  constructor(private readonly _userService: UserService,
              private readonly _authenticationService: AuthenticationService,
              private readonly _router: Router) {
  }

  ngOnInit(): void {
    this._userService.readUser(this._authenticationService.getSubject()).subscribe({
      next: (response: HttpResponse<User>) => {
        if (response.status === 200) {
          this.user = response.body;
          this.editProfileForm.setValue({
            email: this.user?.email,
            firstName: this.user?.firstName,
            lastName: this.user?.lastName,
            username: this.user?.username
          })
        }
      }
    });
  }

  public performEdit(): void {
    if (this.matches(this.user?.firstName, this.editProfileForm.get('firstName')?.value)
      && this.matches(this.user?.lastName, this.editProfileForm.get('lastName')?.value)
      && this.matches(this.user?.email, this.editProfileForm.get('email')?.value)
      && this.matches(this.user?.username, this.editProfileForm.get('username')?.value)) {
      this.errorMessage = 'Cannot change. Data is the same';
      return;
    }

      this._userService.updateUser(this._authenticationService.getSubject(),
        this.editProfileForm.get('firstName')?.value,
        this.editProfileForm.get('lastName')?.value,
        this.editProfileForm.get('email')?.value,
        this.editProfileForm.get('username')?.value).subscribe({
        next: (response: HttpResponse<never>) => {
          if (response.status === 200) {
            this._router.navigate(['profile']);
          }
        }
      })
  }

  private matches(old: any, n: any): boolean {
    return old === n;
  }
}
