import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {JwtHelperService} from "@auth0/angular-jwt";
import {Observable} from "rxjs";
import {BaseResponse} from "@asterisk-frontend/asterisk-common";
import {CONFIG} from "@asterisk-frontend/config";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(@Inject(CONFIG) private _appConfig: any, private readonly _http: HttpClient, private readonly _jwtHelper: JwtHelperService) {
  }

  /**
   *
   * @param email
   * @param password
   */
  public login(email: string, password: string): Observable<HttpResponse<BaseResponse>> {
    return this._http.post<BaseResponse>(this._appConfig.backendUrl + "/auth/login",
      {email: email, password: password}, {observe: 'response'});
  }

  /**
   *
   * @param firstName
   * @param lastName
   * @param username
   * @param email
   * @param password
   */
  public register(firstName: string, lastName: string, username: string, email: string, password: string): Observable<HttpResponse<BaseResponse>> {
    return this._http.post<BaseResponse>(this._appConfig.backendUrl + "/auth/register",
      {
        firstName: firstName,
        lastName: lastName,
        username: username,
        email: email,
        password: password
      }, {observe: 'response'});
  }

  /**
   *
   */
  public logout(): Observable<HttpResponse<never>> {
    return this._http.post<never>(this._appConfig.backendUrl + "/auth/logout",
      {},
      {observe: 'response'});
  }

  /**
   *
   * @param cid
   * @param code
   */
  public confirmRegistration(cid: string, code: string): Observable<HttpResponse<BaseResponse>> {
    return this._http.post<BaseResponse>(this._appConfig.backendUrl + `/auth/register/${cid}/confirm`,
      {code: code},
      {observe: 'response'});
  }

  /**
   *
   * @param cid
   */
  public resendConfirmationCode(cid: string): Observable<HttpResponse<BaseResponse>> {
    return this._http.post<BaseResponse>(this._appConfig.backendUrl + `/auth/register/${cid}/resend-code`,
      {},
      {observe: 'response'});
  }

  /**
   *
   * @param email
   */
  public forgotPassword(email: string): Observable<HttpResponse<never>> {
    return this._http.post<never>(this._appConfig.backendUrl + '/auth/forgot-password',
      {email: email}, {observe: 'response'})
  }

  /**
   *
   * @param fpid
   * @param password
   * @param passwordConfirmation
   */
  public resetPassword(fpid: string, password: string, passwordConfirmation: string): Observable<HttpResponse<never>> {
    return this._http.post<never>(this._appConfig.backendUrl + `/auth/forgot-password/${fpid}/reset`,
      {password: password, passwordConfirmation: passwordConfirmation},
      {observe: 'response'})
  }

  /**
   * Helper function to determine if user is currently authenticated
   */
  public isLoggedIn(): boolean {
    return !this._jwtHelper.isTokenExpired();
  }

  /**
   * Returns the username encoded inside the access token
   */
  public getUsername(): string {
    return this._jwtHelper.decodeToken().username;
  }

  /**
   *
   */
  public getSubject(): string {
    return this._jwtHelper.decodeToken().sub;
  }
}
