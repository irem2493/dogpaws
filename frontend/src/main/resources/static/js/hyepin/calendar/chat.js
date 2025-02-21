import {db} from '/js/cys/firebase-config.js';  // 절대 경로 적용
import {collection, doc, updateDoc, getDoc, addDoc, query, orderBy, onSnapshot, serverTimestamp, where, getDocs}
    from "https://www.gstatic.com/firebasejs/11.2.0/firebase-firestore.js";
let selectedRoomId = null;  // 🔥 선택한 채팅방 ID 저장

// 🔥 일정 공유 버튼 클릭 시 실행
async function openShareForm() {
    console.log("✅ 일정 공유 모달 열기 시도!");

    let shareForm = document.getElementById("shareForm");
    if (!shareForm) {
        console.error("❌ 공유 모달을 찾을 수 없습니다.");
        return;
    }

    shareForm.style.display = "flex";  // ✅ `block` 대신 `flex` 사용!

    const currentDogId = document.getElementById("sessionDogId").value;

    if (!currentDogId) {
        console.error("❌ 현재 로그인된 강아지 ID를 찾을 수 없습니다.");
        alert("로그인 후 이용해주세요.");
        return;
    }

    try {
        const chatRoomsRef = collection(db, 'chatRooms');
        const q = query(
            chatRoomsRef,
            where('participants', 'array-contains', parseInt(currentDogId)),
            orderBy('lastMessage.timestamp', 'desc')
        );

        const querySnapshot = await getDocs(q);

        if (querySnapshot.empty) {
            console.log("❌ 참여 중인 채팅방 없음");
            document.querySelector(".chat-list").innerHTML = "<p>참여 중인 채팅방이 없습니다.</p>";
            return;
        }

        let oneOnOneRooms = [];
        let groupRooms = [];

        querySnapshot.forEach((doc) => {
            const chatRoom = { id: doc.id, ...doc.data() };
            const participants = chatRoom.participants.filter(user => user !== parseInt(currentDogId));

            if (participants.length === 1) {
                oneOnOneRooms.push(chatRoom);
            } else {
                groupRooms.push(chatRoom);
            }
        });

        displayChatRooms(oneOnOneRooms, groupRooms);

        openModal('shareForm');  // ✅ 모달 열기

        // 🔥 `shareUser` 필드의 `required` 속성 조정
        const shareUserInput = document.getElementById("shareUser");
        if (!shareUserInput || shareUserInput.style.display === "none" || shareUserInput.hidden) {
            shareUserInput.removeAttribute("required");
        } else {
            shareUserInput.setAttribute("required", "true");
        }

    } catch (error) {
        console.error("❌ 채팅방 목록 가져오기 실패:", error);
    }
}
window.openShareForm = openShareForm;


// 🔥 채팅방 목록을 HTML에 추가하는 함수
function displayChatRooms(oneOnOneRooms, groupRooms) {
    const oneOnOneContainer = document.querySelector(".one-on-one");
    const groupContainer = document.querySelector(".group");

    oneOnOneContainer.innerHTML = "";
    groupContainer.innerHTML = "";

    // 1:1 채팅방 표시
    oneOnOneRooms.forEach(chatRoom => {
        const participantId = chatRoom.participants.find(id => id !== parseInt(currentDogId));
        const listItem = `
            <div class="chat-one" onclick="selectChatRoom('${chatRoom.id}')">
                <span>${chatRoom.roomName || `1:1 채팅방 (${participantId})`}</span>
            </div>
        `;
        oneOnOneContainer.innerHTML += listItem;
    });

    // 그룹 채팅방 표시
    groupRooms.forEach(chatRoom => {
        const listItem = `
            <div class="chat-one" onclick="selectChatRoom('${chatRoom.id}')">
                <span>${chatRoom.roomName} (멤버: ${chatRoom.participants.length})</span>
            </div>
        `;
        groupContainer.innerHTML += listItem;
    });
}
window.displayChatRooms = displayChatRooms;


// 🔥 선택한 채팅방 저장 함수
window.selectChatRoom = function (roomId) {
    selectedRoomId = roomId;
    console.log("✅ 선택된 채팅방 ID:", selectedRoomId);
};

// 🔥 일정 공유 함수 (선택한 채팅방 ID 적용)
function calendarShare() {
    if (!selectedRoomId) {
        alert("채팅방을 선택해주세요!");
        return;
    }

    alert("일정 공유");

    const username = document.getElementById("sessionUsername").value;
    const calendarId = parseInt(document.querySelector('input[name="calendarId"]').value, 10);

    const formData = new FormData();
    formData.append("username", username);
    formData.append("calendarId", calendarId);
    formData.append("roomId", selectedRoomId);  // 🔥 선택한 채팅방 ID 반영

    // 🔥 폼 데이터 전송
    api.post('/api/calendar/share', formData, {})
        .then(res => {
            if (res.body.body == '일정 공유 성공') {
                alert("공유 성공");
                resetForm();
                window.location.reload();
            } else {
                alert("공유 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("공유 오류");
        });
}
window.calendarShare = calendarShare;

// 🔥 `#shareUserField` 숨길 때 required 제거
function hideShareUserField() {
    const shareUserField = document.getElementById("shareUserField");
    const shareUserInput = document.getElementById("shareUser");

    shareUserField.style.display = "none";
    shareUserInput.removeAttribute("required");
}

// 🔥 `#shareUserField` 보일 때 required 추가
function showShareUserField() {
    const shareUserField = document.getElementById("shareUserField");
    const shareUserInput = document.getElementById("shareUser");

    shareUserField.style.display = "block";
    shareUserInput.setAttribute("required", "true");
}




/*
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
    formData.append("username", otherDogId);
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
 */