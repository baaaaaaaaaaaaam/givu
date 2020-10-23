package com.login.donation.retrofit;

import com.login.donation.Object.groupmemberQuery;
import com.login.donation.Object.responseResult;
import com.login.donation.Object.response_campaign_detail_page;
import com.login.donation.Object.response_mypage_beneficiary_result;
import com.login.donation.Object.response_mypage_foundation_result;
import com.login.donation.Object.response_time_campaign_detail_page;
import com.login.donation.Object.userAccount;
import com.login.donation.Object.response_mypage_donate_result;
import com.login.donation.Object.volunteer_recruitment;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;


public interface UploadService {


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
    // LoginActivity.java에서 사용된다
    //  로그인 할 유저의 id, passwd, request 세개의 Map 타입을 전송할때 사용된다
    // 해당 요청에 대한 응답으로는 result , mode ,imagepath가 있다

    // createDialog.java에서 사용된다
    // 회원가입할 유저의 id와 passwd, mode , request 네가지를 전송 할때 사용된다
    // 해당 요청에 대한 응답으로는 result 뿐이다.

    @GET(" ")
    Call<userAccount> getLogin(@QueryMap Map<String, String> option
    );


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    //add_share_listDialog.java에서 사용된다
    // 자신의 id에 속한 그룹 멤버( 수혜자 ) 리스트를 불러오기 위함이다
    // 전달하는 데이터는 id 와 request 값 뿐이다
    // 해당 요청에 대한 응답값으로는 result와 queryResult 이다
    // queryResult 의 경우 응답값이 아래와 같은 형태 이다
    //,"queryResult":"[{\"memberid\":\"beneficiary\",\"imagepath\":\"default.jpg\"},{\"memberid\":\"beneficial1\",\"imagepath\":\"default.jpg\"}]

    @GET(" ")
    Call<groupmemberQuery> campaign_add_list_dialog_query(@QueryMap Map<String, String> option
    );

//    출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    //profileActivity에서 사용된다
    // 프로필을 등록할 때 이미지 ,id ,request 세가지 값을 전달한다.
    //해당요청은 따로 처리하지 않았다.

    @Multipart
    @POST(" ")
    Call<responseResult> profile_upload(@Part MultipartBody.Part file,
                                        @Part("request") RequestBody request,
                                        @Part("id") RequestBody id);


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

//    write_campaign 에서 사용된다
//    캠페인을 작성할때 자신의 아이로 하여 게시글을 등록한다
//    데이터중 share_list는 campaign_share_list table에 저장되고 , 나머지 데이터는 campaign table에저장된다.
//    요청에대한 응답으로는 ok 와 no 로 응답한다.

    @Multipart
    @POST(" ")
    Call<responseResult> campaign_upload(@Part MultipartBody.Part file,
                                         @Part("request") RequestBody request,
                                         @Part("id") RequestBody id,
                                         @Part("subject") RequestBody subject,
                                         @Part("share_list") RequestBody share_list,
                                         @Part("startDate") RequestBody startDay,
                                         @Part("endDate") RequestBody endDaty,
                                         @Part("content") RequestBody content);


// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

//    mainActivity에서 모든 캠페인 정보를 불러올때 사용한다.
//    전달하는 데이터는 request : getCampaign 이다
//    응답으로는 campaign 테이블의 정보 seq,writer,subject,imagePath,startDate,endDate,content,collection,list,doing 이다
//    list에는 이 기부에 참여한 donation_list에 있는 각 글의 donation 과 해당 이미지 정보를 받아온다.
//    ex ) {"seq":"2","writer":"admin","subject":"abc","imagePath":"1599060938992388290.jpg","startDate":"2020년9월8일","endDate":"2020년9월8일","content":"aaaaaa","collection":"0","list":"[{\"memberid\":\"user1\",\"imagepath\":\"1599062388335061147.jpg\"},{\"memberid\":\"user5\",\"imagepath\":\"default.jpg\"}]"}

    @GET(" ")
    Call<groupmemberQuery> campaign_list(@QueryMap Map<String, String> option);



// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    //campaign_datail_pageActivity.java에서 사용된다
    // 메인 캠페인 게시판에서 선택한 캠패인의 ( 수혜자 ) 리스트를 불러오기 위함이다
    // 전달하는 데이터는 seq 와 request 값 뿐이다
    // 해당 요청에 대한 응답값으로는 result와 queryResult 이다
    // queryResult 의 경우 응답값이 아래와 같은 형태 이다
    //,"queryResult":"[{\"memberid\":\"beneficiary\",\"imagepath\":\"default.jpg\"},{\"memberid\":\"beneficial1\",\"imagepath\":\"default.jpg\"}]

    @GET(" ")
    Call<response_campaign_detail_page> campaign_detail_page(@QueryMap Map<String, String> option
    );

//    출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//


    // 일반 기부자일때
    //mypage_DonationActivity.java에서 사용된다
    // 전달할 key : id  , reqeust 만있으면 전부 조회 가능
    // 해당 요청에 대한 응답값으로는 result와 계정 account , 기부 횟수 count,총 기부한 돈 , 내가 참여한 캠페인 (작성자 ,이미지 ,제목 ,기간) 이다



    @GET(" ")
    Call<response_mypage_donate_result> mypage_lookup_donate_list(@QueryMap Map<String, String> option
    );

//    출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    // 일반 기부자가
    //  마이페이지에서 충전하기 선택했을때 동작한다
    //  bootpayActivity.java에서 사용된다
    // 전달할 key : id  , reqeust , charge_money 를 전달한다.
    // 해당 요청에 대한 응답값으로는 result 이다.



    @GET(" ")
    Call<responseResult> charge_money(@QueryMap Map<String, String> option
    );

//    출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//


    //  마이페이지에서 충전하기 선택했을때 동작한다
    //  bootpayActivity.java에서 사용된다
    // 전달할 key : id  , reqeust , charge_money 를 전달한다.
    // 해당 요청에 대한 응답값으로는 result 이다.



    @GET(" ")
    Call<response_mypage_foundation_result> mypage_lookup_foundation(@QueryMap Map<String, String> option
    );

//    출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//





    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    // 기부자로 로그인하여
    //  기부하기 버튼을 눌럿을떄 동작한다
    //  campaign_detail_pageActivity.java에서 사용된다
    // 전달할 key :  reqeust,seq,id, money 를 전달한다.
    // 해당 요청에 대한 응답값으로는 result 이다.



    @GET(" ")
    Call<responseResult> give_donate(@QueryMap Map<String, String> option
    );



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//


    //  기부 단체로 로그인하여 종료하기 버튼을 눌렀을때 사용된다
    //  campaign_detail_pageActivity.java에서 사용된다
    // 전달할 key :  reqeust,seq,id
    // 해당 요청에 대한 응답값으로는 result 이다.



    @GET(" ")
    Call<responseResult> end_signal_campaign (@QueryMap Map<String, String> option
    );


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    //  수혜자의 마이페이지에서 사용됨


    @GET(" ")
    Call<response_mypage_beneficiary_result> mypage_lookup_beneficiary (@QueryMap Map<String, String> option
    );


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    //  수혜자의 마이페이지에서 사용됨

    @GET(" ")
    Call<groupmemberQuery> mypage_beneficiary_lookup_foundation (@QueryMap Map<String, String> option
    );



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//




    //  수혜자 마이페이지에서 기부단체에 자신의 정보를 등록할떄 사용된다
    // request 와 id만 보내고 정상 처리됫는지 안됫는지만 받아올예정이다


    @GET(" ")
    Call<responseResult> mypage_beneficiary_singup_foundation (@QueryMap Map<String, String> option
    );



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    //  수혜자 마이페이지에서 등록된 기부단체를 삭제할 때 사용된다 .
    // request 와 id만 보내고 정상 처리됫는지 안됫는지만 받아올예정이다
    @GET(" ")
    Call<responseResult> mypage_beneficiary_remove_foundation (@QueryMap Map<String, String> option
    );



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



//   write_volunteer_recruitment에서 사용된다
//    기부단체가 자신의 아이디로 봉사활동을 개시할수 있다.
//    요청에대한 응답으로는 ok 와 no 로 응답한다.

    @Multipart
    @POST(" ")
    Call<responseResult> write_volunteer_recruitment(@Part MultipartBody.Part file,
                                         @Part("request") RequestBody request,
                                         @Part("id") RequestBody id,
                                         @Part("subject") RequestBody subject,
                                         @Part("startDate") RequestBody startDay,
                                         @Part("endDate") RequestBody endDaty,
                                          @Part("startTime") RequestBody startTime,
                                          @Part("endTime") RequestBody endTime,
                                          @Part("location") RequestBody location,
                                         @Part("content") RequestBody content);
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    //자원봉사자 모집 페이지에서 전체 모집글중 상위 3개만 받아온다 ( 페이징 처리함 )
    // result 요청은 ok / no , queryResult 응답에는 자원봉사 모집 내용데이터가 3개씩 들어있다
    @GET(" ")
    Call<groupmemberQuery> volunteer_recuitment (@QueryMap Map<String, String> option
    );



    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    //자원봉사자 모집 상세 페이지
    // result 요청은 ok / no , queryResult 응답에는 선택한 모집 내용이 들어가있다.
    @GET(" ")
    Call<groupmemberQuery> volunteer_recruitment_detail_page (@QueryMap Map<String, String> option
    );





//   write_volunteer_recruitment에서 사용된다
//    기부단체가 자신의 아이디로 봉사활동을 개시할수 있다.
//    요청에대한 응답으로는 ok 와 no 로 응답한다.

    @Multipart
    @POST(" ")
    Call<responseResult> modified_volunteer_recruitment(@Part MultipartBody.Part file,
                                                     @Part("request") RequestBody request, @Part("seq") RequestBody seq,
                                                     @Part("id") RequestBody id,
                                                     @Part("subject") RequestBody subject,
                                                     @Part("startDate") RequestBody startDay,
                                                     @Part("endDate") RequestBody endDaty,
                                                     @Part("startTime") RequestBody startTime,
                                                     @Part("endTime") RequestBody endTime,
                                                     @Part("location") RequestBody location,
                                                     @Part("content") RequestBody content);


    //자원봉사자 모집 수정 페이지 에서 이미지를 변경하지않고 update할 경우 처리하는 부분이다
    //이미 서버로부터받아로 각 값들이 세팅되어있기때문에 다른 데이터는 수정하지않아도 문제가 되지않지만 , 이미지의경우 uri 를 받아 처리하는 부분으로 짜여있기때문에
    // 이미지를 변경하지않고 업데이트를 하면 에러가 발생한다.
    //때문에 이미지를 변경하지않는경우 이미지는 기존의 값을 그대로 사용한다
    @GET(" ")
    Call<responseResult> not_image_modified_volunteer_recruitment (@QueryMap Map<String, String> option
    );




//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    // 상세 페이지에서 글쓴이가삭제를 할 경우
@GET(" ")
Call<responseResult> delete_volunteer_recruitment (@QueryMap Map<String, String> option
);


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

    // 상세 페이지에서 글쓴이가삭제를 할 경우
    @GET(" ")
    Call<responseResult> volunteer_time (@QueryMap Map<String, String> option
    );



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//


    // 상세 페이지에서 글쓴이가삭제를 할 경우
    @GET(" ")
    Call<groupmemberQuery> write_time_campaign_signupActivity (@QueryMap Map<String, String> option
    );



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//


//    write_time_campaign 에서 사용된다
//    시간 캠페인을 작성할때 자신의 아이로 하여 게시글을 등록한다
//    데이터중 share_list는 time_campaign_share_list table에 저장되고 , 나머지 데이터는 time_campaign table에저장된다.
//    요청에대한 응답으로는 ok 와 no 로 응답한다.

    @Multipart
    @POST(" ")
    Call<responseResult> time_campaign_upload(@Part MultipartBody.Part file,
                                         @Part("request") RequestBody request,
                                         @Part("id") RequestBody id,
                                         @Part("subject") RequestBody subject,
                                              @Part("money") RequestBody money,
                                              @Part("goal_time") RequestBody goal_time,
                                              @Part("donation") RequestBody donation,
                                         @Part("share_list") RequestBody share_list,
                                         @Part("startDate") RequestBody startDay,
                                         @Part("endDate") RequestBody endDaty,
                                         @Part("content") RequestBody content);



    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//


    //    time_campaignActivity 게시글을 불러올때 사용된다.
//    시간 캠페인을 작성할때 자신의 아이로 하여 게시글을 등록한다
    @GET(" ")
    Call<groupmemberQuery> time_campaign_list(@QueryMap Map<String, String> option);



//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    // 캠페인 상세 페이지에서  목적 request와 seq를 넘겨주면 결과 , 캠페인정보,나눔대상자 정보를 받을 수 있다.
    @GET(" ")
    Call<response_time_campaign_detail_page> get_time_campaign_detail(@QueryMap Map<String, String> option);


//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//



    // 단순 결과 응답
    @GET(" ")
    Call<responseResult> simple_result(@QueryMap Map<String, String> option);


//    @Multipart
//    @POST(" ")
//    Call<ResponseBody> campaignupload(@Part MultipartBody.Part file,
//                                        @FieldMap Map<String, Object> parameters);

// @POST("/retrofit.php") 는 해당 php 파일 주소 , URL 은 MyRetrofit2.java에 따로 저장된다.
//
//    @FormUrlEncoded
//    @POST(" ")
//    Call<ResponseBody> campaignRegister(@FieldMap Map<String, Object> parameters);
//



}

