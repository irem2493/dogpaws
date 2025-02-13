document.getElementById('checkForm').addEventListener('submit', function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMessage = document.getElementById('error-message');

    // 필수 입력 항목 검증
    if (!password) {
        alert("비밀번호를 입력하세요.");
        document.getElementById('password').focus();
        return;
    }

    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);

    api.post('/api/user/check-user', formData)
        .then(response => {
            console.log("서버 응답:", response); // ✅ 응답 데이터 로그 확인

            if (response.status === 'SUCCESS') {
                const userData = response.body?.body; // ✅ userData 추출
                console.log("UserData:", userData);

                if (!userData) {
                    return Promise.reject(new Error('비밀번호가 올바르지 않습니다.'));
                }

                // ✅ 폼을 동적으로 생성하여 데이터 전송
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/editUser';

                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'userData';
                input.value = JSON.stringify(userData);

                form.appendChild(input);
                document.body.appendChild(form);

                console.log("폼 제출 실행");
                form.submit();
            } else {
                return Promise.reject(new Error('비밀번호가 올바르지 않습니다.'));
            }
        })
        .catch(error => {
            console.error("비밀번호 확인 중 오류 발생:", error);
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        });
});
