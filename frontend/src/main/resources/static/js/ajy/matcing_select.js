document.addEventListener("DOMContentLoaded", function () {

    api.post('/api/join/step3/data')
        .then(data => {
            console.log('세션 데이터:', data);

            // 응답 JSON에서 사용자 정보를 가져옴
            const is_mating_available = data.body?.body;


            if (is_mating_available === 'Y') {
                document.getElementById('isMatingAvailable').checked = true;
            } else {
                document.getElementById('isMatingAvailable').checked = false;
            }

        })
        .catch(error => {
            console.error('세션 데이터 로드 오류:', error);
        });

    // "이전" 버튼 클릭 시 페이지 이동
    document.querySelector("#preButton").addEventListener("click", function () {
        // 이동할 페이지 URL 설정 (예: nextpage.html)
        window.location.href = "/dogprofile";
    });
});

function saveStep3() {

    // FormData 객체 생성
    const formData = new FormData();
    formData.append('is_mating_available', document.getElementById('isMatingAvailable').checked ? 'Y' : 'N');

    // fetch로 FormData 전송
    api.post('/api/join/step3', formData, )
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.status === 'SUCCESS') {
                alert("3단계 저장 성공");
                location.href = '/matching_document';
            } else {
                alert("3단계 저장 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("3단계 저장 중 오류.");
        });

}

