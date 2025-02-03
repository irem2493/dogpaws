document.addEventListener("DOMContentLoaded", function () {

    // "이전" 버튼 클릭 시 페이지 이동
    document.querySelector("#preButton").addEventListener("click", function () {
        // 이동할 페이지 URL 설정 (예: nextpage.html)
        window.location.href = "/matching_select";
    });

});


document.getElementById('step4Form').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    const joinButton = document.getElementById('joinButton');

    const formData = new FormData(this);

    api.post('/api/join/step4', formData)
        .then(data => {
            console.log('Response Data:', data);

            if (data.status === 'SUCCESS') {
                alert("회원가입 완료");
                location.href = '/';
                // 세션 무효화 API 호출
                return api.post('/api/join/success-join');
            } else {
                throw new Error("회원가입 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("회원가입 중 오류가 발생했습니다.");
        })
});
