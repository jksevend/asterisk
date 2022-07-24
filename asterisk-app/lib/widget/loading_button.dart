import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class LoadingButton extends StatefulWidget {
  final bool disabled;
  final AsyncCallback onTap;
  final Widget child;

  const LoadingButton({Key? key, required this.onTap, required this.child, this.disabled = false})
      : super(key: key);

  @override
  State<LoadingButton> createState() => _LoadingButtonState();
}

class _LoadingButtonState extends State<LoadingButton> {
  bool _loading = false;

  @override
  Widget build(BuildContext context) {
    return _loading
        ? const Center(child: CircularProgressIndicator())
        : OutlinedButton(
            onPressed: widget.disabled ? null : () => _onTap(),
            child: widget.child,
          );
  }

  void _onTap() async {
    _toggle();
    await widget.onTap().whenComplete(() => _toggle());
  }

  void _toggle() {
    setState(() {
      _loading = !_loading;
    });
  }
}
