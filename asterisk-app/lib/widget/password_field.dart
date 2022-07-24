import 'package:flutter/material.dart';

class PasswordField extends StatefulWidget {
  final TextEditingController passwordController;
  const PasswordField({Key? key, required this.passwordController}) : super(key: key);

  @override
  State<PasswordField> createState() => PasswordFieldState();

  /// Access [PasswordFieldState] from outside based on [context]
  static PasswordFieldState of(final BuildContext context) {
    final PasswordFieldState? state = context.findAncestorStateOfType<PasswordFieldState>();
    if (state == null) throw StateError('Trying to access null state PasswordFieldState');
    return state;
  }
}

class PasswordFieldState extends State<PasswordField> {
  bool _obscurePassword = true;


  @override
  Widget build(BuildContext context) {
    return Material(
      child: TextFormField(
        controller: widget.passwordController,
        obscureText: _obscurePassword,
        maxLength: 64,
        keyboardType: TextInputType.text,
        decoration: InputDecoration(
          labelText: 'Password',
          hintText: 'Your strong password',
          prefixIcon: const Icon(Icons.lock_outline),
          suffix: IconButton(
            onPressed: () => _togglePasswordVisibility(),
            icon: _determineSuffixIcon,
          ),
        ),
        validator: (final String? value) {
          if (value == null || value.isEmpty) {
            return 'Password is required';
          }

          return null;
        },
      ),
    );
  }

  void _togglePasswordVisibility() => setState(() {
    _obscurePassword = !_obscurePassword;
  });

  Icon get _determineSuffixIcon =>
      _obscurePassword ? const Icon(Icons.visibility_off) : const Icon(Icons.visibility);
}
