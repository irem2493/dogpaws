import {db} from '/js/cys/firebase-config.js';  // 절대 경로 적용
import {collection, doc, updateDoc, getDoc, addDoc, query, orderBy, onSnapshot, serverTimestamp, where, getDocs}
    from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";


// 현재 로그인한 사용자 가져오기
const currentUser = document.querySelector("#username").value;  // 서버에서 넘겨받은 세션 값
const currentUserNickname = document.querySelector("#nickname").value;  // 서버에서 넘겨받은 세션 값
const currentDogId = document.querySelector("#dogId").value;
const currentDogName = document.querySelector("#dogName").value;
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

function getNicknameById(participantsId){
    // 프로필 데이터에서 해당 ID의 프로필 찾기
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantsId));
    return profile.nickname;
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



            const listItem = document.createElement("div");

            if (filterStatus === 'F' || filterStatus === 'M'){
            const profileUrl = getProfileById(participantId);
            const otherNickname = getNicknameById(participantId);
                listItem.innerHTML = `
                    <div class="chat-one" onclick="room('${chatRoom.id}')">
                        <div class="">
                            <img src="${profileUrl}" width="50" height="50" class="profile-img-2" alt="프로필">
                        </div>
                        <div class="pre-chat-content">
                            <div class="pre-chat-with">${otherNickname}</div>
                            <div class="last-chat">${chatRoom['lastMessage'].text}</div>
                        </div>
                    </div>  
                `;
            }else if(filterStatus === 'G'){
                listItem.innerHTML = `
                    <div class="chat-one" onclick="room('${chatRoom.id}')">
                        <div class="">
                            <img src="${chatRoom.roomImage}" width="50" class="profile-img-2" alt="프로필">
                        </div>
                        <div class="pre-chat-content">
                            <div class="pre-chat-with">${chatRoom.roomName}</div>
                            <div class="last-chat">${chatRoom['lastMessage'].text}</div>
                        </div>
                    </div>  
                `;
            }


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

            //일정 공유 채팅
            if(message.status === 'S'){
                if (message.sender === currentDogId) {
                    messageDiv.className = "chat-message right";
                    messageDiv.innerHTML = `
                        <p class="message-time">${formatTime(message.timestamp)}</p>
                        <div class="schedule-message">
                            <p class="schedule-title">${message.text}</p>
                            <div class="schedule-content">
                                <div class="schedule-icon">
                                    <img src="/img/icon/CalendarCheck.svg" alt="일정 아이콘">
                                </div>
                                <div class="schedule-info">
                                    <p class="schedule-name">${message.calendarTitle}</p>
                                    <p class="schedule-time">시간 <span>${message.calendarStartDate} ~ ${message.calendarEndDate}</span></p>
                                </div>
                            </div>
                            <button class="schedule-btn">일정 보기</button>
                        </div>
                    `;
                } else {
                    const profileUrl = getProfileById(message.sender);
                    messageDiv.className = "chat-message left";
                    messageDiv.innerHTML = `
                        <img src="${profileUrl}" width="40" height="40" class="profile-img-2" alt="프로필">
                        <div class="chat-not-profile">
                            <div class="chat-name">${message.nickname}</div>
                            <div class="schedule-message">
                                <p class="schedule-title">일정이 공유되었어요.</p>
                                <div class="schedule-content">
                                    <div class="schedule-icon">
                                        <img src="/img/icon/CalendarCheck.svg" alt="일정 아이콘">
                                    </div>
                                    <div class="schedule-info">
                                        <p class="schedule-name">${message.calendarTitle}</p>
                                        <p class="schedule-time">시간 <span>${message.calendarStartDate} ~ ${message.calendarEndDate}</span></p>
                                    </div>
                                </div>
                                <button class="schedule-btn">일정 보기</button>
                            </div>
                        </div>
                        <p class="message-time">${formatTime(message.timestamp)}</p>
                    `;
                }

            }else if(message.status === 'C' || message.status !== 'S'){
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
                        <img src="${profileUrl}" width="40" height="40" class="profile-img-2" alt="프로필">
                        <div class="chat-not-profile">
                            <div class="chat-name">${message.nickname}</div>
                            <div class="message-content">
                                <div class="message-text">${message.text}</div>
                                <div class="message-time">${formatTime(message.timestamp)}</div>
                            </div>
                        </div>
                    `;
                }
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

/////////////////////////////// 일정 /////////////////////////////\

const schedulesBtn = document.querySelector("#add-calender-icon");
const scheduleModal = document.querySelector(".modal-all");
const closeModal = document.querySelector('.close-btn');

schedulesBtn.addEventListener('click', function (event){
    event.stopPropagation(); // 클릭 이벤트 전파 방지!
    scheduleModal.style.display = "flex";
});

closeModal.addEventListener('click', function (event){
    event.stopPropagation(); // 클릭 이벤트 전파 방지!
    scheduleModal.style.display = 'none';
});

// 모달 내부 클릭 시 이벤트 전파 방지
scheduleModal.addEventListener("click", (event) => {
    event.stopPropagation();
});


document.addEventListener("click", (event) => {
    if (scheduleModal.style.display === "flex") {
        scheduleModal.style.display = "none";
    }
});

//주소 검색
window.sample5_execDaumPostcode = function () {
    new daum.Postcode({
        oncomplete: function (data) {
            // 최종 주소 (도로명 주소 또는 지번 주소)
            let addr = data.roadAddress ? data.roadAddress : data.jibunAddress;

            // 주소 정보를 input 태그에 넣기
            document.getElementById("address").value = addr;
        }
    }).open();
};

//일정 등록
window.goSchedule = function (){

    const calendarType = document.querySelector('#calendarType').value;
    const calendarTitle = document.querySelector('#calendarTitle').value;
    const calendarStartDate = document.querySelector('#calendarStartDate').value;
    const calendarEndDate = document.querySelector('#calendarEndDate').value;
    const address = document.querySelector('#address').value;
    const calendarDescription = document.querySelector('#calendarDescription').value;

    let formData = new FormData();

    formData.append("username",currentUser);
    formData.append("dogId",currentDogId);
    formData.append("dogName",currentDogName);
    formData.append("calendarTitle",calendarTitle);
    formData.append("calendarType",calendarType);
    formData.append("address",address);
    formData.append("calendarStartDate",calendarStartDate);
    formData.append("calendarEndDate",calendarEndDate);
    formData.append("calendarDescription",calendarDescription);

    api.post('/api/chat/schedule', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
        .then(async response => {
            console.log("응답 데이터jjjjj:", response);

            const NewSchedule = {
                sender: currentDogId,
                nickname: currentUserNickname,
                text: "일정이 공유되었습니다.",
                calendarTitle: calendarTitle,
                calendarStartDate: calendarStartDate,
                calendarEndDate: calendarEndDate,
                timestamp: serverTimestamp(),
                status : 'S'
            };

            await addDoc(collection(db, "chatRooms", selectedRoomId, "messages"), NewSchedule);

            //lastMessage Update
            await updateDoc(doc(db, "chatRooms", selectedRoomId), {lastMessage: NewSchedule})

            scheduleModal.style.display = "none";

        })
        .catch(error => console.error(error));
}
