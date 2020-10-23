package com.login.donation.Object;




//  add_share_listDialog,mypage_Beneficiary_signupActivity (기부단체 받아오기) 에서  사용되는 클래스이다.
//   나눔 대상에 추가할 멤버를 선택하는 다이얼로그에서 사용된다
//  Retrofit2 로 그룹에 속한 멤버(수혜자)를 조회 하면 서버에서 응답받은 데이터(response.body())를 groupmemberQuery 객체로 받을 수 있다
//  응답 값은 아래와같으며 key "result" 는 value "ok"가 들어있다
//  key queryResult 는 value 가 json 형태이다.
// 이값을 다시 Gson으로 파싱 해야 한다.
//   {"result":"ok","queryResult":"[{\"memberid\":\"beneficiary\",\"imagepath\":\"default.jpg\"},{\"memberid\":\"beneficial1\",\"imagepath\":\"default.jpg\"}




public class groupmemberQuery {

    String queryResult;
    String result;

    public groupmemberQuery(String queryResult, String result) {
        this.queryResult = queryResult;
        this.result = result;
    }

    public String getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(String queryResult) {
        this.queryResult = queryResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
