let currentEventId = null;  // 수정할 이벤트의 ID를 저장할 변수
 // 서버에서 전달된 데이터 확인

//username 가져오기
const sessionUsername = document.getElementById("sessionUsername");
//dogId 가져오기
const sessionDogId = document.getElementById("sessionDogId");

/* 캘린더 리스트 불러오기 */
let calendarList = [];
const username = sessionUsername.value;
window.onload = getCalendarList;
function getCalendarList() {
    api.get('/api/calendar?username='+username)
        .then(data => {
            calendarList = data.body.body;  // body 속성의 배열을 할당
            console.log('calendarList loaded:', calendarList);  // 배열 확인

            const events = calendarList.map(event => {
                // 날짜 형식을 'YYYY-MM-DD HH:mm:ss' -> 'YYYY-MM-DDTHH:mm:ss'로 변환
                const startDateStr = event.calendar_start_date.replace(" ", "T");
                const endDateStr = event.calendar_end_date.replace(" ", "T");

                // 날짜를 Date 객체로 변환
                const startDate = new Date(startDateStr);
                const endDate = new Date(endDateStr);

                // 잘못된 날짜가 있으면 기본값을 넣어주기
                if (isNaN(startDate) || isNaN(endDate)) {
                    console.error("날짜 변환 오류:", event.calendar_start_date, event.calendar_end_date);
                    // 기본값을 넣거나 그 이벤트를 생략할 수 있습니다.
                    return null; // 또는 defaultDate 객체를 사용할 수 있음
                }

                return {
                    id: event.calendar_id,
                    title: event.calendar_title,
                    textColor : "#000000",
                    start: startDate.toISOString(), // ISO 형식으로 변환
                    end: endDate.toISOString(),     // 종료 시간도 ISO 형식으로 변환
                    extendedProps: {
                        type: event.calendar_type,
                        address: event.address,
                        description: event.calendar_description,
                        dogId: event.dog_id,
                        calendarType: event.calendar_type,
                        scheduleType: event.schedule_type,
                        shareUser: event.share_username,
                        sharedYn: event.shared_yn,
                        sharedId: event.shared_id
                    }
                };
            }).filter(event => event);  // 잘못된 이벤트는 필터링

            console.log('캘린더 이벤트 변환 완료:', events);  // 변환된 JSON 확인
            renderCalendar(events);

        })
        .catch(error => {
            console.error(error);
            alert("오류가 발생했습니다.");
        });
}

//캘린더 표시
function renderCalendar(events){

    var calendarEl = document.getElementById('calendar');

    if (!calendarEl) {
        console.error('캘린더 요소가 없습니다!');
        return;
    }

    var calendar = new FullCalendar.Calendar(calendarEl, {

        //+more 표시
        dayMaxEventRows: true,
        views: {
            timeGrid: {
                dayMaxEventRows: 5
            }
        },

        initialView: 'dayGridMonth', // 기본 월간 달력
        headerToolbar: {
            start: '', // 왼쪽:
            center: 'title',      // 중앙: 제목
            end: 'today prev,next' // 오른쪽: 오늘 버튼, 이전/다음 버튼
        },

        events: events,

        //날짜 클릭했을 때 호출됨
        dateClick: function(info) {
            showCalendarForm(info.dateStr);
        },

        //등록된 날짜 클릭했을 때, 정보 보기
        eventClick: function(info) {
            console.log("이벤트 클릭됨:", info.event);
            const eventId = info.event.id; // 클릭된 이벤트의 ID 가져오기
            const calendarIdInput = document.querySelector('input[name="calendarId"]');
            if (calendarIdInput) {
                calendarIdInput.value = eventId; // ID 값을 input에 설정
            };
            editEventForm(info.event, info.event.extendedProps.scheduleType);
        },

        // 타이틀 옆에 아이콘 이미지 추가
        eventContent: function(info) {
            var icon = '';
            var title = info.event.title;
            switch(info.event.extendedProps.type) {
                case 'W':
                    icon = '<img src="/img/icon/dog-filter/foot.svg" alt="산책 아이콘" style="width: 20px; height: 20px; margin-right: 5px;">';
                    break;
                case 'P':
                    icon = '<img src="/img/icon/dog-filter/dribbble-ball.svg" alt="놀이 아이콘" style="width: 20px; height: 20px; margin-right: 5px;">';
                    break;
                case 'M':
                    icon = '<img src="/img/icon/dog-filter/dog.svg" alt="교배 아이콘" style="width: 20px; height: 20px; margin-right: 5px;">';
                    break;
                default:
                    icon = '<img src="/img/icon/dog-filter/dog.svg" alt="기본 아이콘" style="width: 20px; height: 20px; margin-right: 5px;">';
                    break;
            }
            if(info.event.extendedProps.scheduleType === "oth"){
                var othIcon = '<img src="/img/icon/dog-filter/share.svg" alt="공유 아이콘" style="width: 20px; height: 20px; margin-right: 5px;">';
                return { html: othIcon + icon + title };
            }else{
                return { html: icon + title };
            }


        },

        // `type`에 따라 색상 설정
        eventDidMount: function(info) {
            const type = info.event.extendedProps.type;
            if (type === 'P') {
                info.el.style.backgroundColor = '#FFDDC1'; // 연주황
            } else if (type === 'M') {
                info.el.style.backgroundColor = '#FFD1DC'; // 연핑크
            } else if (type === 'W') {
                info.el.style.backgroundColor = '#CCFFCC'; // 연한 연두
            }

            const isAllDay = (new Date(info.event.start).toLocaleDateString() === new Date(info.event.end).toLocaleDateString());
            if (isAllDay) {
                info.el.style.border = 'none';  // 테두리 없애기
            } else {
                info.el.style.border = 'none';
            }
        },
    });

    calendar.render();
}

//폼 전역변수 선언
let calendarTitleField = document.getElementById('calendarTitle');
let calendarTypeField = document.getElementById('calendarType');
let dogIdField = document.getElementById('dogId');
let calendarStartDateField = document.getElementById('calendarStartDate');
let calendarEndDateField = document.getElementById('calendarEndDate');
let addressField = document.getElementById('sample5_address');
let calendarDescriptionField = document.getElementById('calendarDescription');
const submitBtn = document.getElementById("submit");
const updateBtn = document.getElementById("update");
const shareBtn = document.getElementById("share");
const deleteBtn = document.getElementById("delete");


var mapContainer = document.getElementById('calendar-map');
var mapOption = {
    center: new kakao.maps.LatLng(37.537187, 127.005476), // 초기 중심좌표
    level: 5
};
var map = new kakao.maps.Map(mapContainer, mapOption);
var geocoder = new kakao.maps.services.Geocoder();
var marker = new kakao.maps.Marker({
    position: new kakao.maps.LatLng(37.537187, 127.005476),
    map: map
});

// 일정 추가 폼 띄우기
function showCalendarForm(selectedDate) {
    resetForm();
    console.log("일정 추가 폼");
    openModal('calendarForm');

    currentEventId = null;
    document.getElementById("calendarRegist").style.display = "block";
    document.getElementById("calendarDetail").style.display = "none";
    submitBtn.style.display = "block";
    updateBtn.style.display = "none";
    shareBtn.style.display = "none";
    deleteBtn.style.display = "none";

    calendarTitleField.value = "";
    calendarStartDateField.value = selectedDate + "T00:00";
    calendarEndDateField.value = selectedDate + "T23:59";
    addressField.value = "";
    calendarDescriptionField.value = "";
    calendarTypeField.value = "W";
    dogIdField.value = "1";

    calendarTitleField.removeAttribute('readonly');
    calendarStartDateField.removeAttribute('readonly');
    calendarEndDateField.removeAttribute('readonly');
    calendarDescriptionField.removeAttribute('readonly');
    calendarTypeField.removeAttribute('disabled');
    dogIdField.removeAttribute('disabled');

    // 일정 등록 시 지도 숨김!
    mapContainer.style.display = "none";
}
// 일정 상세 폼 띄우기
function editEventForm(event, scheduleType) {
    document.getElementById("scheduleType").value = event.extendedProps?.scheduleType;
    document.getElementById("sharedYn").value = event.extendedProps?.sharedYn;
    document.getElementById("sharedId").value = event.extendedProps?.sharedId;
    currentEventId = event.id;
    openModal('calendarForm');

    if(scheduleType === "my"){
        shareBtn.style.display = "block";
    } else if(scheduleType === "oth"){
        shareBtn.style.display = "none";
        const shareUserField = document.getElementById("shareUserField");
        shareUserField.style.display = "block";
        const shareUser = document.getElementById("shareUser");
        shareUser.value = event.extendedProps?.shareUser || '';
    }
    deleteBtn.style.display = "block";
    document.getElementById("calendarDetail").style.display = "block";
    document.getElementById("calendarDetail").style.display = "flex";
    submitBtn.style.display = "none";
    updateBtn.style.display = "none";
    document.getElementById("calendarRegist").style.display = "none";

    calendarTitleField.value = event.title;
    calendarTitleField.setAttribute('readonly', true);
    calendarTypeField.value = event.extendedProps?.calendarType;
    dogIdField.value = event.extendedProps?.dogId || 1;

    const startDate = new Date(event.start);
    const endDate = event.end ? new Date(event.end) : startDate;
    const startDateLocal = startDate.toLocaleString("sv-SE", { timeZone: "Asia/Seoul" }).replace(" ", "T").slice(0, 16);
    const endDateLocal = endDate.toLocaleString("sv-SE", { timeZone: "Asia/Seoul" }).replace(" ", "T").slice(0, 16);

    calendarStartDateField.value = startDateLocal;
    calendarStartDateField.setAttribute('readonly', true);
    calendarEndDateField.value = endDateLocal;
    calendarEndDateField.setAttribute('readonly', true);

    calendarTypeField.setAttribute('disabled', true);
    dogIdField.setAttribute('disabled', true);

    //기존 주소 설정
    addressField.value = event.extendedProps?.address || '';

    //상세 설명 설정
    calendarDescriptionField.value = event.extendedProps?.description || '';
    calendarDescriptionField.setAttribute('readonly', true);

    //주소가 있으면 지도 표시
    if (addressField.value) {
        updateMapWithAddress(addressField.value);
    } else {
        mapContainer.style.display = "none"; // 주소 없으면 지도 숨김
    }
}


//주소 검색 버튼 클릭 시 지도 표시
function sample5_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            var addr = data.address; // 최종 주소 변수

            // 📍 주소 입력 필드에 값 설정
            document.getElementById("sample5_address").value = addr;

            // 📍 지도 업데이트 함수 호출 (주소 기반)
            updateMapWithAddress(addr);
        }
    }).open();
}

// 주소를 기반으로 지도 업데이트하는 함수
function updateMapWithAddress(address) {
    geocoder.addressSearch(address, function (results, status) {
        if (status === kakao.maps.services.Status.OK) {
            var result = results[0];
            var coords = new kakao.maps.LatLng(result.y, result.x);

            // 📍 지도 표시
            mapContainer.style.display = "block";
            map.relayout();
            map.setCenter(coords);
            marker.setPosition(coords);
        } else {
            console.error("주소 변환 실패:", status);
        }
    });
}

// 일정 수정 폼 열기
function calendarModify(){
    let scheduleType = document.getElementById("scheduleType").value;
    if(scheduleType === "my"){
        shareBtn.style.display = "none";
        updateBtn.style.display = "block";
        alert("일정 수정");
        calendarTitleField.removeAttribute('readonly');
        calendarStartDateField.removeAttribute('readonly');
        calendarEndDateField.removeAttribute('readonly');
        //addressField.removeAttribute('readonly');
        calendarDescriptionField.removeAttribute('readonly');
        calendarTypeField.removeAttribute('disabled');
        dogIdField.removeAttribute('disabled');
    } else if(scheduleType === "oth"){
        shareBtn.style.display = "none";
        updateBtn.style.display = "block";
        alert("일정 수정");
        dogIdField.removeAttribute('disabled');
    }
}

// 폼 초기화
function resetForm() {
    document.getElementById('addCalendarForm').reset();
    const calendarFormModal = document.getElementById('calendarForm');
    const overlay = document.querySelector('.pawsModal-overlay'); // 오버레이 클래스
    if (calendarFormModal) {
        calendarFormModal.style.display = 'none'; // 폼 숨기기
    }
    if (overlay) {
        overlay.style.display = 'none'; // 배경(오버레이) 숨기기
    }
}

//일정 등록
function calendarSubmit(button) {
    const formData = new FormData(document.getElementById('addCalendarForm'));
    const username = sessionUsername.value;
    formData.append("username", username);
    //폼데이터 보내기
    api.post('/api/calendar', formData, {
    })
        .then(res => {
            if (res.body.body == '일정 저장 성공') {  // res.body.body 로 받아야합니다..
                alert("저장 성공");
                resetForm();  // 폼 초기화
                window.location.reload();
            } else {
                alert("저장 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
}

//일정 수정
function calendarUpdate(){
    const formData = new FormData(document.getElementById('addCalendarForm'));
    const username = sessionUsername.value;
    let scheduleType = document.getElementById("scheduleType").value;
    let sharedYn = document.getElementById("sharedYn").value;
    let sharedId = document.getElementById("sharedId").value;
    formData.append("scheduleType", scheduleType);
    formData.append("sharedYn", sharedYn);
    formData.append("sharedId", sharedId);
    formData.append("username", username);
    //폼데이터 보내기
    api.put('/api/calendar', formData, {
    })
        .then(res => {
            if (res.body.body == '일정 수정 성공') {  // res.body.body 로 받아야합니다..
                alert("수정 성공");
                resetForm();  // 폼 초기화
                window.location.reload();
            } else {
                alert("수정 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("수정 오류");
        });
}

//일정 삭제
function calendarDelete(){
    const formData = new FormData(document.getElementById('addCalendarForm'));
    const username = sessionUsername.value;
    let scheduleType = document.getElementById("scheduleType").value;
    let sharedYn = document.getElementById("sharedYn").value;
    let sharedId = document.getElementById("sharedId").value;
    formData.append("scheduleType", scheduleType);
    formData.append("sharedYn", sharedYn);
    formData.append("sharedId", sharedId);
    formData.append("username", username);
    //폼데이터 보내기
    api.post('/api/calendar/delete', formData, {
    })
        .then(res => {
            if (res.body.body == '일정 삭제 성공') {  // res.body.body 로 받아야합니다..
                alert("삭제 성공");
                resetForm();  // 폼 초기화
                window.location.reload();
            } else {
                alert("삭제 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("삭제 오류");
        });
}


