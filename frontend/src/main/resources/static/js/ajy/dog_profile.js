let provider='';

document.addEventListener("DOMContentLoaded", function() {
    const profileUpload = document.getElementById('profileUpload');
    const fileInput = document.getElementById('fileInput');
    const profileImagePreview = document.getElementById('profileImagePreview');
    const uploadCircle = document.querySelector('.profile-upload-circle');

    api.post('/api/join/step2/data')
        .then(data => {
            console.log('세션 데이터:', data);

            // 응답 JSON에서 사용자 정보를 가져옴
            const dog = data.body?.body;

            if (dog) {
                // 입력 필드에 값 채우기
                document.getElementById('dogName').value = dog.dog_name || '';
                // gender 값 설정 로직
                if (dog.gender === 'M') {
                    document.querySelector('button[onclick="setGender(\'M\', this)"]').classList.add('active');
                } else if (dog.gender === 'F') {
                    document.querySelector('button[onclick="setGender(\'F\', this)"]').classList.add('active');
                }
                document.getElementById('gender').value = dog.gender || '';
                document.getElementById('breedSelect').value = dog.breed || '';
                document.getElementById('weight').value = dog.weight || '';
                document.getElementById('birthYear').value = dog.birth_year || '';
                document.getElementById('birthMonth').value = dog.birth_month || '';
                document.getElementById('dogIntro').value = dog.dog_intro || '';
                document.getElementById('walkStartTime').value = dog.walk_start_time || '';
                document.getElementById('walkEndTime').value = dog.walk_end_time || '';

                if(dog.walk_days)
                    updateCheckboxesFromSavedDays(dog.walk_days);

                if(dog.selected_personalities)
                    // 호출: 저장된 성격 값에 따라 체크박스 업데이트
                    showSelectedOptions(dog.selected_personalities);

                if(dog.selected_plays)
                    // 호출: 저장된 성격 값에 따라 체크박스 업데이트
                    showSelectedPlayOptions(dog.selected_plays);


                if (dog.is_mix === 'Y') {
                    document.getElementById('mixBreed').checked = true;
                } else {
                    document.getElementById('mixBreed').checked = false;
                }

                if (dog.is_neutered === 'Y') {
                    document.getElementById('isNeutered').checked = true;

                } else {
                    document.getElementById('isNeutered').checked = false;
                }

                if (dog.walk_time_yn === 'Y') {
                    document.getElementById('walkTimeYn').checked = true;
                    updateWalkInputs();
                } else {
                    document.getElementById('walkTimeYn').checked = false;
                }

                // 서버에서 가져온 프로필 이미지가 있을 경우 미리보기 표시
                if (dog.profile_url) {
                    console.log('프로필 URL:', dog.profile_url);
                    profileImagePreview.src = dog.profile_url;
                    profileImagePreview.style.display = 'block';
                    uploadCircle.style.display = 'none'; // 프로필 이미지가 있으면 + 버튼 숨기기
                } else {
                    profileImagePreview.style.display = 'none'; // 초기에는 이미지 숨기기
                }

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


            }
            if(dog.activity_image_file_name){
                //displayUploadedImages(dog.activity_image_metadata, dog.activity_image_file_name);
                document.getElementById('activityImageFileName').value = dog.activity_image_file_name || '';
            }
        })
        .catch(error => {
            console.error('세션 데이터 로드 오류:', error);
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
        selectedDays = Array.from(dayCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.id);

        // hidden input에 값 설정
        hiddenDaysInput.value = selectedDays.join(',');
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

    // "이전" 버튼 클릭 시 페이지 이동
    document.querySelector("#preButton").addEventListener("click", function () {
        // 이동할 페이지 URL 설정 (예: nextpage.html)

        api.post('/api/join/social/provider')
            .then(data => {
                console.log('세션 데이터:', data);

                // 응답 JSON에서 사용자 정보를 가져옴
                const provider = data.body?.body;

                if (provider) window.location.href = "/socialJoin";
                else window.location.href = "/join";
            })
            .catch(error => {
                console.error('세션 데이터 로드 오류:', error);
            });
    });

});

// 연도 및 월 옵션 추가
const selectBox_year = document.getElementById('birthYear');
for (let year = 1990; year <= 2025; year++) {
    let option = document.createElement('option');
    option.value = year;
    option.textContent = year;
    option.classList.add('select-box-option');
    selectBox_year.appendChild(option);
}

const selectBox_month = document.getElementById('birthMonth');
for (let i = 1; i <= 12; i++) {
    let option = document.createElement('option');
    let month = i.toString().padStart(2, '0');
    option.value = month;
    option.textContent = month;
    option.classList.add('select-box-option');
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
        hiddenInput.value = checkbox.value; // AC, AG 등의 코드 값이 들어감

        selectedDiv.textContent = displayText; // 사용자에게는 '활발함' 등의 값이 보임
        selectedDiv.appendChild(hiddenInput); // 숨겨진 input 추가

        const removeBtn = document.createElement("span");
        removeBtn.textContent = " ✖";
        removeBtn.style.cursor = "pointer";
        removeBtn.onclick = function () {
            selectedContainer.removeChild(selectedDiv);
            checkbox.checked = false;
        };

        selectedDiv.appendChild(removeBtn);
        selectedContainer.appendChild(selectedDiv);
    } else {
        const selectedOptions = document.querySelectorAll('.selected-option');
        selectedOptions.forEach(option => {
            if (option.textContent.includes(checkbox.value)) {
                selectedContainer.removeChild(option);
            }
        });
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
        hiddenInput.value = checkbox.value; // AC, AG 등의 코드 값이 들어감

        selectedDiv.textContent = displayText; // 사용자에게는 '활발함' 등의 값이 보임
        selectedDiv.appendChild(hiddenInput); // 숨겨진 input 추가

        const removeBtn = document.createElement('span');
        removeBtn.textContent = ' ✖';
        removeBtn.style.cursor = 'pointer';
        removeBtn.onclick = function () {
            selectedContainer.removeChild(selectedDiv);
            checkbox.checked = false;
        };

        selectedDiv.appendChild(removeBtn);
        selectedContainer.appendChild(selectedDiv);
    } else {
        const selectedOptions = document.querySelectorAll('.selected-option');
        selectedOptions.forEach(option => {
            if (option.textContent.includes(checkbox.value)) {
                selectedContainer.removeChild(option);
            }
        });
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
    const buttons = document.querySelectorAll('.gender-select button');
    buttons.forEach(button => button.classList.remove('active'));

    // 클릭된 버튼에 active 클래스 추가
    btn.classList.add('active');
}

function initializeGenderSelection() {
    const genderValue = document.getElementById('gender').value;

    // 초기 값에 따라 active 클래스 추가
    if (genderValue) {
        const button = document.querySelector(`.gender-select button[onclick="setGender('${genderValue}', this)"]`);
        if (button) {
            button.classList.add('active');
        }
    }
}

// 페이지 로드 시 초기 상태 설정
initializeGenderSelection();

function saveStep2() {
    const form = document.getElementById("step2Form");

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

    // fetch로 FormData 전송
    api.post('/api/join/step2', formData, )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.status === 'SUCCESS') {
                alert("2단계 저장 성공");
                location.href = '/matching_select';
            } else {
                alert("2단계 저장 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("2단계 저장 중 오류.");
        });

}

// 체크박스를 업데이트하는 함수
function updateCheckboxesFromSavedDays(savedDays) {
    console.log('저장된 요일 값:', savedDays);

    // 세션에서 가져온 값을 배열로 변환
    const savedDaysArray = savedDays.split(',').map(day => day.trim());

    // 각 체크박스 요소 가져오기
    const dayCheckboxes = document.querySelectorAll('.checkbox-group input[type="checkbox"]:not(#all-days)');

    // 각 요일에 해당하는 체크박스 상태 업데이트
    dayCheckboxes.forEach(checkbox => {
        checkbox.checked = savedDaysArray.includes(checkbox.id);
    });

    // 전체 체크박스 상태 업데이트
    const allCheckbox = document.getElementById('all-days');
    const allChecked = Array.from(dayCheckboxes).every(cb => cb.checked);
    allCheckbox.checked = allChecked;
}

// 선택된 옵션을 표시하는 함수
function showSelectedOptions(selectedValues) {
    const selectedContainer = document.getElementById('selectedOptions');
    const valuesArray = selectedValues.split(',').map(value => value.trim());

    valuesArray.forEach(value => {
        const checkbox = document.querySelector(`input[type="checkbox"][value="${value}"]`);

        if (checkbox) {
            checkbox.checked = true;

            // 사용자에게 선택된 옵션 표시
            const selectedDiv = document.createElement("div");
            selectedDiv.classList.add("selected-option");

            // ✅ 사용자에게 보여줄 값 (gubn_name)
            const displayText = checkbox.getAttribute("data-name");

            // ✅ 실제로 DB에 저장할 값 (gubn_code)
            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "selectedPersonalities"; // 서버로 전송될 필드 이름
            hiddenInput.value = value; // AC, AG 등의 코드 값이 들어감

            selectedDiv.textContent = displayText; // 사용자에게는 '활발함' 등의 값이 보임
            selectedDiv.appendChild(hiddenInput); // 숨겨진 input 추가

            // ✖ 버튼 추가
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
    });
}

// 선택된 옵션을 표시하는 함수
function showSelectedPlayOptions(selectedValues) {
    const selectedContainer = document.getElementById('selectedOptions2');
    const valuesArray = selectedValues.split(',').map(value => value.trim());

    valuesArray.forEach(value => {
        const checkbox = document.querySelector(`input[type="checkbox"][value="${value}"]`);

        if (checkbox) {
            checkbox.checked = true;

            // 사용자에게 선택된 옵션 표시
            const selectedDiv = document.createElement("div");
            selectedDiv.classList.add("selected-option");

            // ✅ 사용자에게 보여줄 값 (gubn_name)
            const displayText = checkbox.getAttribute("data-name");

            // ✅ 실제로 DB에 저장할 값 (gubn_code)
            const hiddenInput = document.createElement("input");
            hiddenInput.type = "hidden";
            hiddenInput.name = "selectedPlays"; // 서버로 전송될 필드 이름
            hiddenInput.value = value; // WA, SW 등의 코드 값이 들어감

            selectedDiv.textContent = displayText; // 사용자에게는 '산책하기' 등의 값이 보임
            selectedDiv.appendChild(hiddenInput); // 숨겨진 input 추가

            // ✖ 버튼 추가
            const removeBtn = document.createElement('span');
            removeBtn.textContent = ' ✖';
            removeBtn.style.cursor = 'pointer';
            removeBtn.onclick = function () {
                selectedContainer.removeChild(selectedDiv);
                checkbox.checked = false;
            };

            selectedDiv.appendChild(removeBtn);
            selectedContainer.appendChild(selectedDiv);
        }
    });
}

/*function displayUploadedImages(imageUrls) {
    const photoPreview = document.getElementById('photoPreview');
    console.log(imageUrls);

    imageUrls.forEach(url => {
        const img = document.createElement('img');
        img.src = url;

        const photoBox = document.createElement('div');
        photoBox.classList.add('photo-box');

        // 사진 삭제 버튼 추가
        const deleteBtn = document.createElement('span');
        deleteBtn.classList.add('delete-btn');
        deleteBtn.textContent = '✖';
        deleteBtn.onclick = function() {
            photoBox.remove();

            // 인풋 박스에서 해당 파일명 제거
            const fileName = url.substring(url.lastIndexOf('/') + 1);
            const currentFileNames = activityImageFileName.value.split(', ').filter(name => name !== fileName);
            activityImageFileName.value = currentFileNames.join(', ');
        };

        photoBox.appendChild(img);
        photoBox.appendChild(deleteBtn);
        photoPreview.appendChild(photoBox);

        // 인풋 박스에 파일명 추가
        const fileName = url.substring(url.lastIndexOf('/') + 1);
        const currentFileNames = activityImageFileName.value ? activityImageFileName.value.split(', ') : [];
        currentFileNames.push(fileName);
        activityImageFileName.value = currentFileNames.join(', ');
    });
}*/

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