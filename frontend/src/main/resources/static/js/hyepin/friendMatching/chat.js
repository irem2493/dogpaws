//1:1 채팅 신청
function chatForm(){
    const userDog = sessionDogId.value;
    alert("1:1 채팅방 연결 / userDog: " + userDog);
    //1:1 채팅방 연결하기
    /*
    api.post('/api/', formData, {})
        .then(res => {
            if (res.body.body == '성공') {  // res.body.body 로 받아야합니다..
                alert("성공!");
            } else {
                alert("실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
        
     */
}

//그룹채팅 목록 가져오기
function getGroupChatRoom(element){
    const otherDogId = element.dataset.target; //상대 강아지 id 저장
    const modal = document.querySelector(".pawsModal"); // 모달 요소 찾기
    modal.setAttribute("data-other-dog-id", otherDogId); // 모달에 데이터 저장
    const username = sessionUsername.value;
    console.log("그룹채팅 목록 가져오기 / username : " + username + "otherDogId : " + otherDogId);
    /*
    api.get('/api')
        .then(data => {
           
        })
        .catch(error => {
            console.error(error);
            alert("오류가 발생했습니다.");
        });

     */
}

//그룹채팅 초대완료
function groupChatSubmit(){
    const username = sessionUsername.value;
    const modal = document.querySelector(".pawsModal"); // 모달 요소 찾기
    const otherDogId = modal.getAttribute("data-other-dog-id"); // 저장된 ID 가져오기
    console.log("그룹채팅 초대 완료 / username: " + username +  " / otherDogId : ", otherDogId);
    /*
    api.post('/api/', formData, {})
        .then(res => {
            if (res.body.body == '성공') {  // res.body.body 로 받아야합니다..
                alert("성공!");
            } else {
                alert("실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });

     */

}