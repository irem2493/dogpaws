//좋아요 토글
function likeToggle(element) {
    let icon = document.getElementById("likeIcon");
    let isLiked = icon.src.includes("like-push.svg");

    const username = sessionUsername.value;
    const dogId = element.dataset.target;
    console.log("dogId:" + dogId);

    //숫자와 char 형식은 변환이 필요하기 때문에 폼데이터로 보내겠습니다.
    const LikeDto = {
        "username": username,
        "dogId": parseInt(dogId), // <-- 숫자로 변환
        "likeCode": "P".charAt(0) // <-- char 변환
    }

    const formData = new FormData();
    formData.append("username", username);
    formData.append("dogId", dogId);
    formData.append("likeCode", "P");

    api.post('/api/likes/toggle', formData, {})
        .then(res => {
            if (res.body.body == '성공') {  // res.body.body 로 받아야합니다..
                icon.src = isLiked ? "/img/icon/like.svg" : "/img/icon/like-push.svg";
                matchList.forEach(dog => {
                    if (dog.dog_id === parseInt(dogId)) {
                        console.log("dog.dog_id: " + dog.dog_id + "dogId: " + dogId + "찾았다 dogId")
                        dog.liked = !dog.liked;
                    }
                });
                updateCards();
            } else {
                alert("좋아요 실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
}

//필터 숨기기
function filterCancle() {
    var matchingFilterForm = document.getElementById("matchingFilter");
    matchingFilterForm.style.display = "none";
    var filterIcon = document.getElementById("filterIcon");
    filterIcon.classList.toggle("rotated");
}

//필터카드 숨기기
function cardCancle() {
    var filterCardForm = document.getElementById("filterCard");
    filterCardForm.style.display = "none";
    var cardIcon = document.getElementById("cardIcon");
    cardIcon.classList.toggle("rotated");
}


