let isDuplicateChecked = false;

document.addEventListener("DOMContentLoaded", function () {

    api.post('/api/join/step1/data')
        .then(data => {
            console.log('세션 데이터:', data);

            // 응답 JSON에서 사용자 정보를 가져옴
            const user = data.body?.body;

            if (user) {
                console.log('user 객체:', user);
                // 입력 필드에 값 채우기
                document.getElementById('username').value = user.username || '';
                document.getElementById('email').value = user.email || '';

                // gender 값 설정 로직
                if (user.gender === 'M') {
                    document.querySelector('button[onclick="setGender(\'M\', this)"]').classList.add('active');
                } else if (user.gender === 'F') {
                    document.querySelector('button[onclick="setGender(\'F\', this)"]').classList.add('active');
                }
                document.getElementById('gender').value = user.gender || '';
                document.getElementById('ageGroup').value = user.age_group || '';
                document.getElementById('nickname').value = user.nickname || '';
                document.getElementById('postcode').value = user.postcode || '';
                document.getElementById('address').value = user.address || '';
                document.getElementById('detailAddress').value = user.detail_address || '';
            }
        })
        .catch(error => {
            console.error('세션 데이터 로드 오류:', error);
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

function saveStep1() {
    const form = document.getElementById("step1Form");

    // FormData 객체 생성
    const formData = new FormData(form);

    /*for (let pair of formData.entries()) {
        console.log(pair[0] + ': ' + pair[1]);  // 폼 데이터 확인
    }*/

    // fetch로 FormData 전송
    api.post('/api/join/step1', formData, )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.body?.body === '1단계 저장 완료') {
                alert("1단계 저장 성공");
                location.href = '/dogprofile';
            } else {
                alert("1단계 저장 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("1단계 저장 중 오류.");
        });
    
}

function idCheck() {
    const username = document.querySelector("input[name='username']");
    const checkButton = document.querySelector("button[onclick='idCheck()']");

    if (!username) {
        alert("아이디를 입력해주세요.");
        return;
    }

    api.get(`/api/join/check/${username.value.trim()}`)
        .then(data => {
            if (data.body === '중복됨') {

                alert("중복된 아이디 입니다.");
                isDuplicateChecked = false;

            } else {
                alert("사용 가능한 아이디입니다.");
                checkButton.classList.add("disabled");
                username.display = true;
                checkButton.disabled = true;
                checkButton.textContent = "확인 완료";
                checkButton.classList.add("disabled");  // 버튼 비활성화 클래스 추가
                isDuplicateChecked = true;
            }
        })
        .catch(error => {
            console.error(error);
            alert("에러 발생.");
        });
}