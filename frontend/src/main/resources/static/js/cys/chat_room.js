import { db } from '/js/cys/firebase-config.js';  // 절대 경로 적용
import { collection, addDoc, query, orderBy, onSnapshot, serverTimestamp, where, getDocs }
    from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";


// 현재 로그인한 사용자 가져오기
const currentUser = document.querySelector("#username").value;  // 서버에서 넘겨받은 세션 값
console.log("현재 사용자:", currentUser);
// const currentUser = sessionStorage.getItem("username");
const chatListElement = document.querySelector(".chat-list");

async function subscribeToNotifications() {
    if (!currentUser) {
        console.error("사용자 정보가 없습니다. 로그인 후 이용해 주세요.");
        window.location.href = '/login';
        return;
    }

    try {
        // Firestore에서 'participants' 배열에 사용자가 포함된 채팅방 가져오기
        const chatRoomsRef = collection(db, 'chatRooms');
        const q = query(chatRoomsRef, where('participants', 'array-contains', currentUser));

        // Firestore 쿼리 실행
        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            chatListElement.innerHTML = "<li>참여 중인 채팅방이 없습니다.</li>";
        } else {
            chatListElement.innerHTML = "";  // 기존 목록 초기화

            querySnapshot.forEach((doc) => {
                const chatRoom = doc.data();
                const listItem = document.createElement("div");

                listItem.innerHTML = `
                    <div class="chat-one" onclick="go-room()">
                        <div class="">
                            <img src="/img/견BTI임시프로필.jpg" width="50" class="profile-img-2" alt="프로필">
                        </div>
                        <div class="pre-chat-content">
                            <div class="pre-chat-with">${chatRoom.roomName}</div>
                            <div class="last-chat">${chatRoom['lastMessage'].text}</div>
                        </div>
                    </div>
                `;

                chatListElement.appendChild(listItem);
            });
        }
    } catch (error) {
        console.error("채팅방 목록 가져오기 실패:", error);
    }
}

// 함수 호출 (페이지 로드 시 실행)
subscribeToNotifications();


const roomId = "[[${roomId}]]";
// const username = sessionStorage.getItem('username');

// 메시지 보내기
async function sendMessage() {

    const messageInput = document.getElementById('chat-input').value;

    if (messageInput.trim() === '') return;

    try {
        await addDoc(collection(db, "chatRooms", roomId, "messages"), {
            sender: username,
            text: messageInput,
            timestamp: serverTimestamp()
        });

        document.getElementById('messageInput').value = '';

    } catch (error) {
        console.log('메시지 전송 실패:', error);
    }
}

// 실시간 메시지 구독
function subscribeToMessages() {
    const q = query(collection(db, "chatRooms", roomId, "messages"), orderBy("timestamp"));

    onSnapshot(q, (snapshot) => {
        const chatMain = document.querySelector('.chat-main'); // 채팅 메시지 컨테이너
        chatMain.innerHTML = ''; // 기존 메시지 초기화

        snapshot.forEach((doc) => {
            const message = doc.data();
            const messageDiv = document.createElement('div');

// 메시지 구조 생성
            if (message.sender === username) {
                // 본인 메시지일 경우 오른쪽 정렬
                messageDiv.className = 'chat-message right';
                messageDiv.innerHTML = `
                    <div class="message-content">
                        <div class="message-time">${formatTime(message.timestamp)}</div>
                        <div class="message-text">${message.text}</div>
                    </div>
                `;
            } else {
                // 상대방 메시지일 경우 왼쪽 정렬
                messageDiv.className = 'chat-message left';
                messageDiv.innerHTML = `
                    <img src="/img/견BTI임시프로필.jpg" width="40" class="profile-img-2" alt="프로필">
                    <div class="chat-not-profile">
                        <div class="chat-name">${message.nickname}</div>
                        <div class="message-content">
                            <div class="message-text">${message.text}</div>
                            <div class="message-time">${formatTime(message.timestamp)}</div>
                        </div>
                    </div>
                `;
            }

            chatMain.appendChild(messageDiv);
        });

        chatMain.scrollTop = chatMain.scrollHeight; // 자동 스크롤 다운
    });
}

window.onload = function () {
    subscribeToMessages();
}

window.handleClick = function() {
    sendMessage();
}

function formatTime(timestamp) {
    const date = new Date(timestamp.seconds * 1000);
    return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
}
