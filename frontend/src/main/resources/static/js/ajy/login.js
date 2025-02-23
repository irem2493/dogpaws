document.getElementById('loginForm').addEventListener('submit', async function (event) {
    event.preventDefault(); // 기본 폼 제출 방지

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMessage = document.getElementById('error-message');

    // 필수 입력 필드 체크
    const requiredFields = ['username', 'password'];
    for (let field of requiredFields) {
        const inputElement = document.querySelector(`input[name='${field}']`);
        const value = inputElement?.value.trim();
        if (!value) {
            alert(`필수 입력 항목을 모두 채워주세요: ${field}`);
            if (inputElement) {
                inputElement.focus();
            }
            return;
        }
    }
    try {
        // 1️⃣ 아이디 존재 여부 확인 API 호출
        const checkUserResponse = await api.get(`/api/join/check/${username}`);

        console.log(checkUserResponse);

        if (checkUserResponse.body?.body === '사용 가능') {
            // 2️⃣ 아이디가 존재하지 않으면 회원가입 페이지로 이동
            alert('입력하신 아이디가 존재하지 않습니다. 회원가입 페이지로 이동합니다.');
            location.href = '/join';
            return;
        }

        // 3️⃣ 아이디가 존재하면 로그인 요청
        const loginResponse = await api.post('/login', { username, password, userType: 'ROLE_USER' });

        sessionStorage.setItem('accessToken', loginResponse.body.access_token);
        sessionStorage.setItem('username', loginResponse.body.username);
        sessionStorage.setItem('role', loginResponse.body.role);
        sessionStorage.setItem('nickname', loginResponse.body.nickname);
        alert('로그인 성공!');

        if (loginResponse.body.role === 'ROLE_USER') {
            location.href = '/dog/dogProfileSelect';
        } else {
            throw new Error('올바르지 않은 역할');
        }

    } catch (error) {
        errorMessage.textContent = '아이디 또는 비밀번호가 올바르지 않습니다.';
        errorMessage.style.display = 'block';
        console.error('로그인 에러:', error);
    }
});
