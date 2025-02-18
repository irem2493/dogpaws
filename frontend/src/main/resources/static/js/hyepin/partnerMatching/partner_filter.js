//필터 컨테이너 토글 & 조회
function filterToggle(element) {
    element.classList.toggle("rotated");
    const filterBox = document.getElementById("matchingFilter");
    const cardBox = document.getElementById("filterCard");
    const target = element.dataset.target;
    if (target === "filter") {
        if (window.getComputedStyle(filterBox).display === "none") {
            console.log("filterBox.style.display == none");
            filterBox.style.display = "block";
            const dogId = sessionDogId.value;
            const matchType = 'P';
            api.get('/api/matching/filter?dogId=' + dogId + '&matchType=' + matchType)
                .then(data => {
                    filter = data.body;  // body 속성의 배열을 할당
                    console.log('filter loaded:', filter);  // 배열 확인
                    applyFilter(filter);
                })
                .catch(error => {
                    console.error(error);
                    alert("오류가 발생했습니다.");
                });
        } else if (window.getComputedStyle(filterBox).display === "block") {
            filterBox.style.display = "none";
        }
    } else if (target === "card") {
        if (window.getComputedStyle(cardBox).display === "none") {
            console.log("filterBox.style.display == none");
            cardBox.style.display = "block";

        } else if (window.getComputedStyle(cardBox).display === "block") {
            cardBox.style.display = "none";
        }
    }
}

//필터 등록 & 업데이트
function filterSubmit(matchType) {
    const dogId = sessionDogId.value;

    const form = document.getElementById("filterForm");
    const formData = new FormData(form);
    formData.append("dogId", dogId);
    formData.append("matchType", matchType);

    dogPersonalGbnCdList.forEach(function(code, index) {
        formData.append("dogPersonalGbnCdList[" + index + "]", code);
        console.log("dogPersonalGbnCdList: " + "dogPersonalGbnCdList[" + index + "]" + code);
    });
    dogPlayGbnCdList.forEach(function(code, index) {
        formData.append("dogPlayGbnCdList[" + index + "]", code);
        console.log("dogPlayGbnCdList: " + "dogPlayGbnCdList[" + index + "]" + code);
    });
    walkDayList.forEach(function(code, index) {
        formData.append("walkDayList[" + index + "]", code);
        console.log("walkDayList: " + "walkDayList[" + index + "]" + code);
    });

    api.post('/api/matching/filter', formData, {})
        .then(res => {
            if (res.body.body == '필터 등록 성공') {  // res.body.body 로 받아야합니다..
                console.log("필터 등록 성공!");
                window.location.reload();
            } else {
                alert("필터 초기화 실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
}

//필터 초기화 (삭제) matchType : F / P
function filterReset(matchType) {
    if (confirm("필터를 초기화 하시겠습니까?")) {
        const dogId = sessionDogId.value;

        const formData = new FormData();
        formData.append("dogId", dogId);
        formData.append("matchType", matchType);

        api.post('/api/matching/filter/delete?dogId=' + dogId + '&matchType=' + matchType)
            .then(res => {
                if (res.body.body == '필터 삭제 성공') {  // res.body.body 로 받아야합니다..
                    console.log("필터 초기화 성공!");
                    window.location.reload();
                } else {
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error("오류:", error);
                alert("저장 오류");
            });
    }
}


