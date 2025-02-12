document.getElementById('checkForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMessage = document.getElementById('error-message');

    const requiredFields = ['password'];
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

    const formData = new FormData();
    formData.append('username', username);
    formData.append('password', password);

    api.post('/api/user/check-user', formData)
        .then(response => {
            if (response.status === 'SUCCESS') {
                const userData = response.body?.body; // 받아온 userData 객체

                // POST 요청으로 /editUser로 데이터 전송
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = '/editUser';

                // hidden input을 만들어서 데이터를 전송합니다
                const input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'userData';  // 서버에서 받을 파라미터 이름
                input.value = JSON.stringify(userData);  // 데이터 직렬화 후 입력값으로 설정

                form.appendChild(input);
                document.body.appendChild(form);

                // 폼 제출
                form.submit();
            } else {
                throw new Error('확인 실패');
            }
        })
        .catch(error => {
            errorMessage.textContent = '비밀번호가 올바르지 않습니다.';
            errorMessage.style.display = 'block';
            console.error('패스워드 에러:', error);
        });
});