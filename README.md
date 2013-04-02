PayPal Payments Example (Android)
================================
An example application that uses 'Log In with PayPal' to login with existing PayPal profiles via OpenID Connect and PayPal's Mobile Payments Library for Android to enable payments.

The app's sample usecase is a pizza configurator.

## Going live:
You'll need to set your credentials for Log In with PayPal and PayPal's Adaptive Payments (via Mobile Payments Library) in the [Configuration.java](src/com/paypal/example/android/mpl/Configuration.java): 

Payments:

```java
	public static final String APP_ID = "APP-80W284485P519543T";
	public static final int	 SERVER = PayPal.ENV_SANDBOX;
```

Log In:

```java
	public static final String CLIENT_ID = "YOUR_ID";
	public static final String CLIENT_SECRET = "YOUR_SECRET";
```

## Libraries used:
- [Crouton](https://github.com/keyboardsurfer/Crouton)
- [ActionBarSherlock](https://github.com/JakeWharton/ActionBarSherlock)
- [SlidingMenuActivity](https://github.com/jfeinstein10/SlidingMenu)

## Developed by:
- Tim Messerschmidt, PayPal Developer Evangelist