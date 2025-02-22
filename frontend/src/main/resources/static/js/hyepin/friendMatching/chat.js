import {db} from '/js/cys/firebase-config.js';  // 절대 경로 적용
import {collection, doc, updateDoc, getDoc, addDoc, query, orderBy, onSnapshot, serverTimestamp, where, getDocs}
    from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";

//1:1 채팅 신청
async function chatForm(element) {
    const currentUser = sessionDogId.value;
    const roomName = element.dataset.username;
    const participantsStr = element.dataset.dogId;        // 상대 강아지 ID 저장

    // 쉼표로 구분된 참가자 목록을 배열로 변환 후, 중복 제거 및 공백 제거
    let participants = participantsStr.split(',')
        .map(user => user.trim())
        .filter(user => user !== "");

    if (currentUser && !participants.includes(currentUser)) {
        participants.push(currentUser);
    }

    participants.sort();
    const participantsKey = participants.join(',');

    // Firestore 쿼리를 통해 동일한 participantsKey를 가진 채팅방이 존재하는지 확인
    const chatRoomsRef = collection(db, "chatRooms");
    const q = query(chatRoomsRef, where("participantsKey", "==", participantsKey));
    const querySnapshot = await getDocs(q);

    if (!querySnapshot.empty) {
        // 이미 존재하는 채팅방이 있으면 첫 번째 문서를 선택
        const existingRoom = querySnapshot.docs[0];
        alert("이미 채팅방이 존재합니다!");
        //To Do => existingRoom으로 해당 채팅방으로 이동하게
        return;
    }

    // 현재 로그인한 사용자 추가 (중복 방지)
    if (!participants.includes(currentUser)) {
        participants.push(currentUser);
    }

    if (roomName.trim() === "" || participants.length === 0) {
        alert("채팅방 이름과 참여자를 입력하세요.");
        return;
    }

    try {
        // Firestore의 chatRooms 컬렉션에 새 채팅방 추가
        const docRef = await addDoc(collection(db, "chatRooms"), {
            createdBy: currentUser,
            participants: participants,
            participantsKey: participantsKey,
            status: "F",
            lastMessage: {
                text: "채팅방이 생성되었습니다.",
                sender: "system",
                timestamp: serverTimestamp()
            }
        });

        const roomId = docRef.id;

        const NewMessage = {
            sender: "system",
            nickname: "system",
            status: "system",
            text: "채팅방이 생성되었습니다.",
            timestamp: serverTimestamp()
        };

        await addDoc(collection(db, "chatRooms", roomId, "messages"), NewMessage);

        console.log("채팅방 생성 성공, ID:", docRef.id);
        alert("채팅방이 생성되었습니다!");
        window.location.href = `/chat-room`;
    } catch (error) {
        console.error("채팅방 생성 중 오류 발생:", error);
    }
}

// 모듈 스코프 내에서 정의된 chatForm 함수를 전역(window) 스코프에 등록
window.chatForm = chatForm;

const chatListElement = document.getElementById("chatList");
let chatProfiles=[];
let chatRooms = [];
let otherParticipants = [];
//그룹채팅 목록 가져오기
async function getGroupChatRoom(element) {
    const otherDogId = element.dataset.dogId; //상대 강아지 id 저장
    const otherUsername = element.dataset.username; //상대 강아지 id 저장
    const modal = document.querySelector(".pawsModal"); // 모달 요소 찾기
    modal.setAttribute("data-other-dog-id", otherDogId); // 모달에 데이터 저장
    modal.setAttribute("data-other-username", otherUsername); // 모달에 데이터 저장
    const username = sessionUsername.value;
    const currentDogId = sessionDogId.value;
    console.log("그룹채팅 목록 가져오기 / username : " + username + " / otherDogId : " + otherDogId + " / currentDogId: " + currentDogId + " / otherUsername: " + otherUsername);

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
                    console.log("응답 데이터:", chatProfiles);

                    chatCategory();
                })
                .catch(error => console.error(error));
        });

    } catch (error) {
        console.error("채팅방 목록 가져오기 실패:", error);
    }
}
window.getGroupChatRoom = getGroupChatRoom;

function chatCategory() {
    const currentDogId = sessionDogId.value;
    const filterStatus = 'G';
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

            listItem.innerHTML = `
                <div class="chat-list-item">
                    <div class="img-container">
                        <img src="${chatRoom.roomImage}" onerror="this.src='/img/증명사진.jpg'" width="50" class="profile-img-2" alt="프로필">
                    </div>
                    <input type="button" value="(${chatRoom.participants.length}명) ${chatRoom.roomName}" title="${chatRoom.roomName}" data-room-id="${chatRoom.id}" data-room-name="${chatRoom.roomName}" onclick="groupChatSubmit(this)" class="unstyled-button"> 
                </div>
            `;
            chatListElement.appendChild(listItem);
        }
    });
}

//그룹채팅 초대완료
function groupChatSubmit(element){
    const username = sessionUsername.value;
    const modal = document.querySelector(".pawsModal"); // 모달 요소 찾기
    const otherDogId = modal.getAttribute("data-other-dog-id"); // 저장된 ID 가져오기
    const otherUsername = modal.getAttribute("data-other-username"); // 저장된 ID 가져오기
    const roomId = element.dataset.roomId;
    const roomName = element.dataset.roomName;
    const message = `그룹 채팅방 [${roomName}]에 초대되었습니다. 참여하시겠습니까?`
    console.log("그룹채팅 초대 완료 / username: " + username +  " / otherDogId : ", otherDogId + " / roomId: " + roomId + "/ otherUsername: " + otherUsername);

    const formData = new FormData();
    formData.append("username", otherUsername);
    formData.append("dogId", otherDogId);
    formData.append("alarmType", "G");
    formData.append("gubnId", roomId);
    formData.append("message", message);

    if (confirm(`[${roomName}] 방으로 초대하시겠습니까?`)) {

        api.post('/api/matching/chat-room', formData, {})
            .then(res => {
                if (res.body.body == '그룹 초대 성공') {  // res.body.body 로 받아야합니다..
                    alert("초대 완료!");
                    const modalElement = document.querySelector('#groupChat');
                    closeModal(modalElement);
                } else {
                    alert("실패!");
                }
            })
            .catch(error => {
                console.error("오류:", error);
                alert("초대 오류");
            });
    }
}
window.groupChatSubmit = groupChatSubmit;