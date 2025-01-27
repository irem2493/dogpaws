let currentEventId = null;  // 수정할 이벤트의 ID를 저장할 변수

document.addEventListener('DOMContentLoaded', function() {
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

        events: [
            {
                id: 2,
                title: '파우즈 운동회2!',
                start: "2025-01-24T10:00",  // KST로 변환된 시간
                end: "2025-01-29T12:00",
                //사용자 정의
                extendedProps: {
                    dogId: "1",
                    allDay: true,
                    type: 'M',
                    address: '혜빈이집',
                    description: '운동회 준비 및 진행'
                }
            },
            {
                id: 3,
                title: '파우즈 운동회3!',
                start: '2025-01-24T16:30',
                end: '2025-01-29T18:00',
                //사용자 정의
                extendedProps: {
                    dogId: "2",
                    allDay: false,
                    type: 'W',
                    address: '우리집',
                    description: '멍멍뭉뭉뭉'
                }
            },
            {
                id: 4,
                title: '파우즈 운동회4!',
                start: '2025-01-28T16:30',
                end: '2025-01-30T18:00',
                //사용자 정의
                extendedProps: {
                    dogId: "3",
                    allDay: false,
                    type: 'P',
                    address: '한강',
                    description: '왈왈오라오랑라오라ㅘㅇ왕라'
                }
            }
        ],

        //날짜 클릭했을 때 호출됨
        dateClick: function(info) {
            showCalendarForm(info.dateStr);
        },

        //등록된 날짜 클릭했을 때, 정보 보기
        eventClick: function(info) {
            const eventId = info.event.id; // 클릭된 이벤트의 ID 가져오기
            const calendarIdInput = document.querySelector('input[name="calendarId"]');
            if (calendarIdInput) {
                calendarIdInput.value = eventId; // ID 값을 input에 설정
            };
            editEventForm(info.event);
        },

        // 타이틀 옆에 아이콘 이미지 추가
        eventContent: function(info) {
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
            var title = info.event.title;
            return { html: icon + title };
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
            info.el.style.color = 'black';
        }
    });

    calendar.render();
});

// 첫 번째 셀렉트 박스
const selectBox1 = document.querySelector('.select-box1');
const selected1 = selectBox1.querySelector('.select-box-selected');
const options1 = selectBox1.querySelector('.select-box-options');
const hiddenInput1 = selectBox1.querySelector('input[type="hidden"]');

selectBox1.addEventListener('click', (e) => {
    selectBox1.classList.toggle('active');
});

options1.addEventListener('click', (e) => {
    if (e.target.classList.contains('select-box-option')) {
        selected1.textContent = e.target.textContent;
        hiddenInput1.value = e.target.getAttribute('data-value');
    }
});

document.addEventListener('click', (e) => {
    if (!selectBox1.contains(e.target)) {
        selectBox1.classList.remove('active');
    }
});

// 두 번째 셀렉트 박스
const selectBox2 = document.querySelector('.select-box2');
const selected2 = selectBox2.querySelector('.select-box-selected');
const options2 = selectBox2.querySelector('.select-box-options');
const hiddenInput2 = selectBox2.querySelector('input[type="hidden"]');

selectBox2.addEventListener('click', (e) => {
    selectBox2.classList.toggle('active');
});

options2.addEventListener('click', (e) => {
    if (e.target.classList.contains('select-box-option')) {
        selected2.textContent = e.target.textContent;
        hiddenInput2.value = e.target.getAttribute('data-value');
    }
});

document.addEventListener('click', (e) => {
    if (!selectBox2.contains(e.target)) {
        selectBox2.classList.remove('active');
    }
});

//폼 전역변수 선언
let calendarTitleField = document.getElementById('calendarTitle');
let calendarTypeField = document.getElementById('calendarType');
let calendarStartDateField = document.getElementById('calendarStartDate');
let calendarEndDateField = document.getElementById('calendarEndDate');
let addressField = document.getElementById('address');
let calendarDescriptionField = document.getElementById('calendarDescription');
const submitBtn = document.getElementById("submit");
const updateBtn = document.getElementById("update");
const shareBtn = document.getElementById("share");
const deleteBtn = document.getElementById("delete");

// 일정 추가 폼 띄우기
function showCalendarForm(selectedDate) {
    openModal('calendarForm');

    currentEventId = null;
    submitBtn.style.display = "block";
    updateBtn.style.display = "none";
    shareBtn.style.display = "none";
    deleteBtn.style.display = "none";

    calendarTitleField.value = "";  // 제목 초기화
    calendarStartDateField.value = selectedDate + "T00:00";  // 기본 시작일 설정
    calendarEndDateField.value = selectedDate + "T23:59";    // 기본 종료일 설정
    addressField.value = ""; // 주소 초기화
    calendarDescriptionField.value = ""; // 일정상세 초기화
    calendarTypeField.value = "W";

    calendarTitleField.removeAttribute('readonly');
    calendarStartDateField.removeAttribute('readonly');
    calendarEndDateField.removeAttribute('readonly');
    addressField.removeAttribute('readonly');
    calendarDescriptionField.removeAttribute('readonly');

    document.querySelectorAll('.select-box').forEach(selectBox => {
        selectBox.classList.remove('readonly'); // 읽기 전용 클래스 제거
    });
}

// 일정 상세 폼 띄우기
function editEventForm(event) {
    currentEventId = event.id;  // 수정할 이벤트 ID 저장
    openModal('calendarForm');

    shareBtn.style.display = "block";
    deleteBtn.style.display = "block";
    document.getElementById("calendarDetail").style.display = "block";
    document.getElementById("calendarDetail").style.display = "flex";
    submitBtn.style.display = "none";
    updateBtn.style.display = "none";
    document.getElementById("calendarRegist").style.display = "none";

    calendarTitleField.value = event.title;
    calendarTitleField.setAttribute('readonly', true);

    // 서버에서 받은 시간
    const startDate = new Date(event.start);
    const endDate = event.end ? new Date(event.end) : startDate;

    // 클라이언트에서 시간을 로컬 타임존에 맞게 변환
    const startDateLocal = startDate.toLocaleString("sv-SE", { timeZone: "Asia/Seoul" }).replace(" ", "T").slice(0, 16);
    const endDateLocal = endDate.toLocaleString("sv-SE", { timeZone: "Asia/Seoul" }).replace(" ", "T").slice(0, 16);

    // 시작일과 종료일 설정 및 읽기 전용
    calendarStartDateField.value = startDateLocal;
    calendarStartDateField.setAttribute('readonly', true);
    calendarEndDateField.value = endDateLocal;
    calendarEndDateField.setAttribute('readonly', true);

    document.querySelectorAll('.select-box').forEach(selectBox => {
        selectBox.classList.add('readonly'); // 읽기 전용 클래스 추가
    });

    // 주소
    addressField.value = event.extendedProps?.address || '';
    addressField.setAttribute('readonly', true); // 읽기 전용 설정

    // 상세 설명
    calendarDescriptionField.value = event.extendedProps?.description || '';
    calendarDescriptionField.setAttribute('readonly', true); // 읽기 전용 설정

}

// 일정 수정 폼 열기
function calendarModify(){
    shareBtn.style.display = "none";
    updateBtn.style.display = "block";

    alert("일정 수정");

    calendarTitleField.removeAttribute('readonly');
    calendarStartDateField.removeAttribute('readonly');
    calendarEndDateField.removeAttribute('readonly');
    addressField.removeAttribute('readonly');
    calendarDescriptionField.removeAttribute('readonly');

    document.querySelectorAll('.select-box').forEach(selectBox => {
        selectBox.classList.remove('readonly'); // 읽기 전용 클래스 제거
    });

}

// 폼 초기화
function resetForm() {
    document.getElementById('addCalendarForm').reset();
    document.getElementById('calendarForm').style.display = 'none';
}

// 일정 추가 폼 취소
function cancelForm(button) {
    resetForm();
    const modal = button.closest('.modal');
    closeModal(modal);
}


//일정 등록
function calendarSubmit() {
    const formData = new FormData(document.getElementById('addCalendarForm'));

    //폼데이터 보내기
    api.post('/api/calendar', formData, {
    })
        .then(res => {
            if (res.body.body == '일정 저장 성공') {  // res.body.body 로 받아야합니다..
                alert("저장 성공");
                resetForm();  // 폼 초기화
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

    //폼데이터 보내기
    api.put('/api/calendar', formData, {
    })
        .then(res => {
            if (res.body.body == '일정 수정 성공') {  // res.body.body 로 받아야합니다..
                alert("수정 성공");
                resetForm();  // 폼 초기화
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

    //폼데이터 보내기
    api.post('/api/calendar/delete', formData, {
    })
        .then(res => {
            if (res.body.body == '일정 삭제 성공') {  // res.body.body 로 받아야합니다..
                alert("삭제 성공");
                resetForm();  // 폼 초기화
            } else {
                alert("삭제 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("삭제 오류");
        });
}

//일정 공유
function calendarShare(){
    updateBtn.style.display = "none";
    alert("일정 공유");
    // 공유 폼 띄우기
    // 받는 사람은 알림에서 확인
    // 알림에서 공유받을 때 다른 정보는 읽기로 확인 가능하고, 반려견 선택할 수 있게.
}