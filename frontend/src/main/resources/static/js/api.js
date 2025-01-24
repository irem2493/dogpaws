/**
 * API 사용법
 *
 * 1. GET 요청 (파라미터 없음)
 *    api.get('/endpoint')
 *       .then(data => console.log(data))
 *       .catch(error => console.error(error));
 *
 * 2. GET 요청 (파라미터 포함)
 *    api.get('/endpoint', { key: 'value' })
 *       .then(data => console.log(data))
 *       .catch(error => console.error(error));
 *
 * 3. POST 요청
 *    api.post('/endpoint', { key: 'value' })
 *       .then(data => console.log(data))
 *       .catch(error => console.error(error));
 *
 * 4. PUT 요청
 *    api.put('/endpoint', { key: 'updatedValue' })
 *       .then(data => console.log(data))
 *       .catch(error => console.error(error));
 *
 * 5. DELETE 요청
 *    api.delete('/endpoint')
 *       .then(data => console.log(data))
 *       .catch(error => console.error(error));
 *
 * 6. 헤더 추가 예시
 *    api.get('/endpoint', {}, { Authorization: 'Bearer token' })
 *       .then(data => console.log(data))
 *       .catch(error => console.error(error));
 *
 *  TODO: 모든 기능 테스트 해보지 않아서 추후 변경될 여지 있음.(현재는 get 방식만 테스트 해봄 )
 */
const api = {
    client: axios.create({
        baseURL: 'http://localhost:8080',
        timeout: 5000,
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
            withCredentials: true,
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

api.client.interceptors.request.use(
    config => {
        console.log('요청 데이터:', config);
        return config;
    },
    error => {
        console.error('요청 에러:', error);
        return Promise.reject(error);
    }
);

api.client.interceptors.response.use(
    response => {
        console.log('응답 데이터:', response);
        return response.data;
    },
    error => {
        console.error('응답 에러:', error.response || error);
        return Promise.reject(error);
    }
);

window.myapi = api;