import {db} from '/js/cys/firebase-config.js'; // 절대 경로 적용
import {
    addDoc,
    collection,
    doc,
    getDocs,
    getDoc,
    limit,
    onSnapshot,
    orderBy,
    query,
    serverTimestamp,
    updateDoc,
    where,
    arrayUnion,
    arrayRemove,
    deleteDoc
} from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";


// 현재 로그인한 사용자 가져오기
const currentUser = document.querySelector("#username").value;  // 서버에서 넘겨받은 세션 값
const currentUserNickname = document.querySelector("#nickname").value;  // 서버에서 넘겨받은 세션 값
const currentDogId = document.querySelector("#dogId").value;
const currentDogName = document.querySelector("#dogName").value;
const currentDogProfile = document.querySelector("#dogProfile").value;
console.log("현재 사용자 강아지:", currentDogId);
const chatListElement = document.querySelector(".chat-list");

// let chatProfiles=[];
let chatProfiles = JSON.parse(localStorage.getItem("chatProfiles")) || [];
const match = document.querySelector('#match');
const group = document.querySelector('#group');

const oneOnOneCategory = document.querySelector(".one-on-one");
const groupCategory = document.querySelector(".group");

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
                    = doc.data().participants;
            // .filter(user => user !== currentDogId)

                filteredParticipants.forEach(participants => {
                    if (!otherParticipants.includes(participants)) {
                        otherParticipants.push(participants);
                    }
                })
            });

            console.log('❌참여자 목록 : ' + otherParticipants);

            api.post('/api/chat/profile', {otherParticipants : otherParticipants},
                {
                    headers: {'Content-Type': 'application/json'}
                })
                .then(response => {
                    chatProfiles = response.body;
                    console.log("❌❌❌❌ chatProfiles:", chatProfiles);

                    localStorage.setItem("chatProfiles", JSON.stringify(chatProfiles));

                    chatCategory(currentFilterStatus);
                })
                .catch(error => console.error(error));
        });

    } catch (error) {
        console.error("채팅방 목록 가져오기 실패:", error);
    }
}

async function openFirstChatRoom() {

    const main = document.querySelector('.chat-main');
    const detail = document.querySelector('.detail-background');

    main.innerHTML ='';
    document.querySelector('.out-chat').style.display = 'none';
    document.querySelector('.media').style.display = 'none';
    document.querySelector('.calender').style.display = 'none';

    main.innerHTML = `
        <div class="defaultMainBox">
            <img src = "/img/chatDefault.png" id="mainDefault" width="314">
            <p id="mainDefaultText">채팅을 시작해 보세요!</p>
        </div>
    `;

}

function getUsernameById(participantsId){
    // 프로필 데이터에서 해당 ID의 보호자 id 찾기
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantsId));

    return profile.username;
}

function getProfileById(participantsId){
    // 프로필 데이터에서 해당 ID의 프로필 찾기
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantsId));

    return profile ? profile.profile_url : '/img/groupchat.png';
}

function getNicknameById(participantsId){
    // 프로필 데이터에서 해당 ID의 프로필 찾기
    console.log(chatProfiles);
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantsId));
    if (!profile) {
        console.log("❌ 닉네임을 찾을 수 없음!", participantsId);
        return "알 수 없음"; // 기본 닉네임 반환
    }
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
                            <img src="${chatRoom.roomProfile}" width="50" height="50" class="profile-img-2" alt="프로필">
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
    document.querySelector('.create-chat').style.display="none";
    chatCategory('M');
    chatCategory('F');
});

group.addEventListener('click', () => {
    chatCategory('G');
    oneOnOneCategory.style.borderBottom = 'none';
    groupCategory.style.borderBottom = "solid 5px #FE904B";
    document.querySelector('.create-chat').style.display="flex";
});

let selectedRoomId;

//리스트에서 채팅방가기
window.room = function (roomId) {
    selectedRoomId = roomId;
    console.log(selectedRoomId);
    subscribeToMessages(roomId);
    detailProfile();
    detailParticipants();
    detailPageMedia(roomId);
    detailPageCalendar();
    document.querySelector('.out-chat').style.display = 'flex';
    document.querySelector('.media').style.display = 'flex';
    document.querySelector('.calender').style.display = 'flex';

}

function subscribeToMessages(roomId) {

    const q = query(collection(db, "chatRooms", roomId, "messages"), orderBy("timestamp"));

    onSnapshot(q, (snapshot) => {
        console.log("메시지 개수:", snapshot.size);

        const chatMain = document.querySelector('.chat-main');

        chatMain.innerHTML = '';

        snapshot.forEach((doc) => {
            console.log("새 메시지:", doc.id, doc.data());

            const message = doc.data();
            const messageDiv = document.createElement('div');


            // ✅ 시스템 메시지 (예: 날짜 변경)
            if (message.status === 'system') {
                messageDiv.className = "system-notice";
                messageDiv.innerHTML = `${message.text}`;
                chatMain.appendChild(messageDiv);
                return;
            }

            if (message.status === 'S') {
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
                                    <p class="schedule-time">${formatDateTime(message.calendarStartDate)}</p>
                                    <p class="schedule-name">${message.calendarTitle}</p>
                                </div>
                            </div>
                            <button class="schedule-btn" data-schedule-id="${message.id}" onclick="scheduleDetail(this)">일정 보기</button>
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
                                        <p class="schedule-time">${formatDateTime(message.calendarStartDate)}</p>
                                        <p class="schedule-name">${message.calendarTitle}</p>
                                    </div>
                                </div>
                                <button class="schedule-btn" data-schedule-id="${message.id}" onclick="scheduleDetail(this)">일정 보기</button>
                            </div>
                        </div>
                        <p class="message-time">${formatTime(message.timestamp)}</p>
                    `;
                }
            }

            else if (message.status === 'M') {

                let imagesHtml = "";
                message.text.forEach(url => {
                    imagesHtml += `<img src="${url}" alt="" width="230" style="border-radius: 15px">`;
                });

                if (message.sender === currentDogId) {
                    messageDiv.className = "chat-message right";
                    messageDiv.innerHTML = `
                        <div class="message-content">
                            <div class="message-time">${formatTime(message.timestamp)}</div>
                            <div class="message-img">${imagesHtml}</div>
                        </div>
                    `;
                } else {
                    const profileUrl = getProfileById(message.sender);
                    messageDiv.className = "chat-message left";
                    messageDiv.innerHTML = `
                        <img src="${profileUrl}" width="40" height="40" class="profile-img-2" alt="프로필">
                        <div class="chat-not-profile">
                            <div class="chat-name">${message.nickname}</div>
                            <div class="message-content">
                                <div class="message-img">${imagesHtml}</div>
                                <div class="message-time">${formatTime(message.timestamp)}</div>
                            </div>
                        </div>
                    `;
                }
            }

            else if (message.status === 'C') {
                if (message.sender === currentDogId) {
                    messageDiv.className = "chat-message right";
                    messageDiv.innerHTML = `
                        <div class="message-content">
                            <div class="message-time">${formatTime(message.timestamp)}</div>
                            <div class="message-text">${message.text}</div>
                        </div>
                    `;
                } else {
                    const profileUrl = getProfileById(message.sender);
                    messageDiv.className = "chat-message left";
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

        setTimeout(() => {
            chatMain.scrollTop = chatMain.scrollHeight;
        }, 10);
    });
}

function scrollToBottom() {
    const chatMain = document.querySelector('.chat-main');
    chatMain.scrollTop = chatMain.scrollHeight;
}


// 함수 호출 (페이지 로드 시 실행)
window.onload = function () {
    openFirstChatRoom();
    subscribeToNotifications();
}

document.querySelector('.chat-input').addEventListener('keydown', function (e){
    if (e.key === 'Enter'){
        e.preventDefault();
        sendMessage(selectedRoomId);
    }
})

// 메시지 보내기
async function sendMessage(selectedRoomId) {

    const messageInput = document.querySelector('.chat-input').value;

    if (messageInput.trim() === '') return;

    try {
        const NewMessage = {
            sender: currentDogId,
            nickname: currentUserNickname,
            text: messageInput,
            timestamp: serverTimestamp(),
            status : 'C'
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

function formatDateTime(dateTimeStr) {
    if (!dateTimeStr) return ""; // null 또는 undefined 처리

    const date = new Date(dateTimeStr);
    if (isNaN(date.getTime())) return ""; // 유효하지 않은 날짜 처리

    const options = {
        month: "long", // '2월'로 출력 (short: '2월', long: '2월')
        day: "numeric",
        hour: "numeric",
        minute: "numeric",
        hour12: true, // 오전/오후 형식 사용
    };

    return date.toLocaleString("ko-KR", options);
}

/////////////////////////////// 일정 /////////////////////////////\

const schedulesBtn = document.querySelector("#add-calender-icon");
const scheduleModal = document.querySelector("#modalForm-all");
const closeModal = document.querySelector('.close-btn');
const detailModal = document.querySelector("#detailModal-all");

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
            console.log("응답 데이터 schedule:", response);
            const calendarId = response.body;

            const NewSchedule = {
                id: calendarId,
                sender: currentDogId,
                nickname: currentUserNickname,
                text: "일정이 공유되었습니다.",
                calendarTitle: calendarTitle,
                calendarStartDate: calendarStartDate,
                calendarEndDate: calendarEndDate,
                timestamp: serverTimestamp(),
                status: 'S'
            };

            await addDoc(collection(db, "chatRooms", selectedRoomId, "messages"), NewSchedule);

            //lastMessage Update
            await updateDoc(doc(db, "chatRooms", selectedRoomId), {lastMessage: NewSchedule})

            scheduleModal.style.display = "none";

        })
        .catch(error => console.error(error));
}

//일정 상세
    const sharedBtn = document.querySelector('#shared-btn');

window.scheduleDetail = function (button){
    const detailClose = document.querySelector("#close-btn");
    detailModal.style.display = "flex";


    detailClose.addEventListener('click', function (){
        detailModal.style.display = 'none';
    });

    const messageId = button.getAttribute("data-message-id");
    const calendarId = button.getAttribute("data-schedule-id");

    console.log("일정Id : "+ calendarId);

    sharedBtn.setAttribute("data-calendar-id", calendarId);
    // sharedBtn.setAttribute("data-message-id", messageId);

    api.get('/api/chat/schedule', { calendarId : calendarId })
        .then(async response => {
            const calendarDto = response.body;
            console.log("dto title : "+ calendarDto.calendar_title);
            console.log("dto username : "+ calendarDto.username);
            console.log("dto currentUser : "+ currentUser);

            if (calendarDto.username === currentUser){
                sharedBtn.style.display = 'none';
            }else{
                sharedBtn.style.display = 'flex';
            }
            
            if (calendarDto.calendar_type === 'W'){
                document.querySelector('#detailType').value = '산책';
            }else if(calendarDto.calendar_type === 'P'){
                document.querySelector('#detailType').value = '놀이';
            }else if(calendarDto.calendar_type === 'M'){
                document.querySelector('#detailType').value = '교배';
            }

            document.querySelector('#detailTitle').value = calendarDto.calendar_title;
            document.querySelector('#detailStartDate').value = calendarDto.calendar_start_date;
            document.querySelector('#detailEndDate').value = calendarDto.calendar_end_date;
            document.querySelector('#detailAddress').value = calendarDto.address;
            document.querySelector('#detailDescription').value = calendarDto.calendar_description;


        })
        .catch(error => console.error(error));
}

//공유 일정 추가
window.sharedCalendar = function (button){
    const roomId = selectedRoomId;
    localStorage.setItem("lastVisitedRoom", roomId);
    detailModal.style.display = 'none';

    const calendarId = button.getAttribute("data-calendar-id");
    // const messageId = button.getAttribute("data-message-id");
    console.log("캘린더 id / "+calendarId);
    // console.log("메시지 아이디 : "+ messageId);

    api.post('/api/chat/shared-schedule', {
        calendarId: calendarId,
        username: currentUser,
        roomId: selectedRoomId,
        dogId: currentDogId
    })
        .then(async data => {
            console.log(data);
            alert("일정에 추가되었습니다.")
            // window.location.reload();

        })
        .catch(error => console.error(error));

}

// 파일 업로드
document.querySelector('#file-icon')
    .addEventListener('click', function (){
    document.querySelector('#mediaUpload').click();
});

// 파일 선택 후 자동 업로드
document.querySelector('#mediaUpload')
    .addEventListener('change', function () {
        fileUpload(); // 파일 선택하면 자동 업로드 🚀
    });

window.fileUpload = function (){

    const fileInput = document.querySelector('#mediaUpload');
    const files = fileInput.files;
    const roomId = selectedRoomId;
    const dogId = currentDogId;

    const formData = new FormData();

    formData.append("roomId", roomId);
    formData.append("dogId", dogId);

    for (let i = 0 ; i <files.length ; i++){
        formData.append("files", files[i]);
    }

    try{
        api.post('/api/chat/fileUpload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
            .then(async response => {
                console.log("응답 데이터 fileUrl:", response);
                const fileUrl = response.body;

                const fileMessage = {
                    sender : currentDogId,
                    text : fileUrl,
                    nickname : currentUserNickname,
                    timestamp : serverTimestamp(),
                    status : 'M'
                }

                const fileLatestMessage = {
                    sender : currentDogId,
                    text : "이미지를 보냈습니다.",
                    nickname : currentUserNickname,
                    timestamp : serverTimestamp(),
                    status : 'M'
                }

                await addDoc(collection(db, "chatRooms", selectedRoomId, "messages"), fileMessage);
                await updateDoc(doc(db, "chatRooms", selectedRoomId), {lastMessage: fileLatestMessage})

            })
            .catch(error => console.error(error));


    }catch{
        console.error("파일 업로드 실패:", error);
    }

}

    
//이름 불러오기
window.detailProfile = async function () {

    const roomSnap = await getDoc(doc(db, 'chatRooms', selectedRoomId));
    const participants = roomSnap.data().participants;
    const filterStatus = roomSnap.data().status;
    const roomName = roomSnap.data().roomName;
    const roomProfile = roomSnap.data().roomProfile;
    console.log('상대 id//'+participants);

    const otherDogId = participants.find(id => id !== currentDogId);
    const profileUrl = getProfileById(otherDogId);
    const profile = document.querySelector('.profile');

    profile.innerHTML = '';
    if(filterStatus === 'G'){
        profile.innerHTML = `
        <img src="${roomProfile}" class="profile-img-3" alt="">
    `
    }else{
        profile.innerHTML = `
        <img src="${profileUrl}" class="profile-img-3" alt="">
    `
    }

    const detailUserName = document.querySelector('.detail-title');

    const starBtn = document.querySelector('.star-btn');
    const linkBtn = document.querySelector('.link-btn');

    detailUserName.innerText = '';
    if (filterStatus === 'G'){
        detailUserName.innerText = `${roomName}`;
        starBtn.style.display='none';
        linkBtn.style.display='flex';
    }else{
        const profileNickName = getNicknameById(otherDogId);
        detailUserName.innerText = `${profileNickName}`;
        starBtn.style.display='flex';
        linkBtn.style.display='none';
    }

    console.log("🟢 participants:", participants);
    console.log("🟢 status:", filterStatus);
    console.log("🟢 roomName:", roomName);
    console.log("🟢 roomProfile:", roomProfile);


}

//참여자 목록
window.detailParticipants = async function () {

    const roomSnap = await getDoc(doc(db, 'chatRooms', selectedRoomId));
    const participants = roomSnap.data().participants;
    const filterStatus = roomSnap.data().status;
    console.log('채팅방 참여자 상세 목록 : '+participants);

    const participantsDetail = document.querySelector('.participants');
    const participantsList = document.querySelector('.participants-list');
    let detailUrlList =[];
        for (const participantId of participants) {
            const profileUrl = await getProfileById(participantId);
            detailUrlList.push(profileUrl);
        }

    if(filterStatus === 'G'){
        participantsDetail.style.display = 'flex';
            participantsList.innerHTML = '';
// 🔥 프로필 이미지를 동적으로 추가
        detailUrlList.slice(0, 4).forEach(url => {
            const profileItem = document.createElement('div'); // 각 프로필을 감쌀 div
            profileItem.classList.add('profile-item'); // 스타일을 위한 클래스 추가

            profileItem.innerHTML = `
                <img src="${url}" class="detail-profile-img" alt="참여자 프로필">
            `;
            participantsList.appendChild(profileItem); // 리스트에 추가
        });
    }else{
        participantsDetail.style.display = 'none';
    }
    console.log("참여자 프로필 url 목록 : "+detailUrlList);

}

// +버튼 누를시 전체 참여자 리스트
window.openParticipantList = async function () {

    const modalOverlay = document.querySelector('.modalDetailParticipants');
    const modalContent = document.querySelector('.allParticipants-modal-list');
    const roomId = selectedRoomId;

    modalOverlay.style.display = 'flex';

    const roomSnap = await getDoc(doc(db, 'chatRooms', selectedRoomId));
    const participants = roomSnap.data().participants;
    console.log('채팅방 참여자 상세 목록 : ' + participants);

    let detailUrlList2 = [];
    for (const participantId of participants) {
        const profileUrl = await getProfileById(participantId);
        const nickname = await getNicknameById(participantId) || '알 수 없음';
        detailUrlList2.push({ id: participantId, url: profileUrl, nickname : nickname });
    }

    modalContent.innerHTML = '';
// 🔥 프로필 이미지를 동적으로 추가
    detailUrlList2.forEach(participant  => {
        const profileItem = document.createElement('div'); // 각 프로필을 감쌀 div
        profileItem.classList.add('profile-plus-item'); // 스타일을 위한 클래스 추가

        profileItem.innerHTML = `
            <img src="${participant.url}" class="detail-profile-img" alt="프로필 이미지">
            <p>${participant.nickname}</p>
        `;

        modalContent.appendChild(profileItem); // 리스트에 추가
    });

    modalOverlay.style.display = 'flex';

    setTimeout(() => {
        window.addEventListener('click', function closeModal(event) {
            if (modalOverlay.style.display === 'flex' && !modalContent.contains(event.target)) {
                modalOverlay.style.display = 'none';

                // 🔥 이벤트 리스너를 제거해서 불필요한 이벤트 감지 방지
                window.removeEventListener('click', closeModal);
            }
        });
    }, 100);
}

//미디어
window.detailPageMedia = function (roomId){

    const q = query(
        collection(db, "chatRooms", roomId, "messages"),
        where("status", "==", "M"),orderBy("timestamp", "desc"),limit(6)
    );
    console.log(q);

    onSnapshot(q, (snapshot) => {
        const mediaList = [];
        snapshot.forEach(doc => {
            let urls = doc.data().text;

                if (Array.isArray(urls)) {
                    mediaList.push(...urls); // ✅ 배열이면 그대로 추가
                } else if (typeof urls === "string") {
                    mediaList.push(urls); // ✅ 문자열이면 개별 추가
                }
            });

            console.log(mediaList);

            const maxImgs = 6;
            const finalMediaList = mediaList.slice(0, maxImgs);

            const mediaModalList = document.querySelector('.media-list');
            mediaModalList.innerHTML = '';

        finalMediaList.forEach(url => {
                const mediaItem = document.createElement('div');
                mediaItem.classList.add('media-item');

                mediaItem.innerHTML = `
            <img src="${url}" alt="미디어" class="media-img" >
        `;

                mediaModalList.appendChild(mediaItem);
            });

        })
}

// +버튼 누를시 전체 이미지 리스트
window.openMediaList = function (){

    const modalOverlay = document.querySelector('.modalMedia');
    const modalContent = document.querySelector('.allMedia-modal-list');
    const roomId = selectedRoomId;

    modalOverlay.style.display = 'flex';

    api.get('/api/chat/mediaListAll', {roomId : roomId })
        .then(data => {
            // console.log(data.body);

            const mediaUrlList = data.body;

            const mediaModalList = document.querySelector('.allMedia-modal-list');

            mediaModalList.innerHTML = '';

            mediaUrlList.forEach(url => {
                const mediaItem = document.createElement('div');
                mediaItem.classList.add('media-item');

                mediaItem.innerHTML = `
                <img src="${url}" alt="미디어" class="media-img"">
            `;

                mediaModalList.appendChild(mediaItem);

            });

        })
        .catch(error => console.error(error));

    setTimeout(() => {
        window.addEventListener('click', function closeModal(event) {
            if (modalOverlay.style.display === 'flex' && !modalContent.contains(event.target)) {
                modalOverlay.style.display = 'none';

                // 🔥 이벤트 리스너를 제거해서 불필요한 이벤트 감지 방지
                window.removeEventListener('click', closeModal);
            }
        });
    }, 100);


}

//상세 공유된 일정 불러오기
window.detailPageCalendar = function (){

    api.get('/api/chat/sharedCalendar', { roomId: selectedRoomId })
        .then(data => {
            console.log(data.body);
            const sharedCalendar = data.body;

            const calendarContent = document.querySelector('.calender-content');

            calendarContent.innerHTML = '';

            const todayDate = new Date().toISOString().split('T')[0];

            sharedCalendar.forEach(calendar => {
                const calendarItem = document.createElement('div');
                calendarItem.classList.add('calendar-item');

                const eventDate = new Date(calendar.calendar_start_date).toISOString().split('T')[0];

                if(todayDate === eventDate){

                    const todayBtn = document.querySelector('.today-btn-box');

                    todayBtn.innerHTML ='';
                    todayBtn.innerHTML = `
                        <button class="today-btn">오늘</button>
                    `;
                }

                calendarItem.innerHTML = `
                  <div class="calender-box"></div>
                    <div class="calender-text">
                        <div class="calender-name">${calendar.calendar_title}</div>
                        <div class="calender-date" style="font-size: 13px; color: #656565">${formatDateTime(calendar.calendar_start_date)}</div>
                    </div>
            `;
                calendarContent.appendChild(calendarItem);
            });

        })
        .catch(error => console.error(error));
}

//////// 그룹채팅방 생성 /////////
const createChat = document.querySelector('.create-chat');

const chatRoomModal = document.getElementById("chatRoomModal");
const closeModalBtn = document.querySelector(".close-modal");

// 모달 닫기
closeModalBtn.addEventListener("click", function () {
    chatRoomModal.style.display = "none";
});


createChat.addEventListener('click', function (){
    chatRoomModal.style.display = "flex";

    const fileInput = document.querySelector('#fileInput');
    const uploadImg = document.querySelector('#uploadImage');

    const createChatRoomBtn = document.querySelector('#createChatRoomBtn');

    uploadImg.addEventListener('click', function () {
        fileInput.click();
    });



    fileInput.addEventListener('change', function (e){
        const file = e.target.files[0];
        if(file){
            const reader = new FileReader();
            reader.onload = function (e){
                uploadImg.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    })

    createChatRoomBtn.addEventListener('click', function (){
        chatRoomModal.style.display = "none";
        const chatRoom = document.querySelector('#chatRoomName').value;

        const file = fileInput.files[0]; // 🔥 파일 선택 후 가져오기
        if (!file) {
            alert("파일을 선택해주세요!");
            return;
        }

        const formData = new FormData();
        formData.append('files',file);
        formData.append('dogId', currentDogId);
        formData.append('roomId', "newRoom");

        api.post('/api/chat/fileUpload', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
            .then(async response => {
                console.log("응답 데이터 fileUrl:", response);
                const fileUrl = response.body;

                const NewMessage = {
                    timestamp : serverTimestamp(),
                    nickname : 'system',
                    sender : 'system',
                    status : 'system',
                    text : '채팅방이 생성되었습니다.'
                }

                const newChatRoom = {
                    createdBy : currentDogId,
                    participants : [currentDogId],
                    roomName : chatRoom,
                    roomProfile : fileUrl,
                    status : 'G',
                    lastMessage : NewMessage
                }

                const docRef = await addDoc(collection(db, "chatRooms"), newChatRoom);
                const newRoomId = docRef.id;
                await addDoc(collection(db, "chatRooms", newRoomId, "messages"), NewMessage);

                console.log("채팅방 생성 성공, ID:", docRef.id);
                alert("채팅방이 생성되었습니다!");
                window.location.href = `/chat-room`;

            })
            .catch(error => console.error(error));
    })
})


let inviteLink;

//초대링크 생성 및 참여자 추가
async function generateInviteLink(roomId) {
    try {
        const inviteRef = await addDoc(collection(db, "chatInvites"), {
            roomId: roomId,
            inviter: currentDogId,
            createdAt: serverTimestamp()
        });

        const inviteId = inviteRef.id; // Firestore에서 자동 생성된 문서 ID
        inviteLink = `localhost:2000/chat-invite?id=${inviteId}`;

        console.log("✅ 초대 링크 생성 완료:", inviteLink);
        return inviteLink;
    } catch (error) {
        console.error("❌ 초대 링크 생성 실패:", error);
    }
}

//클릭시 join
// 초대 링크를 입력하면 Firestore에서 `roomId`를 찾아 참가 처리
async function handleInviteLink(inviteId, dogId) {
    console.log('handleInviteLink실행')
    const inviteRef = doc(db, "chatInvites", inviteId);
    const inviteSnap = await getDoc(inviteRef);


    if (inviteSnap.exists()) {
        const roomId = inviteSnap.data().roomId;

        // 🔥 Firestore에서 채팅방 `participants`에 사용자 추가
        const roomRef = doc(db, "chatRooms", roomId);
        await updateDoc(roomRef, {
            participants: arrayUnion(dogId) // 기존 참가자 목록에 추가 (중복 방지)
        });

        const NewParticipants = {
            timestamp : serverTimestamp(),
            nickname : 'system',
            sender : 'system',
            status : 'system',
            text : currentUserNickname +'님이 참여하였습니다.'
        }

        await addDoc(collection(db, "chatRooms", roomId, "messages"), NewParticipants);

        console.log("✅ 채팅방 참가 완료:", roomId);

        // // 🔥 Firestore에서 `roomId`를 찾아서 `/chat-room?id=roomId`로 이동
        // window.location.href = `/chat-room`;
    } else {
        console.log("❌ 유효하지 않은 초대 링크");
        alert("유효하지 않은 초대 링크입니다.");
    }
}


// 초대 링크를 브라우저 주소창에 입력하면 자동 처리
document.addEventListener("DOMContentLoaded", () => {
    const inviteId = document.querySelector('#inviteId').value;

    console.log('invitedId'+inviteId);
    console.log("🔥 초대 ID 확인:", inviteId); // ✅ 로그 찍어서 확인하기

    if (inviteId) {
        handleInviteLink(inviteId, currentDogId);
    } else {
        console.log("❌ 초대 ID가 없음.");
    }
});


//초대링크 모달(view)
window.linkModal = async function () {
    const linkBox = document.querySelector('.linkBox');
    linkBox.style.display = 'flex';
    inviteLink = await generateInviteLink(selectedRoomId);
    console.log(inviteLink)
    setTimeout(() => {
        window.addEventListener('click', function closeModal(event) {
            if (linkBox.style.display === 'flex' && !linkBox.contains(event.target)) {
                linkBox.style.display = 'none';

                // 🔥 이벤트 리스너를 제거해서 불필요한 이벤트 감지 방지
                window.removeEventListener('click', closeModal);
            }
        });

    }, 100);

    linkBox.innerText = '';
    linkBox.innerText = `${inviteLink}`;

}

//채팅방 나가기
const outChat = document.querySelector('.out-chat');

outChat.addEventListener('click', async function () {

    const isConfirmed = confirm("채팅방을 나가시겠습니까?");
    if(!isConfirmed){
        return;
    }


    const roomRef = doc(db, "chatRooms", selectedRoomId);
    const roomSnap = await getDoc(roomRef);


    const outMessage = {
        timestamp : serverTimestamp(),
        nickname : 'system',
        sender : 'system',
        status : 'system',
        text : currentUserNickname +'님이 나가셨습니다.'
    }

    await addDoc(collection(db, "chatRooms", selectedRoomId, "messages"), outMessage);

    await updateDoc(roomRef, {
        participants: arrayRemove(currentDogId)
    });

    const updateRoomSnap = await getDoc(roomRef);
    const participants = updateRoomSnap.data().participants;

    //participants 없을 때 채팅방 삭제
    if(participants.length === 0){
        await deleteDoc(roomRef);
    }


    window.location.href = "/chat-room";
})

// 멍바닥 주기
const slider = document.getElementById("ratingSlider");
const ratingValue = document.getElementById("ratingValue");
const pawIcon = document.getElementById("pawIcon");

const ratingModal = document.querySelector('.rating-container');
const starBtn = document.querySelector('.star-btn');

starBtn.addEventListener('click', function (){
    ratingModal.style.display = 'flex';

})

let isDragging = false;

// 아이콘 위치 업데이트 함수
function updateRating(value) {
    ratingValue.textContent = value.toFixed(1); // 0.5 단위 표시
    slider.value = value;

    // 슬라이더 위치 업데이트 (0~5 범위에 맞게 이동)
    const percentage = ((value-1) / 4) * 100;
    pawIcon.style.left = `calc(${percentage}% - 15px)`;
}

// 🔥 드래그 시작 (mousedown) → `mousemove` 이벤트 추가
pawIcon.addEventListener("mousedown", (e) => {
    isDragging = true;
    pawIcon.style.cursor = "grabbing";

    // `mousemove` 이벤트 리스너 추가
    document.addEventListener("mousemove", onMouseMove);
});

// 🔥 드래그 중 (mousemove) → `requestAnimationFrame()`으로 즉각 반응!
function onMouseMove(e) {
    if (!isDragging) return;  // 🚨 드래그 중일 때만 실행!

    const sliderRect = slider.getBoundingClientRect();
    let newX = e.clientX - sliderRect.left;

    // 범위 제한 (0 ~ 슬라이더 길이)
    if (newX < 0) newX = 0;
    if (newX > sliderRect.width) newX = sliderRect.width;

    // 0.5 단위 반올림 처리
    let value = ((newX / sliderRect.width) * 4) + 1;
    value = Math.round(value * 2) / 2; // 🚀 0.5 단위로 반올림

    requestAnimationFrame(() => updateRating(value));
}

// 🔥 드래그 종료 (`mouseup`) → `mousemove` 이벤트 제거!
function stopDragging() {
    if (!isDragging) return;

    console.log("🔥 마우스 놓음 (mouseup 감지됨)");

    // 🚀 `setTimeout()`을 사용하여 `isDragging = false`를 비동기적으로 실행
    setTimeout(() => {
        isDragging = false;
        console.log("🛑 드래그 종료됨, isDragging 초기화 완료");
    });

    pawIcon.style.cursor = "grab";

    // 🚨 `mousemove` 이벤트 리스너 제거 → 더 이상 아이콘이 따라오지 않음!
    document.removeEventListener("mousemove", onMouseMove);
}

// 🔥 `mouseup`을 `document`에서 항상 감지! → 드래그 도중에도 확실하게 적용
document.addEventListener("mouseup", stopDragging);

// 초기 위치 설정
updateRating(3.0);

//별점 등록
window.registStr = async function () {
    const ratingModal = document.querySelector('.rating-container');


    const ratingValue = document.querySelector('.slider').value;
    console.log(ratingValue);

    const docRef = await getDoc(doc(db, 'chatRooms', selectedRoomId))
    console.log(selectedRoomId);
    const participants = docRef.data().participants;
    console.log(participants);
    let otherId;

    participants.forEach(dogId => {
        if(dogId !== currentDogId){
            otherId = dogId;
            console.log(otherId);
        }
    })

    const otherUser = getUsernameById(otherId);

    api.post('/api/chat/star', {
        user_star: ratingValue,
        reviewer_id : currentUser,
        recipient_id : otherUser
    })
        .then(data => {
            ratingModal.style.display='none';
            console.log("별점 등록 완료")
            alert("별점 등록 완료");
        })
        .catch(error => console.error(error));
}

document.querySelector('.rating-close-btn').addEventListener('click', function (){
    document.querySelector('.rating-container').style.display = 'none';
})