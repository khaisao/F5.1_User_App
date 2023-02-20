package jp.careapp.counseling.android.navigation

import android.os.Bundle
import dagger.hilt.android.scopes.ActivityScoped
import jp.careapp.core.navigationComponent.BaseNavigatorImpl
import jp.careapp.counseling.R
import javax.inject.Inject

@ActivityScoped
class AppNavigatorImpl @Inject constructor() : BaseNavigatorImpl(), AppNavigation {

    override fun openSplashToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_TopFragment, bundle)
    }

    override fun openSplashToLoginScreen(bundle: Bundle?) {
//        openScreen(R.id.action_homeFragment_to_listUserFragment, bundle)
    }

    override fun openSplashToReRegisterScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_ReRegisterFragment, bundle)
    }

    override fun openSplashToBadUserScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_badUserFragment, bundle)
    }

    override fun openStartToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_startFragment_to_TopFragment, bundle)
    }

    override fun openRegistrationToRegisterNameScreen(bundle: Bundle?) {
        openScreen(R.id.action_registrationFragment_to_registerNameFragment, bundle)
    }

    override fun openStartToInputAndEditEmailScreen(bundle: Bundle?) {
        openScreen(R.id.action_startFragment_to_InputEmailFragment, bundle)
    }

    override fun openLoginEmailScreen(bundle: Bundle?) {
        openScreen(R.id.action_inputEmailFragment_self,bundle)
    }

    override fun openSplashToStartScreen(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_startFragment, bundle)
    }

    override fun openStartToDifficultLoginScreen(bundle: Bundle?) {
        openScreen(R.id.action_startFragment_to_DifficultyWithLoginFragment, bundle)
    }

    override fun openStartToTermsScreen(bundle: Bundle?) {
        openScreen(R.id.action_startFragment_to_TermsFragment, bundle)
    }

    override fun openStartToPrivacyScreen(bundle: Bundle?) {
        openScreen(R.id.action_startFragment_to_PrivacyFragment, bundle)
    }

    override fun openStartWithoutLoginToRegistrationScreen(bundle: Bundle?) {
        openScreen(R.id.action_startFragment_to_RegistrationFragment, bundle)
    }

    override fun openInputEmailToVerifyCode(bundle: Bundle?) {
        openScreen(R.id.action_inputEmailFragment_to_VerifyCodeFragment, bundle)
    }

    override fun openVerifyCodeToVerifyCodeHelpScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_VerifyCodeHelpFragment, bundle)
    }

    override fun openVerifyCodeToRegistrationScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_RegistrationFragment, bundle)
    }

    override fun openVerifyCodeToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_TopFragment, bundle)
    }

    override fun openVerifyCodeToReRegisterScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_ReRegisterFragment, bundle)
    }

    override fun openVerifyCodeToBadUserScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_BadUserFragment, bundle)
    }

    override fun openTutorialToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_tutorial_to_TopFragment, bundle)
    }

    override fun openVerifyCodeToTutorialScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_TutorialFragment, bundle)
    }

    override fun openTopToBuyPointScreen(bundle: Bundle?) {
        openScreen(R.id.action_TopFragment_to_BuyPointFragment, bundle)
    }

    override fun openRegistrationToTermsOfServiceScreen(bundle: Bundle?) {
        openScreen(R.id.action_registrationFragment_to_TermsFragment, bundle)
    }

    override fun openRegistrationToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_registrationFragment_to_topFragment, bundle)
    }

    override fun openTopToUserProfileScreen(bundle: Bundle) {
        openScreen(R.id.action_homeFragment_to_UserProfileFragment, bundle)
    }

    override fun openUserProfileToBuyPointScreen(bundle: Bundle?) {
        openScreen(R.id.action_UserProfile_to_BuyPoint, bundle)
    }

    override fun openMyPageToFavorite(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_favoriteFragment, bundle)
    }

    override fun openMyPageToLabScreen(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_laboFragment, bundle)
    }

    override fun openMyPageToContact(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_contactEditFragment, bundle)
    }

    override fun openEditContactToConfirmContact(bundle: Bundle?) {
        openScreen(R.id.action_contactEditFragment_to_contactConfirmFragment, bundle)
    }

    override fun openMyPageToSettingNotification(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_fragmentNotification, bundle)
    }

    override fun openMyPageToNews(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_newsFragment, bundle)
    }

    override fun openMyPageToEditProfile(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_fragmentEditProfile, bundle)
    }

    override fun openContactConfirmToContactDone(bundle: Bundle?) {
        openScreen(R.id.action_contactConfirmFragment_to_contactDoneFragment, bundle)
    }

    override fun openDetailUserToTroubleSheet(bundle: Bundle?) {
        openScreen(R.id.action_UserProfile_to_TroubleSheet, bundle)
    }

    override fun openTroubleSheetToChooseTypeTrouble(bundle: Bundle) {
        openScreen(R.id.action_TroubleSheet_to_ChooseTypeTroubleFragment, bundle)
    }

    override fun openHomeToBannerFirstTimeUseScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_BannerFirstTimeUseFragment, bundle)
    }

    override fun openRegistrationToTutorialScreen(bundle: Bundle?) {
        openScreen(R.id.action_registrationFragment_to_tutorialFragment, bundle)
    }

    override fun openTopToSearchScreen(bundle: Bundle?) {
        openScreen(R.id.action_TopFragment_to_SearchFragment, bundle)
    }

    override fun openSearchToSearchResultScreen(bundle: Bundle?) {
        openScreen(R.id.action_search_to_searchResultFragment, bundle)
    }

    override fun openSearchResultToProfileScreen(bundle: Bundle?) {
        openScreen(R.id.action_searchResult_to_profileFragment, bundle)
    }

    override fun openProfileUserToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_UserProfile_to_TopFragment, bundle)
    }

    override fun openNewsToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_news_to_TopFragment, bundle)
    }

    override fun openBuyPointToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_buyPoint_to_TopFragment, bundle)
    }

    override fun openTopToNewsScreen(bundle: Bundle?) {
        openScreen(R.id.action_Top_to_NewsFragment, bundle)
    }

    override fun openOtherScreenToTopScreen(bundle: Bundle?) {
        openScreen(R.id.topFragment, bundle)
    }

    override fun openVerifyCodeToEditProfileScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_EditProfileFragment, bundle)
    }

    override fun openEditProfileToEditEmailScreen(bundle: Bundle?) {
        openScreen(R.id.action_fragmentEditProfile_to_editEmailfragment, bundle)
    }

    override fun openUserProfileToReportScreen(bundle: Bundle?) {
        openScreen(R.id.action_UserProfile_to_ReportUser, bundle)
    }

    override fun openMyPageToUpdateTroubleSheet(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_UpdateTrouble, bundle)
    }

    override fun openTroubleSheetUpdateToChooseTypeTrouble(bundle: Bundle?) {
        openScreen(R.id.action_TroubleSheetUpdate_to_ChooseTypeTroubleFragment, bundle)
    }

    override fun openMypageToSetting(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_Setting, bundle)
    }

    override fun openSettingToWebview(bundle: Bundle?) {
        openScreen(R.id.action_settingFragment_to_webViewFragment, bundle)
    }

    override fun openSettingToBlockedList(bundle: Bundle?) {
        openScreen(R.id.action_settingFragment_to_blockedFragment, bundle)
    }

    override fun openSettingToWithdrawal(bundle: Bundle?) {
        openScreen(R.id.action_settingFragment_to_withdrawalFragment, bundle)
    }

    override fun openSettingToWithdrawalStart(bundle: Bundle?) {
        openScreen(R.id.action_settingFragment_to_withdrawalStartFragment, bundle)
    }

    override fun openSettingToDeleteAccount(bundle: Bundle?) {
        openScreen(R.id.action_settingFragment_to_deleteAccountFragment, bundle)
    }

    override fun openWithdrawalStartToEditProfile(bundle: Bundle?) {
        openScreen(R.id.action_withdrawalStartFragment_to_editProfileFragment, bundle)
    }

    override fun openWithdrawalStartToWithdrawal(bundle: Bundle?) {
        openScreen(R.id.action_withdrawalStartFragment_to_withdrawalFragment, bundle)
    }

    override fun openWithdrawalToStart(bundle: Bundle?) {
        openScreen(R.id.action_withdrawalFragment_to_welcomeActivity, bundle)
    }

    override fun openRankingToUserProfileScreen(bundle: Bundle?) {
        openScreen(R.id.action_ranking_to_userProfilefragment, bundle)
    }

    override fun openMyPageToBuyPointScreen(bundle: Bundle?) {
        openScreen(R.id.action_mypage_to_buyPoint, bundle)
    }

    override fun openScreenToWebview(bundle: Bundle?) {
        openScreen(R.id.action_screen_to_webview, bundle)
    }

    override fun openMainToMaintenance(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_maintenance_navigation, bundle)
    }

    override fun openWelcomeToMaintenance(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_maintenance_navigation, bundle)
    }

    override fun openMaintenanceToWebview(bundle: Bundle?) {
        openScreen(R.id.action_maintenanceFragment_to_webViewFragment2, bundle)
    }

    override fun openChatMessageToChatListScreen(data: Bundle?) {
        openScreen(R.id.action_chat_list_to_chat_message, data)
    }

    override fun openChatMessageToBuyPoint(data: Bundle?) {
        openScreen(R.id.action_ChatMessage_to_BuyPoint, data)
    }

    override fun openChatMessageToEditTroubleSheet(data: Bundle?) {
        openScreen(R.id.action_ChatMessgae_to_TroubleSheet, data)
    }

    override fun openDetailUserToChatMessage(data: Bundle?) {
        openScreen(R.id.action_UserProfile_to_ChatMessage, data)
    }

    override fun openDetailUserToTroubleSheetUpdate(data: Bundle?) {
        openScreen(R.id.action_UserProfile_to_TroubleSheetUpdate, data)
    }

    override fun openToCalling(data: Bundle?) {
        openScreen(R.id.action_global_to_calling, data)
    }

    override fun openTroubleSheetToChatMessage(data: Bundle?) {
        openScreen(R.id.action_TroubleSheet_to_ChatMessage, data)
    }

    override fun openTroubleSheetUpdateToChatMessage(data: Bundle?) {
        openScreen(R.id.action_TroubleSheetUpdate_to_ChatMessage, data)
    }

    override fun openChatMessageToUserProfile(data: Bundle?) {
        openScreen(R.id.action_ChatMessgae_to_UserProfile, data)
    }

    override fun popopBackStackToDetination(destination: Int) {
        popopBackStack(destination)
    }

    override fun openWebBuyPointToBuyPointGoogle(data: Bundle?) {
        openScreen(R.id.action_webBuy_Point_to_buyPointGoogle, data)
    }

    override fun openActionToLogin(data: Bundle?) {
        openScreen(R.id.action_to_login, data)
    }

    override fun openReviewConsultant(data: Bundle) {
        openScreen(R.id.action_ChatMessage_to_ReviewConsultant, data)
    }

    override fun openMainToChatMessage(data: Bundle?) {
        openScreen(R.id.action_MainActivity_to_ChatMessage, data)
    }

    override fun openTopToChatMessage(bundle: Bundle?) {
        openScreen(R.id.action_Top_to_ChatMessage, bundle)
    }

    override fun openVerifyCodeHelpToInputEmail(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_InputEmailFragment, bundle)
    }

    override fun openWithdrawalFinish(bundle: Bundle?) {
        openScreen(R.id.action_withdrawalFragment_to_withdrawalFinishFragment, bundle)
    }

    override fun openSelectCategoryToTroubleSheet(data: Bundle?) {
        openScreen(R.id.action_selectCategoryFragment_to_troubleSheetFragment, data)
    }

    override fun openTroubleSheetToTopScreen(data: Bundle?) {
        openScreen(R.id.action_troubleSheetFragment_to_topFragment, data)
    }

    override fun openTroubleSheetToPartnerInfoScreen(data: Bundle?) {
        openScreen(R.id.action_troubleSheetFragment_to_partnerInfoFragment, data)
    }

    override fun openRegistrationToSelectCategoryScreen(data: Bundle?) {
        openScreen(R.id.action_registrationFragment_to_selectCategoryFragment, data)
    }

    override fun openTroubleSheetToTutorialScreen(bundle: Bundle?) {
        openScreen(R.id.action_troubleSheetFragment_to_tutorialFragment, bundle)
    }

    override fun openSearchLabScreen(bundle: Bundle?) {
        openScreen(R.id.action_to_search_lab, bundle)
    }

    override fun openSelectionCagoryFromSearch(bundle: Bundle?) {
        openScreen(R.id.action_searchLabFragment_to_selectionCategoryFragment, bundle)
    }

    override fun openLaboToNewQuestionScreen(bundle: Bundle?) {
        openScreen(R.id.action_laboFragment_to_newQuestionFragment, bundle)
    }

    override fun openNewQuestionToChooseTypeCategoryScreen(bundle: Bundle?) {
        openScreen(R.id.action_newQuestionFragment_to_chooseTypeCategoryFragment, bundle)
    }

    override fun openLabToLabDetailScreen(bundle: Bundle?) {
        openScreen(R.id.action_labFragment_to_labDetailFragment, bundle)
    }

    override fun openLabDetailToBuyPointScreen(bundle: Bundle?) {
        openScreen(R.id.action_labDetailFragment_to_buyPointFragment, bundle)
    }

    override fun openLabDetailToUserProfileScreen(bundle: Bundle?) {
        openScreen(R.id.action_labDetailFragment_to_userProfileFragment, bundle)
    }

    override fun openLabDetailToReportLaboScreen(bundle: Bundle?) {
        openScreen(R.id.action_labDetailFragment_to_reportLaboFragment, bundle)
    }

    override fun openHomeToLabDetailScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_LabDetailFragment, bundle)
    }

    override fun openGlobalToTroubleSheetUpdateFragment(bundle: Bundle?) {
        openScreen(R.id.action_global_troubleSheetUpdateFragment, bundle)
    }

    override fun openGlobalToSettingNotification(bundle: Bundle?) {
        openScreen(R.id.action_global_fragmentNotification, bundle)
    }

    override fun openPartnerInfoToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_partnerInfoFragment_to_topFragment, bundle)
    }

    override fun openPartnerInfoToTutorialScreen(bundle: Bundle?) {
        openScreen(R.id.action_partnerInfoFragment_to_tutorialFragment, bundle)
    }

    override fun openCallingToUpdateTroubleSheet(bundle: Bundle?) {
        openScreen(R.id.action_callingFragment_to_updateTroubleSheet, bundle)
    }

    override fun openCallingToChatMessage(bundle: Bundle?) {
        openScreen(R.id.action_callingFragment_to_ChatMessage, bundle)
    }

    override fun openCallingToBuyPoint(bundle: Bundle?) {
        openScreen(R.id.action_callingFragment_to_buyPointFragment, bundle)
    }
}
