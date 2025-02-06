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
            api.get('/api/matching?dogId='+dogId+'&matchType='+matchType)
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
    let isLiked = icon.src.includes("like-push.svg");

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



    //좋아요 토글 api 연결하기
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

//카드 이동
document.addEventListener('DOMContentLoaded', function () {
    let isSwiping = false;
    let startX = 0;
    let container = document.querySelector('.matching-container');
    let cardSides = document.querySelectorAll('.matching-card-side'); // 카드들
    let cards = document.querySelectorAll('.matching-card'); // 중간 카드들

    // 카드의 총 갯수
    const totalCards = cardSides.length;

    // 마우스 다운 시 스와이프 시작
    container.addEventListener('mousedown', function (e) {
        isSwiping = true;
        startX = e.pageX;  // 마우스 시작 위치 저장
        e.preventDefault(); // 기본 이벤트 방지
    });

    // 마우스 이동 시 스와이프 처리
    container.addEventListener('mousemove', function (e) {
        if (isSwiping) {
            let diff = startX - e.pageX;  // 마우스 이동 거리 계산

            if (diff > 50) {  // 오른쪽 스와이프 (카드 순서가 2 3 4로 변경)
                moveCards('right');
                isSwiping = false;  // 스와이프 완료 후 리셋
            } else if (diff < -50) {  // 왼쪽 스와이프 (카드 순서가 1 2 3으로 변경)
                moveCards('left');
                isSwiping = false;  // 스와이프 완료 후 리셋
            }
        }
    });

    // 마우스 뗄 때 스와이프 종료
    container.addEventListener('mouseup', function () {
        isSwiping = false;
    });

    // 카드 이동 함수
    function moveCards(direction) {
        if (direction === 'right') {
            // 1번째 카드가 마지막으로 가는 방식
            let firstSide = cardSides[0]; // 첫 번째 카드
            let firstCard = cards[0]; // 첫 번째 중간 카드

            container.appendChild(firstSide); // 첫 번째 카드 사이드 맨 뒤로 보냄
            container.appendChild(firstCard); // 첫 번째 카드 중간 맨 뒤로 보냄
        } else if (direction === 'left') {
            // 마지막 카드가 첫 번째로 가는 방식
            let lastSide = cardSides[cardSides.length - 1]; // 마지막 카드
            let lastCard = cards[cards.length - 1]; // 마지막 중간 카드

            container.insertBefore(lastSide, cardSides[0]); // 마지막 카드 사이드를 맨 앞에 보냄
            container.insertBefore(lastCard, cards[0]); // 마지막 중간 카드를 맨 앞에 보냄
        }
    }
});