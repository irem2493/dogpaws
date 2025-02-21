document.addEventListener("DOMContentLoaded", function() {
    console.log("DOMContentLoaded 이벤트 실행됨");

    // ✅ 현재 페이지가 로그인 페이지가 아닐 때만 실행
    if (window.location.pathname !== "/login") {
        verifyAccessToken()
            .then(() => console.log("토큰 검증 완료"))
            .catch(error => console.error("토큰 검증 중 오류 발생:", error));
    }

    // 로그아웃 버튼 이벤트 등록
    const logoutButton = document.getElementById('logoutButton');
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            logout().then(() => {
                location.href="/logout";
            }).catch((error) => {
                console.log('로그아웃 중 오류 발생:', error);
            });
        });
    } else {
        console.error("로그아웃 버튼을 찾을 수 없습니다.");
    }
});
async function verifyAccessToken() {
    const accessToken = sessionStorage.getItem('accessToken');

    // ✅ 로그인하지 않은 경우 실행하지 않음
    if (!accessToken) {
        console.log("🔹 로그인되지 않은 상태입니다.");
        return; // 로그인 전에는 더 이상 실행하지 않음
    }

    try {
        await api.post('/api/verify-token', {}, {
            headers: {
                "Authorization": `Bearer ${accessToken}`
            }
        });
        console.log('✅ Access Token 유효');
    } catch (error) {
        if (error.response && error.response.status === 401) {
            console.log('⚠️ Access Token 만료됨. Refresh Token으로 갱신 시도 중...');
            await refreshAccessToken();
        } else {
            console.error('❌ Access Token 검증 중 오류 발생:', error);
        }
    }
}


async function refreshAccessToken() {
    try {
        // ✅ 로그인한 사용자만 실행하도록 체크
        const accessToken = sessionStorage.getItem('accessToken');
        if (!accessToken) {
            console.log("🔹 로그인되지 않은 상태입니다. 토큰 갱신 불필요");
            return;
        }

        // ✅ 현재 페이지가 로그인 페이지("/login")라면 실행하지 않음
        if (window.location.pathname === "/login") {
            console.log("🛑 로그인 페이지에서는 refreshAccessToken() 실행 안 함");
            return;
        }

        const refreshToken = getRefreshTokenFromCookie();

        if (!refreshToken) {
            console.log('❌ Refresh Token 없음. 로그인 페이지로 이동합니다.');
            if (window.location.pathname !== "/login") {
                alert("세션이 만료되었습니다. 다시 로그인해주세요.");
                window.location.href = "/login"; // ✅ 로그아웃이 아닌 로그인 페이지 이동
            }
            return;
        }

        const response = await api.post('/auth/token/refresh', {}, {
            headers: {
                "Refresh-Token": refreshToken
            }
        });

        if (response.status === 401) {
            console.warn("🚨 Refresh Token 만료됨. 로그아웃 필요");
            if (window.location.pathname !== "/login") {
                alert("세션이 만료되었습니다. 다시 로그인해주세요.");
                window.location.href = "/login"; // ✅ 로그아웃이 아닌 로그인 페이지 이동
            }
            return;
        }

        const newAccessToken = response.data.body.accessToken;
        if (newAccessToken) {
            sessionStorage.setItem('accessToken', newAccessToken);
            console.log('✅ Access Token 갱신 완료');
        }
    } catch (error) {
        console.error('❌ Access Token 갱신 실패:', error);
    }
}



async function logout() {
    try {
        console.log("🔴 로그아웃 요청 시작");
        await api.post('/api/auth/logout', {});

        // ✅ 세션과 쿠키를 확실히 삭제
        sessionStorage.removeItem('accessToken');
        document.cookie = 'Refresh-Token=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;';


        console.log("🟢 로그아웃 성공");
        alert("로그아웃되었습니다.");
        window.location.href = "/login"; // ✅ 로그아웃 후 로그인 페이지로 이동
    } catch (error) {
        console.error('❌ 로그아웃 중 오류 발생:', error);
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

async function verifyUserRole() {
    try {
        const response = await api.post('/api/verify-token', {}, {
            headers: {
                "Authorization": `Bearer ${sessionStorage.getItem('accessToken')}`
            }
        });

        if (!response.body.roles.includes('ROLE_USER')) {
            alert('일반 회원만 이용 가능한 서비스입니다.');
            window.location.href = '/login';
            return false;
        }
        return true;
    } catch (error) {
        console.error('권한 확인 중 오류 발생:', error);
        alert('로그인이 필요한 서비스입니다.');
        window.location.href = '/login';
        return false;
    }
}
