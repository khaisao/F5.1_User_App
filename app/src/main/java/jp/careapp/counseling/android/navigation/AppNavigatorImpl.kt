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

    override fun openLoginEmailScreen(bundle: Bundle?) {
        openScreen(R.id.action_inputEmailFragment_self, bundle)
    }

    override fun openInputEmailToVerifyCode(bundle: Bundle?) {
        openScreen(R.id.action_inputEmailFragment_to_verify_code_navigation, bundle)
    }

    override fun openVerifyCodeToVerifyCodeHelpScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCodeFragment_to_verificationCodeHelpFragment, bundle)
    }

    override fun openVerifyCodeToRegistrationScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCodeFragment_to_registrationFragment, bundle)
    }

    override fun openVerifyCodeToTopScreen(bundle: Bundle?) {
        openScreen(R.id.action_verifyCode_to_TopFragment, bundle)
    }

    override fun openTopToBuyPointScreen(bundle: Bundle?) {
        openScreen(R.id.action_TopFragment_to_BuyPointFragment, bundle)
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

    override fun openMyPageToNews(bundle: Bundle?) {
        openScreen(R.id.action_tabMyPage_to_newsFragment, bundle)
    }

    override fun openMyPageToEditProfile(bundle: Bundle?) {
        openScreen(R.id.action_topFragment_to_fragmentEditProfile, bundle)
    }

    override fun openHomeToBannerFirstTimeUseScreen(bundle: Bundle?) {
        openScreen(R.id.action_homeFragment_to_BannerFirstTimeUseFragment, bundle)
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

    override fun openUserProfileToReportScreen(bundle: Bundle?) {
        openScreen(R.id.action_UserProfile_to_ReportUser, bundle)
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

    override fun openDetailUserToChatMessage(data: Bundle?) {
        openScreen(R.id.action_UserProfile_to_ChatMessage, data)
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

    override fun openWithdrawalFinish(bundle: Bundle?) {
        openScreen(R.id.action_withdrawalFragment_to_withdrawalFinishFragment, bundle)
    }

    override fun openRMEnterNameToRmTop(bundle: Bundle?) {
        openScreen(R.id.action_rmStartFragment_to_rmTopFragment, bundle)
    }

    override fun openSplashToRMStart(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_rmStartFragment, bundle)
    }

    override fun openRMStartToRMEnterName(bundle: Bundle?) {
        openScreen(R.id.action_rmStartFragment_to_rmEnterNameFragment, bundle)
    }

    override fun openRMMyMenuToSettingPush(bundle: Bundle?) {
        openScreen(R.id.action_rmTopFragment_to_RMSettingPushFragment, bundle)
    }

    override fun openRMTopToRMSettingNickName(bundle: Bundle?) {
        openScreen(R.id.action_rmTopFragment_to_rmSettingNickNameFragment, bundle)
    }

    override fun openRMTopToRMSettingProfileMessage(bundle: Bundle?) {
        openScreen(R.id.action_rmTopFragment_to_rmSettingProfileMessageFragment, bundle)
    }

    override fun openRMTopToRMUserDetail(bundle: Bundle?) {
        openScreen(R.id.action_rmTopFragment_to_rmUserDetailFragment, bundle)
    }

    override fun openRMUserDetailToRMLivestream(bundle: Bundle?) {
        openScreen(R.id.action_RMUserDetailFragment_to_RMLiveStreamFragment, bundle)
    }

    override fun openRMUserDetailMessageToRMLivestream(bundle: Bundle?) {
        openScreen(R.id.action_userDetailMsgFragment_to_RMLiveStreamFragment, bundle)
    }

    override fun openSplashToRMTop(bundle: Bundle?) {
        openScreen(R.id.action_splashFragment_to_rmTopFragment, bundle)
    }

    override fun openRMTopToRMBLockList(bundle: Bundle?) {
        openScreen(R.id.action_rmTopFragment_to_rmBlockListFragment, bundle)
    }

    override fun openRMUserDetailToRMUserDetailReport(bundle: Bundle?) {
        openScreen(R.id.action_RMUserDetailFragment_to_RMUserDetailReportFragment, bundle)
    }

    override fun openRMMyMenuToRMSettingContact(bundle: Bundle?) {
        openScreen(R.id.action_rmTopFragment_to_RMSettingContactFragment, bundle)
    }

    override fun openRMSettingContactToRMSettingContactFinish(bundle: Bundle?) {
        openScreen(R.id.action_RMSettingContactFragment_to_RMSettingContactFinishFragment, bundle)
    }

    override fun openTabRMMyMenuToRMStart(bundle: Bundle?) {
        openScreen(R.id.actionToRMStart, bundle)
    }

    override fun openRMUserDetailToUserDetailMsg(bundle: Bundle?) {
        openScreen(R.id.action_RMUserDetailFragment_to_userDetailMsgFragment, bundle)
    }

    override fun openUserDetailMsgToRMUserDetailReport(bundle: Bundle?) {
        openScreen(R.id.action_userDetailMsgFragment_to_RMUserDetailReportFragment, bundle)
    }

    override fun openUserDetailMessageToRMTop(bundle: Bundle?) {
        openScreen(R.id.action_UserDetailMessageFragment_to_RMTopFragment, bundle)
    }

    override fun openRMTopToRMBuyPoint(bundle: Bundle?) {
        openScreen(R.id.action_RMTopFragment_to_RMBuyPointFragment, bundle)
    }

    override fun openMyPageToUsePointsGuide(bundle: Bundle?) {
        openScreen(R.id.action_topFragment_to_usePointsGuideFragment, bundle)
    }

    override fun openEditProfileToEditNickName(bundle: Bundle?) {
        openScreen(R.id.action_fragmentEditProfile_to_editNickNameFragment, bundle)
    }

    override fun openMyPageToBlocked(bundle: Bundle?) {
        openScreen(R.id.action_topFragment_to_blockedFragment, bundle)
    }

    override fun openSettingNotification(bundle: Bundle?) {
        openScreen(R.id.action_global_fragmentNotification, bundle)
    }

    override fun openContactUsToContactUsConfirm(bundle: Bundle?) {
        openScreen(R.id.action_contactUsFragment_to_contactUsConfirmFragment, bundle)
    }

    override fun openContactUsConfirmToContactUsFinish(bundle: Bundle?) {
        openScreen(R.id.action_contactUsConfirmFragment_to_contactUsFinishFragment, bundle)
    }

    override fun openEditMailToVerifyCode(bundle: Bundle?) {
        openScreen(R.id.action_editMailFragment_to_verify_code_navigation, bundle)
    }

    override fun openMyPageToFAQ(bundle: Bundle?) {
        openScreen(R.id.action_topFragment_to_FAQFragment, bundle)
    }

    override fun openFAQToWithdrawal(bundle: Bundle?) {
        openScreen(R.id.action_FAQFragment_to_withdrawalFragment, bundle)
    }

    override fun openRMTermOfService(bundle: Bundle?) {
        openScreen(R.id.action_global_RMTermsOfServiceFragment, bundle)
    }

    override fun openRMPrivacyPolicy(bundle: Bundle?) {
        openScreen(R.id.action_global_RMPrivacyPolicyFragment, bundle)
    }

    override fun openLiveStreamToExitLiveStream(bundle: Bundle?) {
        openScreen(R.id.action_liveStreamFragment_to_exitLiveStreamFragment, bundle)
    }

    override fun openExitLiveStreamToMessage(bundle: Bundle?) {
        openScreen(R.id.action_exitLivestreamFragment_to_chatMessageFragment, bundle)
    }

    override fun openUserDetailToLiveStream(bundle: Bundle?) {
        openScreen(R.id.action_UserProfile_to_LiveStreamFragment, bundle)
    }

    override fun openTermsOfService(bundle: Bundle?) {
        openScreen(R.id.action_global_termsOfServiceFragment, bundle)
    }

    override fun openPrivacyPolicy(bundle: Bundle?) {
        openScreen(R.id.action_global_privacyPolicyFragment, bundle)
    }

    override fun openContactUs(bundle: Bundle?) {
        openScreen(R.id.action_global_contactUsFragment, bundle)
    }

    override fun openLiveStreamBuyPointCredit(bundle: Bundle?) {
        openScreen(R.id.action_liveStreamFragment_to_buyPointCreditFragment, bundle)
    }

    override fun openEditMail(bundle: Bundle?) {
        openScreen(R.id.action_global_edit_mail_navigation, bundle)
    }

    override fun openVerifyCodeHelpToVerifyCode(bundle: Bundle?) {
        openScreen(R.id.action_verificationCodeHelpFragment_to_verifyCodeFragment, bundle)
    }

    override fun openExitLiveStreamToUserDetailFragment(bundle: Bundle?) {
        openScreen(R.id.action_exitLivestreamFragment_to_userProfileFragment, bundle)
    }
}
