package jp.slapp.android.android.network

import jp.slapp.android.android.data.model.message.*
import jp.slapp.android.android.data.network.*
import jp.slapp.android.android.model.network.*
import jp.slapp.android.android.ui.review_mode.enterName.model.RMSetNickNameResponse
import retrofit2.http.*

interface RMApiInterface {

    @GET("api/member/websocket-token")
    suspend fun getWebSocketToken(): ApiObjectResponse<WebsocketResponse>

    @FormUrlEncoded
    @POST("api/member/memapp-reviewer")
    suspend fun setNickNameRM(
        @Field("name") name: String,
        @Field("sex") sex: Int = 3,
        @Field("birth") birth: String = "1900-01-01",
        @Field("push_newsletter") pushNewsLetter: Int = 0,
        @Field("push_mail") pushMail: Int = 0,
        @Field("push_online") pushOnline: Int = 0,
        @Field("push_counseling") pushCounseling: Int = 0
    ): ApiObjectResponse<RMSetNickNameResponse>

    @FormUrlEncoded
    @POST("api/login")
    suspend fun loginRM(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("device_name") deviceName: String
    ): ApiObjectResponse<RMLoginResponse>

    @GET("api/member")
    suspend fun getMember(): ApiObjectResponse<MemberResponse>

    @GET("api/memapp-dummy-performers")
    suspend fun getDummyPerformers(
        @QueryMap params: HashMap<String, Any>
    ): ApiObjectResponse<List<RMPerformerResponse>>

    @GET("api/performers/{performer_code}")
    suspend fun loadUserDetail(@Path("performer_code") userCode: String): ApiObjectResponse<RMUserDetailResponse>

    @FormUrlEncoded
    @POST("api/member/favorites")
    suspend fun addFavoriteUser(@Field("performer_code") userCode: String): ApiObjectResponse<Any>

    @DELETE("/api/member/favorites/{performer_code}")
    suspend fun deleteFavoriteUser(@Path("performer_code") userCode: String): ApiObjectResponse<Any>

    @FormUrlEncoded
    @POST("/api/member/blocks")
    suspend fun blockUser(@Field("performer_code") userCode: String): ApiObjectResponse<Any>

    @GET("/api/member/favorites")
    suspend fun getFavorites(): ApiObjectResponse<Any>

    @GET("/api/member/blocks")
    suspend fun getBlockList(): ApiObjectResponse<Any>

    @DELETE("/api/member/blocks/{code}")
    suspend fun deleteBlock(@Path("code") code: String): ApiObjectResponse<Any>

    @FormUrlEncoded
    @PATCH("/api/member/trouble-sheet")
    suspend fun saveProfileMessage(
        @Field("content") content: String,
        @Field("response") response: Int = 1,
        @Field("reply") reply: Int = 1
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @PATCH("/api/member")
    suspend fun updateNickName(
        @Field("name") name: String
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @PATCH("/api/member")
    suspend fun updateSettingPush(
        @Field("push_mail") pushMail: Int
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @POST("/api/member/contact")
    suspend fun sendContact(
        @Field("category") category: String,
        @Field("content") content: String,
        @Field("reply") reply: Int,
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @POST("/api/member/withdrawal")
    suspend fun withdrawal(
        @Field("reason") reason: String
    ): ApiObjectResponse<Any>

    @FormUrlEncoded
    @POST("/api/performers/{performer_code}/report")
    suspend fun sendReport(
        @Path("performer_code") userCode: String,
        @Field("content") content: String
    ): ApiObjectResponse<Any>

    @GET("api/member/mails/in-out")
    suspend fun loadMessage(@QueryMap data: MutableMap<String, Any>): ApiObjectResponse<List<RMMessageResponse>>

    @GET("api/performers/{id}")
    suspend fun getUserProfileDetail(
        @Path("id") code: String
    ): ApiObjectResponse<RMConsultantResponse>

    @POST("api/member/mails")
    suspend fun sendMessage(@Body messageRequest: RMMessageRequest): ApiObjectResponse<SendMessageResponse>
}