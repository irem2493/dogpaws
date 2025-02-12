//username 가져오기
const sessionUsername = document.getElementById("sessionUsername");
// dogId 가져오기
const sessionDogId = document.getElementById("sessionDogId");

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

// 강아지 성격 배열
const dogPersonalGbnCdList = [];
// 강아지 선호놀이 배열
const dogPlayGbnCdList = [];
//산책 요일 배열
const walkDayList = [];

// 선택된 옵션 추가 함수
function addSelectedOption(checkbox, container, selectedOptions) {

    // selectedOptions가 undefined라면 빈 배열로 초기화
    if (!Array.isArray(selectedOptions)) {
        selectedOptions = [];
    }

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

        const index = selectedOptions.indexOf(value); // 배열에서 해당 값 찾기
        if (index !== -1) {
            selectedOptions.splice(index, 1); // 배열에서 해당 값 제거
        }
    });

    // 체크박스를 비활성화 (삭제 시 다시 활성화)
    checkbox.disabled = true;

    // 배열에 값 추가
    selectedOptions.push(value);
    console.log(selectedOptions); // 배열 상태 확인
}

//드롭다운 표시
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

            addSelectedOption(this, selectedOptionsContainer1, dogPersonalGbnCdList);
            console.log(dogPersonalGbnCdList);
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

            addSelectedOption(this, selectedOptionsContainer2, dogPlayGbnCdList);
            console.log(dogPlayGbnCdList);
        });
    });
});

//체크박스
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
        updateWalkDayList();
    });

    // 개별 요일 체크박스 클릭 시 '전체 선택' 체크박스 상태 업데이트
    dayCheckboxes.forEach(checkbox => {
        checkbox.addEventListener("click", function () {
            const allChecked = Array.from(dayCheckboxes).every(cb => cb.checked);
            selectAllCheckbox.checked = allChecked;
            updateWalkDayList();
        });
    });

    // 배열에 선택된 요일 업데이트
    function updateWalkDayList() {
        // 배열 초기화
        walkDayList.length = 0;

        // 선택된 체크박스 값만 배열에 추가
        dayCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                walkDayList.push(checkbox.parentElement.textContent.trim());
            }
        });
    }

});

//필터 반영
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
        document.querySelector("input[name='weightCategory'][value='U']").checked = true;
    } else if (filter.weight_category === "D") {
        document.querySelector("input[name='weightCategory'][value='D']").checked = true;
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


//메시지 폼 열기
function messageForm(element) {
    const target = element.dataset.target;
    if (target === "O") {
        //개인톡 실행

    } else if (target === "G") {
        //그룹채팅 리스트 출력

    }
}

//신고 폼 열기
function declarationForm() {

}


