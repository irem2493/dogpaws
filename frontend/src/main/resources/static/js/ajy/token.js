async function verifyAccessToken() {
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

            const data = await api.post('/api/auth/token/refresh', { refreshToken });

            // Access Token 갱신 후 세션 스토리지에 저장
            sessionStorage.setItem('accessToken', data.accessToken);
            console.log('Access Token 갱신 완료');
        } catch (error) {
            console.log('토큰 갱신 실패. 재로그인 필요');
            logout();
        }
    }


verifyAccessToken()
    .then(() => {
        console.log("토큰 검증 완료");
    })
    .catch((error) => {
        console.error("토큰 검증 중 오류 발생:", error);
    });


    async function logout() {
        try {
            await api.post('/api/auth/logout', {});

            // 세션 및 쿠키 삭제
            sessionStorage.removeItem('accessToken');
            document.cookie = 'Refresh-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;';

            window.location.href = '/login';  // 로그인 페이지로 이동
        } catch (error) {
            console.error('로그아웃 중 오류 발생:', error);
        }
    }