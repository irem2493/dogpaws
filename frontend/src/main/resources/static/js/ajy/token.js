document.addEventListener("DOMContentLoaded", function() {

    console.log("DOMContentLoaded 이벤트 실행됨"); // 추가

    verifyAccessToken()
        .then(() => {
            console.log("토큰 검증 완료");
        })
        .catch((error) => {
            console.error("토큰 검증 중 오류 발생:", error);
        });

    // 로그아웃 버튼이 존재하는지 확인 후 이벤트 등록
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            logout().then(() => {
                alert('로그아웃 성공');
            }).catch((error) => {
                console.log('로그아웃 중 오류 발생:', error);
            });
        });
    } else {
        console.error("로그아웃 버튼을 찾을 수 없습니다.");
    }
});

async function verifyAccessToken() {

    console.log("verifyAccessToken 호출됨"); // 추가

    const accessToken = sessionStorage.getItem('accessToken');

    if (!accessToken) {
        console.log('Access Token이 없습니다.');
        return;
    }

    try {
        await api.post('/api/verify-token', {}, accessToken);
        console.log('Access Token 유효');
    } catch (error) {
        if (error.message.includes('401')) {
            console.log('Access Token 만료됨. Refresh Token으로 갱신 시도 중...');
            await refreshAccessToken();
        } else {
            console.error('Access Token 검증 중 오류 발생:', error);
        }
    }
}

async function refreshAccessToken() {
    try {
        const refreshToken = getRefreshTokenFromCookie();

        if (!refreshToken) {
            console.log('Refresh Token이 없습니다. 재로그인 필요');
            return logout();
        }

        const response = await api.post('/api/auth/token/refresh', { refreshToken });

        // 응답에서 Access Token을 가져온 후 저장
        const accessToken = response.accessToken;

        if (accessToken) {
            sessionStorage.setItem('accessToken', accessToken);
            console.log('Access Token 갱신 완료');
        } else {
            console.log('Access Token 갱신 실패. 재로그인 필요');
            void logout();
        }
    } catch (error) {
        console.log('토큰 갱신 실패. 재로그인 필요');
        void logout();
    }
}

    async function logout() {
        try {
            // 서버에 GET 요청으로 로그아웃 API 호출
            console.time("authLogoutAPI");
            await api.post('/api/auth/logout', {});
            console.timeEnd("authLogoutAPI");

            // 세션 및 쿠키 삭제
            sessionStorage.removeItem('accessToken');
            document.cookie = 'Refresh-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;';

            location.href="/logout";
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error);
        }
    }

function getRefreshTokenFromCookie() {
    const cookies = document.cookie.split('; ');
    for (let cookie of cookies) {
        const [name, value] = cookie.split('=');
        if (name === 'Refresh-Token') {
            return value;
        }
    }
    return null;
}

