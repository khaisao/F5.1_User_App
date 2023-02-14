package jp.careapp.counseling.android.data.event

sealed class EventBusAction {
    object ReloadMessage : EventBusAction()
}