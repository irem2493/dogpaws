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

                // 토스트 알림 표시
                this.showToast(payload.notification.title, payload.notification.body);

                // 알림 목록에 추가
                this.addNewNotification({
                    message: payload.notification.body,
                    alarmId: payload.data?.alarmId,
                    createdAt: new Date()
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
    },
    // FCMClient 객체 내부에 추가
    showToast(title, message) {
        const toast = document.createElement('div');
        toast.className = 'pawsModal';
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: -320px;
            width: 300px;
            height: 150px;
            z-index: 9999;
            display: block;
            transition: right 0.5s ease;
            background: rgba(255, 252, 250, 0.95);
            backdrop-filter: blur(5px);
            box-shadow: 0 4px 12px rgba(255, 236, 179, 0.3);
        `;
        
        // 알림 타입에 따른 아이콘 결정 (A: 주문/배송, C: 캘린더)
        const alarmType = title.includes('주문') || title.includes('배송') ? 'a' : 'c';
        
        // 메시지 처리
        let displayTitle = title;  // 서버에서 받은 title 그대로 사용
        let displayContent = '';
        
        if (alarmType === 'c') {
            displayContent = message.split(': ')[1];
        } else {
            // 배송 알림 메시지 처리
            if (message.includes('배송이 시작되었습니다')) {
                const match = message.match(/\(운송장번호: (.*?)\)/);
                displayContent = match ? `운송장번호: ${match[1]}` : message;
            } else {
                displayContent = message;
            }
        }
        
        toast.innerHTML = `
            <!-- 닫기 버튼 -->
            <div class="pawsModal-content-center" style="height: 100%; justify-content: left;">
                <div style="display: flex; gap: 15px; align-items: flex-start;">
                    <img src="/img/icon/alarm_${alarmType}_icon.svg" 
                         alt="알림 아이콘" 
                         style="width: 50px; height: 50px;">
                    <div style="flex: 1;">
                        <p style="font-weight: bold; margin: 0 0 8px 0; font-size: 16px;">
                            ${displayTitle}
                        </p>
                        <p style="margin: 0; color: #666; font-size: 14px;">
                            ${displayContent}
                        </p>
                    </div>
                </div>
            </div>
        `;

        document.body.appendChild(toast);
        
        setTimeout(() => {
            toast.style.right = '20px';
        }, 100);

        setTimeout(() => {
            if (document.body.contains(toast)) {
                toast.style.right = '-320px';
                setTimeout(() => {
                    toast.remove();
                }, 500);
            }
        }, 3000);
    },
    addNewNotification(notification) {
        const container = document.getElementById('notifications');
        if (!container) return;

        const notificationElement = document.createElement('div');
        notificationElement.className = 'notification-item unread';
        notificationElement.setAttribute('data-id', notification.alarmId);
        
        // 알림 타입에 따른 아이콘 결정 (A: 주문/배송, C: 캘린더)
        const alarmType = notification.alarmId.toString().startsWith('2') ? 'A' : 'C';
        
        notificationElement.innerHTML = `
            <div class="notification-content" style="display: flex; gap: 10px; align-items: flex-start;">
                <img src="/img/icon/alarm_${alarmType.toLowerCase()}_icon.svg" 
                     alt="알림 아이콘" 
                     style="width: 20px; height: 20px; margin-top: 3px;">
                <div>
                    <p>${notification.message}</p>
                    <small>${getRelativeTimeString(notification.createdAt)}</small>
                </div>
            </div>
        `;

        // 목록 최상단에 추가
        container.insertBefore(notificationElement, container.firstChild);

        // 안읽은 알림 수 업데이트
        const unreadCount = document.getElementById('unreadCount');
        if (unreadCount) {
            const currentCount = parseInt(unreadCount.textContent || '0');
            unreadCount.textContent = currentCount + 1;
        }
    }
};

// 상대 시간 변환 함수 수정
function getRelativeTimeString(dateString) {
    // 현재 시간을 KST로 가져오기
    const now = new Date();
    const koreaTimeDiff = 9 * 60 * 60 * 1000; // 9시간을 밀리초로 변환
    const nowKST = new Date(now.getTime() + koreaTimeDiff);
    
    // 날짜가 Date 객체인 경우 처리
    const date = dateString instanceof Date ? dateString : new Date(dateString);

    const diffInMilliseconds = date - nowKST;
    const diffInMinutes = Math.floor(Math.abs(diffInMilliseconds) / (1000 * 60));
    const diffInHours = Math.floor(diffInMinutes / 60);
    const diffInDays = Math.floor(diffInHours / 24);

    if (diffInMinutes < 1) {
        return '방금 전';
    } else if (diffInMinutes < 60) {
        return `${diffInMinutes}분 전`;
    } else if (diffInHours < 24) {
        return `${diffInHours}시간 전`;
    } else if (diffInDays < 7) {
        return `${diffInDays}일 전`;
    } else {
        return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
    }
}

window.FCMClient = FCMClient;