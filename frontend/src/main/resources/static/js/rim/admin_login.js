console.log("hello admin-login.js...");

document.addEventListener('DOMContentLoaded', function (){
    // 페이지 진입 시 세션스토리지 초기화
    clearSessionStorage();


    const adminLoginForm = document.getElementById('adminLoginForm');
    const logoutButton = document.getElementById('logoutButton');

    adminLoginForm.addEventListener('submit',function (e){
        e.preventDefault();
        adminLogin();
    });
    logoutButton.addEventListener('click', function (e){
        e.preventDefault()
        logout();
    });

});

// 세션스토리지 초기화 함수
function clearSessionStorage() {
    const keysToRemove = ['username', 'role', 'nickname', 'accessToken'];
    keysToRemove.forEach(key => sessionStorage.removeItem(key));
    console.log('세션스토리지 초기화 완료');
}

function adminLogin() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (!username || !password) {
        alert("아이디 또는 비밀번호를 입력하세요");
        return;
    }

    adminApi.post('/auth/login', { username, password })
        .then(response => {
            console.log("로그인 응답 : ", response);
            const accessToken = response.headers['authorization'];
            if (accessToken) {
                sessionStorage.setItem("accessToken", accessToken.split(' ')[1]);
                alert('로그인 성공!');
                window.location.href = "/admin/main";
            } else {
                throw new Error('AccessToken이 없습니다.');
            }
        })
        .catch(error => {
            console.error('로그인 실패:', error);
            alert('로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.');
        });
}