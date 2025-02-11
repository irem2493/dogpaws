document.addEventListener("DOMContentLoaded", function() {

    console.log("DOMContentLoaded 이벤트 실행됨"); // 추가

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

