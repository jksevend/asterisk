class ResponseDto {
  final String hint;
  final dynamic payload;

  ResponseDto(this.hint, this.payload);

  factory ResponseDto.fromJson(final Map<String, dynamic> json) => ResponseDto(json['hint'], json['payload']);
}