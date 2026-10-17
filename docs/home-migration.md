# Home migration

The Home composables, Tabler icons, dialog, shapes and typography come from
`D:/FoodHubApp`. `HomeLegacyTheme` scopes the original theme to Home so the
existing authentication and menu screens keep their theme. Coil AsyncImage is
retained, with bundled fallback images and support for relative media paths.

`HomeViewModel` uses Hilt injection for TokenStore and FoodRepository. It owns
session observation, menu loading, query/category/price filters and ordering
mode. HomeUiState derives filtered and popular foods from the menu. HomeRoute
collects state with lifecycle awareness and refreshes data on resume.

Guest startup now opens Home after Splash. The menu destination is connected.
QR scanning, cart, orders, profile, chat, notifications and food detail are not
implemented in this project yet; Home actions show an explicit message instead
of navigating to a blank placeholder. Cart and notification badge counts remain
zero until their repositories are implemented. Table session state is presentation
data; a validated QR/session repository must be connected before placing orders
or ending a server table session. No backend session is fabricated by Home.

The original guest takeaway wording is intentionally preserved for visual
parity; confirm the backend guest takeaway policy before implementing checkout.

Validation: `:app:testDebugUnitTest :app:assembleDebug --offline`.
