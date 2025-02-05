document.addEventListener("DOMContentLoaded", function () {
    // 첫 번째 드롭다운
    const searchInput1 = document.getElementById("searchInput");
    const dropdown1 = document.getElementById("dropdown");
    const selectedOptionsContainer1 = document.getElementById("selectedOptions");
    const checkboxes1 = document.querySelectorAll("#dropdown .checkbox-basic");

    // 두 번째 드롭다운
    const searchInput2 = document.getElementById("searchInput2");
    const dropdown2 = document.getElementById("dropdown2");
    const selectedOptionsContainer2 = document.getElementById("selectedOptions2");
    const checkboxes2 = document.querySelectorAll("#dropdown2 .checkbox-basic");

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