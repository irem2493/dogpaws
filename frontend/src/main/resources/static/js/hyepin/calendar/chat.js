import {db} from '/js/cys/firebase-config.js';
import {collection, doc, updateDoc, getDoc, addDoc, query, orderBy, onSnapshot, serverTimestamp, where, getDocs}
    from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";

let selectedRoomId = null;
const currentDogId = document.getElementById("sessionDogId").value;
console.log("currentDogId: " + currentDogId);

const chatListElement = document.getElementById("chat-list");
let chatProfiles = [];
let chatRooms = [];
let otherParticipants = [];

let currentFilterStatus = 'ALL';  // 기본 필터 상태를 'ALL'로 설정 (F와 G 모두 포함)

// 일정 공유 버튼 클릭 시 실행
async function openShareForm() {
    console.log("✅ 일정 공유 모달 열기 시도!");
    openModal('shareForm');

    if (!currentDogId) {
        console.error("❌ 현재 로그인된 강아지 ID를 찾을 수 없습니다.");
        window.location.href = '/login';
        return;
    }

    try {
        const chatRoomsRef = collection(db, 'chatRooms');
        const q = query(
            chatRoomsRef,
            where('participants', 'array-contains', currentDogId),
            orderBy('lastMessage.timestamp', 'desc')
        );

        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            chatListElement.innerHTML = "<li>참여 중인 채팅방이 없습니다.</li>";
            return;
        }

        chatRooms = [];

        querySnapshot.forEach((doc) => {
            chatRooms.push({ id: doc.id, ...doc.data() });

            const participants = doc.data().participants.filter(user => user !== currentDogId);
            participants.forEach(participant => {
                if (!otherParticipants.includes(participant)) {
                    otherParticipants.push(participant);
                }
            });
        });

        console.log("참여자 목록:", otherParticipants);

        api.post('/api/chat/profile', {otherParticipants}, {
            headers: {'Content-Type': 'application/json'}
        })
            .then(response => {
                chatProfiles = response.body;
                console.log("응답 데이터:", chatProfiles);
                chatCategory(currentFilterStatus);  // 필터링된 목록 표시
            })
            .catch(error => console.error(error));

    } catch (error) {
        console.error("채팅방 목록 가져오기 실패:", error);
    }
}
window.openShareForm = openShareForm;

// 채팅방 목록을 HTML에 추가하는 함수 (F = 1:1 채팅, G = 그룹 채팅)
function chatCategory(filterStatus) {

    const oneElement = document.getElementById("one-on-one");
    const groupElement = document.getElementById("group");
    currentFilterStatus = filterStatus;  // 현재 필터 상태 업데이트
    //chatListElement.innerHTML = "";
    oneElement.innerHTML = "";
    groupElement.innerHTML = "";

    chatRooms.forEach((chatRoom) => {
        if (filterStatus === 'ALL' || chatRoom.status === filterStatus) {
            const otherParticipants = chatRoom.participants.filter(id => id !== currentDogId);
            let participantId = otherParticipants.length === 1 ? otherParticipants[0] : null;

            const listItem = document.createElement("div");

            if (chatRoom.status === 'F') {
                // 1:1 채팅(F) → 상대방 정보 가져오기
                const profileUrl = getProfileById(participantId);
                const otherNickname = getNicknameById(participantId);

                listItem.innerHTML = `
                    <div class="chat-one" onclick="room('${chatRoom.id}')">
                        <div class="img-container">
                            <img src="${profileUrl}" width="50" height="50" class="profile-img-2" alt="프로필"
                                 onerror="this.onerror=null; this.src='/img/로고.jpg';">
                        </div>
                        <div class="pre-chat-with">
                                <input type="button" value="${otherNickname}" title="${otherNickname}" data-room-id="${chatRoom.id}" data-room-name="${otherNickname}" onclick="calendarShare(this)" class="unstyled-button"> 
                        </div>
                    </div>  
                `;

                oneElement.appendChild(listItem);
            } else if (chatRoom.status === 'G') {
                // 그룹 채팅(G) → 그룹 정보 가져오기
                const groupImage = chatRoom.roomProfile || "/img/groupchat.png";

                listItem.innerHTML = `
                    <div class="chat-one" onclick="room('${chatRoom.id}')">
                        <div class="img-container">
                            <img src="${groupImage}" width="50" height="50" class="profile-img-2" alt="그룹 프로필"
                                onerror="this.onerror=null; this.src='/img/로고.jpg';">
                        </div>
                        <div class="pre-chat-content">
                            <div class="pre-chat-with">
                                <input type="button" value="(${chatRoom.participants.length}명) ${chatRoom.roomName}" title="${chatRoom.roomName}" data-room-id="${chatRoom.id}" data-room-name="${chatRoom.roomName}" onclick="calendarShare(this)" class="unstyled-button"> 
                            </div>
                        </div>
                    </div>  
                `;
                groupElement.appendChild(listItem);
            }
            //chatListElement.appendChild(listItem);
        }
    });
}
window.chatCategory = chatCategory;

// 프로필 가져오는 함수 (ID 기반)
function getProfileById(participantId) {
    if (!participantId) return "/img/로고.jpg";
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantId));
    return profile ? profile.profile_url : "/img/로고.jpg";
}

// 닉네임 가져오는 함수 (ID 기반)
function getNicknameById(participantId) {
    if (!participantId) return "알 수 없음";
    const profile = chatProfiles.find(profile => profile.dog_id === parseInt(participantId));
    return profile ? profile.nickname : "알 수 없음";
}

// 선택한 채팅방 저장 함수
window.selectChatRoom = function (roomId) {
    selectedRoomId = roomId;
    console.log("✅ 선택된 채팅방 ID:", selectedRoomId);
};

// 일정 공유 함수 (선택한 채팅방 ID 적용)
function calendarShare(element) {
    let selectedRoomId = element.dataset.roomId;
    let selectedRoomName = element.dataset.roomName;

    const calendarIdInput = document.querySelector('input[name="calendarId"]');
    console.log("calendarIdInput:", calendarIdInput);
    console.log("selectedRoomId:", selectedRoomId);
    console.log("selectedRoomName:", selectedRoomName);

    if (!calendarIdInput || !calendarIdInput.value) {
        alert("공유할 일정을 선택해주세요!");
        return;
    }

    if (!selectedRoomId) {
        alert("채팅방을 선택해주세요!");
        return;
    }

    if (confirm(`[${selectedRoomName}] 방에 공유하시겠습니까?`)) {
        const username = document.getElementById("sessionUsername").value;
        const currentUserNickname = document.getElementById("sessionNickname").value;
        const calendarId = parseInt(calendarIdInput.value, 10);
        console.log("username: " + username + " / calendarId: " + calendarId + " / selectedRoomId: " + selectedRoomId);

        //캘린더 ID로 제목, 시작시간, 끝나는 시간 받아오기
        api.get('/api/calendar/one?calendarId=' + calendarId)
            .then(data => {
                let calendar = data.body;  // body 속성의 배열을 할당
                console.log('calendar loaded:', calendar);

                if (Array.isArray(calendar) && calendar.length > 0) {
                    calendar = calendar[0]; // 첫 번째 일정 가져오기
                }

                let calendarTitle = calendar.calendar_title ?? "제목 없음";
                let calendarStartDate = calendar.calendar_start_date ?? "날짜 없음";
                let calendarEndDate = calendar.calendar_end_date ?? "날짜 없음";

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
                console.log("Firestore에 추가할 데이터 확인:", NewSchedule);

                addDoc(collection(db, "chatRooms", selectedRoomId, "messages"), NewSchedule)
                    .then(() => {
                        return updateDoc(doc(db, "chatRooms", selectedRoomId), { lastMessage: NewSchedule });
                    })
                    .then(() => {
                        alert("공유가 완료되었습니다!");
                        calendarCloseModal('shareForm');
                    })
                    .catch(error => {
                        console.error("일정 공유 중 오류 발생:", error);
                        alert("공유에 실패했습니다. 다시 시도해주세요!");
                    });
            })
            .catch(error => {
                console.error(error);
                alert("오류가 발생했습니다.");
            });

    }
}
window.calendarShare = calendarShare;

