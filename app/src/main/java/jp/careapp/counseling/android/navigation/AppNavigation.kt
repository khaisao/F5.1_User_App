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

    fun openMyPageToContact(bundle: Bundle? = null)

    fun openEditContactToConfirmContact(bundle: Bundle? = null)

    fun openMyPageToSettingNotification(bundle: Bundle? = null)

    fun openMyPageToNews(bundle: Bundle? = null)

    fun openMyPageToEditProfile(bundle: Bundle? = null)

    fun openContactConfirmToContactDone(bundle: Bundle? = null)

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
}