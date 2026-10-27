# Releasing EmberFocus to Google Play

This is the full, ordered checklist to take the app from source to a live, revenue-generating Play
Store listing. Steps that require your Google account are marked **(you)**. Everything else is
already done in the codebase.

## 0. One-time prerequisites (you)

1. Create a Google Play Developer account ($25 one-time): https://play.google.com/console/signup
2. Install JDK 17+ (already present on the build machine) and the Android SDK.

## 1. Generate a release keystore (you)

Run this once and keep the `.jks` file safe (losing it means you can never update the app):

```bash
keytool -genkeypair -v \
  -keystore emberfocus-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias emberfocus
```

Then copy the template and fill it in:

```bash
cp keystore.properties.template keystore.properties
# edit keystore.properties with your passwords
```

`keystore.properties` and `*.jks` are gitignored, so secrets never reach the repo. When present,
the release build is signed automatically.

## 2. Build the release bundle

```bash
./gradlew :app:bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

(If you did not set up a keystore, the bundle is unsigned and Play will reject it. With Play App
Signing you may alternatively upload an unsigned/upload-key-signed bundle — see step 5.)

## 3. Verify locally

```bash
./gradlew test                      # unit tests
./gradlew :app:assembleRelease      # ensures R8/shrinking succeeds
```

Install the release APK on a device to smoke-test the timer, habits, stats and settings.

## 4. Create the app in Play Console (you)

1. Play Console → **Create app** → name "EmberFocus", app (not game), free.
2. Fill the **store listing**: short + full description (draft below), screenshots, feature graphic,
   app icon (the in-app adaptive icon can be exported at 512×512).
3. Complete **content rating**, **data safety** (declare: no data collected, no account, purchases
   via Play), **target audience**, and the **privacy policy** URL.

## 5. Play App Signing (you)

Opt in to Play App Signing (recommended). Upload your app once; Google manages the signing key and
you keep an upload key. The `keystore.properties` key becomes your upload key.

## 6. Create the in-app products (you) — THIS IS THE REVENUE STEP

In Play Console → **Monetize**:

1. **In-app products** → create a managed product with product ID exactly:
   - `emberfocus_pro_lifetime`  (one-time "unlock forever")
2. **Subscriptions** → create a subscription with product ID exactly:
   - `emberfocus_pro_sub`  (add monthly and/or yearly base plans)

These IDs must match `core/billing/src/main/kotlin/com/emberfocus/core/billing/BillingProducts.kt`.
Set prices per market. Activate both products.

> Billing only returns products once the app is published to at least a **closed testing** track and
> your test account is a licensed tester. Add license testers under Play Console → Setup → License
> testing.

## 7. Upload and roll out (you)

1. Create a release on **Internal testing** first. Upload the `.aab`.
2. Add testers, install via the opt-in link, and verify the paywall shows real prices and a test
   purchase unlocks Pro.
3. Promote to **Production** when satisfied.

## Suggested store listing copy

**Short description (80 chars max):**
> Cozy focus timer with habit streaks and calming ambient sounds.

**Full description (draft):**
> EmberFocus is a warm, distraction-free focus timer. Work in Pomodoro sessions, keep your daily
> habit streaks alive, and settle in with gentle ambient soundscapes. Track your progress with clean
> weekly insights — all offline, no account, no tracking.
>
> • Focus & break cycles with Classic, Deep Work and Sprint presets
> • Daily habit streaks with a 7-day heatmap
> • Weekly focus insights and session history
> • Ambient soundscapes for deeper focus
> • A cozy warm theme in light and dark mode
>
> Upgrade to EmberFocus Pro to unlock every soundscape, full insights, unlimited habits and custom
> presets.

## Monetization notes

- No backend is required. Play verifies purchases on-device; `BillingManager` acknowledges them and
  caches entitlement in DataStore so Pro is reflected instantly and offline.
- For stronger anti-piracy later, add server-side receipt validation with the Play Developer API.
- Consider a short free trial / intro price on the subscription to lift conversion.
