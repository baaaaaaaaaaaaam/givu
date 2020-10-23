package main

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"mime/multipart"
	"net/http"
	"os"
	"strings"

	_ "github.com/go-sql-driver/mysql"

	"io/ioutil"
	"strconv"
	"time"
)

type dbInfo struct {
	user     string
	pwd      string
	url      string
	engine   string
	database string
}

type responseResult struct {
	Result string `json:"result"`
}

type responseResultMode struct {
	Result    string `json:"result"`
	Mode      string `json:"mode"`
	Imagepath string `json:"imagepath"`
}

type userId_ImagePath struct {
	Memberid  string `json:"memberid"`
	ImagePath string `json:"imagepath"`
}

type responseResultJson struct {
	Result      string `json:"result"`
	QueryResult string `json:"queryResult"`
}

type getVolunteerRecruitment struct {
	Seq       string `json:"seq"`
	Id        string `json:"id"`
	Subject   string `json:"subject"`
	ImagePath string `json:"imagePath"`
	StartDate string `json:"startDate"`
	EndDate   string `json:"endDate"`
	StartTime string `json:"startTime"`
	EndTime   string `json:"endTime"`
	Location  string `json:"location"`
	Content   string `json:"content"`
	Doing     string `json:"doing"`
	Barcode   string `json:"barcode"`
}

type getCampaign struct {
	Seq           string `json:"seq"`
	Id            string `json:"writer"`
	Subject       string `json:"subject"`
	ImagePath     string `json:"imagePath"`
	StartDate     string `json:"startDate"`
	EndDate       string `json:"endDate"`
	Content       string `json:"content"`
	Collection    string `json:"collection"`
	Donation_list string `json:"list"`
	Doing         string `json:"doing"`
}

type responseResultgetCampaign struct {
	Result      string `json:"result"`
	QueryResult string `json:"queryResult"`
}

// 마이페이지에서 요청한 총 기부 횟수 , 총 기부금 , 내 잔고 , 캠페인 글번호 , 캠페인 제목 , 글쓴이 , 시작일 , 종료일 ,글쓴이 이미지 을 받아올것이다
type response_mypage_donate struct {
	Campaign_seq string `json:"campaign_seq"`
	Donate_money string `json:"donate_money"`
	Id           string `json:"writer"`
	Subject      string `json:"subject"`
	StartDate    string `json:"startDate"`
	EndDate      string `json:"endDate"`
	ImagePath    string `json:"imagePath"`
}

type response_mypage_donate_result struct {
	Result                           string `json:"result"`
	Count                            string `json:"count"`
	Sum                              string `json:"sum"`
	Account                          string `json:"account"`
	Time                             string `json:"time"`
	Response_mypage_donate           string `json:"response_mypage_donate"`
	Response_mypage_donate_volunteer string `json:"response_mypage_donate_volunteer"`
	Response_mypage_donate_time      string `json:"response_mypage_donate_time"`
	Response_mypage_challenge        string `json:"response_mypage_challenge"`
}
type response_mypage_donate_volunteer struct {
	Campaign string `json:"campaign"`
	Time     int    `json:"time"`
}

////////////////////////////////////////////
// mypage_foundation 에서 사용

type seq_and_donate struct {
	Seq    string `json:"seq"`
	Donate string `json:"donate"`
}

type response_mypage_foundation struct {
	Campaign_seq string `json:"campaign_seq"`
	Subject      string `json:"subject"`
	StartDate    string `json:"startDate"`
	EndDate      string `json:"endDate"`
	Doing        string `json:"doing"`
}

type response_mypage_foundation_result struct {
	Result                string `json:"result"`
	Campaign              string `json:"campaign"`
	Foundation_member     string `json:"foundation_member"`
	Volunteer_recruitment string `json:"volunteer_recruitment"`
	Time_campaign         string `json:"time_campaign"`
}

////////////////////////////////////////////

type response_campaign_detail_page_result struct {
	Result      string `json:"result"`
	Campaign    string `json:"campaign"`
	Donate_list string `json:"donate_list"`
	Share_list  string `json:"share_list"`
}

//시간 기부 캠페인에서 응답값으로 사용될 객체
type response_time_campaign_detail_page_result struct {
	Result     string `json:"result"`
	Campaign   string `json:"time_campaign"`
	Joiner     string `json:"joiner"`
	Share_list string `json:"share_list"`
}

type beneficiary_campaign_info struct {
	Seq        string `json:"seq"`
	Writer     string `json:"writer"`
	Subject    string `json:"subject"`
	ImagePath  string `json:"imagePath"`
	StartDate  string `json:"startDate"`
	EndDate    string `json:"endDate"`
	Content    string `json:"content"`
	Collection string `json:"collection"`
	Doing      string `json:"doing"`
}

type beneficiary_campaign_end_info struct {
	Seq   string `json:"seq"`
	Money string `json:"money"`
}
type response_mypage_beneficiary_result struct {
	Result                string `json:"result"`
	Account               string `json:"account"`
	Groupid               string `json:"groupid"`
	Campaign              string `json:"campaign"`
	Share_inblock         string `json:"share_inblock"`
	TimeCampaign          string `json:"time_campaign"`
	Mission_share_inblock string `json:"mission_share_inblock"`
}

type response_mypage_beneficiary_signup_foundation_result struct {
	Result      string `json:"result"`
	QueryResult string `json:"queryResult"`
}

//블록체인으로 부터 받은 데이터를 파싱하기위해사용됨
// give_donate에서 기부자가 자신의 마이페이지로 접근하였을때 블록으로부터 donate_list를 받아온다
// 이 떄 데이터의 형태가  [{"docType":"share_donate_record","counting":"2","seq":"3","beneficiary":"people","money":"0"}]  와 같다
// 이 데이터를 []docType에 파싱하여 담는다

//블록체인으로 부터 받은 데이터를 파싱하기위해사용됨
// 메인화면에서 모든 캠페인을 받아올때 캠페인별로 기부자가 기부한 금액이 있는지 블록체인에서 확인한다.
//받아온 데이터 형태 [{"docType":"donation_list","counting":"0","seq":"1","donation":"user","donate_money":"10000"}]
// 이 데이터를 []docType에 파싱하여 담는다
// 담으면 배열안에  {donation_list 1 user 10000} 처럼 값만뽑아 담을 수 있다.
type docType1 struct {
	DocType string `json:"doctype"`
	Seq     string `json:"seq"`
	Id      string `json:"id"`
	Money   string `json:"money"`
}

type getTimeCampaign struct {
	Seq         string `json:"seq"`
	Id          string `json:"id"`
	Subject     string `json:"subject"`
	ImagePath   string `json:"imagePath"`
	Money       string `json:"money"`
	CurrentTime string `json:"currentTime"`
	Time        string `json:"time"`
	Donation    string `json:"donation"`
	StartDate   string `json:"startDate"`
	EndDate     string `json:"endDate"`
	Content     string `json:"content"`
	Doing       string `json:"doing"`
	Mission     string `json:"mission"`
	Permission  string `json:"permission"`
}

func dbConn() (db *sql.DB) {

	var db1 = dbInfo{"hyperledger", "Akwldrk1!", "localhost:3306", "mysql", "hyperledger"}

	dataSource := db1.user + ":" + db1.pwd + "@tcp(" + db1.url + ")/" + db1.database
	conn, err := sql.Open(db1.engine, dataSource)
	if err != nil {
		fmt.Println("디비 연결 실패")
		fmt.Println(err)
	}
	return conn
}

var tmp string
var id string
var request string
var pwd string
var mode string
var share_list string
var subject string
var startDate string
var endDate string
var content string
var seq string
var money string
var give_money string
var foundation_id string
var beneficiary string
var collection string
var input_paiging_num string
var startTime string
var endTime string
var location string
var barcode string
var date string
var current string
var check_volunteer_start string
var donation string
var goal_time string
var permission string
var give_time string

// 클라이언트가 접속하는 부분
// r 로 요청을받고 ,w로 응답을 한다.
func defaultHandler(w http.ResponseWriter, r *http.Request) {

	// http request URL 를 읽어 r.Form 에 업데이트한다.
	// POST요청의 경우에도 body를 읽고양식으로 구분한 다음 r.PostForm에 넣는다.
	// 하지만  x-www-form-urlencoded양식이 아닌 경우 body를 읽지못한다.
	r.ParseForm()
	// 전체 데이터 형태를 보여준다
	fmt.Println("r.Form :", r.Form)
	fmt.Println("r.PostForm :", r.PostForm)
	fmt.Println("request IP  :", r.RemoteAddr)
	w.Header().Set("Access-Control-Allow-Origin", "*")

	if r.Method == "POST" {
		fmt.Println("      POST      ")
		// Post의 multipart upload할 경우 text 는 r.ParseMultipartForm(0)로 지정한 후 읽어올수있다 . 해당명령어 없을 경우 FormValue못읽어옴
		// 요청하는 request body를 multipart /form-data로 해석함.
		r.ParseMultipartForm(0)
		// r.FormValue : 키에대한 첫번째 값을 반환함
		request = r.FormValue("request")

		id = r.FormValue("id")
		share_list = r.FormValue("share_list")
		subject = r.FormValue("subject")
		startDate = r.FormValue("startDate")
		endDate = r.FormValue("endDate")
		content = r.FormValue("content")
		startTime = r.FormValue("startTime")
		endTime = r.FormValue("endTime")
		location = r.FormValue("location")
		input_paiging_num = r.FormValue("input_paiging_num")
		donation = r.FormValue("donation")
		goal_time = r.FormValue("goal_time")
		money = r.FormValue("money")
	} else if r.Method == "GET" {
		fmt.Println("      GET      ")
		request = r.FormValue("request")
		id = r.FormValue("id")
		pwd = r.FormValue("passwd")
		mode = r.FormValue("mode")
		seq = r.FormValue("seq")
		subject = r.FormValue("subject")
		startDate = r.FormValue("startDate")
		endDate = r.FormValue("endDate")
		content = r.FormValue("content")
		startTime = r.FormValue("startTime")
		endTime = r.FormValue("endTime")
		location = r.FormValue("location")
		money = r.FormValue("money_charge")
		give_money = r.FormValue("give_money")
		foundation_id = r.FormValue("foundation_id")
		beneficiary = r.FormValue("beneficiary")
		collection = r.FormValue("collection")
		input_paiging_num = r.FormValue("input_paiging_num")
		barcode = r.FormValue("barcode")
		date = r.FormValue("date")
		current = r.FormValue("current")
		check_volunteer_start = r.FormValue("check_volunteer_start")
		permission = r.FormValue("permission")
		give_time = r.FormValue("give_time")

	}

	if request == "createId" {
		// 회원가입 화면에서 사용된다

		//회원가입을 하는데 입력한 아이디가 있는지 중복검사하는 로직이다
		// 쿼리가 잘못되거나 이미 등록한 아이디가 있는 경우 no1를 응답하고 , 검색결과 중복된 아이디가 없다면 ok를리턴한다.
		tmp := lookupId(id)
		if tmp == "ok" {
			//중복된 아아디가 없는경우
			// createId에서 아이디 중복검사를 마친후 중복된 아이디가 없으면 입력한 정보를 member 테이블에저장한다
			// 제대로 입력이된다면 ok를 쿼리가 실패하다면 no를 반환한다
			tmp := signUp(id, pwd, mode)
			var r = responseResult{tmp}
			json.NewEncoder(w).Encode(r)
		} else {
			// 중복된 아이디가 있는경우
			var r = responseResult{tmp}
			json.NewEncoder(w).Encode(r)
		}

	} else if request == "login" {
		// 로그인 페이지에서 아이디와 패스워드를 입력하면 실행된다

		// 로그인화면에서 로그인을 위해 아이디와 패스워드를 입력하면 실행된다
		// 로그인이 성공되면 해당 아이디의 모드와 이미지경로를 반환한다
		tmp, mode, imagepath := lookupIdPw(id, pwd)

		fmt.Println("tmp,mode = lookupIdPw(id,pwd) : ", tmp, mode)

		// 어차피 아래와 차이가없어서 하나로 바꿈
		var r = responseResultMode{tmp, mode, imagepath}
		json.NewEncoder(w).Encode(r)

		// if tmp == "ok:login" {
		// 	var r =responseResultMode{tmp , mode,imagepath}
		// 	 json.NewEncoder(w).Encode(r)
		// }else if tmp == "no:login" {
		// 	var r =responseResultMode{tmp , mode,imagepath}
		// 	 json.NewEncoder(w).Encode(r)
		// }

	} else if request == "querygroupmember" {

		// 기부단체에 가입한 수혜자 리스트

		// 캠페인에서 나눔대상을 선택할때 나눔대상 리스트 불러오기
		// 기부단체 아이디에 가입을 한 수혜자와 수혜자이미지 가져오기

		groupmemberlist := lookupGroupmember(id)

		//만약 쿼리가 에러가 발생하면 크기가 0이다.
		// responseResultJson 는 결과와 string값 두개를 json으로 반환한다.

		if len(groupmemberlist) == 0 {
			var r = responseResultJson{"no", ""}
			json.NewEncoder(w).Encode(r)
		} else {

			// json 형태로 encoding함
			jsonData, err := json.Marshal(groupmemberlist)
			if err != nil {
				log.Println(err)
			}
			var r = responseResultJson{"ok", string(jsonData)}
			json.NewEncoder(w).Encode(r)
		}

	} else if request == "profileImage" {

		// 프로필 업로드 페이지에서 이미지를 보내면 updateProfileImage에서 이미지를 저장하고 responseResult통해 클라이언트에게 전송한다.

		tmp = updateProfileImage(r, id)

		// 이미지를 업로드하고 받은 result 값을 클라이언트에게 response한다
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)

	} else if request == "campaign_upload" {
		// 기부단체에서 캠페인을 등록할 경우 동작한다.
		// 캠페인 등록전 saveImage를 통해 이미지를 먼저 저장하고 이미지 경로를 받는다
		// 이후 캠페인 작성자 ,제목,이미지 ,시작일,종료일, 내용을 campaign 테이블에 저장한다
		// 방금 저장한 캠페인의 seq를 받아오기 위해 방금 저장한 이미지와 작성자아이디를 가지고 seq를 조회한다
		// 조회한 seq와 캠페인 수혜자 리스트를 campaign_share_list에 따로 저장한다.

		tmp := upload_campaign(id, subject, r, share_list, startDate, endDate, content)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)

	} else if request == "getCampaign" {

		//메인화면에 접근하면 무조건 동작한다.
		//메인화면 접속할경우 모든 캠페인 정보를 받아온다
		//  메인화면에서 캠페인 정보를 받아올 때 campaign 테이블에서 정보와 해당 캠페인 별 현재 donation_list를 받아온다
		// 캠페인  {시퀀스,작성자 , 제목 ,이미지 ,시작일,종료일 ,진행중여부,{기부 참여자 아이디, 이미지}}

		tmp := get_campaign(input_paiging_num)
		tmp_split := strings.Split(tmp, ":")

		if tmp_split[0] == "no" {
			var r = responseResultJson{"no", tmp}
			json.NewEncoder(w).Encode(r)
		} else if tmp_split[0] == "paiging_end" {
			var r = responseResultJson{"paiging", tmp}
			json.NewEncoder(w).Encode(r)
		} else {
			var r = responseResultJson{"ok", tmp}
			json.NewEncoder(w).Encode(r)
		}

	} else if request == "get_campaign_share_list" {

		//캠페인별로 나눔 대상자로 등록된 리스트를 불러온다
		// 캠페인 seq를 기준으로 campaign_share_list 테이블리스트 에서 받아온다.
		//  해당 campaign_share_list를 json으로 파싱하여 string으로 반환한다.
		// (만약 에러가 발생할경우 no : 에러 사유가 반환되고 정상일경우 json형태의 쉐어 대상자 이미지와 아이디가 반한됨 )
		tmp := get_campaign_share_list(seq)
		tmp_split := strings.Split(tmp, ":")

		if tmp_split[0] == "no" {
			var r = responseResultJson{"no", tmp}
			json.NewEncoder(w).Encode(r)
		} else {
			var r = responseResultJson{"ok", tmp}
			json.NewEncoder(w).Encode(r)
		}

	} else if request == "mypage_Donation" {

		// 기부자가 mypage에 들어가면 호출된다
		// 담길 내용은 기부자의 잔액 ,  총기부한금액 ,총 기부한 횟수, 캠페인정보 ( 켐피인 이미지 ,작성자 , 제목 , 시작일,종료일 , 내가 기부한 돈)이 담겨있다.
		tmp := mypage_Donation(id)
		json.NewEncoder(w).Encode(tmp)
	} else if request == "money_charge" {

		//  기부자 마이페이지에서 충전하기를 통해 돈을 충전하면 현재 가지고있는 돈에 + 충전할돈을 합하여 update한다.
		tmp := money_charge(id, money)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "mypage_foundation" {

		//기부단체로 로그인 할 경우 마이페이지로 이동하게되면 자신이 등록한 켐페인(번호,작성자,캠페인 대표이미지 ,시작일 , 종료일 , 내용,캠페인 별 기부금 ,진행상황) 리스트와
		//자신을 등록한 수혜자 리스트 ( 이미지 , 아이디 )를 가져온다.
		log, campaign, foundation_member, volunteer_recruitment_list, time_campaign := mypage_foundation(id)
		var r = response_mypage_foundation_result{log, campaign, foundation_member, volunteer_recruitment_list, time_campaign}
		json.NewEncoder(w).Encode(r)

	} else if request == "campaign_detail_page" {

		// 메인 페이지에서 캠페인을 터치하였을때 해당 캠페인의 시퀀스번호를 바탕으로 해당 켐페인의 제목,작성자,이미지,시작일,종료일,진행중유무,모은금액 을 받아온다.
		tmp1 := campaign_detail_page(seq)
		// 메인 페이지에서 캠페인을 터치하였을때 해당 캠페인의 시퀀스번호를 바탕으로 해당 켐페인에 참여한 기부자의 아이디와 이미지를 받아온다
		tmp2 := campaign_detail_page_donate_list(seq)
		// 메인 페이지에서 캠페인을 터치하였을때 해당 캠페인의 시퀀스번호를 바탕으로 해당 켐페인에 수혜대상자의 아이디와 이미지를 받아온다
		tmp3 := campaign_detail_page_share_list(seq)

		var r = response_campaign_detail_page_result{"ok", tmp1, tmp2, tmp3}
		json.NewEncoder(w).Encode(r)

	} else if request == "give_donate" {

		// 기부자가 지정한 캠페인에 자신의아이디와 기부할 금액을 선택하여 넘겨준다
		// 먼저 donation_list에 내 아이디 ,캠페인 시퀀스 , 금액을등록한다.
		// 이후 캠페인 시퀀스의 collection을 불러와 내가 기부하려는 금액을 더해 update 해준다
		// 마지막으로 내가 기부한 금액을 member 테이블의 내아이디 account에서 뺀다.
		tmp := give_donate(seq, id, give_money)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "end_signal_campaign" {

		//기부 단체에서 캠페인 종료를 요청한다
		//1.  기부 단체에서 전달받은 캠페인 seq를 바탕으로 campaign에서 seq를 조회한후 collection을 가지고 온다
		//2.  기부 단체에서 전달받은 캠페인 seq를 바탕으로 campaign_share_list에서 campaign_seq 를 기준으로 count 를 불러 나눔대상자 수를 확인한다
		//3.  불러온 collection을 count로 나눠 각각 얼마씩 분배되야하는지 계산한다.
		//4.  그 다음 나눠줄대상을 찾기위해  campaign_share_list 테이블에서  beneficiary을 for문으로 불러온다

		//*************   지금은 sql에 benenficiary 에게 나눠주는 부분을 저장하지만 나중에는 블록체인으로 변경할 부분******************//
		//5 임시로 만든 campaign_end_share_beneficiary table에 seq,beneficiary , 나눈 금액을 넣어 저장한다.
		//6.    campaign_end_share_beneficiary에 저장한 후 member table에 id가 beneficiary인 컬럼을 찾아 account를 불러온다
		//7.    이후 account와 collection을 더하여 member table account에 update를 한다.
		//*************************************************************************************************************************//
		//8. 마지막으로 campaign의 doing 을 false로 업데이트 한다.
		fmt.Println("end_signal_campaign 시작")
		tmp := end_signal_campaign(seq)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "mypage_beneficiary" {

		// 수혜자로 로그인하여 마이페이지로 이동하면 두가지 정보를 받아온다
		// 1. mysql에서 campaign_share_list에서 수혜자가 내 아이디로된 시퀀스를 찾아 , campaign테이블에서 그 시퀀스에 해당하는 캠페인 모든정보
		// 2. 블록체인에서 캠페인이 종료될경우 모은 금액을 수혜대상자에게 나눔을 진행하는 내용

		tmp, account, groupid, campaign, share_list, time_campaign, mission_share_list := mypage_beneficiary(id)
		var r = response_mypage_beneficiary_result{tmp, account, groupid, campaign, share_list, time_campaign, mission_share_list}
		json.NewEncoder(w).Encode(r)
	} else if request == "daily_check_campaign" {
		fmt.Println("daily_check_campaign", endDate)
		daily_check_campaign_end(endDate)
		daily_check_volunteer_recruitment_end(endDate)
		daily_check_time_campaign_end(endDate)
	} else if request == "lookupFoundation" {

		// 수혜자가 마이페이지로 이동을 했을때 자신이 등록한 기부단체가 없을 경우 기부단체를 등록할수있다
		//이때 등록하는 버튼을 누르면 동작하는 로직이다
		// member 테이블에 mode가 2로된 기부단체 리스트를 전부 불러온다.

		tmp, tmp1 := lookupFoundation()
		fmt.Println(tmp, tmp1)
		var r = response_mypage_beneficiary_signup_foundation_result{tmp, tmp1}
		json.NewEncoder(w).Encode(r)
	} else if request == "singupFoundation" {
		//수혜자가 lookupFoundation을 통해 기부단체 리스트중 하나의 단체를 선택하여 등록을 요청하면 동작하는 로직이다
		//자신의 아이디를 memberid ,기부단체 아이디를 groupid로하여 groupmember 테이블에 등록한다.
		tmp := singupFoundation(id, foundation_id)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "remove_foundation" {

		//수혜자가 마이페이지에서 자신이 등록한 기부단체를 삭제 할때 동작한다
		// groupmember에서 자신의 memberid가 자신의 아이디인 경우 삭제한다

		tmp := remove_foundation(id)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "write_volunteer_recruitment" {

		//기부단체가 봉사활동 모집을 할때 실행된다 =

		tmp := write_volunteer_recruitment(id, subject, r, startDate, endDate, startTime, endTime, location, content)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "get_volunteer_recruitment" {
		//봉사 활동 모집 게시판을 열면 실행된다
		// 각 게시글은 3개씩 페이징된다
		result, queryResult := get_volunteer_recruitment(input_paiging_num)
		var r = responseResultJson{result, queryResult}
		json.NewEncoder(w).Encode(r)
	} else if request == "volunteer_recruitment_detail_page" {
		//상세 페이지이동시 클라이언트가 준 seq 번호를 기준으로 해당 내용을 불러온다
		result, queryResult := volunteer_recruitment_detail_page(seq)
		var r = responseResultJson{result, queryResult}
		json.NewEncoder(w).Encode(r)
	} else if request == "change_image_update_volunteer_recruitment" {
		//수정을 할 경우 seq + write때 사용한걸모두사용한다
		result := change_image_update_volunteer_recruitment(seq, id, subject, r, startDate, endDate, startTime, endTime, location, content)
		var r = responseResult{result}
		json.NewEncoder(w).Encode(r)
	} else if request == "not_change_image_update_volunteer_recruitment" {
		result := not_change_image_update_volunteer_recruitment(seq, subject, startDate, endDate, startTime, endTime, location, content)
		var r = responseResult{result}
		json.NewEncoder(w).Encode(r)
	} else if request == "delete_volunteer_recruitment" {

		//상세 페이지에서 삭제버튼을 누를 경우 삭제된다.
		result := delete_volunteer_recruitment(seq)
		var r = responseResult{result}
		json.NewEncoder(w).Encode(r)
	} else if request == "volunteer_time" {
		result := volunteer_time(seq, id, barcode, date, current, check_volunteer_start)
		var r = responseResult{result}
		json.NewEncoder(w).Encode(r)
	} else if request == "time_campaign_upload" {
		fmt.Println("time_campaign_upload")

		tmp := time_campaign_upload(id, subject, r, money, goal_time, donation, share_list, startDate, endDate, content)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)

	} else if request == "write_time_campaign_signupActivity" {
		result, queryResult := write_time_campaign_signupActivity()
		var r = responseResultJson{result, queryResult}
		json.NewEncoder(w).Encode(r)
	} else if request == "time_campaignActivity" {
		tmp, result := time_campaignActivity(input_paiging_num)
		tmp_split := strings.Split(tmp, ":")

		if tmp_split[0] == "no" {
			var r = responseResultJson{"no", result}
			json.NewEncoder(w).Encode(r)
		} else if tmp_split[0] == "paiging_end" {
			var r = responseResultJson{"paiging", result}
			json.NewEncoder(w).Encode(r)
		} else {
			var r = responseResultJson{"ok", result}
			json.NewEncoder(w).Encode(r)
		}
	} else if request == "detail_time_campaignActivity" {

		fmt.Println("detail_time_campaignActivity", seq)

		//캠페인 정보 받아오기
		tmp_time_campaign := detail_time_campaignActivity(seq)
		json_time_data, err := json.Marshal(tmp_time_campaign)
		if err != nil {

		}
		//캠페인에 시간 기부한 리스트 받아오기
		tmp_joiner_list := get_time_campaign_joiner_list(seq)

		//캠페인에 지정된 기부 대상자 불러오기
		tmp_share_list := get_time_campaign_share_list(seq)

		var r = response_time_campaign_detail_page_result{"ok", string(json_time_data), tmp_joiner_list, tmp_share_list}
		json.NewEncoder(w).Encode(r)

	} else if request == "time_campaign_upload_permission" {
		tmp := time_campaign_upload_permission(permission, seq, id)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "give_time_donate" {
		tmp := give_time_donate(seq, id, give_time)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "time_campaign_end" {
		tmp := time_campaign_end(seq)
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "get_total_count" {
		tmp := get_total_count()
		var r = responseResult{tmp}
		json.NewEncoder(w).Encode(r)
	} else if request == "test" {
		test()
	}

}

//회원가입을 하는데 입력한 아이디가 있는지 중복검사하는 로직이다
// 쿼리가 잘못되거나 이미 등록한 아이디가 있는 경우 no1를 응답하고 , 검색결과 중복된 아이디가 없다면 ok를리턴한다.
func lookupId(input_id string) string {
	fmt.Println("lookupId")
	var count int

	var log string
	db := dbConn()
	err := db.QueryRow("select count(*) from member where id =?", input_id).Scan(&count)
	if err != nil {
		fmt.Println("아이디 조회 에러 ")
		fmt.Println(err)
		log = "no1"
	} else {
		if count < 1 {
			log = "ok"
		} else {
			log = "no1"
		}
	}
	fmt.Println(log)
	defer db.Close()
	return log

}

// 로그인화면에서 로그인을 위해 아이디와 패스워드를 입력하면 실행된다
// 로그인이 성공되면 해당 아이디의 모드와 이미지경로를 반환한다
func lookupIdPw(input_id string, input_pw string) (string, string, string) {
	fmt.Println("lookupIdPw 시작")
	var seq int
	var id string
	var passwd string
	var account int
	var mode string
	var imagePath string
	var time int
	db := dbConn()

	var log string
	// 1개의 응답값임
	err := db.QueryRow("select * from member WHERE id = ? AND passwd = ?", input_id, input_pw).Scan(&seq, &id, &passwd, &account, &mode, &imagePath, &time)

	fmt.Println(id)
	defer db.Close()

	if err != nil {
		fmt.Println(err)
		fmt.Println("login")
		log = "no:login"

	} else {
		fmt.Println("login")
		log = "ok:login"

	}
	fmt.Println("lookupIdPw 종료")
	defer db.Close()
	return log, "mode:" + mode, imagePath
}

// 캠페인에서 나눔대상을 선택할때 나눔대상 리스트 불러오기
// 기부단체 아이디에 가입을 한 수혜자와 수혜자이미지 가져오기
func lookupGroupmember(input_id string) []userId_ImagePath {

	fmt.Println("lookupGroupmember 시작")

	var groupid string
	var memberid string
	var imagePath string

	db := dbConn()

	// gropupmember 테이블에서 기부단체 아이디로 조회를 했을때 나오는 기부단체아이디와 수혜자 아이디중 수혜자 아이디와 member 테이블에서 imagePath를 불러온다.
	//ex) member table ( 1. ansgyqja , 1 , 2. ansgyqja1,2  ) , gropumember table ( group id = korea , memberid = ansgyqja) , 기부단체 아이디 korea
	//  == > korea에 속한 ansgyqja 아이디를 member table에서 조회하여 1이라는 값을 불러온다.
	rows, err := db.Query("select m.imagePath , g.groupid , g.memberid from member AS m JOIN groupmember AS g ON g.memberid = m.id where g.groupid=? ", input_id)
	if err != nil {
		fmt.Println(err)
	}
	defer rows.Close()

	// 빈 배열 만들기
	// userId_ImagePath struct는 userId와 imagePath를 string으로 받는다.
	var groupmemberlist = []userId_ImagePath{}

	for rows.Next() {
		err := rows.Scan(&imagePath, &groupid, &memberid)
		if err != nil {
			fmt.Println(err)
			fmt.Println("에러")
			return groupmemberlist

		}
		// 검색한 결과를 R이라는 객체에 담고 groupmemberlist에 추가한다.
		R := userId_ImagePath{memberid, imagePath}
		// R.Memberid=memberid
		// R.ImagePath=imagePath

		groupmemberlist = append(groupmemberlist, R)

	}
	fmt.Println("에러 없이 종료 ")
	defer db.Close()
	return groupmemberlist
}

// 수혜자가 마이페이지로 이동을 했을때 자신이 등록한 기부단체가 없을 경우 기부단체를 등록할수있다
//이때 등록하는 버튼을 누르면 동작하는 로직이다
// member 테이블에 mode가 2로된 기부단체 리스트를 전부 불러온다.
func lookupFoundation() (string, string) {

	var log string
	var id string
	var imagePath string
	db := dbConn()

	userId_ImagePath_list := []userId_ImagePath{}
	rows, err := db.Query("select id,imagePath from member where mode ='2'")
	defer db.Close()
	if err != nil {
		log = "no"
		fmt.Println(err)
		return log, ""
	} else {

		for rows.Next() {
			rows.Scan(&id, &imagePath)

			R := userId_ImagePath{id, imagePath}
			userId_ImagePath_list = append(userId_ImagePath_list, R)
		}

		var jsonData []byte
		jsonData, err = json.Marshal(userId_ImagePath_list)
		if err != nil {

			log = "no"
			fmt.Println(err)
			db.Close()
			return log, ""
		} else {

			db.Close()
			log = "ok"
			return log, string(jsonData)
		}
	}

}

// createId에서 아이디 중복검사를 마친후 중복된 아이디가 없으면 입력한 정보를 member 테이블에저장한다
// 제대로 입력이된다면 ok를 쿼리가 실패하다면 no를 반환한다
func signUp(inputId string, inputPw string, mode string) string {
	fmt.Println("signUp")

	var log string

	db := dbConn()
	conn, err := db.Prepare("insert into member(id,passwd,mode) value (?,?,?)")
	defer db.Close()
	if err != nil {
		fmt.Println(err)
		fmt.Println("createId fail")
		log = "no"
	} else {
		conn.Exec(inputId, inputPw, mode)
		fmt.Println("createId suceess")
		log = "ok"
	}
	fmt.Println(log)
	defer db.Close()
	return log
}

// 클라이언트로부터 받은 이미지를 꺼내 현재시간을 나노초로 변환한 임시 파일명으로 생성한다
// 이후  /var/www/html/upload/라는 경로에 파일을 만든다
// 만든 파일을 io.Copy를 통해 받은원본을  빈 파일에 넣어주고 해당 파일명을 반환한다.
func make_random_value() string {
	// 현재시간을 받아옴
	t := time.Now()
	// 현재시간을 나노Sec 10자리수로 변경하여 파일의 임시 파일명으로 사용할 것이다
	// 파일이름을 원본으로할 경우 같은 파일명을 업데이트할때 에러가 발생하여 임시 파일명으로사용함
	value := strconv.FormatInt(t.UnixNano(), 10)
	return value
}

func saveImage(r *http.Request) string {

	// updateProfileImage 에서 프로필을 저장할나 upload_campaign에서 캠페인 등록시 이미지를 저장할때 사용된다
	// 리턴 값은 파일 이름 이다.

	var file multipart.File
	var fileHeader *multipart.FileHeader
	var e error
	var uploadedFileName string
	// POST된 파일 데이터를 얻는다

	tmpImageName := make_random_value()
	// key가 image로 되어있는 파일을 불러와 file에 담는다
	file, fileHeader, e = r.FormFile("image")

	if e != nil {
		fmt.Println("파일 확인안됨")
		return "no: not found file"
	}

	// fileHeader에서 파일 이름을 꺼내 split으로 " . " 기준으로 자른다.
	// 자른문자열 == 확장자
	//  확장자를 위에서 만든 임시 파일명에 덧붙인다

	uploadedFileName = fileHeader.Filename
	extend := strings.Split(uploadedFileName, ".")
	tmpImageName = tmpImageName + "." + extend[1]

	// 서버 측에 저장하기 위해 빈 파일을 만든다
	var saveImage *os.File

	// 저장을 하기전 권한 설멍을 미리 해놔야한다.
	// /var/www/html/upload 가 root 권한이기 때문에 쓰기 권한을 줘야한다.
	// 해당 경로에 위에서 만든 임시 파일명으로 빈파일을 만든다.
	saveImage, e = os.Create("/var/www/html/upload/" + tmpImageName)
	if e != nil {

		fmt.Printf("이미지 파일 생성 실패 ")
		return "no: don't create file"
	}

	defer saveImage.Close()
	defer file.Close()

	// 위에서 만든 saveImage는 파일만생성한것이고 실제로 io.Copy를 통해 이미지를 붙여넣기한다.
	size, e := io.Copy(saveImage, file)
	if e != nil {
		fmt.Println(e)
		fmt.Println("업로드된 파일 쓰기에 실패하였다")
		return "no: not copy  file"
	}

	fmt.Println(size)
	// fmt.Fprintf(w, "문자열 HTTP로 출력시킨다");
	return tmpImageName
}

// 프로필 업로드 ( 이미지 + 텍스트 )
// 프로필 액티비티에서 프로필을 자신의 아이디와 함께 업로드할 경우 사용된다

func updateProfileImage(r *http.Request, input_id string) string {

	// saveImage는 클라이언트로부터 받은 이미지를 꺼내 현재시간을 나노초로 변환한 임시 파일명으로 생성한다
	// 이후  /var/www/html/upload/라는 경로에 파일을 만든다
	// 만든 파일을 io.Copy를 통해 받은원본을  빈 파일에 넣어주고 해당 파일명을 반환한다.
	tmp := saveImage(r)

	if tmp == "no: not found file" {
		return tmp
	} else if tmp == "no: don't create file" {
		return tmp
	} else if tmp == "no: not copy  file" {
		return tmp
	} else {
		// 파일이 정상적으로 생성되었을 경우 파일명을 member 테이블에 해당 아이디에맞게 저장한다.
		db := dbConn()
		conn, err := db.Prepare("update member SET imagePath=? where id=?")
		defer db.Close()
		fmt.Println("updateProfileImage")
		if err != nil {
			db.Close()
			fmt.Println(err)
			return "no:updateProfileImage"
		} else {
			conn.Exec(tmp, input_id)
			db.Close()
			return "ok:" + tmp
		}
	}

}

// 캠페인 등록 ( 이미지 + 텍스트 )

// 캠페인 등록전 saveImage를 통해 이미지를 먼저 저장하고 이미지 경로를 받는다
// 이후 캠페인 작성자 ,제목,이미지 ,시작일,종료일, 내용을 campaign 테이블에 저장한다
// 방금 저장한 캠페인의 seq를 받아오기 위해 방금 저장한 이미지와 작성자아이디를 가지고 seq를 조회한다
// 조회한 seq와 캠페인 수혜자 리스트를 campaign_share_list에 따로 저장한다.

func upload_campaign(input_id string, input_subject string, r *http.Request, input_share_list string, input_startDate string, input_endDate string, input_content string) string {

	//캠페인 저장
	db := dbConn()
	conn, err := db.Prepare("insert into campaign(id,subject,imagePath,startDate,endDate,content) value (?,?,?,?,?,?)")
	defer db.Close()
	if err != nil {
		fmt.Println(err)
		fmt.Println("이미지 업로드 및 camapaign 테이블 insert 실패")
		return "no: campaign insert fail"

	} else {

		// 이미지를 저장하고 이미지 파일을 불러온다
		imagePath := saveImage(r)
		fmt.Println("이미지 업로드 및 camapaign 테이블 insert 성공 ")
		conn.Exec(input_id, input_subject, imagePath, input_startDate, input_endDate, input_content)

		//캠페인은 저장하였으나 캠페인에 포함된 기부 대상자 리스트는 아직 저장못함
		// 방금 등록한 캠페인의 seq를 가지고 campaign_share_list에 기부자 리스트를 등록할 수 있음
		// 방금 등록한 seq를 알수있는 고유 identity인 imagePath를 기준으로 방금 올린 캠페인 seq를 받아옴

		var tmp_seq string

		//  방금 내가 올린 캠페인의 seq 번호 받아 검색하기
		//  그 seq는 campaign_share_list의 key로 사용될 것이다.
		// err = db.QueryRow("select seq from campaign WHERE id = ? AND imagePath = ?", input_id,imagePath).Scan(&tmp_seq,&tmp_id,&subject,&tmp_image,&startDate,&endDate,&content,&collection,&doing)
		err = db.QueryRow("select seq from campaign WHERE id = ? AND imagePath = ?", input_id, imagePath).Scan(&tmp_seq)

		defer db.Close()

		if err != nil {
			fmt.Println(err)
			fmt.Println("방금 등록한 글 못찾음 ")
			return "no: not found campaign "
		} else {
			fmt.Println("방금 등록한 글 찾음 ")

			// 나눔 대상자는 " &* " 을 구분자로 사용하여 등록되어있어 분할 해야한다.
			tmp_list := strings.Split(input_share_list, "&*")

			for i := 0; i < len(tmp_list); i++ {
				conn, err = db.Prepare("insert into campaign_share_list(campaign_seq,beneficiary) value (?,?)")

				if err != nil {
					break
				} else {
					conn.Exec(tmp_seq, tmp_list[i])
					fmt.Println("upload_campaign success")
					// return "ok : signupCampaing";
				}

			}
			fmt.Println("upload_campaign 종료")
			if err != nil {
				fmt.Println(err)
				fmt.Println("campaign_share_list 에 campaign seq 와 나눔 대상자 넣기  fail")
				return "no: not insert campaign_share_list  "
			} else {
				fmt.Println("campaign_share_list 에 campaign seq 와 나눔 대상자 넣기  최종 성공")
				db.Close()
				return "ok"
			}

		}

	}

}

//메인화면 접속할경우 모든 캠페인 정보를 받아온다
//  메인화면에서 캠페인 정보를 받아올 때 campaign 테이블에서 정보와 해당 캠페인 별 현재 donation_list를 받아온다
// 캠페인  {시퀀스,작성자 , 제목 ,이미지 ,시작일,종료일 ,진행중여부,{기부 참여자 아이디, 이미지}}

func get_campaign(input_paiging_num string) string {

	fmt.Println("get_campaign 시작")
	fmt.Println("input_paiging_num", input_paiging_num)
	var seq string
	var id string
	var subject string
	var imagePath string
	var startDate string
	var endDate string
	var content string
	var collection string
	var doing string
	var total_count int
	var tmp_input_paiging_num int

	db := dbConn()

	db.QueryRow("select count(*) from campaign").Scan(&total_count)

	tmp_input_paiging_num, err := strconv.Atoi(input_paiging_num)
	if total_count < tmp_input_paiging_num {
		fmt.Println("더이상불러올 데이터")
		return "paiging_end: end"
	}

	rows, err := db.Query("select * from campaign order by seq desc limit ?,3", input_paiging_num)
	if err != nil {
		fmt.Println("에러 있음")
	} else {
		fmt.Println("에러 없음")
	}
	defer rows.Close()

	//  campaign  값을 담을 빈 배열을 만든다.
	var getCampaignlist = []getCampaign{}

	for rows.Next() {
		err := rows.Scan(&seq, &id, &subject, &imagePath, &startDate, &endDate, &content, &collection, &doing)

		//캠페인 정보를 받아온다
		if err != nil {
			fmt.Println("에러")
			fmt.Println(err)
			
			return "no: getCampaignlist1"

		} else {

			// 객체를만든다
			R := getCampaign{}
			R.Seq = seq
			R.Id = id
			R.Subject = subject
			R.ImagePath = imagePath
			R.StartDate = startDate
			R.EndDate = endDate
			R.Content = content
			R.Collection = collection
			R.Doing = doing

			// 모든 캠페인을 가져오는데 가져온느 반복문 안에서 seq번호가 일치하는 donation_list_Record가 있는지 확인 한다.
			//블록에서 가자온 데이터를 담을 배열
			var data []docType1

			// 각 시퀀스마다 기부자 아이디와 이미지를 담을 배열
			var list = []userId_ImagePath{}

			//기부자 유저 프로필 이미지 담을 변수
			var user_imagePath string

			//블록에서 받아온 데이터를 배열에 담는다
			// block_return:=B_select_seq("donation_list",seq)
			block_return := B_select("select_seq", "", seq, "donation_list")
			log.Println("")

			json.Unmarshal([]byte(block_return), &data)
			for i, _ := range data {
				//배열에서 donation( 유저 이아디)를 꺼내고 꺼낸 아이디를 member 테이블에서 imagePath를 받아와 userId_ImagePath객체로 만들어 list에 저장한다
				fmt.Println("ta[i].Donation", data[i].Id)
				db.QueryRow("select imagePath from member where id=?", data[i].Id).Scan(&user_imagePath)
				T := userId_ImagePath{data[i].Id, user_imagePath}
				list = append(list, T)
				var jsonData []byte
				jsonData, err := json.Marshal(list)
				if err != nil {
					log.Println(err)
					return "no: getCampaignlist4"
				} else {
					R.Donation_list = string(jsonData)
					// fmt.Println("Donation_list  : " , R.Donation_list)
				}
			}

			defer rows.Close()
			getCampaignlist = append(getCampaignlist, R)
			// fmt.Println("getCampaignlist  : " , getCampaignlist)
		}

	}
	var jsonData []byte
	jsonData, err = json.Marshal(getCampaignlist)
	if err != nil {
		log.Println(err)
		return "no: getCampaignlist5"
	}
	fmt.Println(string(jsonData))
	fmt.Println("getCampaign 반복문 종료  ")
	defer db.Close()
	return string(jsonData)
}

//캠페인별로 나눔 대상자로 등록된 리스트를 불러온다
// 캠페인 seq를 기준으로 campaign_share_list 테이블리스트 에서 받아온다.
//  해당 campaign_share_list를 json으로 파싱하여 string으로 반환한다.
// (만약 에러가 발생할경우 no : 에러 사유가 반환되고 정상일경우 json형태의 쉐어 대상자 이미지와 아이디가 반한됨 )
func get_campaign_share_list(input_seq string) string {
	fmt.Println("get_campaign_share_list 시작")

	var beneficiary string
	var imagePath string

	db := dbConn()

	// rows, err := db.Query("select * from groupmember where groupid = ?",input_id)
	rows, err := db.Query("select m.imagePath, c.beneficiary from member as m join campaign_share_list as c on c.beneficiary = m.id where c.campaign_seq=?", seq)

	if err != nil {
		fmt.Println(err)
	}
	defer rows.Close()

	// 빈 배열 만들기
	var groupmemberlist = []userId_ImagePath{}

	for rows.Next() {
		err := rows.Scan(&imagePath, &beneficiary)
		if err != nil {
			fmt.Println(err)
			fmt.Println("에러")
			return "no: not found"

		}
		R := userId_ImagePath{}
		R.Memberid = beneficiary
		R.ImagePath = imagePath

		groupmemberlist = append(groupmemberlist, R)

	}
	fmt.Println("에러 없이 get_campaign_share_list 종료 ")

	var jsonData []byte
	jsonData, err = json.Marshal(groupmemberlist)
	if err != nil {
		log.Println(err)
		return "no: getCampaignlist5"
	}
	fmt.Println(string(jsonData))

	defer db.Close()
	return string(jsonData)
}

//member 테이블에서 입력한 아이디로 가지고잇는 account 불러오기
func get_myaccount(input_id string) string {
	var account string

	db := dbConn()
	err := db.QueryRow("select account from member where id =?", input_id).Scan(&account)
	db.Close()
	if err != nil {
		fmt.Println("get_myaccount 쿼리 에러 ")
		fmt.Println(err)
		return ""
	} else {
		return account
	}

}

func get_mytime(input_id string) string {
	var time string

	db := dbConn()
	err := db.QueryRow("select time from member where id =?", input_id).Scan(&time)
	defer db.Close()
	if err != nil {
		fmt.Println("get_mytime 쿼리 에러 ")
		fmt.Println(err)
		return ""
	} else {
		return time
	}
}

// 기부자가 mypage에 들어가면 호출된다
// 담길 내용은 기부자의 잔액 ,  총기부한금액 ,총 기부한 횟수, 캠페인정보 ( 켐피인 이미지 ,작성자 , 제목 , 시작일,종료일 , 내가 기부한 돈)이 담겨있다.
func mypage_Donation(input_id string) response_mypage_donate_result {

	fmt.Println("mypage_Donation 시작 ")

	var campaign_seq string
	var campaign_writer string
	var subject string
	var startDate string
	var endDate string
	var imagePath string
	var account string
	var time string
	sum := "0"
	count := "0"

	//현재 계정의 account 받아오기
	db := dbConn()
	account = get_myaccount(input_id)
	time = get_mytime(input_id)

	// 내가 기부한 정보를 donation_list에서 조회하여 해당 캠페인 번호를 가지고 일치하는 캠페인을 campaign 테이블에서 찾는다.
	//  join member as m on c.id = m.id 부분 지워도 상관없음

	//블록체인에 request 와 id를 넘기고 json을 받아옴== > 파싱 필요

	// block_return := B_select_id("donation_list",input_id)
	block_return := B_select("select_id", input_id, "", "donation_list")
	//받아온 데이터 형태 [{"docType":"donation_list","counting":"0","seq":"1","donation":"user","donate_money":"10000"}]
	// 이 데이터를 []docType에 파싱하여 담는다
	// 담으면 배열안에  {donation_list 1 user 10000} 처럼 값만뽑아 담을 수 있다.
	var data []docType1
	json.Unmarshal([]byte(block_return), &data)
	list := []response_mypage_donate{}
	for i, _ := range data {
		fmt.Println("data[i].Donate_money", data[i])
		db.QueryRow("select seq,id,subject,startDate,endDate,imagePath from campaign where seq=?", data[i].Seq).Scan(&campaign_seq, &campaign_writer, &subject, &startDate, &endDate, &imagePath)

		// 기부자가 마이페이지를 들어갔을 때 불러올 캠페인 정보 객체
		tmp := response_mypage_donate{}
		tmp.Campaign_seq = campaign_seq
		tmp.Donate_money = data[i].Money //내가 기부한 돈
		tmp.Id = id
		tmp.Subject = subject
		tmp.StartDate = startDate
		tmp.EndDate = endDate
		tmp.ImagePath = imagePath
		list = append(list, tmp)

		////////////////////// 총 기부한 횟수와 총기부한 금액 계산 ///////////////////
		// 카운팅

		//String to integer
		tmp_count, err := strconv.Atoi(count)
		if err != nil {
			fmt.Print(err)
		}
		tmp_count = tmp_count + 1

		//integer to string
		count = strconv.Itoa(tmp_count)

		// 총합 기부한 금액 계산
		tmp_sum, err := strconv.Atoi(sum)
		tmp_donate_money, err := strconv.Atoi(data[i].Money)
		tmp_sum = tmp_sum + tmp_donate_money
		sum = strconv.Itoa(tmp_sum)

		fmt.Println(" 카운팅 : 총합 ", count, sum)
		///////////////////////////////////////////////////////////////////////////
	}

	// for문으로 파싱한 데이터의 seq와 money를 꺼낸다

	// 꺼낸 데이터의  seq번호를 기준으로 campaign의 seq와 비교하여 일치하는 campaign정보 받아오기

	// 받은 정보를 response_mypage_donate 객체로 만들어 배열에 담기

	// rows ,err :=db.Query("select d.campaign_seq,d.donate_money, c.id,c.subject,c.startDate,c.endDate,c.imagePath from donation_list as d join campaign as c on d.campaign_seq = c.seq join member as m on c.id = m.id where d.donation=?",input_id)

	// 총 기부한 기부 리스트 json
	jsonData, err := json.Marshal(list)
	if err != nil {
		fmt.Println("json 파싱 에러 ")

	}

	// 내가기부한 봉사활동 시간 리스트
	donate_time := mypage_Donation_in_donate_time(input_id)
	json_donate_time, err := json.Marshal(donate_time)
	if err != nil {
		fmt.Println("json 파싱 에러 ")

	}
	//블록에 저장된 챌린지를 성공하여 기부한 금액
	challenge_tmp := mypage_Donation_in_challenge(input_id)
	json_challenge, err := json.Marshal(challenge_tmp)
	if err != nil {
		fmt.Println("json 파싱 에러 ")

	}

	// 결과 , 기부한 횟수 ,총 기부금액 ,내 아이디에 충전된 돈 잔고 , 내가 기부한 캠페인 리스트 (이미지,작성자,제목,시작일,종료일,금액 )
	//추가 기부한 봉사활동 시간
	tmp_result := response_mypage_donate_result{}
	tmp_result.Result = "ok"
	tmp_result.Count = count
	tmp_result.Sum = sum
	tmp_result.Account = account
	tmp_result.Time = time
	tmp_result.Response_mypage_donate = string(jsonData)
	tmp_result.Response_mypage_donate_volunteer = mypage_Donation_in_volunteer_time(input_id)
	tmp_result.Response_mypage_donate_time = string(json_donate_time)
	tmp_result.Response_mypage_challenge = string(json_challenge)
	// tmp_result.Response_mypage_challenge_donate
	defer db.Close()
	return tmp_result
}

//내가 참여한 봉사활동 별 총시간을 합한 시간과 각 봉사활동 정보 가져오기
func mypage_Donation_in_volunteer_time(input_id string) string {
	fmt.Println("mypage_Donation_in_volunteer_time")
	var log string
	var seq string
	var start_time string
	var end_time string
	var total int
	db := dbConn()
	list := []response_mypage_donate_volunteer{}
	//1.내 아이디가 들어간 volunteer_time 테이블을 조회해서 해당 seq를 중복없이 찾는다
	//2.찾은 seq로 해당 volunteer_recruitment 데이터를 조회한다.
	//3.조회한 값의 seq번호와 내아이디가 일치하는 volunteer_time을 찾아 총 시간을 계산한다
	//4.마지막으로 volunteer_recruitment 데이터와 , time 형태로 반환한다.

	//1.
	rows, err := db.Query("select distinct volunteer_recruitment_seq from volunteer_time where status='true' AND volunteer=?", input_id)
	if err != nil {
		log = "no"
	} else {
		for rows.Next() {
			total = 0
			rows.Scan(&seq)
			//2.
			result, info := volunteer_recruitment_detail_page(seq)
			fmt.Println(result, info)
			//3.
			rows1, err := db.Query("select start_time,end_time from volunteer_time where status='true' AND volunteer=? AND volunteer_recruitment_seq=?", input_id, seq)
			if err != nil {
				log = "no"
			} else {
				for rows1.Next() {
					rows1.Scan(&start_time, &end_time)
					fmt.Println("start_time", start_time, "end_time", end_time)
					int_start_time, err := strconv.Atoi(start_time)
					int_end_time, err := strconv.Atoi(end_time)
					int_total := int_end_time - int_start_time
					int_total = int_total / 60000
					fmt.Println("int_total", int_total)
					total = total + int_total
					fmt.Println("total", total)
					if err != nil {
						log = "no"
					}
				}

			}
			//4.
			object := response_mypage_donate_volunteer{info, total}
			list = append(list, object)

		}

	}
	jsondata, err := json.Marshal(list)
	fmt.Println(log)
	defer db.Close()
	return string(jsondata)
}

// 시간 기부 봉사홀동에 내가 기부한 시간 + 시간 모금 봉사활동 정보
func mypage_Donation_in_donate_time(input_id string) []getTimeCampaign {
	//내가 기부한봉사활동시간이 있는 seq와 시간을 가져온다
	var campaign_seq, time string
	list := []getTimeCampaign{}
	db := dbConn()
	conn, err := db.Query("select campaign_seq,time from time_campaign_joiner_list where joiner=?", input_id)
	if err != nil {

	} else {
		for conn.Next() {
			conn.Scan(&campaign_seq, &time)
			R := detail_time_campaignActivity(campaign_seq)
			R.Time = time
			list = append(list, R)
		}
	}
	defer db.Close()
	//가져온 정보마다 모금 정보를 받아온다
	return list
}

//시간 기부 봉사활동이 성공하여 블록에 저장된 time_donate를 불러온다
func mypage_Donation_in_challenge(input_id string) []getTimeCampaign {
	var time, seq string

	list := []getTimeCampaign{}

	db := dbConn()
	conn, err := db.Query("select seq from time_campaign where donation=?", input_id)
	if err != nil {

	} else {
		for conn.Next() {
			conn.Scan(&seq)
			R := detail_time_campaignActivity(seq)
			R.Time = time
			list = append(list, R)
		}

	}
	defer db.Close()
	return list
}

//  기부자 마이페이지에서 충전하기를 통해 돈을 충전하면 현재 가지고있는 돈에 + 충전할돈을 합하여 update한다.
func money_charge(input_id string, input_money string) string {

	db := dbConn()

	var account string
	var err error
	// 현재 가지고있는 돈 불러오기
	account = get_myaccount(input_id)

	var tmp_account int
	var tmp_input_money int
	var total_money int
	var money string

	// string to integer
	tmp_account, err = strconv.Atoi(account)
	tmp_input_money, err = strconv.Atoi(input_money)
	total_money = tmp_account + tmp_input_money
	//  integer to string
	money = strconv.Itoa(total_money)

	// 충전한 돈과 기존에 있던돈을 합쳐 update한다
	conn, err := db.Prepare("update member SET account = ? where id = ?")

	if err != nil {
		fmt.Println(err)
		return "no"
	} else {
		//마지막으로 업데이트한돈을 조회해봄 ( 사용 안함 )
		var id string
		var account string
		conn.Exec(money, input_id)
		db.QueryRow("select account,id from member where id = ?", input_id).Scan(&account, &id)
		defer db.Close()
		fmt.Println(id, account)
		return "ok"
	}

}

//기부단체로 로그인 할 경우 마이페이지로 이동하게되면 자신이 등록한 켐페인(번호,작성자,캠페인 대표이미지 ,시작일 , 종료일 , 내용,캠페인 별 기부금 ,진행상황) 리스트와
//자신을 등록한 수혜자 리스트 ( 이미지 , 아이디 )를 가져온다.
func mypage_foundation(input_id string) (string, string, string, string, string) {

	var log string

	db := dbConn()

	var seq string
	var id string
	var subject string
	var imagePath string
	var startDate string
	var endDate string
	var content string
	var collection string
	var doing string

	//내 아이디로 등록된 모든 캠페인 정보 불러오기
	rows, err := db.Query("select * from campaign where id = ?", input_id)

	beneficiary_campaign_info_list := []beneficiary_campaign_info{}
	if err != nil {
		log = "no"
		fmt.Println("no1")
		fmt.Println(err)

	} else {

		for rows.Next() {

			//캠페인 정보를 받아온다
			rows.Scan(&seq, &id, &subject, &imagePath, &startDate, &endDate, &content, &collection, &doing)

			var each_total_donate string

			//블록에서받아오는 부분

			// 해당 캠페인에 시퀀스와 일치하는 기부금액을 불러와 합산한다 == > 총 모금된 기부액 사용될 예정.

			// block_return := B_select_seq("donation_list",seq)
			block_return := B_select("select_seq", "", seq, "donation_list")
			//받아온 데이터 형태 [{"docType":"donation_list","counting":"0","seq":"1","donation":"user","donate_money":"10000"}]
			// 이 데이터를 []docType에 파싱하여 담는다
			// 담으면 배열안에  {donation_list 1 user 10000} 처럼 값만뽑아 담을 수 있다.
			var data []docType1
			json.Unmarshal([]byte(block_return), &data)

			for i, _ := range data {
				fmt.Println("data[i].Donate_money", data[i])
				var tmp_donate int
				var tmp_total int

				// 캠패인별  모금된 기부금 계산
				tmp_donate, err = strconv.Atoi(data[i].Money)
				tmp_total, err = strconv.Atoi(each_total_donate)
				tmp_total = tmp_total + tmp_donate
				each_total_donate = strconv.Itoa(tmp_total)
			}

			//만약 캠페인에 모금된 기부금이 없는 경우
			if each_total_donate == "" {
				each_total_donate = "0"

			}
			// 캠페인의 정보 ( 시퀀스 , 작성자 ,제목 ,이미지 ,시작일,종료일,내용, 이 캠페인의 모금된 금액 ,현재 진행 상태 )
			R := beneficiary_campaign_info{seq, id, subject, imagePath, startDate, endDate, content, each_total_donate, doing}
			beneficiary_campaign_info_list = append(beneficiary_campaign_info_list, R)
			log = "ok"

		}

		log = "ok"

	}

	//time_campaign에 id가 기부단체 인 seq를 찾아서 detail_time_campaignActivity(seq)로 보내서 받아오기
	time_campaign_list := []getTimeCampaign{}

	tmp, err := db.Query("select seq from time_campaign where id=?", input_id)
	if err != nil {

	} else {
		for tmp.Next() {
			tmp.Scan(&seq)
			r := detail_time_campaignActivity(seq)
			time_campaign_list = append(time_campaign_list, r)
			fmt.Println(r)
		}
	}
	tim_campaign_json, err := json.Marshal(time_campaign_list)
	if err != nil {
		fmt.Println("json 파싱 에러 ")
		fmt.Println("no3")
		log = "no"
	}

	//input_id가 진행한 봉사활동 리스트 불러오기
	//input here to do
	tmp_result, tmp_mypage_foundation_in_volunteer_recruitment := mypage_foundation_in_volunteer_recruitment(input_id)
	if tmp_result == "no" {
		log = tmp_result
	} else if tmp_result == "ok" {
		log = tmp_result
	}

	////////////////////////
	fmt.Println(tmp_mypage_foundation_in_volunteer_recruitment)

	var campaign_json []byte
	campaign_json, err = json.Marshal(beneficiary_campaign_info_list)
	if err != nil {
		fmt.Println("json 파싱 에러 ")
		fmt.Println("no3")
		log = "no"
	}

	//이 기부단체를 등록한 수혜자 리스트
	lookup_member := lookupGroupmember(input_id)
	var lookup_member_json []byte
	lookup_member_json, err = json.Marshal(lookup_member)
	if err != nil {
		fmt.Println("json 파싱 에러 ")
		fmt.Println("no4")
		log = "no"

	}
	defer db.Close()
	return log, string(campaign_json), string(lookup_member_json), tmp_mypage_foundation_in_volunteer_recruitment, string(tim_campaign_json)
}

//mypage_foundation 에서 //input_id가 진행한 봉사활동 리스트 불러오기
func mypage_foundation_in_volunteer_recruitment(input_id string) (string, string) {
	var log string
	var seq string
	list := []getVolunteerRecruitment{}
	db := dbConn()
	rows, err := db.Query("select seq from volunteer_recruitment where id=?", input_id)
	if err != nil {
		log = "no"
	} else {
		for rows.Next() {
			rows.Scan(&seq)
			result, response := volunteer_recruitment_detail_page(seq)
			if result == "no" {
				log = "no"
			} else {
				var u getVolunteerRecruitment
				json.Unmarshal([]byte(response), &u)

				list = append(list, u)
				log = "ok"
			}
		}
	}
	jsondata, err := json.Marshal(list)
	if err != nil {
		log = "no"
	}
	defer db.Close()
	return log, string(jsondata)
}

// 메인 페이지에서 캠페인을 터치하였을때 해당 캠페인의 시퀀스번호를 바탕으로 해당 켐페인의 제목,작성자,이미지,시작일,종료일,진행중유무,모은금액 을 받아온다.
func campaign_detail_page(input_seq string) string {

	//캠페인 정보 , 기부자 아아디 + 이미지 , 수혜자 아이디, 이미지
	//1. 캠페인 정보

	fmt.Println("캠페인 디테일 정보 가져오기 시작")

	var seq string
	var id string
	var subject string
	var imagePath string
	var startDate string
	var endDate string
	var content string
	var collection string
	var doing string

	db := dbConn()

	err := db.QueryRow("select * from campaign where seq=?", input_seq).Scan(&seq, &id, &subject, &imagePath, &startDate, &endDate, &content, &collection, &doing)
	list := []getCampaign{}
	if err != nil {
		fmt.Println("캠페인 디테일 정보 쿼리 에러")
		fmt.Println(err)
	}

	// 객체를만든다
	R := getCampaign{}
	R.Seq = seq
	R.Id = id
	R.Subject = subject
	R.ImagePath = imagePath
	R.StartDate = startDate
	R.EndDate = endDate
	R.Content = content
	R.Collection = collection
	R.Doing = doing
	list = append(list, R)
	//가져온 캠페인정보의 seq로 기부자 리스트와 수혜자 리스트를 가져온다
	var jsonData []byte
	jsonData, err = json.Marshal(list)
	if err != nil {
		log.Println(err)
	}
	defer db.Close()
	return string(jsonData)

}

// 메인 페이지에서 캠페인을 터치하였을때 해당 캠페인의 시퀀스번호를 바탕으로 해당 켐페인에 참여한 기부자의 아이디와 이미지를 받아온다
func campaign_detail_page_donate_list(intput_seq string) string {

	var imagePath string

	var list = []userId_ImagePath{}
	db := dbConn()

	//블록에서 가자온 데이터를 담을 배열
	var data []docType1
	// block_return:=B_select_seq("donation_list",intput_seq)
	block_return := B_select("select_seq", "", seq, "donation_list")
	json.Unmarshal([]byte(block_return), &data)
	log.Println(block_return)
	for i, _ := range data {

		log.Println("data[i].id " + data[i].Id + " ,data[i].Money :" + data[i].Money + " ,data[i].Seq :" + data[i].Seq)
		db.QueryRow("select imagePath from member where id = ?", data[i].Id).Scan(&imagePath)

		T := userId_ImagePath{data[i].Id, imagePath}
		list = append(list, T)
	}

	jsonData, err := json.Marshal(list)
	if err != nil {
		log.Println(err)
	}
	defer db.Close()
	return string(jsonData)
}

// 메인 페이지에서 캠페인을 터치하였을때 해당 캠페인의 시퀀스번호를 바탕으로 해당 켐페인에 등록된 수혜자 아이디와 이미지를 받아온다
func campaign_detail_page_share_list(intput_seq string) string {

	tmp := get_campaign_share_list(intput_seq)
	return tmp
}

// 기부자가 지정한 캠페인에 자신의아이디와 기부할 금액을 선택하여 넘겨준다
// 먼저 donation_list에 내 아이디 ,캠페인 시퀀스 , 금액을등록한다.
// 이후 캠페인 시퀀스의 collection을 불러와 내가 기부하려는 금액을 더해 update 해준다
// 마지막으로 내가 기부한 금액을 member 테이블의 내아이디 account에서 뺀다.
func give_donate(input_seq string, input_id string, input_money string) string {

	db := dbConn()

	// input_money를 member table에서 자신의 account 와 비교한 후
	// account 보다 적은 돈을 기부한다면 진행 / 아닌경우 no return
	// 진행 일 경우 account 에 직행 하려는 기부금 뺀 금액 update는 맨마지막에
	// donation_list  table에 기부내역 입력하고
	// campaign table 에서 선택한 캠페인의 seq에 collection 을 불러와 현재 collection 과 input_money 를 합하여 update 한다

	var campaign_money string
	var account_money string

	//자신의 잔고
	err := db.QueryRow("select account from member where id = ?", input_id).Scan(&account_money)

	if err != nil {

		fmt.Println(err)
		return "no:give_donate1"
	} else {

		//string to integer
		int_account_money, err := strconv.Atoi(account_money)
		int_input_money, err := strconv.Atoi(input_money)
		if err != nil {
			fmt.Println(err)
		}

		if int_account_money < int_input_money {
			fmt.Println(" 유저가 가지고있는 돈보다 많은 돈을 기부하려고 하기때문에 실패 ")
			return "no:give_donate2"
		} else {

			fmt.Println(" 유저가 가지고있는 돈보다 적은 돈을 기부하려고 하기때문에 성공 ")
			//donation_list테이블에 자신의 아이디와 캠페인 seq , 기부하려는 금액을 넣는다.
			// 이부분 블록체인으로 변경
			var counting string
			db.QueryRow("select count(*) from block_count").Scan(&counting)

			////////////////////////////////////////////////////////////////////////////
			//블록에 저장 //////////////////////////
			block_return := B_insert("donation_list", counting, input_seq, input_id, input_money)
			fmt.Println("block return ", block_return)
			tmp_conn, tmp_err := db.Prepare("insert into block_count(counting)value('1')")
			if tmp_err != nil {
				fmt.Println(tmp_err)
			}
			tmp_conn.Exec()

			// 현재 지정한 캠페인에 현재 모여있는 기부금
			err = db.QueryRow("select collection from campaign where seq = ?", input_seq).Scan(&campaign_money)
			if err != nil {
				fmt.Println("선택한 캠페인의 총 기부금액 불러오기 실패 ")
				fmt.Println(err)
				return "no:give_donate4"
			} else {
				fmt.Println("선택한 캠페인의 총 기부금액 불러오기 성공 ", campaign_money)

				//string to integer
				int_campaign_money, err := strconv.Atoi(campaign_money)
				int_campaign_money = int_campaign_money + int_input_money
				//integer to string
				//현재 캠페인에 등록된 금액과 내가 기부하려는금액을 합하여 string으로 변환한다.
				campaign_money = strconv.Itoa(int_campaign_money)
				fmt.Println("기부 하려는 금액 ", input_money)

				//변환한 돈을 해당 campaign collection에 update한다.
				conn, err := db.Prepare("update campaign set collection = ? where seq = ?")
				if err != nil {
					fmt.Println(err)
					return "no:give_donate5"
				} else {
					fmt.Println("선택한 캠페인의 총 기부금액 처리후 금액 ", campaign_money)
					conn.Exec(campaign_money, input_seq)

					//기부가 성공하면 자신의 account에서 해당금액을 빼서 update한다.
					int_account_money = int_account_money - int_input_money
					account_money = strconv.Itoa(int_account_money)
					conn, err := db.Prepare("update member set account =? where id = ?")
					if err != nil {
						fmt.Println(err)
						return "no:give_donate6"
					} else {
						conn.Exec(account_money, input_id)
						defer db.Close()
						fmt.Println("기부하기 후 현재 내 잔고 불러오기 성공 ", account_money)
						return "ok"
					}
				}

			}
		}

	}

}

//기부 단체에서 캠페인 종료를 요청한다
//1.  기부 단체에서 전달받은 캠페인 seq를 바탕으로 campaign에서 seq를 조회한후 collection을 가지고 온다
//2.  기부 단체에서 전달받은 캠페인 seq를 바탕으로 campaign_share_list에서 campaign_seq 를 기준으로 count 를 불러 나눔대상자 수를 확인한다
//3.  불러온 collection을 count로 나눠 각각 얼마씩 분배되야하는지 계산한다.
//4.  그 다음 나눠줄대상을 찾기위해  campaign_share_list 테이블에서  beneficiary을 for문으로 불러온다

//*************   지금은 sql에 benenficiary 에게 나눠주는 부분을 저장하지만 나중에는 블록체인으로 변경할 부분******************//
//5 임시로 만든 campaign_end_share_beneficiary table에 seq,beneficiary , 나눈 금액을 넣어 저장한다.
//6.    campaign_end_share_beneficiary에 저장한 후 member table에 id가 beneficiary인 컬럼을 찾아 account를 불러온다
//7.    이후 account와 collection을 더하여 member table account에 update를 한다.
//*************************************************************************************************************************//
//8. 마지막으로 campaign의 doing 을 false로 업데이트 한다.
func end_signal_campaign(input_seq string) string {

	db := dbConn()

	var collection string
	var message string
	//1.
	err := db.QueryRow("select collection from campaign where seq = ?", input_seq).Scan(&collection)
	if err != nil {
		fmt.Println("캠페인 종료 쿼리 에러1")
		fmt.Println(err)
		message = "no : end_signal_campaign1"
	} else {
		var tmp_collection int
		tmp_collection, err = strconv.Atoi(collection)
		fmt.Println("켐페인에 모인 금액 ", tmp_collection, input_seq)

		//2.
		var count string
		err = db.QueryRow("select count(*) from campaign_share_list where campaign_seq=?", &input_seq).Scan(&count)
		if err != nil {
			fmt.Println("캠페인 종료 쿼리 에러2")
			fmt.Println(err)
			message = "no : end_signal_campaign2"
		} else {

			//3.
			var tmp_count int
			tmp_count, err = strconv.Atoi(count)
			tmp_collection = tmp_collection / tmp_count
			collection = strconv.Itoa(tmp_collection)
			fmt.Println("켐페인에 모인 금액 / 등록된 인원 수 ", tmp_collection)

			//4.
			conn, err := db.Query("select beneficiary from campaign_share_list where campaign_seq = ?", input_seq)
			if err != nil {
				fmt.Println("캠페인 종료 쿼리 에러3")
				fmt.Println(err)
				message = "no : end_signal_campaign3"

			} else {
				var benenficiary string
				//4.검색한 사람수 대로 반복문이 동작한다
				for conn.Next() {
					err = conn.Scan(&benenficiary)

					if err != nil {
						fmt.Println("캠페인 종료 쿼리 에러4")
						fmt.Println(err)
						message = "no : end_signal_campaign4"
						break
					} else {

						//5.블록체인에 정보를 저장하고 block_count 테이블 갯수를 늘린다
						//  block_count는 데이터를 저장할떄 key를 지정해야하는데 해당 키가 중복되지않게 하기 위함이다
						//  키가중복될경우 블록체인 couchdb  안에 데이터가 덮어씌운것처럼보일수있다
						//  ( 실제 데이터는 추가되지만 couchdb table값은덮어씌워짐 )
						var counting_id string
						db.QueryRow("select count(*) from block_count").Scan(&counting_id)

						B_insert("share_list", counting_id, input_seq, benenficiary, collection)

						tmp_conn, tmp_err := db.Prepare("insert into block_count(counting)value('1')")
						if tmp_err != nil {
							fmt.Println(tmp_err)
						}
						tmp_conn.Exec()

						//6.
						var account string
						err = db.QueryRow("select account from member where id=?", benenficiary).Scan(&account)
						if err != nil {
							fmt.Println("캠페인 종료 쿼리 에러6")
							fmt.Println(err)
							message = "no : end_signal_campaign6"
							break
						} else {
							var tmp_account int
							fmt.Println("캠페인에 등록된 대상자의 나눔 진행 전 account  ", account)
							tmp_account, err = strconv.Atoi(account)
							tmp_account = tmp_account + tmp_collection
							account = strconv.Itoa(tmp_account)

							//7.
							conn, err := db.Prepare("update member set account = ? where id=?")
							if err != nil {
								fmt.Println("캠페인 종료 쿼리 에러7")
								fmt.Println(err)
								message = "no : end_signal_campaign7"
								break
							} else {
								conn.Exec(account, benenficiary)
								fmt.Println("캠페인에 등록된 대상자의 나눔 진행 후 account  ", account)
								//8.
								conn, err = db.Prepare("update campaign set doing = ? where seq=?")
								if err != nil {
									fmt.Println("캠페인 종료 쿼리 에러8")
									fmt.Println(err)
									message = "no : end_signal_campaign8"
									break
								} else {
									conn.Exec("false", input_seq)
									fmt.Println("모두 정상 처리 ")
									message = "ok : success"

								}
							}
						}

					}
				}
			}
		}
	}
	fmt.Println("message", message)
	return message
}

// 수혜자로 로그인하여 마이페이지로 이동하면 두가지 정보를 받아온다
// 1. mysql에서 campaign_share_list에서 수혜자가 내 아이디로된 시퀀스를 찾아 , campaign테이블에서 그 시퀀스에 해당하는 캠페인 모든정보
// 2. 블록체인에서 캠페인이 종료될경우 모은 금액을 수혜대상자에게 나눔을 진행하는 내용
func mypage_beneficiary(input_id string) (string, string, string, string, string, string, string) {

	fmt.Println("mypage_beneficiary 시작")

	var log string
	account := get_myaccount(input_id)
	db := dbConn()
	var groupid string
	var block_return, block_return1 string

	beneficiary_campaign_info_list := []beneficiary_campaign_info{}
	getTimeCampaign_list := []getTimeCampaign{}
	// beneficiary_campaign_end_info_list := [] beneficiary_campaign_end_info {}

	//자신의 아이디 ( 수혜자 )로 groupmember에서 조회하여 자신이 등록한 groupid를 찾는다 .만약 등록한 groupid가 없는경우 클라이언트는
	// 그룹을 등록할수있는버튼이 활성화된다.
	err := db.QueryRow("select groupid from groupmember where memberid=?", input_id).Scan(&groupid)
	if err != nil {
		fmt.Println(err)
		log = "no"
	} else {
		var seq string
		var writer string
		var subject string
		var imagePath string
		var startDate string
		var endDate string
		var content string
		var collection string
		var doing string

		//캠페인 나눔 대상자 (campaign_share_list ) 테이블에서 수혜자 (beneficiary)가 자신의 아이디로 등록된 시퀀스를 찾은 후
		// 이 시퀀스를 사용하여 캠패인 테이블에서 해당 캠페인 정보를 모두 가져온다
		rows, err := db.Query(" select c.* from campaign_share_list as l  join campaign as c on l.campaign_seq = c.seq  where l.beneficiary = ?", input_id)
		if err != nil {
			fmt.Println(err)
			log = "no"
		} else {
			//캠페인 정보
			for rows.Next() {
				rows.Scan(&seq, &writer, &subject, &imagePath, &startDate, &endDate, &content, &collection, &doing)
				// 찾은 정보를 바탕으로 캠페인 객체를 저장
				R := beneficiary_campaign_info{seq, writer, subject, imagePath, startDate, endDate, content, collection, doing}
				beneficiary_campaign_info_list = append(beneficiary_campaign_info_list, R)
			}
			log = "ok"

			rows1, err := db.Query("select campaign_seq from time_campaign_share_list where beneficiary=?", input_id)
			if err != nil {
				log = "no19"
			}
			for rows1.Next() {
				rows1.Scan(&seq)
				R := detail_time_campaignActivity(seq)
				getTimeCampaign_list = append(getTimeCampaign_list, R)
			}

			//*************************************나중에 블록에서 받아와야할부분 *************************************
			// 이부분은 내가 등록된 캠페인이 종료되어 나에게 기부금이 나눔된 정보를 받아오는 곳이다
			//  수혜자 (beneficiary) 가 내 아이디로된 정보를 모두 가져온다.

			// block_return =B_select_id("share_list",input_id)
			block_return = B_select("select_id", input_id, "", "share_list")
			block_return1 = B_select("select_id", input_id, "", "mission_share_list")
		}

	}

	campaign_json, err := json.Marshal(beneficiary_campaign_info_list)
	if err != nil {
		fmt.Println(err)
		log = "no"
	}
	time_campaign_json, err := json.Marshal(getTimeCampaign_list)
	if err != nil {
		fmt.Println(err)
		log = "no"
	}

	defer db.Close()
	fmt.Println("log", log)
	return log, account, groupid, string(campaign_json), block_return, string(time_campaign_json), block_return1

}

// 서버에서 매일 0시 1분에 진행중인 캠페인을 대상으로 오늘 날짜와 비교하여 캠페인을 종료시키는 로직이다
func daily_check_campaign_end(input_endDate string) {

	db := dbConn()
	var endDate string
	var seq string
	conn, err := db.Query("select seq,endDate from campaign where doing ='true'")

	file, _ := os.Create(input_endDate + "_campaign")
	if err != nil {
		fmt.Println("no24")
	} else {
		for conn.Next() {
			conn.Scan(&seq, &endDate)
			tmp := camapare_date(input_endDate, endDate)
			if tmp == "ok" {
				//종료할 캠페인
				log := end_signal_campaign(seq)

				defer file.Close()
				fmt.Fprintln(file, log+" : "+seq+" , "+endDate)
			} else if tmp == "no" {
				//종료하지않을켐페인
				fmt.Println("no25")
			}
		}
	}
	defer db.Close()
}

// 서버에서 매일 0시 1분에 진행중인 캠페인을 대상으로 오늘 날짜와 비교하여 캠페인을 종료시키는 로직이다
func daily_check_volunteer_recruitment_end(input_endDate string) {

	db := dbConn()
	var endDate string
	var seq string
	conn, err := db.Query("select seq,endDate from volunteer_recruitment where doing ='true'")
	file, _ := os.Create(input_endDate + "_volunteer_recruitment")

	if err != nil {
		fmt.Println("no21")
	} else {
		for conn.Next() {
			conn.Scan(&seq, &endDate)
			tmp := camapare_date(input_endDate, endDate)
			if tmp == "ok" {
				//종료할 캠페인
				conn1, err := db.Prepare("update volunteer_recruitment set doing='false' where seq =?")
				if err != nil {
					fmt.Println("no22")
				}
				conn1.Exec(seq)
				defer file.Close()
				fmt.Fprintln(file, "ok"+" : "+seq+" , "+endDate)

			} else if tmp == "no" {
				//종료하지않을켐페인
				fmt.Println("no23")
			}
		}
	}
	defer db.Close()
}

// 서버에서 매일 0시 1분에 진행중인 캠페인을 대상으로 오늘 날짜와 비교하여 캠페인을 종료시키는 로직이다
func daily_check_time_campaign_end(input_endDate string) {

	db := dbConn()
	var log string
	var endDate string
	var seq string
	conn, err := db.Query("select seq,endDate from time_campaign where doing ='true'")
	file, _ := os.Create(input_endDate + "time_campaign")

	if err != nil {
		log = "no22"
	} else {
		for conn.Next() {
			conn.Scan(&seq, &endDate)
			tmp := camapare_date(input_endDate, endDate)
			fmt.Println("성공 실패 유무 ", tmp)
			if tmp == "ok" {
				//종료할 캠페인
				log = time_campaign_end(seq)
				defer file.Close()
				fmt.Fprintln(file, log+" : "+seq+" , "+endDate)

			} else if tmp == "no" {
				//종료하지않을켐페인
				log = "no23"
			}
		}
	}
	defer db.Close()
}

//날짜 비교하여 캠페인 종료 날짜보다 현재 날짜가 빠르면  no를 느리면 yes를 보낸다
func camapare_date(input_endDate string, campaign_endDate string) string {

	var log string

	current_year, current_month, current_day := Date_parsing(input_endDate)
	campaign_year, campaign_month, campaign_day := Date_parsing(campaign_endDate)

	if campaign_year < current_year {
		// 연도가 작을 경우 무조건 처리
		log = "ok"
	} else if campaign_year == current_year {
		// 연도가 같을 경우
		if campaign_month < current_month {
			//월이 작을 경우 무조거 처리
			log = "ok"
		} else if campaign_month == current_month {
			//월이 같은 경우
			if campaign_day < current_day {
				//일이 작은경우 무조건 처리
				log = "ok"
			} else if campaign_day == current_day {
				//일이 같으면 아무동작하지않음
				log = "no"
			} else if campaign_day > current_day {
				//일이 크면 아무동작하지않음
				log = "no"
			}

		} else if campaign_month > current_month {
			//월이 크면 아무동작하지않음
			log = "no"
		}

	} else if campaign_year > current_year {
		//연도가 클 경우 아무동작하지않음
		log = "no"
	}

	return log
}

//입력받은 날짜를 파싱하여 숫자만 뽑아냄
func Date_parsing(input_data string) (int, int, int) {

	tmp := strings.Split(input_data, "년")
	split_year := tmp[0]
	tmp = strings.Split(tmp[1], "월")
	split_month := tmp[0]
	tmp = strings.Split(tmp[1], "일")
	split_day := tmp[0]

	int_year, err := strconv.Atoi(split_year)
	int_month, err := strconv.Atoi(split_month)
	int_day, err := strconv.Atoi(split_day)
	if err != nil {
		fmt.Println(err)
	}
	return int_year, int_month, int_day
}

//수혜자가 lookupFoundation을 통해 기부단체 리스트중 하나의 단체를 선택하여 등록을 요청하면 동작하는 로직이다
//자신의 아이디를 memberid ,기부단체 아이디를 groupid로하여 groupmember 테이블에 등록한다.
func singupFoundation(input_id string, input_foundationid string) string {

	fmt.Println("singupFoundation", input_id, input_foundationid)
	var log string
	db := dbConn()

	conn, err := db.Prepare("insert into groupmember (groupid,memberid) value (?,?)")
	if err != nil {
		log = "no"
		fmt.Println(err)
	} else {
		conn.Exec(input_foundationid, input_id)
		log = "ok"
	}
	defer db.Close()
	return log
}

//수혜자가 마이페이지에서 자신이 등록한 기부단체를 삭제 할때 동작한다
// groupmember에서 자신의 memberid가 자신의 아이디인 경우 삭제한다

func remove_foundation(input_id string) string {
	var log string
	db := dbConn()

	conn, err := db.Prepare("delete from groupmember where memberid = ?")
	if err != nil {
		fmt.Println(err)
		log = "error"
	} else {
		conn.Exec(input_id)
		log = "ok"
	}
	defer db.Close()
	return log
}

func write_volunteer_recruitment(input_id string, input_subject string, r *http.Request, input_startDate string, input_endDate string, input_startTime string, input_endTime string, input_location string, input_content string) string {
	//이미지저장하고 파일 경로 받아오기
	var log string

	imagePath := saveImage(r)

	barcode := make_random_value()

	fmt.Println("write_volunteer_recruitment", input_id, input_subject, imagePath, input_startDate, input_endDate, input_startTime, input_endTime, input_location, input_content)
	db := dbConn()

	conn, err := db.Prepare("insert into volunteer_recruitment (id,subject,imagePath,startDate,endDate,startTime,endTime,location,content,barcode) value(?,?,?,?,?,?,?,?,?,?)")
	if err != nil {
		log = "error"
	} else {
		conn.Exec(input_id, input_subject, imagePath, input_startDate, input_endDate, input_startTime, input_endTime, input_location, input_content, barcode)
		log = "ok"
	}

	defer db.Close()
	return log
}

//자원봉사 모집 페이지에서 자원봉사모집글3개씩 불러오기 ( 페이징 처리됨 )
func get_volunteer_recruitment(input_paiging_num string) (string, string) {

	db := dbConn()
	var seq string
	var id string
	var subject string
	var imagePath string
	var startDate string
	var endDate string
	var startTime string
	var endTime string
	var location string
	var content string
	var doing string
	var total_count int
	var barcode string

	db.QueryRow("select count(*) from volunteer_recruitment").Scan(&total_count)

	tmp_input_paiging_num, err := strconv.Atoi(input_paiging_num)
	if total_count < tmp_input_paiging_num {
		fmt.Println("더이상불러올 데이터")
		return "paiging", ""
	}
	rows, err := db.Query("select * from volunteer_recruitment order by seq desc limit ?,3", input_paiging_num)
	if err != nil {
		fmt.Println("에러 있음")
		return "no", ""
	}
	defer rows.Close()
	list := []getVolunteerRecruitment{}
	for rows.Next() {
		err := rows.Scan(&seq, &id, &subject, &imagePath, &startDate, &endDate, &startTime, &endTime, &location, &content, &doing, &barcode)
		if err != nil {
			fmt.Println(err)
			fmt.Println("에러")
			return "no", ""

		}
		R := getVolunteerRecruitment{seq, id, subject, imagePath, startDate, endDate, startTime, endTime, location, content, doing, barcode}
		list = append(list, R)

	}
	jsonData, err := json.Marshal(list)
	defer db.Close()
	return "ok", string(jsonData)
}

//선택한 봉사활동 페이지 정보 받아오기
func volunteer_recruitment_detail_page(input_seq string) (string, string) {
	fmt.Println("volunteer_recruitment_detail_page")
	var seq string
	var id string
	var subject string
	var imagePath string
	var startDate string
	var endDate string
	var startTime string
	var endTime string
	var location string
	var content string
	var doing string
	var barcode string
	db := dbConn()
	db.QueryRow("select * from volunteer_recruitment where seq=?", input_seq).Scan(&seq, &id, &subject, &imagePath, &startDate, &endDate, &startTime, &endTime, &location, &content, &doing, &barcode)
	R := getVolunteerRecruitment{seq, id, subject, imagePath, startDate, endDate, startTime, endTime, location, content, doing, barcode}
	jsonData, err := json.Marshal(R)
	if err != nil {
		return "no", ""
	}
	defer db.Close()
	return "ok", string(jsonData)
}

func change_image_update_volunteer_recruitment(input_seq string, input_id string, input_subject string, r *http.Request, input_startDate string, input_endDate string, input_startTime string, input_endTime string, input_location string, input_content string) string {
	//이미지저장하고 파일 경로 받아오기
	var log string
	imagePath := saveImage(r)
	db := dbConn()
	fmt.Println(imagePath)
	fmt.Println("change_image_update_volunteer_recruitment", input_seq, input_id, input_subject, imagePath, input_startDate, input_endDate, input_startTime, input_endTime, input_location, input_content)

	conn, err := db.Prepare("update volunteer_recruitment set subject=?,imagePath=?,startDate=?,endDate=?,startTime=?,endTime=?,location=?,content=? where seq=?")
	if err != nil {
		log = "error"
	} else {
		conn.Exec(input_subject, imagePath, input_startDate, input_endDate, input_startTime, input_endTime, input_location, input_content, input_seq)
		log = "ok"
	}

	defer db.Close()
	return log
}

func not_change_image_update_volunteer_recruitment(input_seq string, input_subject string, input_startDate string, input_endDate string, input_startTime string, input_endTime string, input_location string, input_content string) string {
	//이미지저장하고 파일 경로 받아오기
	var log string

	db := dbConn()

	conn, err := db.Prepare("update volunteer_recruitment set subject=?,startDate=?,endDate=?,startTime=?,endTime=?,location=?,content=? where seq=?")
	if err != nil {
		log = "error"
	} else {
		conn.Exec(input_subject, input_startDate, input_endDate, input_startTime, input_endTime, input_location, input_content, input_seq)
		log = "ok"
	}

	defer db.Close()
	return log
}

func delete_volunteer_recruitment(input_seq string) string {
	var log string

	db := dbConn()

	conn, err := db.Prepare("delete from volunteer_recruitment where seq=?")
	if err != nil {
		log = "error"
	} else {
		conn.Exec(input_seq)
		log = "ok"
	}

	defer db.Close()
	return log
}

func volunteer_time(input_seq string, input_id string, input_barcode string, input_date string, input_currrent string, input_check_volunteer_start string) string {
	fmt.Println(input_seq, input_id, input_barcode, input_currrent, input_date, input_check_volunteer_start)
	var count string
	var log string
	var status string
	var varcode string
	db := dbConn()

	//봉사활동 시작인지 종료인지 구분한다

	db.QueryRow("select barcode from volunteer_time where barcode = ?", input_barcode).Scan(&varcode)

	if input_barcode == barcode {
		if input_check_volunteer_start == "true" { //시작일경우

			db.QueryRow("select count(*) from volunteer_time where volunteer_recruitment_seq=? AND volunteer=? AND date=?", input_seq, input_id, input_date).Scan(&count)

			if count == "1" { //이미 시작한게있는지 select ( 게시물 시퀀스 , 기부자 , 날자 ) == > 이미 있다고 리턴해주기
				log = "already start"
			} else { //없다면 insert
				conn, err := db.Prepare("insert into volunteer_time(volunteer_recruitment_seq,volunteer,date,start_time,barcode) value (?,?,?,?,?)")
				if err != nil {
					log = "error"
				} else {
					conn.Exec(input_seq, input_id, input_date, input_currrent, input_barcode)
					log = "ok"
				}
			}

		} else { //종료일경우

			//완료 상태가 (statue) true 인지 false 인지 ==> true = 시작/종료 완료 , false = 시작만
			db.QueryRow("select status from volunteer_time where volunteer_recruitment_seq=? AND volunteer=? AND date=?", input_seq, input_id, input_date).Scan(&status)
			db.QueryRow("select count(*) from volunteer_time where volunteer_recruitment_seq=? AND volunteer=? AND date=?", input_seq, input_id, input_date).Scan(&count)

			if status == "false" { //완료되지 않은 상태

				if count == "1" { //이미 시작한게있는지 select ( 게시물 시퀀스 , 기부자 , 날자 ) == > 이미 있다고 리턴해주기

					//날짜,게시글번호,작성자가 같고 시작시간이 있는 데이터일 경우 종료시간과 상태를 업데이트 한다.
					conn, err := db.Prepare("update volunteer_time set end_time=?,status='true' where volunteer=? AND date=? AND volunteer_recruitment_seq=?")
					if err != nil {
						log = "error"
					} else {
						conn.Exec(input_currrent, input_id, input_date, input_seq)

						log = update_member_time(input_seq, input_id, input_date)
					}
				} else { //없다면 ==> 시작한거 없다고 리턴하기
					log = "not start"
				}
			} else { //완료된 형태
				log = "already finish"
			}

		}
	} else {
		log = "not barcode"
	}

	defer db.Close()
	return log

}

func update_member_time(input_seq string, input_id string, input_date string) string {
	db := dbConn()
	var log string
	var status string
	var start_time string
	var end_time string
	var member_time string
	// var total string

	//검색한 결과가 true (봉사시작,종료 다 입력완료) 인지확인 한다
	//만약 true이면 시작시간과 종료시간을 가져온다
	// 시작 시간과 종료시간을 뺀후 /60000을 하면 분으로 계산된다
	// 해당 분을 멤버의 time을 가져와 더한후 업데이트한다.
	db.QueryRow("select start_time,end_time,status from volunteer_time where volunteer_recruitment_seq=? AND volunteer=? AND date=?", input_seq, input_id, input_date).Scan(&start_time, &end_time, &status)
	if status == "true" {
		int_start_time, err := strconv.Atoi(start_time)
		int_end_time, err := strconv.Atoi(end_time)
		int_total := int_end_time - int_start_time
		int_total = int_total / 60000
		if err != nil {
			log = "no"
		} else {
			//member 테이블에 time을 가져온다
			// 가져온 time을 숫자로 변환한다
			//변환한숫자 + 계산한 봉사활동시간 ( 분)을 더한다
			// 총 합을 member 테이블 time에 update 한다'
			db.QueryRow("select time from member where id =?", input_id).Scan(&member_time)
			int_member_time, err := strconv.Atoi(member_time)
			int_member_time = int_member_time + int_total
			member_time = strconv.Itoa(int_member_time)
			conn, err := db.Prepare("update member set time=? where id=?")
			if err != nil {
				log = "no"
			} else {
				conn.Exec(member_time, input_id)
				log = "ok"
			}
		}

	}
	defer db.Close()
	return log
}

func write_time_campaign_signupActivity() (string, string) {
	var log string
	var id string
	var imagePath string
	list := []userId_ImagePath{}
	db := dbConn()
	rows, err := db.Query("select id,imagePath from member where mode='1'")
	if err != nil {
		log = "no"
	} else {
		for rows.Next() {
			rows.Scan(&id, &imagePath)
			R := userId_ImagePath{id, imagePath}
			list = append(list, R)
			log = "ok"
		}
	}
	jsondata, err := json.Marshal(list)
	if err != nil {
		jsondata = nil
	}
	defer db.Close()
	return log, string(jsondata)
}

func time_campaign_upload(input_id string, input_subject string, r *http.Request, input_money string, input_time string, input_donation string, input_share_list string,
	input_startDate string, input_endDate string, input_content string) string {

	fmt.Println(input_id, input_subject, input_money, input_time, input_donation, input_startDate, input_endDate, input_content)
	var log string
	db := dbConn()
	conn, err := db.Prepare("insert into time_campaign(id,subject,imagePath,money,time,donation,startDate,endDate,content)value (?,?,?,?,?,?,?,?,?)")
	if err != nil {
		log = "no1"
	} else {
		var tmp_seq string
		imagePath := saveImage(r)

		conn.Exec(input_id, input_subject, imagePath, input_money, input_time, input_donation, input_startDate, input_endDate, input_content)
		err = db.QueryRow("select seq from time_campaign WHERE id = ? AND imagePath = ?", input_id, imagePath).Scan(&tmp_seq)
		if err != nil {
			log = "no2"
			fmt.Println(err)
		} else {
			tmp_list := strings.Split(input_share_list, "&*")
			for i := 0; i < len(tmp_list); i++ {
				conn, err = db.Prepare("insert into time_campaign_share_list(campaign_seq,beneficiary) value (?,?)")

				if err != nil {
					log = "no3"
				} else {
					conn.Exec(tmp_seq, tmp_list[i])
					log = "ok"
					fmt.Println("time_campaign_upload success")
					// return "ok : signupCampaing";
				}

			}
			fmt.Println("time_campaign_upload 종료")
		}
	}
	defer db.Close()
	return log
}

// 시간 기부 게시글 번호로 1개씩 가져오기
func detail_time_campaignActivity(input_seq string) getTimeCampaign {

	var seq string
	var id string
	var subject string
	var imagePath string
	var money string
	var currentTime string
	var time string
	var donation string
	var startDate string
	var endDate string
	var content string
	var doing string
	var mission string
	var permission string
	db := dbConn()

	fmt.Println(input_seq)

	err := db.QueryRow("select * from time_campaign where seq=?", input_seq).Scan(&seq, &id, &subject, &imagePath, &money, &currentTime, &time, &donation, &startDate, &endDate, &content, &doing, &mission, &permission)

	if err != nil {
		fmt.Println(err)
	}
	fmt.Println(seq, id, subject, imagePath, money, time, donation, startDate, endDate, content, doing, mission, permission)
	R := getTimeCampaign{seq, id, subject, imagePath, money, currentTime, time, donation, startDate, endDate, content, doing, mission, permission}

	defer db.Close()
	return R
}

func time_campaignActivity(input_paiging_num string) (string, string) {
	var seq string
	var total_count int
	var tmp_input_paiging_num int
	var log string

	db := dbConn()

	db.QueryRow("select count(*) from time_campaign").Scan(&total_count)
	tmp_input_paiging_num, err := strconv.Atoi(input_paiging_num)
	if total_count < tmp_input_paiging_num {
		fmt.Println("더이상불러올 데이터")
		log = "paiging_end: end"
	}
	var list = []getTimeCampaign{}
	rows, err := db.Query("select seq from time_campaign order by seq desc limit ?,3", input_paiging_num)
	if err != nil {
		log = "no5"
	} else {

		for rows.Next() {

			rows.Scan(&seq)
			fmt.Println("seq ", seq)
			R := detail_time_campaignActivity(seq)
			list = append(list, R)
		}
	}

	jsondata, err := json.Marshal(list)

	if err != nil {
		log = "no6"
	}
	defer db.Close()
	return log, string(jsondata)
}

//기부에 참여한 사용자를 게시글 번호별로 불러온다
func get_time_campaign_joiner_list(input_seq string) string {
	fmt.Println("get_time_campaign_joiner_list 시작")

	var beneficiary string
	var imagePath string

	db := dbConn()

	// rows, err := db.Query("select * from groupmember where groupid = ?",input_id)
	rows, err := db.Query("select m.imagePath, c.joiner from member as m join time_campaign_joiner_list as c on c.joiner = m.id where c.campaign_seq=?", seq)

	if err != nil {
		fmt.Println(err)
	}
	defer rows.Close()

	// 빈 배열 만들기
	var groupmemberlist = []userId_ImagePath{}

	for rows.Next() {
		err := rows.Scan(&imagePath, &beneficiary)
		if err != nil {
			fmt.Println(err)
			fmt.Println("에러")
			return "no: not found"

		}
		R := userId_ImagePath{}
		R.Memberid = beneficiary
		R.ImagePath = imagePath

		groupmemberlist = append(groupmemberlist, R)

	}
	fmt.Println("에러 없이 get_campaign_share_list 종료 ")

	var jsonData []byte
	jsonData, err = json.Marshal(groupmemberlist)
	if err != nil {
		log.Println(err)
		return "no: getCampaignlist5"
	}
	fmt.Println(string(jsonData))

	defer db.Close()
	return string(jsonData)
}

//캠페인별로 나눔 대상자로 등록된 리스트를 불러온다
// 캠페인 seq를 기준으로 campaign_share_list 테이블리스트 에서 받아온다.
//  해당 campaign_share_list를 json으로 파싱하여 string으로 반환한다.
// (만약 에러가 발생할경우 no : 에러 사유가 반환되고 정상일경우 json형태의 쉐어 대상자 이미지와 아이디가 반한됨 )
func get_time_campaign_share_list(input_seq string) string {
	fmt.Println("get_time_campaign_share_list 시작")

	var beneficiary string
	var imagePath string

	db := dbConn()

	// rows, err := db.Query("select * from groupmember where groupid = ?",input_id)
	rows, err := db.Query("select m.imagePath, c.beneficiary from member as m join time_campaign_share_list as c on c.beneficiary = m.id where c.campaign_seq=?", seq)

	if err != nil {
		fmt.Println(err)
	}
	defer rows.Close()

	// 빈 배열 만들기
	var groupmemberlist = []userId_ImagePath{}

	for rows.Next() {
		err := rows.Scan(&imagePath, &beneficiary)
		if err != nil {
			fmt.Println(err)
			fmt.Println("에러")
			return "no: not found"

		}
		R := userId_ImagePath{}
		R.Memberid = beneficiary
		R.ImagePath = imagePath

		groupmemberlist = append(groupmemberlist, R)

	}
	fmt.Println("에러 없이 get_campaign_share_list 종료 ")

	var jsonData []byte
	jsonData, err = json.Marshal(groupmemberlist)
	if err != nil {
		log.Println(err)
		return "no: getCampaignlist5"
	}
	fmt.Println(string(jsonData))

	defer db.Close()
	return string(jsonData)
}

func time_campaign_upload_permission(input_permission, input_seq, input_id string) string {
	fmt.Println("time_campaign_upload_permission")
	var log string
	var account string
	var money string
	db := dbConn()
	db.QueryRow("select account from member where id=?", input_id).Scan(account)
	db.QueryRow("select money from time_campaign where seq=?", input_seq).Scan(money)

	int_account, err := strconv.Atoi(account)
	int_money, err := strconv.Atoi(money)
	if err != nil {

	}
	if int_account < int_money {
		log = "no7"
	} else {
		conn, err := db.Prepare("update time_campaign set permission=? where seq=?")

		if err != nil {
			log = "no8"
		} else {
			conn.Exec(input_permission, input_seq)
			log = "ok"
		}
	}

	defer db.Close()
	return log
}

//1.내가 가지고있는돈이 입력한 값보다 많은지 체크
//2. 입력한 값 + currentTime이 <= total_time 인지 체크
//3 만약 더 크다면 입력한 값 + currentTime이 > total_time
func give_time_donate(input_seq, input_id, input_time string) string {

	var log string

	db := dbConn()

	//계정이 보유한 시간
	time := get_mytime(input_id)

	int_time, int_input_time := chnage_string_to_int(time, input_time)

	//캠페인에 기부자들이 현재 기부한 시간
	var get_campaign_current_time string

	//캠페인에 목표 시간
	var get_campaign_total_time string
	db.QueryRow("select currentTime,time from time_campaign where seq=?", input_seq).Scan(&get_campaign_current_time, &get_campaign_total_time)

	int_currunt_time, int_total_time := chnage_string_to_int(get_campaign_current_time, get_campaign_total_time)

	//1.내가 가지고있는시간 입력한 값보다 많은지 체크
	if int_input_time < int_time {
		//진행

		if int_input_time <= int_total_time {
			//진행
			if int_input_time+int_currunt_time <= int_total_time {
				fmt.Println("1", int_input_time, int_currunt_time, int_total_time)
				//입력시간 +현재 시간 <= 총 시간 int_input_time 전체 사용
				conn, err := db.Prepare("insert into time_campaign_joiner_list(campaign_seq,joiner,time) value(?,?,?)")
				conn.Exec(input_seq, input_id, input_time)
				conn1, err := db.Prepare("update time_campaign set currentTime=? where seq=?")
				conn1.Exec(strconv.Itoa(int_input_time+int_currunt_time), input_seq)
				conn2, err := db.Prepare("update member set time=? where id=?")
				int_time = int_time - int_input_time
				conn2.Exec(strconv.Itoa(int_time), input_id)
				log = "ok"
				if err != nil {

				}
			} else {
				fmt.Println("2", int_input_time, int_currunt_time, int_total_time)
				//총 시간 = 현재 시간 - 입력시간 계산해서 남은 시간만 int_input_time으로 사용
				int_input_time = int_input_time - int_currunt_time
				conn, err := db.Prepare("insert into time_campaign_joiner_list(campaign_seq,joiner,time) value(?,?,?)")
				conn.Exec(input_seq, input_id, strconv.Itoa(int_input_time))
				conn1, err := db.Prepare("update time_campaign set currentTime=? where seq=?")
				conn1.Exec(get_campaign_total_time, input_seq)
				conn2, err := db.Prepare("update member set time=? where id=?")
				int_time = int_time - int_input_time
				conn2.Exec(strconv.Itoa(int_time), input_id)
				log = "ok"
				if err != nil {

				}
			}

		} else {
			//  총액에서 기부하려는 시간 뺀 나머지 시간만 진행
			// 총시간 = 총시간 -int_input_time 만 사용
			//총 시간 = 현재 시간 - 입력시간 계산해서 남은 시간만 int_input_time으로 사용
			fmt.Println("3", int_input_time, int_currunt_time, int_total_time)
			int_input_time = int_total_time - int_currunt_time
			conn, err := db.Prepare("insert into time_campaign_joiner_list(campaign_seq,joiner,time) value(?,?,?)")
			conn.Exec(input_seq, input_id, strconv.Itoa(int_input_time))
			conn1, err := db.Prepare("update time_campaign set currentTime=? where seq=?")
			conn1.Exec(int_input_time, input_seq)
			conn2, err := db.Prepare("update member set time=? where id=?")
			int_time = int_time - int_input_time
			conn2.Exec(strconv.Itoa(int_time), input_id)
			log = "ok"
			if err != nil {

			}
		}

	} else {
		// 더이상 진행 할 필요없음
	}
	defer db.Close()
	return log
}

func time_campaign_end(input_seq string) string {
	fmt.Println("time_campaign_end")
	var log string
	var currentTime string
	var time string
	var joiner string
	var donation string
	var give_money string
	db := dbConn()
	var counting string
	var beneficiary_count, beneficiary string

	//수혜자 리스트에있는 해당 캠페인 수혜자 수
	db.QueryRow("select count(*) from time_campaign_share_list where campaign_seq=?", input_seq).Scan(&beneficiary_count)

	//블록의 key 값
	db.QueryRow("select count(*) from block_count").Scan(&counting)

	//해당 캠페인 정보 ( 모금한 시간 ,목표시간 ,기부자 , 기부금액 )
	db.QueryRow("select currentTime,time,donation,money from time_campaign where seq=?", input_seq).Scan(&currentTime, &time, &donation, &give_money)

	//미션 성공 실패 확인용
	int_currentTime, int_time := chnage_string_to_int(currentTime, time)

	if int_currentTime >= int_time {
		fmt.Println("성공")
		//해당 시간 캠페인의 상태를 성공으로 바꾼다 .
		conn1, err := db.Prepare("update time_campaign set permission='agree',mission='success',doing='false' where seq=?")
		if err != nil {
			log = "no16"
		} else {
			conn1.Exec(input_seq)
			log = "ok"
		}
		///////////////////////////////////////////////////////////////////
		//미션 성공 - 기부자의 돈을 member account에서 뺀다
		result := del_account(donation, give_money)

		if result {
			//기부자 이름으로 기부금을 블록에 넣는다
			log = B_insert("mission_donation_list", counting, input_seq, donation, give_money)
			//block_account를 1올린다
			tmp_conn, tmp_err := db.Prepare("insert into block_count(counting)value('1')")
			if tmp_err != nil {
				log = "no17"
			}
			tmp_conn.Exec()

			//인원수와 돈을 나누기위해 정수화
			int_give_money, int_beneficiary_count := chnage_string_to_int(give_money, beneficiary_count)
			//블롷에 넣을 돈을 인원수에 맞게 나눈다

			fmt.Println("mission_donation_list", int_give_money, int_beneficiary_count, int_beneficiary_count, input_seq)
			int_give_money = int_give_money / int_beneficiary_count
			fmt.Println("mission_donation_list", int_give_money)

			fmt.Println("???")

			// 이글에  수혜자로 등록된 수혜자를 찾는다
			conn2, err := db.Query("select beneficiary from time_campaign_share_list where campaign_seq=?", input_seq)
			if err != nil {
				fmt.Println("해당 수혜자 찾기 에러")
				fmt.Println(err)
				log = "no18"
			} else {
				for conn2.Next() {
					conn2.Scan(&beneficiary)
					//현재 블록 height 검색
					db.QueryRow("select count(*) from block_count").Scan(&counting)

					fmt.Println("수혜자 정보 넣어 주기 전  ", counting, input_seq, beneficiary, strconv.Itoa(int_give_money))
					//찾은 수혜자마다 블록에 정보를 넣어준다
					log = B_insert("mission_share_list", counting, input_seq, beneficiary, strconv.Itoa(int_give_money))
					//block_account를 1올린다
					tmp_conn1, tmp_err := db.Prepare("insert into block_count(counting)value('1')")
					if tmp_err != nil {
						fmt.Println(tmp_err)
					}
					tmp_conn1.Exec()
					//블록에 저장된 금액을 수혜자의 account에 추가한다.
					add_account(beneficiary, strconv.Itoa(int_give_money))
				}

				log = "ok"
			}

		} else {
			log = "no19"
		}

		//미션 성공 - 수혜자에게 기부자가 기부한 돈 저장
		//수혜자 수 게산, 지급하기로 한 돈 / 수혜자 수 , 연산한 결과데로 블록에 저장

	} else {

		//미션 실패 - 시간 다시 돌려줌
		conn, err := db.Query("select joiner,time from time_campaign_joiner_list where seq=?", input_seq)
		if err != nil {
			log = "no15"
		} else {
			//time_campaign_joiner_list 에 같은 seq에 해당하는 유저를 전체 찾아 시간을 가져와 돌려줌
			for conn.Next() {
				conn.Scan(&joiner, &time)

				refund_time(joiner, time)
			}
			//캠페인의 상태를 doing =false , mission=fail 로 처리함
			conn1, err := db.Prepare("update time_campaign set permission='agree',mission='fail',doing='false' where seq=?")
			if err != nil {
				log = "no16"
			} else {
				conn1.Exec(input_seq)
				log = "ok"
			}

		}
	}
	defer db.Close()
	return log

}

//입력값 두개를 받아서 a가 b보다 크면 true 적으면 false를 반환한다.
func chnage_string_to_int(input_a, input_b string) (int, int) {

	int_input_a, err := strconv.Atoi(input_a)
	int_input_b, err := strconv.Atoi(input_b)
	if err != nil {

	}
	return int_input_a, int_input_b

}

// 시간 기부 캠페인 미션 실패시 시간을 돌려줌
func refund_time(input_joiner, input_time string) {
	var time string
	db := dbConn()
	db.QueryRow("select time from member where id=?", input_joiner).Scan(&time)
	int_time, int_input_time := chnage_string_to_int(time, input_time)
	int_time = int_time + int_input_time
	time = strconv.Itoa(int_time)

	conn, err := db.Prepare("update member set time=? where id=?")
	if err != nil {

	} else {
		conn.Exec(time, input_joiner)
	}
	defer db.Close()
}

func add_account(input_user, input_money string) bool {
	db := dbConn()

	var log bool
	var user_account string
	//계정이 가지고있는 돈
	db.QueryRow("select account from member where id=?", input_user).Scan(&user_account)
	//추가할 돈==input_money

	// 계산하기전 숫자로 바꿈
	int_user_account, int_input_money := chnage_string_to_int(user_account, input_money)
	int_total_money := int_user_account + int_input_money
	//계산 한 후 string으로 바꿈
	total := strconv.Itoa(int_total_money)

	//업데이트
	conn, err := db.Prepare("update member set account=? where id=?")
	if err != nil {
		log = false
	} else {
		conn.Exec(total, input_user)
		log = true
	}
	defer db.Close()
	return log
}

func del_account(input_user, input_money string) bool {
	db := dbConn()

	var log bool
	var user_account string
	//계정이 가지고있는 돈
	db.QueryRow("select account from member where id=?", input_user).Scan(&user_account)
	//추가할 돈==input_money

	// 계산하기전 숫자로 바꿈
	int_user_account, int_input_money := chnage_string_to_int(user_account, input_money)
	int_total_money := int_user_account - int_input_money
	//계산 한 후 string으로 바꿈
	total := strconv.Itoa(int_total_money)

	//업데이트
	conn, err := db.Prepare("update member set account=? where id=?")
	if err != nil {
		log = false
	} else {
		conn.Exec(total, input_user)
		log = true
	}
	defer db.Close()
	return log
}

func get_total_count() string {
	var block_count string
	db := dbConn()
	db.QueryRow("select count(*) from block_count").Scan(&block_count)
	defer db.Close()
	return block_count
}

// golang 에서 hyperledger application 으로 요청하기
// func B_getAll(input_id string) string {

// 	fmt.Println("B_getAll",input_id)
// 	url := "http://3.86.130.24:8080/B_getAll/?id="+input_id

// 	resp, _ := http.Get(url)
// 	robots, _ := ioutil.ReadAll(resp.Body)
// 	resp.Body.Close()

// 	fmt.Printf("%s\n", robots)
// 	return string(robots)
// }

//블록에 데이터 넣기
func test() {

	for i := 1; i <= 111; i++ {
		url := "http://3.86.130.24:9090/?request=give_donate&id=user1&seq=2&give_money=" + strconv.Itoa(i)
		resp, _ := http.Get(url)
		robots, _ := ioutil.ReadAll(resp.Body)
		resp.Body.Close()
		fmt.Printf("%s\n", robots)
		fmt.Printf(strconv.Itoa(i))
	}

}

func B_insert(input_requst string, input_counting string, input_seq string, input_user_Id string, input_money string) string {

	fmt.Println("B_insert", input_requst, input_counting, input_seq, input_user_Id, input_money)

	url := "http://3.86.130.24:8080/insert/?request=" + input_requst + "&counting=" + input_counting + "&seq=" +
		input_seq + "&id=" + input_user_Id + "&money=" + input_money

	resp, _ := http.Get(url)
	robots, _ := ioutil.ReadAll(resp.Body)
	resp.Body.Close()

	fmt.Printf("%s\n", robots)
	return string(robots)
}

func B_select(input_request, input_id, input_seq, input_doctype string) string {

	url := "http://3.86.130.24:8080/select/?request=" + input_request + "&id=" + input_id + "&seq=" + input_seq + "&doctype=" + input_doctype

	resp, _ := http.Get(url)
	robots, _ := ioutil.ReadAll(resp.Body)
	resp.Body.Close()

	fmt.Println("b_select 응답")

	fmt.Println(string(robots))

	return string(robots)
}

func main() {

	// http 웹어플리케이션 서버로 동작할수있게 해줌
	//  서비스 포트 9090으로 요청하면됨
	http.HandleFunc("/", defaultHandler)

	err := http.ListenAndServe(":9090", nil)

	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	} else {
		fmt.Println("ListenAndServe Started! -> port(9090)")
	}
}
