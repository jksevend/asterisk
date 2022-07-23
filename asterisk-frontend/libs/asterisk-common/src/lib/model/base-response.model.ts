export class BaseResponse {
  private readonly _hint: string;
  private readonly _payload?: any;


  constructor(hint: string, payload: any) {
    this._hint = hint;
    this._payload = payload;
  }

  get hint(): string {
    return this._hint;
  }

  get payload(): any {
    return this._payload;
  }
}
