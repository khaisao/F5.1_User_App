package jp.slapp.android.android.data.event

sealed class EventBusAction {
    object ReloadMessage : EventBusAction()
}