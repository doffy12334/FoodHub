# Dialog catalog

All dialog composables are in
`app/src/main/java/com/example/foodhub_app/feature/shared/ui/FoodHubDialog.kt`.
The original project is `D:/FoodHubApp`; it is unchanged. Presentation model
types needed by features that do not exist yet are in `DialogModels.kt`.

| Original location | Shared functions |
| --- | --- |
| Shared UI | `FoodHubDialog`, `FoodHubAlertDialog` |
| Food detail / cart / orders / notifications | `LoginRequiredDialog`, `AddToCartLoginDialog`, `CartLoginDialog`, `OrdersLoginDialog`, `NotificationLoginDialog`, `TableSessionExpiredDialog` |
| Home / table scanner | `OrderModeDialog`, `EndTableSessionDialog`, `ScanTableErrorDialog` |
| Cart | `CheckoutDialog`, `CartEditDialog`, `DeleteCartItemDialog`, `ClearCartDialog`, `LoadingCartOptionsDialog` |
| Orders | `ReviewDialog`, `CancelOrderDialog` |
| Profile | `ProfileEditDialog` |
| Chat | `TableSessionEndedDialog`, `SupportRequestSentDialog`, `ChatSessionDialog` |
| Payment | `ZaloPayLaunchDialog`, `PaymentDetailDialog` |
| Admin menu | `AdminMenuEditorDialog`, `AdminCategoryEditorDialog`, `AdminFlashSaleDialog`, `AdminVariantManager`, `AdminVariantEditorDialog`, `AdminDeleteMenuItemDialog`, `AdminDeleteCategoryDialog` |
| Admin tables | `AdminCreateTableDialog`, `AdminQrDialog`, `AdminRegenerateQrDialog`, `AdminDeleteTableDialog` |
| Admin vouchers / reviews | `AdminVoucherEditorDialog`, `AdminDeleteVoucherDialog`, `AdminDeleteReviewDialog` |
| Admin orders / chat | `AdminOrderActionDialog`, `AdminDeleteConversationDialog` |

Standalone form dialogs retain the original form fields and validation. Inline
dialogs have been extracted into callback-based functions. Shared confirmation
dialogs use the FoodHub frame. Network calls from the old variant manager,
payment detail button and chat session handler are intentionally supplied by the
calling feature rather than constructed inside a composable.

Home now calls `LoginRequiredDialog` and `OrderModeDialog`; its visibility state
stays in Home. Other functions are ready to call when their feature is built.
Adding these functions does not implement checkout, upload, QR session APIs,
administration or payment polling. Callers pass data, busy/error state and
callbacks. Run callbacks only after explicit user confirmation and close the
dialog from the caller after a successful operation.

The voucher form uses strict SimpleDateFormat parsing and Calendar to support
Android 7+ directly. Dates follow the Vietnam time zone and are sent as UTC ISO
timestamps. QR rendering uses the existing ZXing dependency.

Validation: `:app:assembleDebug --offline`.
