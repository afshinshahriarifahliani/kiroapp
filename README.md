# EmberFocus 🔥

A warm, cozy focus timer with habit streaks and ambient soundscapes. Built 100% with Jetpack
Compose, Kotlin, a multi-module architecture and Koin for dependency injection.

EmberFocus helps you work in focused Pomodoro sessions, keep daily habit streaks alive, and wind
down with ambient sound. It works fully offline and respects your privacy — there is no backend and
no account required.

## Why it exists

A solo-developer product designed to be shippable and to generate revenue on Google Play without
running (and paying for) any server infrastructure. Monetization is handled entirely by Google Play
Billing.

## Features

- **Focus timer** — Pomodoro-style focus/break cycles with Classic, Deep Work and Sprint presets.
- **Habit streaks** — track daily habits, see current and best streaks, and a 7-day heatmap.
- **Insights** — weekly focus chart, totals, and recent session history.
- **Ambient soundscapes** — rain, forest, cafe and more.
- **Warm theming** — a cozy ember/amber/terracotta palette in both light and dark mode.

## Monetization: EmberFocus Pro

Free tier is useful on its own. **EmberFocus Pro** unlocks premium soundscapes, full insights,
unlimited habits and custom presets. It is sold through Google Play Billing as:

- A one-time purchase (`emberfocus_pro_lifetime`), and
- An auto-renewing subscription (`emberfocus_pro_sub`).

Product IDs live in `core/billing/.../BillingProducts.kt` and must be created in the Play Console
with matching IDs. See [`docs/RELEASE.md`](docs/RELEASE.md) for the full release checklist.

## Tech stack

| Area            | Choice                                            |
|-----------------|---------------------------------------------------|
| Language        | Kotlin 2.0                                         |
| UI              | Jetpack Compose + Material 3                       |
| Architecture    | Multi-module (core/* + feature/*), MVVM            |
| DI              | Koin                                               |
| Persistence     | Room (sessions, habits) + DataStore (preferences)  |
| Billing         | Google Play Billing Library 7                      |
| Build           | Gradle convention plugins in `build-logic`         |
| Min / Target    | SDK 26 / 35                                         |

## Module layout

```
app/                      # Application, navigation, theme host, DI startup
build-logic/              # Gradle convention plugins (one source of truth for config)
core/
  model/                  # Pure Kotlin domain models
  common/                 # Dispatchers and shared utilities
  designsystem/           # Warm theme, typography, shapes, reusable components
  ui/                     # Shared Compose helpers (formatting, colors)
  data/                   # Repositories + mappers (the domain boundary)
  database/               # Room database, DAOs, entities
  datastore/              # Preferences DataStore
  billing/                # Play Billing wrapper + entitlement state
feature/
  timer/                  # Pomodoro timer
  habits/                 # Habit tracking + streaks
  stats/                  # Insights and charts
  settings/               # Preferences, theme, upsell
  paywall/                # Pro purchase screen
```

## Build

```bash
./gradlew :app:assembleDebug        # debug APK
./gradlew test                      # unit tests
./gradlew :app:bundleRelease        # release AAB (needs keystore.properties)
```

## Release signing

Copy `keystore.properties.template` to `keystore.properties` and fill in your keystore details.
The file is gitignored. See [`docs/RELEASE.md`](docs/RELEASE.md).

## License

Copyright © the project owner. All rights reserved.
