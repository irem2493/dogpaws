function checkTokenExpiration() {
    const accessToken = sessionStorage.getItem('accessToken');
    if (accessToken) {
        const tokenPayload = JSON.parse(atob(accessToken.split('.')[1]));
        const expirationTime = tokenPayload.exp * 1000;
        const currentTime = Date.now();
        const timeRemaining = expirationTime - currentTime;

        console.log('토큰 만료 시간:', new Date(expirationTime).toLocaleString());
        console.log('만료까지 남은 시간:', Math.floor(timeRemaining / 1000), '초');

        // 만료되기 1분 전에 갱신
        if (timeRemaining < 60 * 1000) {
            refreshAccessToken().then(() => {
                console.log('새로운 액세스 토큰이 설정되었습니다.');
                // 토큰 갱신 후 새로운 만료 시간 계산 및 타이머 재설정
                const newAccessToken = sessionStorage.getItem('accessToken');
                const newTokenPayload = JSON.parse(atob(newAccessToken.split('.')[1]));
                const newExpirationTime = newTokenPayload.exp * 1000;
                const newTimeRemaining = newExpirationTime - Date.now();

                // 새로운 만료 시간 1분 전에 다시 체크하도록 타이머 설정
                setTimeout(checkTokenExpiration, newTimeRemaining - 60 * 1000);

            }).catch(error => {
                console.error('토큰 갱신 실패:', error);
                alert('세션이 만료되었습니다. 다시 로그인해주세요.');
                logout();
            });
        }
    } else {
        // 액세스 토큰이 없는 경우
        alert('로그인이 필요합니다.');
        window.location.href = '/admin/login';
    }
}

function logout() {
    adminApi.post('/auth/logout')
        .then(response => {
            console.log("로그아웃 응답 : ", response);

            if(sessionStorage.getItem('accessToken')){
                sessionStorage.removeItem('accessToken');
            }

            alert('로그아웃 성공!');
            window.location.href = "/admin/login"; // 로그아웃 후 로그인 페이지로 이동
        })
        .catch(error => {
            console.error("로그아웃 실패 : ", error);
            alert('로그아웃 실패: 다시 시도해 주세요.');
        });
}

// 현재 페이지가 /admin/login이 아닌 경우에만 토큰 만료 여부 확인
if (!window.location.pathname.includes('/admin/login')) {
    document.addEventListener('DOMContentLoaded', function() {
        const accessToken = sessionStorage.getItem('accessToken');
        if (!accessToken) {
            alert('로그인이 필요합니다.');
            window.location.href = '/admin/login';
            return;
        }

        checkTokenExpiration();

        const tokenPayload = JSON.parse(atob(accessToken.split('.')[1]));
        const expirationTime = tokenPayload.exp * 1000;
        const currentTime = Date.now();
        const timeRemaining = expirationTime - currentTime;
        
        // 토큰 만료 1분 전에 갱신 시도
        setTimeout(checkTokenExpiration, timeRemaining - 60 * 1000);
    });
}