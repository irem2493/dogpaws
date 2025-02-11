document.addEventListener("DOMContentLoaded", function () {

    api.post('/api/join/social/step1/data')
        .then(data => {
            console.log('세션 데이터:', data);

            // 응답 JSON에서 사용자 정보를 가져옴
            const user = data.body?.body;

            if (user) {
                console.log('user 객체:', user);
                document.getElementById('postcode').value = user.postcode || '';
                document.getElementById('address').value = user.address || '';
                document.getElementById('detailAddress').value = user.detail_address || '';
            }
        })
        .catch(error => {
            console.error('세션 데이터 로드 오류:', error);
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

function socialSaveStep1(){
    const form = document.getElementById("socialStep1Form");

    // FormData 객체 생성
    const formData = new FormData(form);

    const requiredFields = ['postcode', 'address', 'detailAddress'];
    for (let field of requiredFields) {
        const inputElement = document.querySelector(`input[name='${field}']`);
        const value = inputElement?.value.trim();
        if (!value) {
            alert(`필수 입력 항목을 모두 채워주세요: ${field}`);
            if (inputElement) {
                inputElement.focus();  // 빈 필드에 포커스 설정
            }
            return;
        }
    }

    api.post('/api/join/social/step1', formData, )
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