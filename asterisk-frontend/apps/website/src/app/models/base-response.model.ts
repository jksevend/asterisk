/**
 * A basic response of the spring boot backend
 * Check class `ResponseDto` in backend for reference
 */
export class BaseResponse {

  private _hint: string;
  private _payload?: any;

  constructor(hint: string, payload?: any) {
    this._hint = hint;
    this._payload = payload;
  }


  get hint(): string {
    return this._hint;
  }

  set hint(value: string) {
    this._hint = value;
  }

  get payload(): any {
    return this._payload;
  }

  set payload(value: any) {
    this._payload = value;
  }
}
