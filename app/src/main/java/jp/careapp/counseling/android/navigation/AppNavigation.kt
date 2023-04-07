package jp.careapp.counseling.android.navigation

import android.os.Bundle
import jp.careapp.core.navigationComponent.BaseNavigator

interface AppNavigation : BaseNavigator {

    fun openSplashToTopScreen(bundle: Bundle? = null)

    fun openSplashToLoginScreen(bundle: Bundle? = null)

    fun openSplashToReRegisterScreen(bundle: Bundle? = null)

    fun openSplashToBadUserScreen(bundle: Bundle? = null)

    fun openStartToTopScreen(bundle: Bundle? = null)

    // TODO
    fun openRegistrationToRegisterNameScreen(bundle: Bundle? = null)

    fun openStartToInputAndEditEmailScreen(bundle: Bundle? = null)

    fun openLoginEmailScreen(bundle: Bundle? = null)

    fun openSplashToStartScreen(bundle: Bundle? = null)

    fun openStartToDifficultLoginScreen(bundle: Bundle? = null)

    fun openStartToTermsScreen(bundle: Bundle? = null)

    fun openStartToPrivacyScreen(bundle: Bundle? = null)

    fun openStartWithoutLoginToRegistrationScreen(bundle: Bundle? = null)

    fun openInputEmailToVerifyCode(bundle: Bundle? = null)

    fun openVerifyCodeToVerifyCodeHelpScreen(bundle: Bundle? = null)

    fun openVerifyCodeToRegistrationScreen(bundle: Bundle? = null)

    fun openVerifyCodeToTopScreen(bundle: Bundle? = null)

    fun openVerifyCodeToReRegisterScreen(bundle: Bundle? = null)

    fun openVerifyCodeToBadUserScreen(bundle: Bundle? = null)

    fun openTutorialToTopScreen(bundle: Bundle? = null)

    fun openVerifyCodeToTutorialScreen(bundle: Bundle? = null)

    fun openTopToBuyPointScreen(bundle: Bundle? = null)

    fun openRegistrationToTermsOfServiceScreen(bundle: Bundle? = null)

    fun openRegistrationToTopScreen(bundle: Bundle? = null)

    fun openTopToUserProfileScreen(bundle: Bundle)

    fun openUserProfileToBuyPointScreen(bundle: Bundle? = null)

    fun openMyPageToFavorite(bundle: Bundle? = null)

    fun openMyPageToLabScreen(bundle: Bundle? = null)

    fun openMyPageToSettingNotification(bundle: Bundle? = null)

    fun openMyPageToNews(bundle: Bundle? = null)

    fun openMyPageToEditProfile(bundle: Bundle? = null)

    fun openHomeToBannerFirstTimeUseScreen(bundle: Bundle? = null)

    fun openRegistrationToTutorialScreen(bundle: Bundle? = null)

    fun openDetailUserToTroubleSheet(bundle: Bundle? = null)

    fun openTroubleSheetToChooseTypeTrouble(bundle: Bundle)

    fun openVerifyCodeToEditProfileScreen(bundle: Bundle? = null)

    fun openEditProfileToEditEmailScreen(bundle: Bundle? = null)

    fun openUserProfileToReportScreen(bundle: Bundle? = null)

    fun openMyPageToUpdateTroubleSheet(bundle: Bundle? = null)

    fun openTroubleSheetUpdateToChooseTypeTrouble(bundle: Bundle?)

    fun openMypageToSetting(bundle: Bundle? = null)

    fun openSettingToWebview(bundle: Bundle? = null)

    fun openSettingToBlockedList(bundle: Bundle? = null)

    fun openSettingToWithdrawal(bundle: Bundle? = null)

    fun openSettingToWithdrawalStart(bundle: Bundle? = null)

    fun openSettingToDeleteAccount(bundle: Bundle? = null)

    fun openWithdrawalStartToEditProfile(bundle: Bundle? = null)

    fun openWithdrawalStartToWithdrawal(bundle: Bundle? = null)

    fun openWithdrawalToStart(bundle: Bundle? = null)

    fun openTopToSearchScreen(bundle: Bundle? = null)

    fun openSearchToSearchResultScreen(bundle: Bundle? = null)

    fun openSearchResultToProfileScreen(bundle: Bundle? = null)

    fun openRankingToUserProfileScreen(bundle: Bundle? = null)

    fun openMainToMaintenance(bundle: Bundle? = null)

    fun openWelcomeToMaintenance(bundle: Bundle? = null)

    fun openMaintenanceToWebview(bundle: Bundle? = null)

    fun openMyPageToBuyPointScreen(bundle: Bundle? = null)

    fun openScreenToWebview(bundle: Bundle? = null)

    fun openProfileUserToTopScreen(bundle: Bundle? = null)

    fun openNewsToTopScreen(bundle: Bundle? = null)

    fun openBuyPointToTopScreen(bundle: Bundle? = null)

    fun openTopToNewsScreen(bundle: Bundle? = null)

    fun openOtherScreenToTopScreen(bundle: Bundle? = null)

    fun openChatMessageToChatListScreen(data: Bundle? = null)

    fun openChatMessageToBuyPoint(data: Bundle? = null)

    fun openChatMessageToEditTroubleSheet(data: Bundle? = null)

    fun openDetailUserToChatMessage(data: Bundle? = null)

    fun openDetailUserToTroubleSheetUpdate(data: Bundle? = null)

    fun openToCalling(data: Bundle? = null)

    fun openCallingToLivestream(data: Bundle? = null)

    fun openTroubleSheetToChatMessage(data: Bundle? = null)

    fun openTroubleSheetUpdateToChatMessage(data: Bundle? = null)

    fun openChatMessageToUserProfile(data: Bundle? = null)

    fun popopBackStackToDetination(destination: Int)

    fun openWebBuyPointToBuyPointGoogle(data: Bundle? = null)

    fun openActionToLogin(data: Bundle? = null)

    fun openReviewConsultant(data: Bundle)

    fun openMainToChatMessage(data: Bundle? = null)

    fun openTopToChatMessage(bundle: Bundle? = null)

    fun openVerifyCodeHelpToInputEmail(bundle: Bundle? = null)

    fun openWithdrawalFinish(bundle: Bundle? = null)

    fun openSelectCategoryToTroubleSheet(data: Bundle? = null)

    fun openTroubleSheetToTopScreen(data: Bundle? = null)

    fun openTroubleSheetToPartnerInfoScreen(data: Bundle? = null)

    fun openRegistrationToSelectCategoryScreen(data: Bundle? = null)

    fun openTroubleSheetToTutorialScreen(bundle: Bundle? = null)

    fun openSearchLabScreen(bundle: Bundle? = null)

    fun openSelectionCagoryFromSearch(bundle: Bundle? = null)

    fun openLaboToNewQuestionScreen(bundle: Bundle? = null)

    fun openNewQuestionToChooseTypeCategoryScreen(bundle: Bundle? = null)

    fun openLabToLabDetailScreen(bundle: Bundle? = null)

    fun openLabDetailToBuyPointScreen(bundle: Bundle? = null)

    fun openLabDetailToUserProfileScreen(bundle: Bundle? = null)

    fun openLabDetailToReportLaboScreen(bundle: Bundle? = null)

    fun openHomeToLabDetailScreen(bundle: Bundle? = null)

    fun openGlobalToTroubleSheetUpdateFragment(bundle: Bundle? = null)

    fun openGlobalToSettingNotification(bundle: Bundle? = null)

    fun openPartnerInfoToTopScreen(bundle: Bundle? = null)

    fun openPartnerInfoToTutorialScreen(bundle: Bundle? = null)

    fun openCallingToUpdateTroubleSheet(bundle: Bundle? = null)

    fun openCallingToChatMessage(bundle: Bundle? = null)

    fun openCallingToBuyPoint(bundle: Bundle? = null)

    fun openRMEnterNameToRmTop(bundle: Bundle? = null)

    fun openSplashToRMStart(bundle: Bundle? = null)

    fun openRMStartToRMEnterName(bundle: Bundle? = null)

    fun openRMTopToRMBLockList(bundle: Bundle? = null)

    fun openRMMyMenuToSettingPush(bundle: Bundle? = null)

    fun openRMTopToRMSettingNickName(bundle: Bundle? = null)

    fun openRMTopToRMSettingProfileMessage(bundle: Bundle? = null)

    fun openRMTopToRMUserDetail(bundle: Bundle? = null)

    fun openRMUserDetailToRMCalling(bundle: Bundle? = null)

    fun openRMCallingToRMLivestream(bundle: Bundle? = null)

    fun openSplashToRMTop(bundle: Bundle? = null)

    fun openRMUserDetailToRMUserDetailReport(bundle: Bundle? = null)

    fun openRMMyMenuToRMSettingContact(bundle: Bundle? = null)

    fun openRMSettingContactToRMSettingContactFinish(bundle: Bundle? = null)

    fun openTabRMMyMenuToRMStart(bundle: Bundle? = null)

    fun openRMUserDetailToUserDetailMsg(bundle: Bundle? = null)

    fun openUserDetailMsgToRMUserDetailReport(bundle: Bundle? = null)

    fun openUserDetailMessageToRMTop(bundle: Bundle? = null)

    fun openWithdrawalToSettingNotification(bundle: Bundle? = null)

    fun openRMTopToRMBuyPoint(bundle: Bundle? = null)

    fun openMyPageToTermsOfService(bundle: Bundle? = null)

    fun openMyPageToPrivacyPolicy(bundle: Bundle? = null)

    fun openMyPageToUsePointsGuide(bundle: Bundle? = null)

    fun openEditProfileToEditNickName(bundle: Bundle? = null)

    fun openMyPageToBlocked(bundle: Bundle? = null)

    fun openMyPageToNotification(bundle: Bundle? = null)

    fun openMyPageToContactUs(bundle: Bundle? = null)

    fun openContactUsToContactUsConfirm(bundle: Bundle? = null)

    fun openContactUsConfirmToContactUsFinish(bundle: Bundle? = null)

    fun openEditProfileToEditMail(bundle: Bundle? = null)

    fun openEditMailToVerifyCode(bundle: Bundle? = null)

    fun openMyPageToFAQ(bundle: Bundle? = null)

    fun openFAQToWithdrawal(bundle: Bundle? = null)

    fun openRMTermOfService(bundle: Bundle? = null)

    fun openRMPrivacyPolicy(bundle: Bundle? = null)

    fun openLiveStreamToExitLiveStream(bundle: Bundle? = null)

    fun openExitLiveStreamToMessage(bundle: Bundle? = null)

    fun openUserDetailToLiveStream(bundle: Bundle?)
}
