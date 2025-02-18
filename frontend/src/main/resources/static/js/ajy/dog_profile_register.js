document.addEventListener("DOMContentLoaded", function() {
    const profileUpload = document.getElementById('profileUpload');
    const fileInput = document.getElementById('fileInput');
    const profileImagePreview = document.getElementById('profileImagePreview');
    const uploadCircle = document.querySelector('.profile-upload-circle');

    const isMatingAvailable = document.getElementById('isMatingAvailable');
    const fileUploadContainer = document.getElementById('fileUploadContainer');

    const walkDays = document.getElementById('walkDays').value;

    if (!isMatingAvailable || !fileUploadContainer) {
        console.error("체크박스 또는 파일 업로드 컨테이너를 찾을 수 없습니다.");
        return;
    }

    // ✅ 초기 상태 반영 (체크박스가 체크되어 있으면 파일 업로드 컨테이너 보이게)
    fileUploadContainer.style.display = isMatingAvailable.checked ? 'block' : 'none';

    // ✅ 체크박스 변경 이벤트 리스너 추가
    isMatingAvailable.addEventListener('change', function () {
        console.log("체크박스 상태 변경됨:", this.checked); // 상태 확인 로그
        fileUploadContainer.style.display = this.checked ? 'block' : 'none';
    });

    document.getElementById("dogForm").addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 제출 방지
        saveDog(); // saveDog() 실행
    });

    // 프로필 업로드 클릭 시 파일 선택 창 열기
    profileUpload.addEventListener('click', function () {
        fileInput.click();
    });

    // 파일 선택 시 미리보기 업데이트
    fileInput.addEventListener('change', function (event) {
        const file = event.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                profileImagePreview.src = e.target.result;
                profileImagePreview.style.display = 'block';
                uploadCircle.style.display = 'none'; // 이미지가 선택되면 + 버튼 숨기기
            };
            reader.readAsDataURL(file);
        }
    });

    document.querySelectorAll('.select-box').forEach(selectBox => {
        const selected = selectBox.querySelector('.select-box-selected');
        const options = selectBox.querySelector('.select-box-options');

        if (selected && options) {
            // select-box 클릭시 옵션 목록 토글
            selected.addEventListener('click', (e) => {
                e.stopPropagation();
                document.querySelectorAll('.select-box').forEach(box => {
                    if (box !== selectBox) {
                        box.classList.remove('active');
                    }
                });
                selectBox.classList.toggle('active');
            });

            // 옵션 클릭 시 선택된 텍스트 변경
            options.addEventListener('click', (e) => {
                if (e.target.classList.contains('select-box-option')) {
                    selected.textContent = e.target.textContent;
                    selectBox.classList.remove('active');
                }
            });
        }

        //수정 시 필요
        var gender = document.getElementById('gender').value;
        if (gender === 'M') {
            setGender('M', document.querySelector(".gender-select button:nth-child(1)"));
        } else if (gender === 'F') {
            setGender('F', document.querySelector(".gender-select button:nth-child(2)"));
        }
    });

    // 외부 클릭 시 드롭다운 닫기
    document.addEventListener('click', (e) => {
        document.querySelectorAll('.select-box').forEach(selectBox => {
            if (!selectBox.contains(e.target)) {
                selectBox.classList.remove('active');
            }
        });
    });

    const allCheckbox = document.getElementById('all-days');
    const dayCheckboxes = document.querySelectorAll('.checkbox-group input[type="checkbox"]:not(#all-days)');
    const hiddenDaysInput = document.createElement('input');

    // **숨겨진 input 요소 생성**
    hiddenDaysInput.type = 'hidden';
    hiddenDaysInput.name = 'walkDays';
    hiddenDaysInput.id = 'hiddenDaysInput';
    document.querySelector('form').appendChild(hiddenDaysInput);  // 폼에 hidden input 추가

    let selectedDays = [];

// 선택된 요일 값들을 업데이트하는 함수
    function updateSelectedDays() {
        // 체크된 요일들만 배열로 만듦
        selectedDays = Array.from(dayCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.id);

        // 기존 값 덮어쓰고 선택된 요일만 저장
        hiddenDaysInput.value = selectedDays.join(',');

        // 콘솔에 확인
        console.log('선택된 요일:', hiddenDaysInput.value);
    }


// 전체 체크박스 클릭 이벤트
    allCheckbox.addEventListener('change', function () {
        const isChecked = this.checked;

        // 모든 요일 체크박스 상태 변경
        dayCheckboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
        });

        updateSelectedDays();
    });

// 각 요일 체크박스 상태 변경 이벤트
    dayCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', function () {
            if (!this.checked) {
                allCheckbox.checked = false;  // 하나라도 체크 해제되면 전체 체크박스 해제
            } else {
                // 모든 요일이 체크되었는지 확인
                const allChecked = Array.from(dayCheckboxes).every(cb => cb.checked);
                allCheckbox.checked = allChecked;
            }

            updateSelectedDays();
        });
    });

    const notRelevantCheckbox = document.getElementById('walkTimeYn'); // 상관없음 체크박스
    const startTimeInput = document.getElementById('walkStartTime'); // 산책 시작 시간 입력
    const endTimeInput = document.getElementById('walkEndTime'); // 산책 종료 시간 입력

    // 초기 상태 설정
    updateWalkInputs();

    // 체크박스 변경 시 입력 필드 상태 업데이트
    notRelevantCheckbox.addEventListener('change', function () {
        updateWalkInputs();
    });

    function updateWalkInputs() {
        if (notRelevantCheckbox.checked) {
            // 비활성화 상태로 변경
            startTimeInput.disabled = true;
            endTimeInput.disabled = true;
            startTimeInput.value = ""; // 입력값 초기화
            endTimeInput.value = ""; // 입력값 초기화

            startTimeInput.classList.add('disabled-input');
            endTimeInput.classList.add('disabled-input');
        } else {
            // 활성화 상태로 변경
            startTimeInput.disabled = false;
            endTimeInput.disabled = false;

            startTimeInput.classList.remove('disabled-input');
            endTimeInput.classList.remove('disabled-input');
        }
    }
// 페이지 로드 시 초기 상태 설정
    initializeGender();

    document.querySelectorAll(".checkbox-basic[data-category='personality']:checked").forEach(checkbox => {
        selectOption(checkbox);
    });

    document.querySelectorAll(".checkbox-basic[data-category='play']:checked").forEach(checkbox => {
        selectOption2(checkbox);
    });

    // ✅ 저장된 값이 있으면 체크박스 업데이트 실행
    updateCheckboxesFromSavedDays(walkDays);

});

// 연도 및 월 옵션 추가
const selectBox_year = document.getElementById('birthYear');
const selectedYear = selectBox_year.getAttribute("data-selected");  // 기존 값 가져오기
for (let year = 1990; year <= 2025; year++) {
    let option = document.createElement('option');
    option.value = year;
    option.textContent = year;
    option.classList.add('select-box-option');

    // 기존 값이 있다면 선택
    if (selectedYear && selectedYear == year) {
        option.selected = true;
    }

    selectBox_year.appendChild(option);
}

const selectBox_month = document.getElementById('birthMonth');
const selectedMonth = selectBox_month.getAttribute("data-selected");  // 기존 값 가져오기
for (let i = 1; i <= 12; i++) {
    let option = document.createElement('option');
    let month = i.toString().padStart(2, '0');
    option.value = month;
    option.textContent = month;
    option.classList.add('select-box-option');

// 기존 값이 있다면 선택
    if (selectedMonth && selectedMonth == month) {
        option.selected = true;
    }

    selectBox_month.appendChild(option);
}

// 드롭다운 열기 함수
function showDropdown() {
    document.getElementById('dropdown').style.display = 'block';
}

// 드롭다운 닫기 함수
function hideDropdown() {
    document.getElementById('dropdown').style.display = 'none';
}

// 입력창 클릭 시 드롭다운 표시 및 이벤트 전파 방지
document.getElementById('searchInput').addEventListener('click', function(event) {
    event.stopPropagation(); // 클릭 이벤트가 부모 요소로 전파되지 않도록 함
    showDropdown();
});

// 바깥 클릭 시 드롭다운 닫기
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('dropdown');
    const searchInput = document.getElementById('searchInput');

    // 클릭한 요소가 검색창이나 드롭다운 내부가 아닐 때 드롭다운 닫기
    if (!dropdown.contains(event.target) && event.target !== searchInput) {
        hideDropdown();
    }
});

function selectOption(checkbox) {
    const selectedContainer = document.getElementById('selectedOptions');

    // 🛑 중복 추가 방지 (이미 추가된 경우 무시)
    if (document.querySelector(`#selectedOptions input[value="${checkbox.value}"]`)) {
        return;
    }

    if (checkbox.checked) {
        if (selectedContainer.childElementCount >= 5) {
            alert('최대 5개까지 선택 가능합니다.');
            checkbox.checked = false;
            return;
        }

        const selectedDiv = document.createElement("div");
        selectedDiv.classList.add("selected-option");

        // ✅ 사용자에게 보여줄 값 (gubn_name)
        const displayText = checkbox.getAttribute("data-name");

        // ✅ 실제로 DB에 저장할 값 (gubn_code)
        const hiddenInput = document.createElement("input");
        hiddenInput.type = "hidden";
        hiddenInput.name = "selectedPersonalities"; // 서버로 전송될 필드 이름
        hiddenInput.value = checkbox.value;

        selectedDiv.textContent = displayText; // 사용자에게 보일 값
        selectedDiv.appendChild(hiddenInput);  // 숨겨진 input 추가

        const removeBtn = document.createElement("span");
        removeBtn.textContent = " ✖";
        removeBtn.style.cursor = "pointer";
        removeBtn.onclick = function () {
            selectedContainer.removeChild(selectedDiv);
            checkbox.checked = false;
        };

        selectedDiv.appendChild(removeBtn);
        selectedContainer.appendChild(selectedDiv);
    }
}

// 검색 기능
document.getElementById('searchInput').addEventListener('input', function () {
    const searchValue = this.value.toLowerCase();
    const options = document.querySelectorAll('.dropdown .option');
    options.forEach(option => {
        const label = option.querySelector('label').textContent.toLowerCase();
        if (label.includes(searchValue)) {
            option.style.display = 'flex';
        } else {
            option.style.display = 'none';
        }
    });
});


//놀이
// 드롭다운 표시 함수
function showDropdown2() {
    document.getElementById('dropdown2').style.display = 'block';
}

// 입력창 클릭 시 드롭다운 표시 및 이벤트 전파 방지
document.getElementById('searchInput2').addEventListener('click', function(event) {
    event.stopPropagation();
    showDropdown2();
});

// 바깥 클릭 시 드롭다운 닫기
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('dropdown2');
    const searchInput = document.getElementById('searchInput2');

    if (!dropdown.contains(event.target) && event.target !== searchInput) {
        dropdown.style.display = 'none';
    }
});

// 입력 초기화
function clearInput2() {
    document.getElementById('searchInput2').value = '';
    showDropdown2();
}

// 옵션 선택 처리
function selectOption2(checkbox) {
    const selectedContainer = document.getElementById('selectedOptions2');

    // 🛑 중복 추가 방지 (이미 추가된 경우 무시)
    if (document.querySelector(`#selectedOptions2 input[value="${checkbox.value}"]`)) {
        return;
    }

    if (checkbox.checked) {
        if (selectedContainer.childElementCount >= 5) {
            alert('최대 5개까지 선택 가능합니다.');
            checkbox.checked = false;
            return;
        }

        const selectedDiv = document.createElement("div");
        selectedDiv.classList.add("selected-option");

        // ✅ 사용자에게 보여줄 값 (gubn_name)
        const displayText = checkbox.getAttribute("data-name");

        // ✅ 실제로 DB에 저장할 값 (gubn_code)
        const hiddenInput = document.createElement("input");
        hiddenInput.type = "hidden";
        hiddenInput.name = "selectedPlays"; // 서버로 전송될 필드 이름
        hiddenInput.value = checkbox.value;

        selectedDiv.textContent = displayText; // 사용자에게 보일 값
        selectedDiv.appendChild(hiddenInput);  // 숨겨진 input 추가

        const removeBtn = document.createElement("span");
        removeBtn.textContent = " ✖";
        removeBtn.style.cursor = "pointer";
        removeBtn.onclick = function () {
            selectedContainer.removeChild(selectedDiv);
            checkbox.checked = false;
        };

        selectedDiv.appendChild(removeBtn);
        selectedContainer.appendChild(selectedDiv);
    }
}

// 검색 기능
document.getElementById('searchInput2').addEventListener('input', function () {
    const searchValue = this.value.toLowerCase();
    const options = document.querySelectorAll('.dropdown .option');
    options.forEach(option => {
        const label = option.querySelector('label').textContent.toLowerCase();
        if (label.includes(searchValue)) {
            option.style.display = 'flex';
        } else {
            option.style.display = 'none';
        }
    });
});

document.getElementById('addPhotoButton').addEventListener('click', function() {
    document.getElementById('activityImages').click(); // 파일 선택 창 열기
});

document.getElementById('activityImages').addEventListener('change', function(event) {
    const files = event.target.files;
    const photoPreview = document.getElementById('photoPreview');

    // 현재 이미지 미리보기 갯수
    const currentImagesCount = photoPreview.children.length;

    if (files.length + currentImagesCount > 6) {
        alert('최대 6장까지 선택 가능합니다.');
        return;
    }

    // 파일명을 인풋 박스에 표시
    const fileNames = Array.from(files).map(file => file.name).join(', ');
    activityImageFileName.value = fileNames;

    // 파일을 미리보기로 추가
    Array.from(files).forEach(file => {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            const photoBox = document.createElement('div');
            photoBox.classList.add('photo-box');
            photoBox.setAttribute('data-file-name', file.name);  // 파일명을 저장

            // 사진 미리보기 삭제 버튼
            const deleteBtn = document.createElement('span');
            deleteBtn.classList.add('delete-btn');
            deleteBtn.textContent = '✖';
            deleteBtn.onclick = function() {
                photoBox.remove();

                // 파일명 업데이트
                /*const remainingFiles = Array.from(photoPreview.children)
                    .map(child => child.getAttribute('data-file-name'));
                activityImageFileName.value = remainingFiles.join(', ');*/
            };

            photoBox.appendChild(img);
            photoBox.appendChild(deleteBtn);
            photoPreview.appendChild(photoBox);
        };
        reader.readAsDataURL(file);
    });
});

function setGender(value, btn) {
    // 성별 값을 hidden input에 설정
    document.getElementById('gender').value = value;

    // 모든 버튼에서 active 클래스 제거
    document.querySelectorAll('.gender-select button').forEach(button => button.classList.remove('active'));

    // 클릭된 버튼에 active 클래스 추가
    btn.classList.add('active');
}

// ✅ 2️⃣ 페이지가 로드될 때 기존 성별 값을 찾아서 버튼을 활성화하는 함수
function initializeGender() {
    const genderValue = document.getElementById('gender').value;  // 현재 hidden input 값 가져오기
    if (genderValue) {
        // 해당 성별 버튼을 찾아서 활성화
        const selectedButton = document.querySelector(`.gender-select button[data-gender="${genderValue}"]`);
        if (selectedButton) {
            selectedButton.classList.add('active');
        }
    }
}



function saveDog() {
    const form = document.getElementById("dogForm");

    // FormData 객체 생성
    const formData = new FormData(form);

    // 폼 데이터에 파일이 포함되어 있는지 확인
    for (let pair of formData.entries()) {
        console.log(`${pair[0]}:`, pair[1]);
    }

    const dogNameInput = document.getElementById('dogName');
    const value = dogNameInput.value.trim();
    if (!value) {
        alert(`필수 입력 항목을 모두 채워주세요: 강아지 이름`);
        if (dogNameInput) {
            dogNameInput.focus();  // 빈 필드에 포커스 설정
        }
        return;
    }

    const breedSelect = document.getElementById('breedSelect');
    if (!breedSelect.value){
        alert(`필수 입력 항목을 모두 채워주세요: 품종`);
        breedSelect.focus();
        return;
    }

    const firstGenderButton = document.querySelector('.gender-select button');
    const gender = document.getElementById('gender');
    if (!gender.value){
        alert(`필수 입력 항목을 모두 채워주세요: 성별`);
        // 버튼 중 첫 번째 버튼에 포커스를 줌
        firstGenderButton.focus();
        return;
    }

    // 전송 전에 체크박스가 체크되지 않은 경우 값을 설정
    if (!formData.has('isMix')) {
        formData.append('isMix', 'N');  // 체크되지 않으면 'N'으로 설정
    }

    // 전송 전에 체크박스가 체크되지 않은 경우 값을 설정
    if (!formData.has('isNeutered')) {
        formData.append('isNeutered', 'N');  // 체크되지 않으면 'N'으로 설정
    }

    // 전송 전에 체크박스가 체크되지 않은 경우 값을 설정
    if (!formData.has('walkTimeYn')) {
        formData.append('walkTimeYn', 'N');  // 체크되지 않으면 'N'으로 설정
    }

    for (let pair of formData.entries()) {
        console.log(pair[0] + ': ' + pair[1]);  // 폼 데이터 확인
    }

    const username = sessionStorage.getItem('username');
    // fetch로 FormData 전송
    api.put('/api/dog/' + username, formData, )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.status === 'SUCCESS') {
                alert("강아지 프로필 저장 완료");
                location.href = '/dog/mypage/dogList';
            } else {
                alert("강아지 프로필 저장 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("강아지 프로필 저장 중 오류.");
        });

}

// ✅ 기존 저장된 요일 값을 가져와 체크박스 업데이트
function updateCheckboxesFromSavedDays(savedDays) {
    if (!savedDays || savedDays.trim() === '') {
        console.log('저장된 요일 값이 없습니다.');
        return;
    }

    console.log('✅ 저장된 요일 값:', savedDays);

    // 저장된 요일 값을 배열로 변환 (예: "월,화,수" → ["월", "화", "수"])
    const savedDaysArray = savedDays.split(',').map(day => day.trim());

    // 모든 요일 체크박스 가져오기 (전체 선택 제외)
    const dayCheckboxes = document.querySelectorAll('.checkbox-group input[type="checkbox"]:not(#all-days)');

    // 각 요일 체크박스 상태 업데이트
    dayCheckboxes.forEach(checkbox => {
        if (savedDaysArray.includes(checkbox.id)) {
            checkbox.checked = true;  // 체크된 상태로 설정
        } else {
            checkbox.checked = false; // 체크 해제 상태로 설정
        }
    });

    // ✅ 전체 선택 박스(#all-days) 상태 업데이트
    const allCheckbox = document.getElementById('all-days');
    if (allCheckbox) {
        const allChecked = Array.from(dayCheckboxes).every(cb => cb.checked);
        allCheckbox.checked = allChecked;  // 모든 요일이 체크되었는지 확인
    }
}

// 파일 찾기 버튼 클릭 시 파일 선택창 열기
function openFileDialog(inputId, fileNameInputId) {
    const fileInput = document.getElementById(inputId);
    const fileNameInput = document.getElementById(fileNameInputId);

    fileInput.click();

    // 파일 선택 후 파일명을 인풋 박스에 표시하는 이벤트 리스너 추가
    fileInput.addEventListener('change', function() {
        if (fileInput.files.length > 0) {
            fileNameInput.value = fileInput.files[0].name;  // 첫 번째 파일명 표시
        }
    }, { once: true });  // 이벤트 리스너가 한 번만 실행되도록 설정
}

/**
 * 파일 목록 업데이트
 */
function updateFileList() {
    const fileInput = document.getElementById('activityImages');
    const fileNamesField = document.getElementById('activityImageFileName');

    if (fileInput.files.length > 3) {
        alert("최대 6개의 파일만 선택할 수 있습니다.");
        fileInput.value = "";
        fileNamesField.value = "선택된 파일이 없습니다.";
        return;
    }

    const fileNames = Array.from(fileInput.files).map(file => file.name).join(', ');
    fileNamesField.value = fileNames || "선택된 파일이 없습니다.";
}

// 파일을 폼데이터에 추가하는 함수
function addFileToFormData(fileInputId, fileNameInputId, formData) {
    const fileInput = document.getElementById(fileInputId);
    const fileNameInput = document.getElementById(fileNameInputId);

    if (fileInput.files.length > 0) {
        formData.append(fileInputId, fileInput.files[0]);
    } else {
        console.log(`${fileNameInputId}에 선택된 파일이 없습니다.`);
    }
}


// 체크박스 상태에 따라 파일 업로드 리스트 표시/숨기기
document.getElementById('isMatingAvailable').addEventListener('change', function() {
    const fileUploadContainer = document.getElementById('fileUploadContainer');

    if (this.checked) {
        fileUploadContainer.style.display = 'block';  // 체크 시 파일 리스트 보이기
    } else {
        fileUploadContainer.style.display = 'none';   // 체크 해제 시 숨기기
    }
});
