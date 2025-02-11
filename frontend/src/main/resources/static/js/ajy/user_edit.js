document.getElementById('editForm').addEventListener('submit', function(event) {

    event.preventDefault();  // 기본 폼 제출 방지

    const form = document.getElementById("editForm");

    // FormData 객체 생성
    const formData = new FormData(form);

    // 기존 값 저장
    const fields = ["nickname", "password","confirmPassword", "ageGroup", "email", "postcode", "address", "detailAddress"];
    const previousValues = {};

    fields.forEach(field => {
        const element = document.getElementById(field);
        previousValues[field] = element.getAttribute("th:value") || "";
    });

    // 입력값 가져오기
    const username = document.getElementById("nickname").value.trim();
    const password = document.getElementById("password").value.trim();
    const nickname = document.getElementById("nickname").value.trim();
    const ageGroup = document.getElementById("ageGroup").value;
    const email = document.getElementById("email").value.trim();
    const postcode = document.getElementById("postcode").value.trim();
    const address = document.getElementById("address").value.trim();
    const detailAddress = document.getElementById("detailAddress").value.trim();

    // 필수값 검증 및 기존 값 유지
    if (!nickname) document.getElementById("nickname").value = previousValues.nickname;
    if (!ageGroup) document.getElementById("ageGroup").value = previousValues.ageGroup;
    if (!email) document.getElementById("email").value = previousValues.email;
    if (!postcode) document.getElementById("postcode").value = previousValues.postcode;
    if (!address) document.getElementById("address").value = previousValues.address;
    if (!detailAddress) document.getElementById("detailAddress").value = previousValues.detailAddress;


   /* for (let field of fields) {
        const inputElement = document.querySelector(`input[name='${field}']`);
        const value = inputElement?.value.trim();
        if (!value) {
            alert(`필수 입력 항목을 모두 채워주세요: ${field}`);
            if (inputElement) {
                inputElement.focus();  // 빈 필드에 포커스 설정
            }
            return;
        }
    }*/

    // 이메일 형식 검증 정규식 (RFC 5322 표준을 기반으로 간단하게 작성)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(email)) {
        alert("올바른 이메일 형식이 아닙니다.");
        return;
    }

    const confirmPasswordInput = document.querySelector("input[name='confirmPassword']");
    const confirmPassword = confirmPasswordInput.value.trim();
    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        if (confirmPasswordInput) {
            confirmPasswordInput.focus();  // 빈 필드에 포커스 설정
        }
        return;
    }

    api.put('/api/user/edit-user', formData, )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.body?.body === '개인정보 수정 완료') {
                alert("개인정보 수정 완료");
                location.href = '/';
            } else {
                alert("개인정보 수정 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("회원정보 수정 중 오류.");
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

function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {

            // 팝업을 통한 검색 결과 항목 클릭 시 실행
            var addr = ''; // 주소_결과값이 없을 경우 공백
            var extraAddr = ''; // 참고항목

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 도로명 주소를 선택
                addr = data.roadAddress;
            } else { // 지번 주소를 선택
                addr = data.jibunAddress;
            }

            if(data.userSelectedType === 'R'){
                if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                    extraAddr += data.bname;
                }
                if(data.buildingName !== '' && data.apartment === 'Y'){
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if(extraAddr !== ''){
                    extraAddr = ' (' + extraAddr + ')';
                }
            } else {
                document.getElementById("address").value = '';
            }

            // 선택된 우편번호와 주소 정보를 input 박스에 넣는다.
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById("address").value = addr;
            document.getElementById("address").value += extraAddr;
            document.getElementById("detailAddress").focus(); // 우편번호 + 주소 입력이 완료되었음으로 상세주소로 포커스 이동
        }
    }).open({
        left: Math.max(0, (window.innerWidth / 2) - (500 / 2) + window.screenX),  // 화면 중앙 정렬
        top: Math.max(0, (window.innerHeight / 2) - (600 / 2) + window.screenY)   // 화면 중앙 정렬
    });
}

async function deleteUser(){
    username = sessionStorage.getItem('username');

    // 로그아웃 실행
    try {
        await logout();  // 로그아웃 함수 호출
    } catch (error) {
        console.error('로그아웃 중 오류 발생:', error);
        alert("로그아웃 중 오류가 발생했습니다.");
        return;  // 오류 발생 시 탈퇴 프로세스 중단
    }

    api.put(`/api/user/delete-user?username=${username}` )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.body?.body === '회원탈퇴 완료') {
                alert("회원탈퇴 완료");
                location.href = '/';
            } else {
                alert("회원탈퇴 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("회원탈퇴 중 오류.");
        });
}

