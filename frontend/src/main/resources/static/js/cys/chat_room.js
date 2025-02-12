import {db} from '/js/cys/firebase-config.js';  // 절대 경로 적용
import {collection, doc, updateDoc, getDoc, addDoc, query, orderBy, onSnapshot, serverTimestamp, where, getDocs}
    from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";


// 현재 로그인한 사용자 가져오기
const currentUser = document.querySelector("#username").value;  // 서버에서 넘겨받은 세션 값
const currentUserNickname = document.querySelector("#nickname").value;  // 서버에서 넘겨받은 세션 값
const currentDogId = document.querySelector("#dogId").value;
// const currentDogName = document.querySelector("#dogName").value;
const currentDogProfile = document.querySelector("#dogProfile").value;
console.log("현재 사용자 강아지:", currentDogId);
const chatListElement = document.querySelector(".chat-list");

let chatProfiles=[];
const match = document.querySelector('#match');
const group = document.querySelector('#group');

const oneOnOneCategory = document.querySelector(".one-on-one");
const groupCategory = document.querySelector(".group");
const detailTitle = document.querySelector('.detail-title');

const chatRooms = [];
let otherParticipants = [];

async function subscribeToNotifications() {
    if (!currentDogId) {
        console.error("사용자 정보가 없습니다. 로그인 후 이용해 주세요.");
        window.location.href = '/login';
        return;
    }

    try {
        // Firestore에서 'participants' 배열에 사용자가 포함된 채팅방 가져오기
        const chatRoomsRef = collection(db, 'chatRooms');
        const q = query(
            chatRoomsRef,
            where('participants', 'array-contains', currentDogId),
            orderBy('lastMessage.timestamp', 'desc')  // 인덱스 필요
        );

        let status;
        // Firestore 쿼리 실행
        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            chatListElement.innerHTML = "<li>참여 중인 채팅방이 없습니다.</li>";
            return;
        }

        onSnapshot(q, (querySnapshot) => {
            chatRooms.length = 0;  // 기존 데이터 초기화
            querySnapshot.forEach((doc) => {
                chatRooms.push({id: doc.id, ...doc.data()})

                const filteredParticipants
                    = doc.data().participants.filter(user => user !== currentDogId);

                filteredParticipants.forEach(participants => {
                    if (!otherParticipants.includes(participants)) {
                        otherParticipants.push(participants);
                    }
                })
            });

            console.log('참여자 목록 : ' + otherParticipants);

            api.post('/api/chat/profile', {otherParticipants : otherParticipants},
                {
                    headers: {'Content-Type': 'application/json'}
                })
                .then(response => {
                    chatProfiles = response.body;
                    console.log("응답 데이터jjjjj:", chatProfiles);

                    chatCategory(currentFilterStatus);
                })
                .catch(error => console.error(error));
        });

    } catch (error) {
        console.error("채팅방 목록 가져오기 실패:", error);
    }
}

function getProfileById(participantsId){
    // 프로필 데이터에서 해당 ID의 프로필 찾기
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantsId));

    return profile ? profile.profile_url : '/img/groupchat.png';
}

let currentFilterStatus = 'F'

function chatCategory(filterStatus) {
    currentFilterStatus = filterStatus;
    chatListElement.innerHTML = "";

    chatRooms.forEach((chatRoom) => {
        if (!filterStatus || chatRoom.status === filterStatus) {

            // 참여자 ID 중 첫 번째 참가자 ID를 기준으로 프로필 가져오기
            const otherParticipants = chatRoom.participants.filter(id => id !== currentDogId);
            let participantId;
            participantId = otherParticipants;
            if (otherParticipants.length === 1) {
                participantId = otherParticipants;  // 참여자가 1명일 경우 해당 ID 사용
            } else {
                participantId = '/img/groupchat.png';  // 여러 명일 경우 기본값 설정
            }

            const profileUrl = getProfileById(participantId);

            const listItem = document.createElement("div");

            listItem.innerHTML = `
                    <div class="chat-one" onclick="room('${chatRoom.id}')">
                        <div class="">
                            <img src="${profileUrl}" width="50" class="profile-img-2" alt="프로필">
                        </div>
                        <div class="pre-chat-content">
                            <div class="pre-chat-with">${chatRoom.roomName}</div>
                            <div class="last-chat">${chatRoom['lastMessage'].text}</div>
                        </div>
                    </div>  
                `;
            chatListElement.appendChild(listItem);
            if (filterStatus === 'F') {
                oneOnOneCategory.style.borderBottom = "solid 5px #FE904B";
            } else if (filterStatus === 'G') {
                groupCategory.style.borderBottom = "solid 5px #FE904B";
            }
        }
    });
}

match.addEventListener('click', () => {
    groupCategory.style.borderBottom = 'none';
    oneOnOneCategory.style.borderBottom = "solid 5px #FE904B";
    chatCategory('M');
    chatCategory('F');
});

group.addEventListener('click', () => {
    chatCategory('G');
    oneOnOneCategory.style.borderBottom = 'none';
    groupCategory.style.borderBottom = "solid 5px #FE904B";
});

let selectedRoomId;

//리스트에서 채팅방가기
window.room = function (roomId) {
    selectedRoomId = roomId;
    console.log(selectedRoomId);
    subscribeToMessages(roomId);
}

// 실시간 메시지 구독
function subscribeToMessages(roomId) {
    const q = query(collection(db, "chatRooms", roomId, "messages"), orderBy("timestamp"));
    onSnapshot(q, (snapshot) => {
        console.log(snapshot);
        console.log("메시지 개수:", snapshot.size);

        const chatMain = document.querySelector('.chat-main'); // 채팅 메시지 컨테이너
        chatMain.innerHTML = ''; // 기존 메시지 초기화

        snapshot.forEach((doc) => {
            // console.log("문서 ID:", doc.id, "데이터:", doc.data());
            const message = doc.data();
            const messageDiv = document.createElement('div');

// 메시지 구조 생성
            if (message.sender === currentDogId) {
                // 본인 메시지일 경우 오른쪽 정렬
                messageDiv.className = "chat-message right";
                messageDiv.innerHTML = `
                        <div class="message-content">
                            <div class="message-time">${formatTime(message.timestamp)}</div>
                            <div class="message-text">${message.text}</div>
                        </div>
                    `;
            } else {
                const profileUrl = getProfileById(message.sender);
                // 상대방 메시지일 경우 왼쪽 정렬
                messageDiv.className = 'chat-message left';
                messageDiv.innerHTML = `
                        <img src="${profileUrl}" width="40" class="profile-img-2" alt="프로필">
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

// 함수 호출 (페이지 로드 시 실행)
window.onload = function () {
    subscribeToNotifications();
}


// 메시지 보내기
async function sendMessage(selectedRoomId) {

    const messageInput = document.querySelector('.chat-input').value;

    if (messageInput.trim() === '') return;

    try {
        const NewMessage = {
            sender: currentDogId,
            nickname: currentUserNickname,
            text: messageInput,
            timestamp: serverTimestamp()
        };

        await addDoc(collection(db, "chatRooms", selectedRoomId, "messages"), NewMessage);

        //lastMessage Update
        await updateDoc(doc(db, "chatRooms", selectedRoomId), {lastMessage: NewMessage})

        document.querySelector('.chat-input').value = '';

    } catch (error) {
        console.log('메시지 전송 실패:', error);
    }
}

window.goChat = function (e) {
    sendMessage(selectedRoomId);
}


function formatTime(timestamp) {
    if (typeof timestamp === 'string') {
        // 문자열인 경우 Date 객체로 변환
        const date = new Date(timestamp);
        return date.toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});
    } else if (timestamp && timestamp.seconds) {
        // Firestore Timestamp 객체인 경우
        const date = new Date(timestamp.seconds * 1000);
        return date.toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});
    } else if (timestamp instanceof Date) {
        // 만약 이미 Date 객체로 넘어오는 경우
        return timestamp.toLocaleTimeString('ko-KR', {hour: '2-digit', minute: '2-digit'});
    }

    console.error("유효하지 않은 timestamp:", timestamp);
    return "알 수 없음";
}
