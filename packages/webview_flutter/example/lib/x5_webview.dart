import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';

class X5WebView extends StatefulWidget {
  @override
  X5WebViewState createState() =>  X5WebViewState();
}

class X5WebViewState extends State<X5WebView> {
  // final WebViewController _controller = WebViewController();
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('X5浏览器'),
      ),
      body: WebView(
        initialUrl: 'https://mp.weixin.qq.com/s/jJoxQuOvFQTBNl7_sqxjaQ',
      ),
    );
  }
  @override
  void initState() {
    // _controller.init();
    super.initState();
  }


}