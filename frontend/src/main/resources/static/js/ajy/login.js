document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMessage = document.getElementById('error-message');

    const requiredFields = ['username', 'password'];
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

    api.post('/login', {username, password, userType:'ROLE_USER'})
        .then(response => {
            sessionStorage.setItem('accessToken', response.body.access_token);
            sessionStorage.setItem('username', response.body.username);
            sessionStorage.setItem('role', response.body.role);
            sessionStorage.setItem('nickname', response.body.nickname);
            alert('로그인 성공!');

          if (response.body.role === 'ROLE_USER') {
                location.href = '/dog/dogProfileSelect';
            } else {
                throw new Error('올바르지 않은 역할');
            }
        })
        .catch(error => {
            errorMessage.textContent = '아이디 또는 비밀번호가 올바르지 않습니다.';
            errorMessage.style.display = 'block';
            console.error('로그인 에러:', error);
        });
});