

//페이징 로직 : 페이징 할 페이지수는 mysql 에 block_count에서 count(*) 하여 가져온다
//  가져온 숫자를  10 으로 나눈 후 나머지가 없으면 나눈 몫을 페이지수로 한다  ex) 가져온 count =20 ,  20%10==0 이면 페이지수 2 
//  가져온 숫자를  10 으로 나눴는데 나머지가 있으면 페이지수를 +1 한다 ,  ex) 가져온 count = 25 , 25%10>=1 이면 페이지수 3 
//  만약 가져온 숫자가 페이징 수 10 개 즉 데이터가 100개를 넘으면 페이징 수는 10개까지만 보여준다.


//한페이지에 하나만 있어야한다 
//페이지가 완료된 시점에 자동으로 호출함
window.onload=function() {

    //현재 블록에 저장된 총 데이터 수

    get_parsing();

}

function get_parsing(){
        var url  = location.href
   
       // ? 뒤에 값을 가져와서 구분자 & 로 split 함
       var URL_get_request = (url.slice(url.indexOf('?') + 1, url.length)).split('&');  // user_id=ab
           //URL_get_request 배열에서 값만 추출
        console.log(URL_get_request)
      
       for (var i = 0; i < URL_get_request.length; i++) {
   
           //get의 parameter 가 paging 인지 확인하기위하여 추출한다
           // check_paging이 "paging"일 경우 값을 paging_num에 담는다.
           // 만약 디폴트 경로로 접속하였다면 else 로 빠져 paging_num=1 이된다.
           var title=URL_get_request[i].split('=')[0]
           var value=URL_get_request[i].split('=')[1]
           if(title=="select_only_id"){
            console.log("select_only_id")
                get_block_data(title,value)
               
           }
         
     
       }
}

function get_block_data(title,value){
    console.log("get_block_data")
    $.ajax({
        type:"GET",
        url:"http://www.givu.shop:8080/select",
        data : {request:title,id:value},
        
        // dataType : "text/plain",
        success: function(result){
          
            const obj = JSON.parse(result);
            
             //테이블로 만듬 
            //  show_table(obj.reverse())
            desc_sorting(obj)
        },
        error: function(xhr, status, error) {
            console.log(error);
        }  
    });
}

//자바스크립트 객채 정렬 
function desc_sorting(obj){

    obj.sort(function(a,b){
        return b.counting-a.counting
    })
    show_table(obj)
    
  
}

function show_table(obj){
    //mysql 과 다르게 블록체인에서 11 ~ 20번까지 가져오라고 했을때 11~20 번 데이터만 가져오는게아니라 110~200번 데이턱 가져온다 
    //때문에 아래의 조건문을 통해 잘못가져온 데이터는 표시하지 않도록 조건문이 필요하다 
    
    var html="";
     for(var i=0;i<obj.length;i++){
       
        
         
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
     $("#dynamicTbody").empty();
     $("#dynamicTbody").append(html);
    
     if(obj.length<100){
        pagination(obj.length,1, Math.ceil(obj.length/10))
    }else{
        //맨처음 페이지 실행시에만 동작함으로 첫페이지 부터 10 
        pagination(obj.length,1,10)
    }
}








function pagination(count,_start_num_page,_end_num_page){
    console.log(count,_start_num_page,_end_num_page)

       // 시작 페이지 와 종료 페이지 
    var start_num_page=_start_num_page
    var end_num_page=_end_num_page
   



    var req_num_row=10;
    var $tr=jQuery('tbody tr');
    var total_num_row=count;
    var num_pages=0;
    if(total_num_row % req_num_row ==0){
        num_pages=total_num_row / req_num_row;
    }
    if(total_num_row % req_num_row >=1){
        num_pages=total_num_row / req_num_row;
        num_pages++;
        num_pages=Math.floor(num_pages++);
    }

jQuery('.pagination').append("<li><a class=\"prev\">Previous</a></li>");

    for(var i=start_num_page; i<=end_num_page; i++){
        jQuery('.pagination').append("<li><a>"+i+"</a></li>");
  jQuery('.pagination li:nth-child(2)').addClass("active");
  jQuery('.pagination a').addClass("pagination-link");
    }

jQuery('.pagination').append("<li><a class=\"next\">Next</a></li>");

        console.log(start_num_page,req_num_row+(start_num_page-1)*10)
        $tr.hide();
        for(var i=0+(start_num_page-1)*10; i< req_num_row+(start_num_page-1)*10; i++){
            $tr.eq(i).show();
        }
    jQuery('.pagination-link').click('.pagination-link', function(e){
        e.preventDefault();
        $tr.hide();

        console.log("클릭시")
        var page=jQuery(this).text();
        console.log(page)
        var temp;
        var start;
        var current_link ;
        if(this.innerText=="Previous" || this.innerText=="Next"){
         
            // temp=start_num_page-1;
            // start=temp*req_num_row;
            // current_link = temp;
        }else{
            temp=page-1;
            start=temp*req_num_row;
            current_link = temp;
            jQuery('.pagination li').removeClass("active");
            jQuery(this).parent().addClass("active");
    
            for(var i=0; i< req_num_row; i++){
                $tr.eq(start+i).show();
            }
      
      if(temp >= 1){
        jQuery('.pagination li:first-child').removeClass("disabled");
      }
      else {
        jQuery('.pagination li:first-child').addClass("disabled");
      }
        }
     
    });

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
  

        // 현재 UI에 표시되는 텍스트가 21~30 이라면 11~20으로 다시 만든다 
        pagination(count,start_num_page,end_num_page)
    });

    jQuery('.next').click(function(e){
        var prev_end_num_page=end_num_page

        if(num_pages==end_num_page){   //페이지가 총 페이지수 와 현재 end_num_page가 같은경우 더이상 next 안됨 

        }else if(num_pages-end_num_page<10){
            start_num_page=start_num_page+10
            end_num_page=num_pages
        } else{
            start_num_page=start_num_page+10
            end_num_page=end_num_page+10
        }
        console.log(start_num_page,end_num_page)


        //이전 end_num_page 와 게산한 end_num_page 가같을경우 아무동작않함
        if(prev_end_num_page!=end_num_page){
            //위에서 그린 paging 에 관련되 버튼을 지운다
            remove()
            // 현재 UI에 표시되는 텍스트가 21~30 이라면 31~40으로 다시 만든다 
            pagination(count,start_num_page,end_num_page)
        }
      
    });
}
function remove(){
    // var li = document.querySelector("li");	//제거하고자 하는 엘리먼트
    // li.parentNode.removeChild(li);

    $('li').remove();
}

