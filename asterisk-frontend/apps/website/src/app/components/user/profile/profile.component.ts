import {Component, OnInit} from '@angular/core';
import {UserService} from "../../../services/user.service";
import {User} from "../../../models/user.model";
import {AuthenticationService} from "../../../services/authentication.service";
import {HttpResponse} from "@angular/common/http";
import {NavigationExtras, Router} from "@angular/router";

@Component({
  selector: 'asterisk-frontend-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {

  public user: User | null = null;

  constructor(private readonly _userService: UserService,
              private readonly _authenticationService: AuthenticationService,
              private readonly _router: Router) {

  }

  ngOnInit() {
    this._userService.readUser(this._authenticationService.getSubject()).subscribe({
      next: (response: HttpResponse<User>) => {
        if (response.status === 200) {
          this.user = response.body;
        }
      }
    });
  }
}
