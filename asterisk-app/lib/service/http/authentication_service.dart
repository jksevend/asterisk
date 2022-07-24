import 'dart:convert';

import 'package:asterisk_app/model/response_dto.dart';
import 'package:http/http.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'authentication_error.dart';

class AuthenticationService {
  static const String _jwtKey = '_uid';
  static const String _fingerprintKey = '_fgp';
  static const String _backendUrl = 'http://192.168.0.122:8080';

  /// Attempts to authenticate a given [email] and [password]
  Future<bool> login(final String email, final String password) async {
    // Perform request
    final Response response = await post(Uri.parse('$_backendUrl/auth/login'),
        body: jsonEncode({'email': email, 'password': password}),
        headers: {
          'Content-type': 'application/json',
        });

    if (response.statusCode == 200) {
      // Parse response body
      final Map<String, dynamic> parsedJson = jsonDecode(response.body) as Map<String, dynamic>;
      final ResponseDto responseDto = ResponseDto.fromJson(parsedJson);

      // Parse the cookie
      final String? fingerprint = _parseFingerprint(response);
      if (fingerprint == null) throw AuthenticationError('No fingerprint detected!');

      final SharedPreferences preferences = await SharedPreferences.getInstance();
      await preferences.setString(_jwtKey, responseDto.payload);
      await preferences.setString(_fingerprintKey, fingerprint);
      return true;
    }

    return false;
  }

  /// Attempts to logout an authenticated user
  Future<void> logout() async {
    final SharedPreferences preferences = await SharedPreferences.getInstance();
    final String? jwt = preferences.getString(_jwtKey);
    final String? fingerprint = preferences.getString(_fingerprintKey);
    if (jwt == null || fingerprint == null) {
      return;
    }

    // Perform request
    final Response response = await post(Uri.parse('$_backendUrl/auth/logout'), headers: {
      'Content-type': 'application/json',
      'Authorization': 'Bearer $jwt',
      'Cookie': 'fgp=$fingerprint',
    });

    if (response.statusCode == 200) {
      preferences.remove(_jwtKey);
      preferences.remove(_fingerprintKey);
    }
  }

  /// Registers a new user account
  Future<String?> register(final String firstName, final String lastName, final String username,
      final String email, final String password) async {
    // Perform request
    final Response response = await post(Uri.parse('$_backendUrl/auth/register'),
        body: jsonEncode({
          'firstName': firstName,
          'lastName': lastName,
          'username': username,
          'email': email,
          'password': password
        }),
        headers: {
          'Content-type': 'application/json',
        });

    if (response.statusCode == 200) {
      // Parse response body
      final Map<String, dynamic> parsedJson = jsonDecode(response.body) as Map<String, dynamic>;
      final ResponseDto responseDto = ResponseDto.fromJson(parsedJson);

      return responseDto.payload;
    }

    return null;
  }

  /// Confirms a newly registered user account
  Future<bool> confirmRegistration(
      final String confirmationId, final String confirmationCode) async {
    // Perform request
    final Response response =
        await post(Uri.parse('$_backendUrl/auth/register/$confirmationId/confirm'),
            body: jsonEncode({
              'code': confirmationCode,
            }),
            headers: {
          'Content-type': 'application/json',
        });

    if (response.statusCode == 200) {
      return true;
    }

    return false;
  }

  /// Resends the confirmation mail after a registration
  Future<bool> resendConfirmationCode(final String confirmationId) async {
    // Perform request
    final Response response =
        await post(Uri.parse('$_backendUrl/auth/register/$confirmationId/confirm'), headers: {
      'Content-type': 'application/json',
    });

    if (response.statusCode == 200) {
      return true;
    }

    return false;
  }

  /// Request a forgot password id
  Future<String?> forgotPassword(final String email) async {
    // Perform request
    final Response response = await post(Uri.parse('$_backendUrl/auth/forgot-password'),
        body: jsonEncode({
          'email': email,
        }),
        headers: {
          'Content-type': 'application/json',
        });

    if (response.statusCode == 200) {
      // Parse response body
      final Map<String, dynamic> parsedJson = jsonDecode(response.body) as Map<String, dynamic>;
      final ResponseDto responseDto = ResponseDto.fromJson(parsedJson);

      return responseDto.payload;
    }

    return null;
  }

  /// Reset forgotten password
  Future<bool> resetPassword(
      final String forgotPasswordId, final String password, final String confirmation) async {
    // Perform request
    final Response response = await post(
        Uri.parse('$_backendUrl/auth/forgot-password/$forgotPasswordId/reset'),
        body: jsonEncode({'password': password, 'passwordConfirmation': confirmation}),
        headers: {
          'Content-type': 'application/json',
        });

    if (response.statusCode == 200) {
      return true;
    }

    return false;
  }

  /// Utility method to check if the jwt is expired
  /// If it is remove the jwt from shared preferences
  static Future<bool> isJwtExpired() async {
    final SharedPreferences preferences = await SharedPreferences.getInstance();

    final String? jwt = preferences.getString(_jwtKey);
    if (jwt == null) return true;

    final bool expired = JwtDecoder.isExpired(jwt);
    if (expired) {
      await preferences.remove(_jwtKey);
      return true;
    }
    return false;
  }

  /// Parse a set-cookie from an authentication [response] and return its value
  String? _parseFingerprint(final Response response) {
    final String? setCookie = response.headers['set-cookie'];
    if (setCookie == null) return null;

    final List<String> cookieList = setCookie.split(";");
    if (cookieList.isEmpty) return null;

    final String firstValue = cookieList[0];
    final List<String> fingerprintParts = firstValue.split("=");

    if (fingerprintParts.length == 2 && fingerprintParts[0] == "fgp") {
      return fingerprintParts[1];
    }

    return null;
  }
}
