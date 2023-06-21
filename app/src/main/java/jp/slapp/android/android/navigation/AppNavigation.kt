package jp.slapp.android.android.navigation

import android.os.Bundle
import jp.careapp.core.navigationComponent.BaseNavigator

interface AppNavigation : BaseNavigator {

    fun openSplashToTopScreen(bundle: Bundle? = null)

    fun openSplashToLoginScreen(bundle: Bundle? = null)

    fun openLoginEmailScreen(bundle: Bundle? = null)

    fun openInputEmailToVerifyCode(bundle: Bundle? = null)

    fun openVerifyCodeToVerifyCodeHelpScreen(bundle: Bundle? = null)

    fun openVerifyCodeToRegistrationScreen(bundle: Bundle? = null)

    fun openVerifyCodeToTopScreen(bundle: Bundle? = null)

    fun openTopToBuyPointScreen(bundle: Bundle? = null)

    fun openWebViewToBuyPointAndCloseWebView(bundle: Bundle? = null)

    fun openRegistrationToTopScreen(bundle: Bundle? = null)

    fun openTopToUserProfileScreen(bundle: Bundle)

    fun openUserProfileToBuyPointScreen(bundle: Bundle? = null)

    fun openMyPageToFavorite(bundle: Bundle? = null)

    fun openMyPageToNews(bundle: Bundle? = null)

    fun openMyPageToEditProfile(bundle: Bundle? = null)

    fun openHomeToBannerFirstTimeUseScreen(bundle: Bundle? = null)

    fun openUserProfileToReportScreen(bundle: Bundle? = null)

    fun openWithdrawalToStart(bundle: Bundle? = null)

    fun openTopToSearchScreen(bundle: Bundle? = null)

    fun openSearchToSearchResultScreen(bundle: Bundle? = null)

    fun openSearchResultToProfileScreen(bundle: Bundle? = null)

    fun openRankingToUserProfileScreen(bundle: Bundle? = null)

    fun openMainToMaintenance(bundle: Bundle? = null)

    fun openWelcomeToMaintenance(bundle: Bundle? = null)

    fun openMaintenanceToWebview(bundle: Bundle? = null)

    fun openScreenToWebview(bundle: Bundle? = null)

    fun openProfileUserToTopScreen(bundle: Bundle? = null)

    fun openNewsToTopScreen(bundle: Bundle? = null)

    fun openBuyPointToTopScreen(bundle: Bundle? = null)

    fun openTopToNewsScreen(bundle: Bundle? = null)

    fun openOtherScreenToTopScreen(bundle: Bundle? = null)

    fun openChatMessageToChatListScreen(data: Bundle? = null)

    fun openChatMessageToLivestreamScreen(data: Bundle? = null)

    fun openChatMessageToBuyPoint(data: Bundle? = null)

    fun openDetailUserToChatMessage(data: Bundle? = null)

    fun openChatMessageToUserProfile(data: Bundle? = null)

    fun popopBackStackToDetination(destination: Int)

    fun openWebBuyPointToBuyPointGoogle(data: Bundle? = null)

    fun openActionToLogin(data: Bundle? = null)

    fun openActionToLoginAndClearBackstack(data: Bundle? = null)

    fun openActionToRMLoginAndClearBackstack(data: Bundle? = null)

    fun openMainToChatMessage(data: Bundle? = null)

    fun openTopToChatMessage(bundle: Bundle? = null)

    fun openWithdrawalFinish(bundle: Bundle? = null)

    fun openRMEnterNameToRmTop(bundle: Bundle? = null)

    fun openSplashToRMStart(bundle: Bundle? = null)

    fun openRMStartToRMEnterName(bundle: Bundle? = null)

    fun openRMTopToRMBLockList(bundle: Bundle? = null)

    fun openRMMyMenuToSettingPush(bundle: Bundle? = null)

    fun openRMTopToRMSettingNickName(bundle: Bundle? = null)

    fun openRMTopToRMSettingProfileMessage(bundle: Bundle? = null)

    fun openRMTopToRMUserDetail(bundle: Bundle? = null)

    fun openRMUserDetailToRMLivestream(bundle: Bundle? = null)

    fun openRMUserDetailMessageToRMLivestream(bundle: Bundle? = null)

    fun openSplashToRMTop(bundle: Bundle? = null)

    fun openRMUserDetailToRMUserDetailReport(bundle: Bundle? = null)

    fun openRMMyMenuToRMSettingContact(bundle: Bundle? = null)

    fun openRMSettingContactToRMSettingContactFinish(bundle: Bundle? = null)

    fun openTabRMMyMenuToRMStart(bundle: Bundle? = null)

    fun openRMUserDetailToUserDetailMsg(bundle: Bundle? = null)

    fun openUserDetailMsgToRMUserDetailReport(bundle: Bundle? = null)

    fun openUserDetailMessageToRMTop(bundle: Bundle? = null)

    fun openRMTopToRMBuyPoint(bundle: Bundle? = null)

    fun openMyPageToUsePointsGuide(bundle: Bundle? = null)

    fun openEditProfileToEditNickName(bundle: Bundle? = null)

    fun openMyPageToBlocked(bundle: Bundle? = null)

    fun openSettingNotification(bundle: Bundle? = null)

    fun openContactUsToContactUsConfirm(bundle: Bundle? = null)

    fun openContactUsConfirmToContactUsFinish(bundle: Bundle? = null)

    fun openEditMailToVerifyCode(bundle: Bundle? = null)

    fun openMyPageToFAQ(bundle: Bundle? = null)

    fun openFAQToWithdrawal(bundle: Bundle? = null)

    fun openRMTermOfService(bundle: Bundle? = null)

    fun openRMPrivacyPolicy(bundle: Bundle? = null)

    fun openLiveStreamToExitLiveStream(bundle: Bundle? = null)

    fun openExitLiveStreamToMessage(bundle: Bundle? = null)

    fun openUserDetailToLiveStream(bundle: Bundle?)

    fun openTermsOfService(bundle: Bundle? = null)

    fun openPrivacyPolicy(bundle: Bundle? = null)

    fun openContactUs(bundle: Bundle? = null)

    fun openEditMail(bundle: Bundle? = null)

    fun openVerifyCodeHelpToVerifyCode(bundle: Bundle? = null)

    fun openExitLiveStreamToUserDetailFragment(bundle: Bundle? = null)
}
