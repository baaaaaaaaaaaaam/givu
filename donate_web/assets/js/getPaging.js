

//페이징 로직 : 페이징 할 페이지수는 mysql 에 block_count에서 count(*) 하여 가져온다
//  가져온 숫자를  10 으로 나눈 후 나머지가 없으면 나눈 몫을 페이지수로 한다  ex) 가져온 count =20 ,  20%10==0 이면 페이지수 2 
//  가져온 숫자를  10 으로 나눴는데 나머지가 있으면 페이지수를 +1 한다 ,  ex) 가져온 count = 25 , 25%10>=1 이면 페이지수 3 
//  만약 가져온 숫자가 페이징 수 10 개 즉 데이터가 100개를 넘으면 페이징 수는 10개까지만 보여준다.


//한페이지에 하나만 있어야한다 
//페이지가 완료된 시점에 자동으로 호출함
window.onload=function() {

    //현재 블록에 저장된 총 데이터 수
    get_total_count()

    // 맨처음에 불러올 값.
    // get_data_range()

}

// 웹서버로부터 현재 mysql 에서 가져와야할 총 데이터 (block_count)를 요청하여  전체 페이지 수를 확인한다.
// 전체페이지 수를 확인 한 후 pagination을 실행하여 10개씩 페이징이 보이도록 UI를 실행시킨다 
function get_total_count(){
    $.ajax({
        type:"GET",
        url:"http://www.givu.shop:9090",
        data : {request:'get_total_count'},
        
        // dataType : "text/plain",
        success: function(result){
           //  console.log(result);
            const obj = JSON.parse(result);
           
            var count=obj.result
            console.log("get_total_count   :"  +count)

            if(count<100){
                pagination(count,1, Math.ceil(count/10))
            }else{
                //맨처음 페이지 실행시에만 동작함으로 첫페이지 부터 10 
                pagination(count,1,10)
            }
         
      
        },
        error: function(xhr, status, error) {
            console.log(error);
        }  
    });
}


//실제 페이지 수와 Previous , Next를 만드는 곳 
function pagination(count,_start_num_page,_end_num_page){

    //표 하단에 저체 트랜잭션 수 카운트함
    var total_count = document.getElementById("total_count");
    
    total_count.innerHTML=count ;

    console.log(total_count.innerText)
    if(_start_num_page==1){
        first_get_data(total_count.innerText)
    }
    
    //보여줄 행의 갯수 
    var req_num_row=10;

    // 전체 행의 수 
    var total_num_row=count;

   


    // 시작 페이지 와 종료 페이지 
    var start_num_page=_start_num_page
    var end_num_page=_end_num_page


    


     // 총 페이지 수 
    var num_pages=0;
    // 총 페이지수 연산 
    //20개의 데이터를 10개씩 페이징 한다고했을 때 나머지가 0이라면 페이징 수가 2가된다
    //25개의 데이터를 10개씩 페이징 한다면 페이징수가 2가 되는데 강제로 +1 해줘 페이징수를 3으로 한다
    // Math.floor 소수점 이하 버림 , Math.ceil 올림 , Math.round 반올림 
    if(total_num_row % req_num_row ==0){
        num_pages=total_num_row / req_num_row;
    }
    if(total_num_row % req_num_row >=1){
        num_pages=total_num_row / req_num_row;
        num_pages++;
        num_pages=Math.floor(num_pages++);
    }


    
     // 페이지 Privious  num  . . . NEXT 구조 만듬
     //html 파일에 <ul class pagination> 이라는 부분에 <li><a> 형태로 Previous , 페이지수 , Next를 표시함 
    jQuery('.pagination').append("<li><a class=\"prev\">Previous</a></li>");
            for(var i=start_num_page; i<=end_num_page; i++){
           
                jQuery('.pagination').append("<li><a>"+i+"</a></li>");
                //위에서 만든 구조에 속성을 만듬
                //li:nth-child(2) li 들중 2번째 일때 active 클래스를 추가해주겟다.
                jQuery('.pagination li:nth-child(2)').addClass("active");
                jQuery('.pagination a').addClass("pagination-link");
            }
    jQuery('.pagination').append("<li><a class=\"next\">Next</a></li>");
        // 페이지 Privious  num  . . . NEXT 구조 끝 




         //html 파일에 class=pagination  에 <a> 의 class 가 pagination-link 인경우 클릭할때 이벤트 발생
         // this.innerText는 <a>20</a> 일경우 20을 가져옴
         //번호를 누를때마다 get_data를 통해 해당 페이지에 맞는 아이템 가져옴 
         //만약 클릭한게 숫자가 아니라 Previous나 Next인경우 데이터를 가져올수 없기때문에 아무동작하지않음

        jQuery('.pagination a').click('.pagination-link', function(e){

            //원하는 번호를 누를때마다 해당 번호 부터  ~ 번호 +9 까지 리스트 보여줌 
            console.log(this.innerText)
            if(this.innerText=="Previous" || this.innerText=="Next"){

            }else{
                // get_data(this.innerText)
                reverse_paging(this.innerText)
            }
            jQuery('.pagination li').removeClass("active");
            jQuery(this).parent().addClass("active");
        });




        //이전 버튼 누를경우 처리 
    jQuery('.prev').click(function(e){
        if(start_num_page==1){// 페이지가 1~10 인경우 아무 동작안함

        }else if (start_num_page%10==1){   //페이지가 11~20 이거나 21~30 인 경우 
            if(end_num_page%10!=0){             //페이지가 21~23 인 경우  startpage+9  end_num_page하여 를 10단위로 맞춤 
                end_num_page=start_num_page+9
            }
            start_num_page=start_num_page-10   //페이지가  21~30 인 경우  21-10 , 30-10 연산하여 11~20 으로 맞춤 
            end_num_page=end_num_page-10
        } 
        console.log(start_num_page,end_num_page)
        //위에서 그린 paging 에 관련되 버튼을 지운다
        remove()
        //현재 페이지가 21~30 이라면 이전버튼을 누를때 11번 페이지가 보이도록 한다
        reverse_paging(start_num_page)

        // 현재 UI에 표시되는 텍스트가 21~30 이라면 11~20으로 다시 만든다 
        pagination(count,start_num_page,end_num_page)
    });

    //다음 버튼 누를 경우 처리 
    jQuery('.next').click(function(e){
        if(num_pages==end_num_page){   //페이지가 총 페이지수 와 현재 end_num_page가 같은경우 더이상 next 안됨 

        }else if(num_pages-end_num_page<10){
            start_num_page=start_num_page+10
            end_num_page=num_pages
        } else{
            start_num_page=start_num_page+10
            end_num_page=end_num_page+10
        }
        console.log(start_num_page,end_num_page)
        //위에서 그린 paging 에 관련되 버튼을 지운다
        remove()
         //현재 페이지가 21~30 이라면 이전버튼을 누를때 31번 페이지가 보이도록 한다
        reverse_paging(start_num_page)
        // 현재 UI에 표시되는 텍스트가 21~30 이라면 31~40으로 다시 만든다 
        pagination(count,start_num_page,end_num_page)
    });

}

function remove(){
    // var li = document.querySelector("li");	//제거하고자 하는 엘리먼트
    // li.parentNode.removeChild(li);

    $('li').remove();
}




//가져와야할 데이터 범위
function get_data_range(){
    //현재 경로 가져오기 
    //    var url  = location.href
   
    //    // ? 뒤에 값을 가져와서 구분자 & 로 split 함
    //    var URL_get_request = (url.slice(url.indexOf('?') + 1, url.length)).split('&');  // paging=2 , 
   
    //    //URL_get_request 배열에서 값만 추출
   
    //    //디폴트 경로로 접속할 경우 length==1 , 값 ["http://http://www.givu.shop/homepage/index.html"]
    //    //?paging=2 경로로 접속할 경우 length==1 , 값  [paging=2]
    //    var paging_num;
   
    //    for (var i = 0; i < URL_get_request.length; i++) {
   
    //        //get의 parameter 가 paging 인지 확인하기위하여 추출한다
    //        // check_paging이 "paging"일 경우 값을 paging_num에 담는다.
    //        // 만약 디폴트 경로로 접속하였다면 else 로 빠져 paging_num=1 이된다.
    //        var check_paging=URL_get_request[i].split('=')[0]
   
    //        if(check_paging=="paging"){
    //            paging_num = URL_get_request[i].split('=')[1];
    //        }else {
    //            paging_num=1
    //        }
    //    }
       //실제 데이터를 가져오도록 요청함 
       //1번부터 데이터 가져옴 
    //    get_data(1)
    // var total_count = document.getElementById("total_count");
        
   }
   


   //실제 데이터를 요청하여 가져오는 부분
   function first_get_data(current_num){
       var start_num = current_num-10
       var end_num = parseInt(current_num)

       console.log(start_num,end_num);
    //블록체인에 있는 정보를 불러올때 counting 번호를 기준으로 가져오는데 0부터 9까지는 00~09로 저장되어있다.
    //때문에 불러올때도 0~9까지 불러오라고하는게 아니라 00부터 09까지 불러오라고 요청해야함
   
       if (start_num>=0 && start_num <10){
           start_num="0"+start_num
       }
       console.log(start_num,end_num);
       $.ajax({
        type:"GET",
        url:"http://www.givu.shop:8080/select",
        data : {request:'select_range',start_num:start_num,end_num:end_num},
        
        // dataType : "text/plain",
        success: function(result){
           //  console.log(result);
            const obj = JSON.parse(result);
           
             //테이블로 만듬 
             show_table(obj.reverse(),start_num)
        },
        error: function(xhr, status, error) {
            console.log(error);
        }  
    });

   }


//실제 데이터를 요청하여 가져오는 부분 
function  reverse_paging(paging_num){
    var total_count = document.getElementById("total_count").innerText;


    var paging_start =total_count-(paging_num*10)
    var paging_end = total_count-(paging_num*10-10)

    console.log(paging_start,paging_end)

    //시작 숫자가 - 인경우 00 부터 불러오도록 하고 , 시작이 0~9 인경우 00부터 09로 치환하여 불러오도록한다
    // 종료 숫자가 0~9인경우 00부터 09로 치환하여 불러오도록한다.
    if(paging_start<0){
        paging_start=0;
    }
    if (paging_start>=0 && paging_start <10){
        paging_start="0"+paging_start
    }
   
    if (paging_end>=0 && paging_end <10){
        paging_end="0"+paging_end
    }
    console.log(paging_start,paging_end)
    //블록체인에 있는 정보를 불러올때 counting 번호를 기준으로 가져오는데 0부터 9까지는 00~09로 저장되어있다.
    //때문에 불러올때도 0~9까지 불러오라고하는게 아니라 00부터 09까지 불러오라고 요청해야함
    

  
    $.ajax({
        type:"GET",
        url:"http://www.givu.shop:8080/select",
        data : {request:'select_range',start_num:paging_start,end_num:paging_end},
        
        // dataType : "text/plain",
        success: function(result){
           //  console.log(result);
            const obj = JSON.parse(result);

             //테이블로 만듬 
             show_table(obj.reverse(),paging_start)
        },
        error: function(xhr, status, error) {
            console.log(error);
        }  
    });
}

  
   
   //데이터 불러와 10개씩 보여주기 
   function show_table(obj,paging_start){
       //mysql 과 다르게 블록체인에서 11 ~ 20번까지 가져오라고 했을때 11~20 번 데이터만 가져오는게아니라 110~200번 데이턱 가져온다 
       //때문에 아래의 조건문을 통해 잘못가져온 데이터는 표시하지 않도록 조건문이 필요하다 



       var paging_end=Number(paging_start)+10
       var html="";
        for(var i=0;i<obj.length;i++){
            console.log(obj[i].counting,paging_start,paging_end);
           if(obj[i].counting>=paging_start&&obj[i].counting<paging_end){
            
               if(obj[i].docType=="share_list"){
                   obj[i].docType="일반 나눔"
                  }else if(obj[i].docType=="donation_list"){
                   obj[i].docType="일반 기부"
                  }else if(obj[i].docType=="mission_share_list"){
                   obj[i].docType="미션 나눔"
                  }else if(obj[i].docType=="mission_donation_list"){
                   obj[i].docType="미션 기부"
                  }
                   html += '<tr>';
                   html += '<td>'+obj[i].docType+'</td>';
                   html += '<td>'+obj[i].id+'</td>';
                   html += '<td>'+obj[i].money+'</td>';
                   html += '<td>'+obj[i].channel+'</td>';
                   html += '<td>'+obj[i].timestamp+'</td>';
                   html += '<td>'+obj[i].txId+'</td>';
                   html += '</tr>';	
                   
           }
        }
        $("#dynamicTbody").empty();
        $("#dynamicTbody").append(html);
   }
   

