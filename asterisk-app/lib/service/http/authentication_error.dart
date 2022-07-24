class AuthenticationError extends Error {
  /// The message of the error
  final String message;

  /// Constructor
  AuthenticationError(this.message);

  @override
  String toString() {
    return 'Error during connection: ${Error.safeToString(message)}';
  }
}