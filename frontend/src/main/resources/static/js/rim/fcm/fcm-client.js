const FCMClient = {
    messaging: null,
    isInitialized: false,

    init() {
        if (this.isInitialized) return;

        try {
            if (!window.firebase) {
                console.error('Firebase SDK가 로드되지 않았습니다.');
                return;
            }

            // 이미 초기화되어 있는지 확인
            if (!firebase.apps.length) {
                firebase.initializeApp({
                    apiKey: "AIzaSyBNqsnBfUpdLzD5O62ixY8Yyk6RUdUNFVo",
                    authDomain: "paws2-6ce51.firebaseapp.com",
                    projectId: "paws2-6ce51",
                    storageBucket: "paws2-6ce51.firebasestorage.app",
                    messagingSenderId: "978825288307",
                    appId: "1:978825288307:web:f04f5a168e8aa4aba0a806"
                });
            }


            this.messaging = firebase.messaging();

            // 포그라운드 메시지 핸들러 등록
            this.messaging.onMessage((payload) => {
                console.log('포그라운드 메시지 수신:', payload);
                new Notification(payload.notification.title, {
                    body: payload.notification.body,
                    icon: '/images/logo.png'
                });
            });

            this.isInitialized = true;
            console.log('FCM 초기화 완료');
        } catch (error) {
            console.error('FCM 초기화 실패:', error);
        }
    },

    async requestNotificationPermission() {
        if (!this.isInitialized) {
            this.init();
        }

        try {
            const permission = await Notification.requestPermission();
            console.log('알림 권한 상태:', permission);

            if (permission === 'granted') {
                // 서비스 워커 등록 및 활성화 대기
                const registration = await navigator.serviceWorker
                    .register('/firebase-messaging-sw.js');
                console.log('Service Worker 등록 성공:', registration);

                // Service Worker가 활성화될 때까지 대기
                await new Promise((resolve) => {
                    if (registration.active) {
                        resolve();
                    } else {
                        registration.addEventListener('activate', () => resolve());
                    }
                });
                console.log('Service Worker 활성화 완료');

                // FCM 토큰 발급
                const token = await this.messaging.getToken({
                    vapidKey: "BFFqpT7nFXptOVTrwt9gf59JFiRHuHpZqZh0egf2TZDbcEsbCxyF8NwreJWA5-PUmVePUbVjGmmL5zMt3ciT3Co",
                    serviceWorkerRegistration: registration
                });

                console.log('FCM 토큰 발급됨:', token);
                await this.saveFCMToken(token);
                return token;
            }
        } catch (error) {
            console.error('알림 권한 요청 실패:', error);
            console.error('에러 상세:', error.stack);
        }
    },

    async saveFCMToken(token) {
        try {
            const accessToken = sessionStorage.getItem('accessToken');
            const username = sessionStorage.getItem('username');

            if (!accessToken) {
                console.error('액세스 토큰이 없습니다.');
                return;
            }

            console.log('저장할 FCM 토큰:', token);
            console.log('사용자:', username);
            console.log('액세스 토큰:', accessToken);

            await api.post('/api/fcm/token',
                { token, username },
                {
                    headers: {  // headers 객체 안에 Authorization 포함
                        'Authorization': `Bearer ${accessToken}`,
                        'Content-Type': 'application/json'
                    }
                }
            );
            console.log('FCM 토큰 서버 저장 성공');
        } catch (error) {
            console.error('FCM 토큰 서버 저장 실패:', error);
            if (error.response) {
                console.error('서버 응답:', error.response.data);
            }
        }
    }
};

window.FCMClient = FCMClient;