
const adminApi = {
    client: axios.create({
        baseURL: 'http://192.168.0.242:8080/api/admin',
        timeout: 5000,
        withCredentials : true,
        /*    headers: {
                'Content-Type': 'application/json',
            },*/
    }),

    get(url, params = {}, headers = {}) {
        return this.client.get(url, {
            params,
            headers,
        });
    },

    post(url, data = {}, headers = {}) {
        return this.client.post(url, data, {
            headers,
        });
    },

    put(url, data = {}, headers = {}) {
        return this.client.put(url, data, {
            headers,
        });
    },

    delete(url, headers = {}) {
        return this.client.delete(url, {
            headers,
        });
    },
};


// 요청 인터셉터
adminApi.client.interceptors.request.use(
    config => {
        const accessToken = sessionStorage.getItem('accessToken');
        if (accessToken) {
            const tokenPayload = JSON.parse(atob(accessToken.split('.')[1]));
            const expirationTime = tokenPayload.exp * 1000;
            const currentTime = Date.now();
            const timeRemaining = expirationTime - currentTime;

            console.log('토큰 만료 시간:', new Date(expirationTime).toLocaleString());
            console.log('만료까지 남은 시간:', Math.floor(timeRemaining / 1000), '초');

            const isTokenExpired = expirationTime < currentTime;
            if (isTokenExpired) {
                // 토큰이 만료된 경우, 갱신 로직을 추가
                return refreshAccessToken().then(newAccessToken => {
                    config.headers.Authorization = `Bearer ${newAccessToken}`;
                    return config;
                });
            }
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터
adminApi.client.interceptors.response.use(
    response => response,
    async error => {
        const originalRequest = error.config;
        if (error.response.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const newAccessToken = await refreshAccessToken();
                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                // HTTP 메서드에 따라 요청을 다시 시도
                return adminApi.request(originalRequest);
            } catch (refreshError) {
                console.error('토큰 갱신 실패:', refreshError);
                sessionStorage.removeItem('accessToken');
                sessionStorage.removeItem('refreshToken');
                window.location.href = '/admin/login';
            }
        }
        return Promise.reject(error);
    }
);

function refreshAccessToken() {
    const accessToken = sessionStorage.getItem('accessToken');
    return axios.post('http://192.168.0.242:8080/api/admin/auth/token/verify', null, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        },
        withCredentials: true // 쿠키를 포함하여 요청
    }).then(response => {
        const newAccessToken = response.headers['authorization'].split(' ')[1];
        sessionStorage.setItem('accessToken', newAccessToken);
        return newAccessToken;
    }).catch(error => {
        console.error('refreshAccessToken 토큰 갱신 실패:', error);
        throw error;
    });
}


window.adminApi = adminApi; // 전역 객체로 설정