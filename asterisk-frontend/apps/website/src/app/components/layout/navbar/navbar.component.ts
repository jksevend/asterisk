import {Component} from '@angular/core';
import {AuthenticationService} from "../../../services/authentication.service";
import {HttpResponse} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'asterisk-frontend-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  constructor(readonly _authenticationService: AuthenticationService,
              private readonly _toastr: ToastrService) {
  }

  public logout(): void {
    this._authenticationService.logout().subscribe({
      next: (response: HttpResponse<never>) => {
        if (response.status === 200) {
          localStorage.removeItem('_uid');
          this._toastr.info('Until next time', 'Bye!', {positionClass: 'toast-bottom-right'})
        }
      }
    })
  }
}
