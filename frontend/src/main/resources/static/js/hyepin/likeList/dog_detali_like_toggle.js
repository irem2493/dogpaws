//좋아요 토글
function likeToggle(element) {
    let isLiked = element.src.includes("push.svg");


    //dogId 가져오기
    const sessionDogId = document.getElementById("sessionDogId");
    const myDogId = sessionDogId.value;
    const dogId = document.getElementById("hiddenDogId").value;
    const likeCode = element.dataset.likeCode;
    let baseIcon = likeCode === 'F' ? 'like' : 'mating';
    let newSrc = isLiked ? `/img/icon/${baseIcon}.svg` : `/img/icon/${baseIcon}-push.svg`;

    console.log("dogId:" + dogId);

    //숫자와 char 형식은 변환이 필요하기 때문에 폼데이터로 보내겠습니다.
    const LikeDto = {
        "myDogId": myDogId,
        "dogId": parseInt(dogId), // <-- 숫자로 변환
        "likeCode": likeCode.charAt(0) // <-- char 변환
    }

    const formData = new FormData();
    formData.append("myDogId",  parseInt(myDogId));
    formData.append("dogId",  parseInt(dogId));
    formData.append("likeCode", likeCode.charAt(0));

    api.post('/api/likes/toggle', formData, {})
        .then(res => {
            if (res.body.body == '성공') {  // res.body.body 로 받아야합니다..
                element.src = newSrc;
                element.dataset.liked = isLiked ? "false" : "true";
            } else {
                alert("좋아요 실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
}