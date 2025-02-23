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
<<<<<<< HEAD
=======

>>>>>>> origin/REQ-68-관리자
    const form = document.getElementById("filterForm");
    const formData = new FormData(form);
    formData.append("dogId", dogId);
    formData.append("matchType", matchType);

<<<<<<< HEAD
    // 최신 체크된 값들 다시 수집
    updateCheckedValues();

    // 체크된 값들 추가
    dogPersonalGbnCdList.forEach((code, index) => {
        formData.append("dogPersonalGbnCdList[" + index + "]", code);
    });
    dogPlayGbnCdList.forEach((code, index) => {
        formData.append("dogPlayGbnCdList[" + index + "]", code);
    });
    walkDayList.forEach((code, index) => {
        formData.append("walkDayList[" + index + "]", code);
=======
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
>>>>>>> origin/REQ-68-관리자
    });

    api.post('/api/matching/filter', formData, {})
        .then(res => {
<<<<<<< HEAD
            if (res.body.body.includes("성공")) {
                console.log("필터 저장 성공!");
                alert("필터가 저장되었습니다!");
                window.location.reload();
            } else {
                alert("필터 저장 실패!");
=======
            if (res.body.body == '필터 등록 성공') {  // res.body.body 로 받아야합니다..
                console.log("필터 등록 성공!");
                window.location.reload();
            } else {
                alert("필터 초기화 실패!");
>>>>>>> origin/REQ-68-관리자
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

<<<<<<< HEAD
// 현재 체크된 값들 다시 가져오기
function updateCheckedValues() {
    dogPersonalGbnCdList.length = 0;
    dogPlayGbnCdList.length = 0;
    walkDayList.length = 0;

    document.querySelectorAll("#dropdown .checkbox-basic:checked").forEach(checkbox => {
        dogPersonalGbnCdList.push(checkbox.value);
    });

    document.querySelectorAll("#dropdown2 .checkbox-basic:checked").forEach(checkbox => {
        dogPlayGbnCdList.push(checkbox.value);
    });

    document.querySelectorAll(".day-checkbox:checked").forEach(checkbox => {
        walkDayList.push(checkbox.parentElement.textContent.trim());
    });

    console.log("최신 체크값 업데이트 완료!", {
        dogPersonalGbnCdList,
        dogPlayGbnCdList,
        walkDayList
    });
}

//업데이트 배열 확인
document.addEventListener("DOMContentLoaded", function () {
    const checkboxes1 = document.querySelectorAll("#dropdown .checkbox-basic");
    const checkboxes2 = document.querySelectorAll("#dropdown2 .checkbox-basic");

    checkboxes1.forEach(checkbox => {
        checkbox.addEventListener("change", function () {
            updateSelectedOptions(checkbox, dogPersonalGbnCdList);
        });
    });

    checkboxes2.forEach(checkbox => {
        checkbox.addEventListener("change", function () {
            updateSelectedOptions(checkbox, dogPlayGbnCdList);
        });
    });
});

function updateSelectedOptions(checkbox, selectedOptions) {
    const value = checkbox.value;

    if (checkbox.checked) {
        if (!selectedOptions.includes(value)) {
            selectedOptions.push(value);
        }
    } else {
        const index = selectedOptions.indexOf(value);
        if (index !== -1) {
            selectedOptions.splice(index, 1);
        }
    }

    console.log("업데이트된 선택값:", selectedOptions);
}
=======

>>>>>>> origin/REQ-68-관리자
