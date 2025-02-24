import {db} from '/js/cys/firebase-config.js';
import {
    collection,
    addDoc,
    query,
    orderBy,
    onSnapshot,
    serverTimestamp,
    where,
    getDocsf
} from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";

// 전역 변수
let currentInquiryId = null;
const currentUser = document.getElementById('username')?.value;

// 전역 함수로 선언
window.toggleChatBot = function() {
    const chatWindow = document.querySelector('.chat-bot-window');
    const chatIcon = document.querySelector('.chat-bot-icon');
    
    if (chatWindow.style.display === 'none') {
        chatWindow.style.display = 'flex';
        chatIcon.style.display = 'none';
    } else {
        chatWindow.style.display = 'none';
        chatIcon.style.display = 'flex';
    }
};

// 기존 문의 확인 또는 새 문의 생성
async function loadOrCreateInquiry() {
    try {
        // 사용자의 진행중인 문의 확인
        const q = query(
            collection(db, "inquiries"),
            where("userId", "==", currentUser),
            where("status", "==", "PENDING"),
            orderBy("timestamp", "desc"),
            limit(1)
        );

        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            // 진행중인 문의가 없으면 새로 생성
            await createNewInquiry();
        } else {
            // 기존 문의 로드
            currentInquiryId = querySnapshot.docs[0].id;
            subscribeToMessages();
        }
    } catch (error) {
        console.error("문의 로드 실패:", error);
    }
}

// 새 문의 생성
async function createNewInquiry() {
    try {
        const docRef = await addDoc(collection(db, "inquiries"), {
            userId: currentUser,
            status: "PENDING",
            timestamp: serverTimestamp(),
            lastMessage: {
                text: "무엇을 도와드릴까요?",
                isAdmin: true,
                timestamp: serverTimestamp()
            }
        });

        currentInquiryId = docRef.id;

        // 시스템 첫 메시지
        await addDoc(collection(db, "inquiries", currentInquiryId, "messages"), {
            text: "무엇을 도와드릴까요?",
            isAdmin: true,
            isSystem: true,
            timestamp: serverTimestamp()
        });

        subscribeToMessages();
    } catch (error) {
        console.error("새 문의 생성 실패:", error);
    }
}

// 메시지 전송
async function sendMessage() {
    const inputElement = document.querySelector('.chat-bot-input input');
    const message = inputElement.value.trim();

    if (!message || !currentInquiryId) return;

    try {
        // 메시지 추가
        await addDoc(collection(db, "inquiries", currentInquiryId, "messages"), {
            text: message,
            isAdmin: false,
            timestamp: serverTimestamp()
        });

        // lastMessage 업데이트
        await updateDoc(doc(db, "inquiries", currentInquiryId), {
            lastMessage: {
                text: message,
                isAdmin: false,
                timestamp: serverTimestamp()
            }
        });

        inputElement.value = '';
    } catch (error) {
        console.error("메시지 전송 실패:", error);
    }
}

// 메시지 구독
function subscribeToMessages() {
    if (!currentInquiryId) return;

    const q = query(
        collection(db, "inquiries", currentInquiryId, "messages"),
        orderBy("timestamp", "asc")
    );

    onSnapshot(q, (snapshot) => {
        snapshot.docChanges().forEach((change) => {
            if (change.type === "added") {
                displayMessage(change.doc.data());
            }
        });
    });
}

// 메시지 화면에 표시
function displayMessage(message) {
    const messagesContainer = document.querySelector('.chat-bot-messages');
    const messageElement = document.createElement('div');
    messageElement.className = `message ${message.isAdmin ? 'admin' : 'user'}`;

    messageElement.innerHTML = `
        <div class="message-content">
            ${message.text}
        </div>
        <div class="message-time">
            ${formatTimestamp(message.timestamp)}
        </div>
    `;

    messagesContainer.appendChild(messageElement);
    messagesContainer.scrollTop = messagesContainer.scrollHeight;
}

// 타임스탬프 포맷
function formatTimestamp(timestamp) {
    if (!timestamp) return '';
    const date = timestamp.toDate();
    return date.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 이벤트 리스너 설정
document.addEventListener('DOMContentLoaded', () => {
    const sendButton = document.querySelector('.chat-bot-input button');
    const inputElement = document.querySelector('.chat-bot-input input');

    sendButton.addEventListener('click', sendMessage);
    inputElement.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            sendMessage();
        }
    });
});