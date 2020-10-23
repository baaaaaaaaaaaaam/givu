   //search_click 버튼 누를 경우 검색내용을 가져온다 
document.getElementById('id_lookup').onclick = function () {
    //텍스트 안의 값을 불러온다 
    var searching_text=document.getElementById('input_id').value;
    if(searching_text==""){
        alert("id를 입력하세요")
    }else{
        // searching_text=document.getElementById('search').value="값변경"
        location.href="http://www.givu.shop/searching.html?select_only_id="+searching_text;
    }
};


document.getElementById('home').onclick = function () {
 
        location.href="http://www.givu.shop"
    
};
document.getElementById('search').onclick = function () {
 
    location.href="http://www.givu.shop/searching.html"

};
