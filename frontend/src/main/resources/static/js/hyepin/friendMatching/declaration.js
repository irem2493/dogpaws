//신고폼 열면 해당 강아지 id 저장
function declarationForm(element){
    const otherDogId = element.dataset.target; //상대 강아지 id 저장
    const modal = document.querySelector(".pawsModal"); // 모달 요소 찾기
    modal.setAttribute("data-other-dog-id", otherDogId); // 모달에 데이터 저장
    console.log("신고 폼 열기 / otherDogId : " + otherDogId);
}

//신고 완료
function declarationSubmit(){
    const username = sessionUsername.value;
    const modal = document.querySelector(".pawsModal"); // 모달 요소 찾기
    const otherDogId = modal.getAttribute("data-other-dog-id"); // 저장된 ID 가져오기
    console.log("신고 완료 / username: " + username +  " / otherDogId : ", otherDogId);
    //신고 테이블에 저장
    const declaration = document.getElementById("declaration");
    const formData = new FormData(declaration);
    formData.append("username", username);
    formData.append("dogId", otherDogId);

    api.post('/api/declaration', formData, {})
        .then(res => {
            if (res.body.body == '신고 성공') {  // res.body.body 로 받아야합니다..
                alert("신고 성공!");
            } else {
                alert("실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });

}
