package jp.slapp.android.android.network

import com.google.gson.JsonObject
import jp.slapp.android.android.data.model.*
import jp.slapp.android.android.data.model.history_chat.HistoryChatResponse
import jp.slapp.android.android.data.model.message.FreeTemplateRequest
import jp.slapp.android.android.data.model.message.MessageRequest
import jp.slapp.android.android.data.model.message.MessageResponse
import jp.slapp.android.android.data.model.message.SendMessageResponse
import jp.slapp.android.android.data.model.user_profile.TroubleSheetRequest
import jp.slapp.android.android.data.network.*
import jp.slapp.android.android.model.user_profile.MailInfoOfUser
import jp.slapp.android.android.model.user_profile.ReviewUserProfile
import jp.slapp.android.android.utils.SocketInfo
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiInterface {

    @GET("api/member")
    suspend fun getMember(): ApiObjectResponse<MemberResponse>

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_name") deviceName: String
    ): ApiObjectResponse<LoginResponse>

    @FormUrlEncoded
    @POST("api/mailaddress-auth")
    suspend fun sendEmail(@Field("email") email: String): ApiObjectResponse<Object>

    @FormUrlEncoded
    @POST("api/mailaddress-auth/verify")
    suspend fun sendVerifyCode(
        @Field("email") email: String,
        @Field("auth_code") authCode: String
    ): ApiObjectResponse<InfoUserResponse>

    @GET("api/member/favorites")
    suspend fun getMemberFavorite(): JsonObject

    @GET("api/member/blocks")
    suspend fun getMemberBlocked(): JsonObject

    @DELETE("api/member/favorites/{code}")
    suspend fun deleteFavorite(
        @Path("code") code: String = ""
    ): ApiObjectResponse<Any>

    @DELETE("api/member/blocks/{code}")
    suspend fun deleteBlocked(
        @Path("code") code: String = ""
    ): ApiObjectResponse<Any>

    @POST("api/member")
    suspend fun registerWithEmail(@Body registrationRequest: InfoRegistrationWithEmailRequest): ApiObjectResponse<RegistrationWithEmailResponse>

    @POST("api/member/normal-without-email")
    suspend fun registerWithoutEmail(@Body registrationRequest: InfoRegistrationWithoutEmailRequest): ApiObjectResponse<RegistrationWithoutEmailResponse>

    @PATCH("api/member")
    suspend fun updateProfile(
        @Body() updateMember: ParamsUpdateMember
    ): ApiObjectResponse<Object>

    @GET("api/performers/{id}")
    suspend fun getUserProfileDetail(
        @Path("id") code: String
    ): ApiObjectResponse<ConsultantResponse>

    @GET("api/performers/{id}/gallery-images")
    suspend fun getUserGallery(
        @Path("id") code: String
    ): ApiObjectResponse<List<GalleryResponse>>

    @PATCH("api/member")
    suspend fun updateNotification(
        @Body() updateNotificationParams: UpdateNotificationParams
    ): ApiObjectResponse<Any>

    @GET("api/performers/{code}/reviews")
    suspend fun getReviewUserProfile(
        @Path("code") code: String
    ): ApiObjectResponse<List<ReviewUserProfile>>

    @FormUrlEncoded
    @POST("api/member/favorites")
    suspend fun addUserToFavorite(@Field("performer_code") code: String): ApiObjectResponse<Object>

    @DELETE("api/member/favorites/{code}")
    suspend fun removeUserToFavorite(@Path("code") code: String): ApiObjectResponse<Any>

    @GET("api/member/mails/out")
    suspend fun getEmailSentByMember(@Query("performer_code") code: String): ApiObjectResponse<List<MailInfoOfUser>>

    @POST("api/member/contact")
    suspend fun sendContactWithoutMail(@Body contactRequestWithoutMail: ContactRequestWithoutMail): ApiObjectResponse<Any>

    @POST("api/contact")
    suspend fun sendContactWithMail(@Body contactRequestWithMail: ContactRequestWithMail): ApiObjectResponse<Any>

    @GET("api/performers/count")
    suspend fun getTotalNumberConsultant(@QueryMap params: MutableMap<String, Any>): ApiObjectResponse<NumberNotificationResponse>

    @GET("api/performers/count")
    suspend fun getTotalNumberConsultantSearch(
        @Query("presence_status") statusConsultant: Int?,
        @Query("name") nameConsultant: String?,
        @Query("review_total_number") haveNumberReview: Int?,
        @Query("sex") genderCondition: Int?,
        @Query("genres_select_condition") genresSelectCondition: String?,
        @Query("genres[]", encoded = true) listGenresSelected: ArrayList<Int>?,
        @Query("stages[]", encoded = true) listRankingSelected: ArrayList<Int>?,
        @Query("review_average") reviewAverageCondition: Int?
    ): ApiObjectResponse<NumberNotificationResponse>

    @GET("api/performers")
    suspend fun getListConsultant(@QueryMap params: MutableMap<String, Any>): ApiObjectResponse<ArrayList<ConsultantResponse>>

    @GET("api/chat-performers/online")
    suspend fun getListConsultantOnline(): ApiObjectResponse<ConsultantOnlineResponse>

    @GET("api/chat-performers/wait")
    suspend fun getListConsultantWait(): ApiObjectResponse<ArrayList<ConsultantResponse>>

    @GET("api/chat-performers/offline")
    suspend fun getListConsultantOffline(@QueryMap params: MutableMap<String, Any>): ApiObjectResponse<ArrayList<ConsultantResponse>>

    @GET("api/member/watch-latest-histories")
    suspend fun getPerformerHaveSeen(@QueryMap params: MutableMap<String, Any>): ApiObjectResponse<ArrayList<HistoryResponse>>

    @GET("api/performers")
    suspend fun getListConsultantSearch(
        @Query("presence_status") statusConsultant: Int?,
        @Query("name") nameConsultant: String?,
        @Query("review_total_number") haveNumberReview: Int?,
        @Query("sex") genderCondition: Int?,
        @Query("genres_select_condition") genresSelectCondition: String?,
        @Query("genres[]", encoded = true) listGenresSelected: ArrayList<Int>?,
        @Query("stages[]", encoded = true) listRankingSelected: ArrayList<Int>?,
        @Query("review_average") reviewAverageCondition: Int?,
        @Query("sort") sort: String?,
        @Query("order") order: String?,
        @Query("sort2") sort2: String?,
        @Query("order2") order2: String?,
        @Query("limit") limit: Int?,
        @Query("page") page: Int?
    ): ApiObjectResponse<ArrayList<ConsultantResponse>>

    @GET("api/member/blocks")
    suspend fun getListBlockedConsultant(): ApiObjectResponse<ArrayList<BlockedConsultantResponse>>

    @GET("api/news/count")
    suspend fun getNumberNotification(@QueryMap params: MutableMap<String, Any>): ApiObjectResponse<NumberNotificationResponse>

    @FormUrlEncoded
    @POST("api/member/reregist")
    suspend fun reRegister(
        @Field("email") email: String,
        @Field("member_code") authCode: String
    ): ApiObjectResponse<Object>

    @FormUrlEncoded
    @POST("api/member/mailaddress-change")
    suspend fun updateEmail(@Field("email") email: String): ApiObjectResponse<Object>

    @FormUrlEncoded
    @POST("api/member/mailaddress-change/verify")
    suspend fun sendVerifyCodeAfterEditEmail(
        @Field("email") email: String,
        @Field("auth_code") authCode: String
    ): ApiObjectResponse<Object>

    @PATCH("api/member/trouble-sheet")
    suspend fun sendTroubleSheet(@Body troubleSheetRequest: TroubleSheetRequest): ApiObjectResponse<Any>

    @FormUrlEncoded
    @POST("api/member/blocks")
    suspend fun handleClickblock(
        @Field("performer_code") performerCode: String
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @POST("api/performers/{performer_code}/report")
    suspend fun sendReport(
        @Path("performer_code") code: String,
        @Field("content") content: String
    ): ApiObjectResponse<Any>

    @GET("api/news")
    suspend fun getNotices(): JsonObject

    @FormUrlEncoded
    @PATCH("api/member/news-last-view")
    suspend fun updateNoticeLast(
        @Field("news_last_view_datetime") lastTime: String
    ): ApiObjectResponse<Any>

    @GET("api/news/{id}")
    suspend fun getDetailNotice(
        @Path("id") id: Int = 0
    ): ApiObjectResponse<NewsResponse>

    @POST
    suspend fun checkBuyPoit(
        @Url url: String,
        @Body body: RequestBody
    ): String

    @FormUrlEncoded
    @POST("api/member/withdrawal")
    suspend fun membershipWithdrawal(
        @Field("reason") reason: String = ""
    ): ApiObjectResponse<Any>

    @GET("api/performer-rankings")
    suspend fun getListRanking(
        @QueryMap params: MutableMap<String, Any>
    ): ApiObjectResponse<ArrayList<TypeRankingResponse>>

    @GET("api/performer-recommend-rankings")
    suspend fun getListRecommendRanking(
        @QueryMap params: MutableMap<String, Any>
    ): ApiObjectResponse<ArrayList<TypeRankingResponse>>

    @GET("api/member/mails/latest")
    suspend fun getListHistoryMessage(@QueryMap data: MutableMap<String, Any>): ApiObjectResponse<List<HistoryChatResponse>>

    @GET("api/member/mails/in-out")
    suspend fun loadMessage(@QueryMap data: MutableMap<String, Any>): ApiObjectResponse<List<MessageResponse>>

    @GET("/api/member/mails/out")
    suspend fun loadTransmissionMessage(@QueryMap data: MutableMap<String, Any>): ApiObjectResponse<List<MessageResponse>>

    @POST("api/member/mails/{id}/pay-mail-open")
    suspend fun openPayMessage(@Path("id") code: String): ApiObjectResponse<SendMessageResponse>

    @GET("api/member/websocket-token")
    suspend fun getWebSocketToken(): ApiObjectResponse<WebsocketResponse>

    @GET("api/member/mails/unread-count")
    suspend fun getUnreadCount(): ApiObjectResponse<UnreadCountResponse>

    @FormUrlEncoded
    @POST("api/member/device-token")
    suspend fun registerDeviceToken(
        @Field("os") os: String,
        @Field("token") content: String,
        @Field("platform_app_id") platformAppId: Int
    ): ApiObjectResponse<Any>

    @POST("api/member/mails")
    suspend fun sendMessage(@Body messageRequest: MessageRequest): ApiObjectResponse<SendMessageResponse>

    @POST("api/member/mails/first-notice")
    suspend fun sendFirstMessage(@Body messageRequest: MessageRequest): ApiObjectResponse<SendMessageResponse>

    @FormUrlEncoded
    @POST("api/member/reviews/post")
    suspend fun submitReview(
        @Field("performer_code") performer_code: String = "",
        @Field("point") point: Int = 0,
        @Field("review") review: String = ""
    ): ApiObjectResponse<Any>

    @GET("api/trouble-genres")
    suspend fun getListCategory(): ApiObjectResponse<List<CategoryResponse>>

    @GET("api/free-templates")
    suspend fun getListTemplate(): ApiObjectResponse<List<FreeTemplateResponse>>

    @POST("api/member/mails/free-template")
    suspend fun sendFreeTemplate(@Body freeTemplateRequest: FreeTemplateRequest): ApiObjectResponse<SendMessageResponse>

    @FormUrlEncoded
    @PATCH("api/member/trouble-genre")
    suspend fun updateGenre(@Field("genre") genre: Int): ApiObjectResponse<Any>

    @GET("/api/prices")
    suspend fun getCreditPrices(
        @Query("kind") kind: String = "credit",
        @Query("first_buy_credit") firstBuyCredit: Boolean,
    ): ApiObjectResponse<List<CreditItem>>

    @FormUrlEncoded
    @POST("/api/member/account-delete")
    suspend fun deleteAccount(
        @Field("reason") reason: String = ""
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @PATCH("api/member/trouble-partner")
    suspend fun updatePartnerInfo(
        @FieldMap params: MutableMap<String, Any>
    ): ApiObjectResponse<Any>

    @GET
    suspend fun getConfigCall(
        @Url url: String = SocketInfo.URL_GET_CALL_CONFIG
    ): ConfigCallResponse

    @GET
    suspend fun fssMemberAppAuth(
        @Url url: String
    ): FssMemberAuthResponse

    @GET("api/app-banners")
    suspend fun getListBanner(): ApiObjectResponse<List<BannerResponse>>

    @GET("api/app-mode")
    suspend fun getAppMode(): ApiObjectResponse<AppModeResponse>

    @GET("api/app-version")
    suspend fun getAppVersion(): ApiObjectResponse<AppVersionResponse>
}
