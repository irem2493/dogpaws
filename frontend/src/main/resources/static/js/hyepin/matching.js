// 첫 번째 드롭다운
let searchInput1 = document.getElementById("searchInput");
let dropdown1 = document.getElementById("dropdown");
let selectedOptionsContainer1 = document.getElementById("selectedOptions");
let checkboxes1 = document.querySelectorAll("#dropdown .checkbox-basic");

// 두 번째 드롭다운
let searchInput2 = document.getElementById("searchInput2");
let dropdown2 = document.getElementById("dropdown2");
let selectedOptionsContainer2 = document.getElementById("selectedOptions2");
let checkboxes2 = document.querySelectorAll("#dropdown2 .checkbox-basic");

// 선택된 옵션 추가 함수
function addSelectedOption(checkbox, container) {
    const value = checkbox.value;
    const name = checkbox.getAttribute("data-name");

    // 이미 추가된 옵션인지 확인
    if (document.getElementById("selected-" + value)) return;

    // 선택된 옵션 추가
    const optionDiv = document.createElement("div");
    optionDiv.classList.add("selected-option");
    optionDiv.setAttribute("id", "selected-" + value);
    optionDiv.innerHTML = `${name} <span class="remove-option" data-value="${value}">| ✖</span>`;

    container.appendChild(optionDiv);

    // 옵션 삭제 기능 (X 버튼 클릭 시)
    optionDiv.querySelector(".remove-option").addEventListener("click", function () {
        document.getElementById("selected-" + value).remove();
        const checkboxToEnable = document.getElementById("option" + value); // 해당 체크박스 찾기
        checkboxToEnable.checked = false; // 체크 해제
        checkboxToEnable.disabled = false; // 다시 활성화
    });

    // 체크박스를 비활성화 (삭제 시 다시 활성화)
    checkbox.disabled = true;
}

//필터 컨테이너 토글
function filterToggle(element){
    element.classList.toggle("rotated");
    const filterBox = document.getElementById("matchingFilter");
    const cardBox = document.getElementById("filterCard");
    const target = element.dataset.target;
    if(target === "filter"){
        if (window.getComputedStyle(filterBox).display === "none") {
            console.log("filterBox.style.display == none");
            filterBox.style.display = "block";

            const dogId = 1;
            const matchType = 'F';
            api.get('/api/matching/filter?dogId='+dogId+'&matchType='+matchType)
                .then(data => {
                    filter = data.body;  // body 속성의 배열을 할당
                    console.log('filter loaded:', filter);  // 배열 확인
                    applyFilter(filter);
                })
                .catch(error => {
                    console.error(error);
                    alert("오류가 발생했습니다.");
                });
        }else if (window.getComputedStyle(filterBox).display === "block") {
            filterBox.style.display = "none";
        }
    }else if(target === "card"){
        if (window.getComputedStyle(cardBox).display === "none") {
            console.log("filterBox.style.display == none");
            cardBox.style.display = "block";

        }else if (window.getComputedStyle(cardBox).display === "block") {
            cardBox.style.display = "none";
        }
    }

}

function applyFilter(filter) {
    console.log("filter" + filter);
    if (!filter) return;

    // 품종 선택
    if (filter.breed_gbn_cd) {
        document.getElementById("breedSelect").value = filter.breed_gbn_cd;
    }

    // 순종 체크박스
    if (filter.is_mix === "N") {
        document.querySelector(".checkbox-basic").checked = true;
    }

    // 체중 입력 및 이상/이하 라디오 버튼 선택
    if (filter.weight) {
        document.querySelector(".input-a").value = filter.weight;
    }
    if (filter.weight_category === "U") {
        document.querySelector("input[name='radio2'][value='U']").checked = true;
    } else if (filter.weight_category === "D") {
        document.querySelector("input[name='radio2'][value='D']").checked = true;
    }

    // 견BTI 선택
    if (filter.dog_type_code_gbn_cd) {
        document.getElementById("dogTypeSelect").value = filter.dog_type_code_gbn_cd;
    }

    // 강아지 성격 체크
    if (filter.dog_personal_gbn_cd_list) {
        filter.dog_personal_gbn_cd_list.forEach(code => {
            let checkbox = document.querySelector(`input[value='${code}']`);
            if (checkbox) {
                checkbox.checked = true;  // 체크박스 체크

                // 선택된 체크박스에 대해 addSelectedOption 함수 호출
                addSelectedOption(checkbox, selectedOptionsContainer1);
            }
        });
    }

    // 강아지 선호 놀이 체크
    if (filter.dog_play_gbn_cd_list) {
        filter.dog_play_gbn_cd_list.forEach(code => {
            let checkbox = document.querySelector(`input[value='${code}']`);
            if (checkbox) checkbox.checked = true;

            // 선택된 체크박스에 대해 addSelectedOption 함수 호출
            addSelectedOption(checkbox, selectedOptionsContainer2);
        });
    }

    // 산책 시작 및 종료 시간 설정
    let timeInputs = document.querySelectorAll("input[type='time']");
    if (filter.str_walk_start_time) {
        timeInputs[0].value = filter.str_walk_start_time;
    }
    if (filter.str_walk_end_time) {
        timeInputs[1].value = filter.str_walk_end_time;
    }

    // 산책 요일 체크
    if (filter.walk_day_list) {
        let allChecked = true;
        document.querySelectorAll(".day-checkbox").forEach(checkbox => {
            let day = checkbox.parentElement.textContent.trim();
            if (filter.walk_day_list.includes(day)) {
                checkbox.checked = true;
            } else {
                allChecked = false;
            }
        });
        document.getElementById("selectAll").checked = allChecked;
    }
}


document.addEventListener("DOMContentLoaded", function () {

    // 첫 번째 드롭다운 표시
    searchInput1.addEventListener("focus", function () {
        dropdown1.style.display = "block";
    });

    // 두 번째 드롭다운 표시
    searchInput2.addEventListener("focus", function () {
        dropdown2.style.display = "block";
    });

    // 다른 곳 클릭 시 드롭다운 숨기기
    document.addEventListener("click", function (event) {
        if (!searchInput1.contains(event.target) && !dropdown1.contains(event.target)) {
            dropdown1.style.display = "none";
        }
        if (!searchInput2.contains(event.target) && !dropdown2.contains(event.target)) {
            dropdown2.style.display = "none";
        }
    });

    // 첫 번째 드롭다운 체크박스 클릭 이벤트
    checkboxes1.forEach(checkbox => {
        checkbox.addEventListener("click", function () {
            const checkedBoxes = document.querySelectorAll("#dropdown .checkbox-basic:checked");

            if (checkedBoxes.length > 5) {
                alert("최대 5개까지 선택 가능합니다.");
                this.checked = false;
                return;
            }

            addSelectedOption(this, selectedOptionsContainer1);
        });
    });

    // 두 번째 드롭다운 체크박스 클릭 이벤트
    checkboxes2.forEach(checkbox => {
        checkbox.addEventListener("click", function () {
            const checkedBoxes = document.querySelectorAll("#dropdown2 .checkbox-basic:checked");

            if (checkedBoxes.length > 5) {
                alert("최대 5개까지 선택 가능합니다.");
                this.checked = false;
                return;
            }

            addSelectedOption(this, selectedOptionsContainer2);
        });
    });


});

document.addEventListener("DOMContentLoaded", function () {
    // '전체 선택' 체크박스
    const selectAllCheckbox = document.getElementById("selectAll");
    const dayCheckboxes = document.querySelectorAll(".day-checkbox");

    // '전체 선택' 체크박스 클릭 시 모든 요일 체크박스 토글
    selectAllCheckbox.addEventListener("click", function () {
        const isChecked = selectAllCheckbox.checked;
        dayCheckboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
        });
    });

    // 개별 요일 체크박스 클릭 시 '전체 선택' 체크박스 상태 업데이트
    dayCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("click", function () {
            const allChecked = Array.from(dayCheckboxes).every(cb => cb.checked);
            selectAllCheckbox.checked = allChecked;
        });
    });
});

//필터카드 숨기기
function cardCancle(){
    var filterCardForm = document.getElementById("filterCard");
    filterCardForm.style.display = "none";
}

//필터 초기화
function filterReset(){

}

//좋아요 토글
function likeToggle() {
    let icon = document.getElementById("likeIcon");
    //let isLiked = icon.src.includes("like-push.svg");

    const username = "안혜빈";
    const dogId = 1;

    //숫자와 char 형식은 변환이 필요하기 때문에 폼데이터로 보내겠습니다.
    const LikeDto = {
        "username" : username,
        "dogId": parseInt(dogId), // <-- 숫자로 변환
        "likeCode": "F".charAt(0) // <-- char 변환
    }

    const formData = new FormData();
    formData.append("username", username);
    formData.append("dogId", dogId);
    formData.append("likeCode", "F");

    api.post('/api/likes/toggle', formData, {
    })
        .then(res => {
            if (res.body.body == '성공') {  // res.body.body 로 받아야합니다..
                icon.src = isLiked ? "/img/icon/like.svg" : "/img/icon/like-push.svg";
            } else {
                alert("좋아요 실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
}

//메시지 폼 열기
function messageForm(element){
    const target = element.dataset.target;
    if(target === "O"){
        //개인톡 실행

    }else if(target === "G"){
        //그룹채팅 리스트 출력

    }
}

//신고 폼 열기
function declarationForm(){

}


let matchList = [];  // 데이터를 저장할 배열
let startIndex = 0;   // 현재 시작 인덱스

const dogId = 1;

// 컨트롤러에서 matchList 데이터 가져오기
api.get('/api/matching?dogId=' + dogId)
    .then(data => {
        matchList = data.body;
        console.log('match loaded:', matchList);
        updateCards();
    })
    .catch(error => {
        console.error(error);
        alert("오류가 발생했습니다.");
    });

function updateCards() {
    const container = document.getElementById('cardContainer');
    container.style.padding = "0px";
    container.innerHTML = '';

    matchList.slice(startIndex, startIndex + 3).forEach((dog, index) => {
        const card = document.createElement('div');

        // 가운데 카드(index === 1)만 다른 클래스 적용
        if (index === 1) {
            card.classList.add('matching-card');
            card.classList.add('col-6');
            card.classList.add('mx-auto');
            card.style.padding = "0px";
        } else {
            card.classList.add('matching-card-side');
            card.classList.add('col-2');
            card.style.margin = "80px";
            card.style.padding = "0px";
        }

        card.innerHTML = `
            
            <div class="card-top" style="height: 55%; position: relative;">
                <img src="${dog.profile_url}" alt="${dog.dog_name}" style="width: 100%; height: 100%; object-fit: cover;" />
                ${index === 1 ?
            '<div style="position: absolute; top: 5px; right: 5px; width: 8%; border-radius: 20px; font-size: 18px; background-color: rgba(255, 255, 255, 0.6);">아이콘 영역</div>' :
            '<div style="position: absolute; top: 5px; right: 5px; width: 10%; border-radius: 20px; background-color: rgba(255, 255, 255, 0.6);">아이콘 영역</div>'
        }
            </div>
            ${index === 1 ?
            '<div class="card-center" style="font-size: 24px; height: 33%;">' :
            '<div class="card-center" style="height: 33%;">'
        }
                <div>${dog.dog_name} | ${dog.breed_name}</div>
                <div>${dog.gender} (${dog.neutered ? '중성화 O' : '중성화 X'})</div>
                <div>${dog.address}</div>
            </div>
            
            ${index === 1 ?
            '<div class="card-bottom center-card" style="height: 12%;">' :
            '<div class="card-bottom" style="height: 12%; display: flex; justify-content: space-around;">'
        }
               <div class="card-bottom-items">
                    <img id="likeIcon" src="/img/icon/like.svg" alt="like-icon" data-index="${index}" onclick="likeToggle(this)">
                </div>
                <div class="card-bottom-items">
                    <img src="/img/icon/messege.svg" alt="messege-icon" data-target="O" onclick="messageForm(this)">
                </div>
                <div class="card-bottom-items">
                    <img src="/img/icon/wechat-logo.svg" alt="wechat-logo-icon" data-target="G" onclick="messageForm(this)">
                </div>
                <div class="card-bottom-items">
                    <img src="/img/icon/alarm-warning-line.svg" alt="alarm-warning-line-icon" onclick="declarationForm()">
                </div>
            </div>
             
        `;


        container.appendChild(card);
    });
}

document.getElementById("next").addEventListener("click", () => {
    if (startIndex + 1 < matchList.length) {
        startIndex += 1;
        updateCards();
    }
});

document.getElementById("prev").addEventListener("click", () => {
    if (startIndex - 1 >= 0) {
        startIndex -= 1;
        updateCards();
    }
});

