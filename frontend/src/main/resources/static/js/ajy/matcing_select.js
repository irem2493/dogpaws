document.addEventListener("DOMContentLoaded", function () {

    // "이전" 버튼 클릭 시 페이지 이동
    document.querySelector("#preButton").addEventListener("click", function () {
        // 이동할 페이지 URL 설정 (예: nextpage.html)
        window.location.href = "/dogprofile";
    });
});


// 체크박스 상태에 따라 파일 업로드 리스트 표시/숨기기
document.getElementById('isMatingAvailable').addEventListener('change', function() {
    const fileUploadContainer = document.getElementById('fileUploadContainer');

    if (this.checked) {
        fileUploadContainer.style.display = 'block';  // 체크 시 파일 리스트 보이기
    } else {
        fileUploadContainer.style.display = 'none';   // 체크 해제 시 숨기기
    }
});

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


document.getElementById('step3Form').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    console.log("폼 제출 이벤트가 호출되었습니다.");  // 테스트 메시지

    const formData = new FormData();

    // 파일 선택 상태 확인 및 폼데이터에 추가
    addFileToFormData('fileInput1', 'fileName1', formData);
    addFileToFormData('fileInput2', 'fileName2', formData);
    addFileToFormData('fileInput3', 'fileName3', formData);

    // 기타 다른 필드 추가 (예시)
    formData.append('isMatingAvailable', document.getElementById('isMatingAvailable').checked ? 'Y' : 'N');

    // 폼데이터 확인
    for (let pair of formData.entries()) {
        console.log(`${pair[0]}: ${pair[1]}`);
    }

    api.post('/api/join/step3', formData)
        .then(data => {
            console.log('Response Data:', data);

            if (data.body?.body === '회원가입 완료') {
                alert("회원가입 완료");
                location.href = '/';
                // 세션 무효화 API 호출
                return api.post('/api/join/success-join');
            } else if(data.body?.body === '로그인 완료'){

                console.log(data.body.access_token);
                console.log(data.body.username);
                console.log(data.body.role);
                console.log(data.body.nickname);

                sessionStorage.setItem('accessToken', data.body.access_token);
                sessionStorage.setItem('username', data.body.username);
                sessionStorage.setItem('role', data.body.role);
                sessionStorage.setItem('nickname', data.body.nickname);
                alert('로그인 성공!');

                location.href='/dogProfileSelect';
            }else {
                throw new Error("회원가입 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("회원가입 중 오류가 발생했습니다.");
        })
});

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
