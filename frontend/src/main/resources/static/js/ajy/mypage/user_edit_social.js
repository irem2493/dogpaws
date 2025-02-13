document.getElementById('editForm').addEventListener('submit', function(event) {

    event.preventDefault();  // 기본 폼 제출 방지

    const form = document.getElementById("editForm");

    // FormData 객체 생성
    const formData = new FormData(form);

    // 기존 값 저장
    const fields = ["postcode", "address", "detailAddress"];
    const previousValues = {};

    fields.forEach(field => {
        const element = document.getElementById(field);
        previousValues[field] = element.getAttribute("th:value") || "";
    });



    fields.forEach(field => {
        const inputElement = document.getElementById(field);
        if (inputElement) {
            const currentValue = inputElement.value.trim();
            const prevValue = inputElement.getAttribute("data-prev-value") || "";

            // 값이 비어 있으면 이전 값으로 설정
            if (!currentValue) {
                inputElement.value = prevValue;
            }
        }
    });


    api.put('/api/user/edit-user-social', formData, )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.body?.body === '개인정보 수정 완료') {
                alert("개인정보 수정 완료");
            } else {
                alert("개인정보 수정 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("회원정보 수정 중 오류.");
        });


});


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

document.getElementById("deleteAccountBtn").addEventListener("click", function () {
    if (confirm("정말로 회원탈퇴 하시겠습니까?")) {
        deleteUser(); // ✅ 회원탈퇴 진행
    }
});

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

