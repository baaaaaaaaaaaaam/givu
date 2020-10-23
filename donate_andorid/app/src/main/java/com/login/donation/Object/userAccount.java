package com.login.donation.Object;

public class userAccount {

    //  loginActivity에서 사용되는 클래스이다.
    //  Retrofit2 로 로그인 요청을 하면 서버에서 응답받은 데이터(response.body())를 userAccount의 객체로 받을 수 있다
    //  loginActivity에서는 mode , imagepath ,result 값만 사용한다


    // createIdDialog에서 사용되는 클래스이다
    //  Retrofit2 로 로그인 요청을 하면 서버에서 응답받은 데이터(response.body())를 userAccount의 객체로 받을 수 있다
    //  createIdDialog에서는 result 값만 사용된다.


    public String request;  // request는 createid , login 두가지가 있다
    public String id;
    public String pwd;
    public String imagepath;
    public String account;
    public String mode;
    public String result;
    public userAccount(String id, String pwd) {
        this.id=id;
        this.pwd=pwd;
    }
}
