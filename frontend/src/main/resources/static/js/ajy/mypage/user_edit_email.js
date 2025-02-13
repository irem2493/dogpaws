document.getElementById('emailForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    const provider = document.getElementById('provider').value.trim();
    const email = document.getElementById('email').value.trim();
    const errorMessage = document.getElementById('error-message');

    console.log(provider);

    const requiredFields = ['email'];
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

    const emailElement = document.getElementById('email').value.trim();
    // 이메일 형식 검증 정규식 (RFC 5322 표준을 기반으로 간단하게 작성)
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(emailElement)) {
        alert("올바른 이메일 형식이 아닙니다.");
        return;
    }

    const formData = new FormData();
    formData.append('provider', provider);
    formData.append('email', email);

    api.post('/api/user/checkUserEmail', formData)
        .then(response => {
            console.log("서버 응답:", response); // ✅ 응답 데이터 로그 확인

            if (response.status === 'SUCCESS') {
                const userData = response.body?.body; // ✅ userData 추출
                console.log("UserData:", userData);

                if (!userData) {
                    return Promise.reject(new Error('이메일이 올바르지 않습니다.'));
                }

                // ✅ 폼을 동적으로 생성하여 데이터 전송
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/editUserSocial';

                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'userData';
                input.value = JSON.stringify(userData);

                form.appendChild(input);
                document.body.appendChild(form);

                console.log("폼 제출 실행");
                form.submit();
            } else {
                return Promise.reject(new Error('이메일이 올바르지 않습니다.'));
            }
        })
        .catch(error => {
            console.error("이메일 확인 중 오류 발생:", error);
            errorMessage.textContent = error.message;
            errorMessage.style.display = 'block';
        });
});