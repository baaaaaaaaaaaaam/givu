< -- 수정 중 -- >
 
 # 개인 포트폴리오 -1
 
 
 # 서비스 소개
 
 donate_android ,donate_application_golang, donate_application_hyperledger,donate_web은 하나의 프로젝트입니다.
 
이 프로젝트는 코로나가 발병한 2020년 3~4월 대기업과 유명연예인 , 일반 시민들이 코로나 지원금 기부를 약 1000억원 가까이 하였는데 , 대구 현장에 코로나 지원을 나간 간호사가 인스타에 현장 지원이 형편없다는 글과 컵라면을 찍어올린 사진을보고 관심을 가지게 되었다.

매년 기부를 하는 일반 시민이 줄어들고 있는 가운데 가장 큰 이유는 기부단체에대한 불신이다. 기부단체는 법의 규제하에서 투명하게 관리하게 노력하고있지만 ,  기부단체의 기부금 오남용/횡령 사건 ( 정의연, 새희망씨앗 ..) , 수혜자의 오남용 ( 어금니 아빠 ) 등 문제들이 계속해서 발생하여 일반 시민의 기부는 점점 줄어들고있는 추세이다.

![기부안하는이유](https://user-images.githubusercontent.com/57000871/96362916-cd642f80-116b-11eb-8ae6-4cd167be5a16.jpg)
![기부안하는이유1](https://user-images.githubusercontent.com/57000871/96362932-eec51b80-116b-11eb-8043-ae46288b0947.jpg)
<출처 연합뉴스>









이런 상황에서 기부금을 투명하게 모금하고 사용하겠다는 취지로 블록체인을 활용한 기부 서비스들이 생겨나고있다.

나 또한 그런 생각으로 이런 프로젝트를 하였다.
이 프로젝트는 사용자가 안드로이드 모바일을 사용하여 돈을 충전하고 기부한 내역과 수혜자에게 실제 나눔되는 내역은 블록체인에 저장하여 사용내역을 수정하거나 변경할수 없게하고 , 투명하게 공개하는데 초점을 두었다.



# 기능
1. 회원가입/로그인
2. 프로필 등록
3. 게시글 작성 ( 일반 모금 ,미션 모금 ,봉사활동 모집 )
4. 기부금 충전 ( bootpay )
5. 기부하기 
6. 봉사활동 참여 (QR 코드)
7. 기부/나눔 내역 웹/앱에서 확인

# 사용기술

● 서버: AWS (Linux)

● 웹 서버: apache, go(net/http), nodejs

● 데이터베이스: Mysql

● 언어: Java, go, javascript, shell script

● 라이브러리/API : Daum_address, glide, express, Retrofit2, bootpay, gson, kakao_map

# 스크린샷 

구성도는 다음과같다

![hyperledger_구성도](https://user-images.githubusercontent.com/57000871/96362968-3481e400-116c-11eb-94b3-782841532dc6.png)




기부에 필요한 아이디 , 패스워드 , 게시글 내용등을 MYSQL 을 따로 사용하여 저장해 두고 , 기부내역과 나눔 내역만 블록체인 저장하는 형식이다.
블록체인 특성상 처리량과 처리속도의 한계와 , 회원탈퇴시 지워야하는 개인정보등 데이터는 블록체인에 저장하지 않았다
패브릭 fabric-sample 코드 중 asset-transfer-ledger-queries 를 수정하여 사용하였습니다.

기능 1 - 일반기부

![hyperledger기능1](https://user-images.githubusercontent.com/57000871/96363327-83c91400-116e-11eb-9f2f-c375834afd4f.png)


기능 2- 미션 기부

![hyperledger기능2](https://user-images.githubusercontent.com/57000871/96363331-8e83a900-116e-11eb-9c93-55bb0a919bed.png)


기능 -3 웹에서 확인

![hyperledger기능3](https://user-images.githubusercontent.com/57000871/96363332-95aab700-116e-11eb-8562-43afdd053671.png)


# 영상 링크

https://www.youtube.com/watch?v=QYOqx10RF0I
