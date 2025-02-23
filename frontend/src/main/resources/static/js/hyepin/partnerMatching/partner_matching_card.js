
//매칭 리스트
let matchList = [];  // 빈 객체로 초기화;  // 데이터를 저장할 배열
let startIndex = 0;   // 현재 시작 인덱스

const dogId = sessionDogId.value;
const username = sessionUsername.value;
// 컨트롤러에서 matchList 데이터 가져오기
api.get('/api/matching?dogId=' + dogId + '&username=' + username + '&matchType=P')
    .then(data => {
        matchList = [...matchList, ...data.body];
        console.log('match loaded:', matchList);
        updateCards();
    })
    .catch(error => {
        console.error(error);
        alert("오류가 발생했습니다.");
    });


//매칭 띄워주기
function updateCards() {
    const container = document.getElementById('cardContainer');
    container.style.padding = "0px";
    container.innerHTML = '';

    // matchList에서 startIndex에 해당하는 단 하나의 카드만 표시
    if(matchList.length > startIndex) {
        const dog = matchList[startIndex];
        const card = document.createElement('div');
        const cardWeb = document.createElement('div');

        // 중앙 카드 전용 클래스 및 스타일 적용 (모바일용)
        card.classList.add('matching-card');
        card.classList.add('col-6');
        card.classList.add('mx-auto');
        card.style.padding = "0px";
        
        //웹용
        cardWeb.classList.add('matching-card-web');
        cardWeb.style.padding = "35px";

        // 아이콘 HTML은 기존 로직을 그대로 사용 (예시로 iconHtml 변수 사용)
        const iconHtml = dog.matched_criteria_list && dog.matched_criteria_list.length > 0 ?
            dog.matched_criteria_list.map(criteria => {
                switch (criteria) {
                    case '품종':
                        return '<img src="/img/icon/dog-filter/dog.svg" alt="dog-icon" title="견종이 일치해요!">';
                    case '서류':
                        return '<img src="/img/icon/dog-filter/document.svg" alt="document-icon" style="max-width: 18px; height: auto;" title="서류가 일치해요!">';
                    case '체중':
                        return '<img src="/img/icon/dog-filter/weight.svg" alt="weight-icon" style="max-width: 18px; height: auto;" title="선호하는 체중대와 맞아요!">';
                    case '성격유형':
                        return '<img src="/img/icon/dog-filter/foot.svg" alt="foot-icon" title="견BTI가 같아요!">';
                    case '성격':
                        return '<img src="/img/icon/dog-filter/bone.svg" alt="bone-icon" title="성격 유형이 잘 맞아요!">';
                    case '놀이':
                        return '<img src="/img/icon/dog-filter/dribbble-ball.svg" alt="dribbble-ball-icon" title="좋아하는 놀이 스타일이 같아요!">';
                    case '산책시간':
                        return '<img src="/img/icon/dog-filter/clock.svg" alt="clock-icon" title="산책 시간이 잘 맞아요!">';
                    case '산책요일':
                        return '<img src="/img/icon/dog-filter/calendar.svg" alt="calendar-icon" title="산책 요일이 잘 맞아요!">';
                    default:
                        return '';
                }
            }).join('') : '';

        card.innerHTML = `
            <div class="card-top" style="height: 60%; position: relative; z-index: 9999">
                <img class="card-top-img" 
                     src="${dog.profile_url ? dog.profile_url : '/img/로고.jpg'}" 
                     alt="${dog.dog_name}" 
                     onerror="this.onerror=null; this.src='/img/로고.jpg';"
                     style="width: 100%; height: 100%; object-fit: cover;" />
                <div class="card-top-icon" style="
                    position: absolute; 
                    top: 8px; 
                    right: 8px; 
                    min-width: 5%; 
                    padding: 10px 5px; 
                    border-radius: 10px; 
                    background-color: #FBEEEE;
                    display: flex; 
                    flex-direction: column; 
                    gap: 10px; 
                    align-items: center; 
                    justify-content: center;">
                    ${iconHtml}
                </div>
            </div>
            <div class="card-center" style="font-size: 18px; height: 28%;">
                <div style="line-height: 1.8;">${dog.dog_name} ${dog.breed_name !== '' ? `| ${dog.breed_name}` : ''} ${dog.is_mix === 'N' ? '(순종)' : (dog.is_mix ? '(믹스)' : '')}</div>
                <div style="line-height: 1.8;">${dog.gender === "M" ? '남' : (dog.gender ? '여' : '')} ${dog.is_neutered === "Y" ? '(중성화 O)' : (dog.is_neutered ? '(중성화 X)' : '')}</div>
                <div style="line-height: 1.8; color: #5e5e5e; font-size: 0.8em; text-align: center; width: 100%;">${dog.address != null ? dog.address : '지정된 산책로가 없습니다.'}</div>
                <div class="documents-container">
                    ${dog.blood_test_certified === "Y" ? `<div class="documents-item"><img src="/img/icon/check.svg" alt="check-icon"> 혈통서</div>` : ""}
                    ${dog.vaccination_certified === "Y" ? `<div class="documents-item"><img src="/img/icon/check.svg" alt="check-icon"> 예방접종 증명서</div>` : ""}
                    ${dog.health_record_certified === "Y" ? `<div class="documents-item"><img src="/img/icon/check.svg" alt="check-icon"> 건강기록 증명서</div>` : ""}
                </div>
            </div>
            <div class="card-bottom center-card" style="height: 12%;">
                <div class="card-bottom-items">
                    <img id="likeIcon" src="${dog.liked ? '/img/icon/mating-push.svg' : '/img/icon/mating.svg'}" alt="mating-icon" data-target="${dog.dog_id}" onclick="likeToggle(this)">
                </div>
                <div class="card-bottom-items">
<<<<<<< HEAD
                    <img src="/img/icon/partner-messege.svg" alt="messege-icon"  >
=======
                    <img src="/img/icon/partner-messege.svg" alt="messege-icon" data-dog-id="${dog.dog_id}" data-username="${dog.username}" onclick="chatForm(this)">
>>>>>>> origin/REQ-68-관리자
                </div>
                <div class="card-bottom-items">
                    <img src="/img/icon/partner-wechat-logo.svg" alt="wechat-logo-icon" data-dog-id="${dog.dog_id}" data-username="${dog.username}" onclick="openModal('groupChat'); getGroupChatRoom(this)">
                </div>
                <div class="card-bottom-items">
                    <img src="/img/icon/alarm-warning-line.svg" alt="alarm-warning-line-icon" data-target="${dog.dog_id}" onclick="openModal('declarationForm'); declarationForm(this)">
                </div>
            </div>
        `;

        cardWeb.innerHTML = `
            <div style="position: relative; z-index: 9999">
                <div style="display: flex; gap: 40px;">
                    <div class="card-img-web">
                        <img
                         src="${dog.profile_url ? dog.profile_url : '/img/로고.jpg'}" 
                         alt="${dog.dog_name}" 
                         onerror="this.onerror=null; this.src='/img/로고.jpg';"
                         style="width: 100%; height: 100%; object-fit: cover;" />
                    </div>
                    <div class="card-right-web">
                        <div class="card-icon-web">
                            ${iconHtml}
                        </div>
                        <div class="card-right-web-item" style="font-size: 24px; height: 28%;">
                            <div>${dog.dog_name} ${dog.breed_name !== '' ? `| ${dog.breed_name}` : ''} ${dog.is_mix === 'N' ? '(순종)' : (dog.is_mix ? '(믹스)' : '')}</div>
                            <div style="font-size: 18px">${dog.gender === "M" ? '남' : (dog.gender ? '여' : '')} ${dog.is_neutered === "Y" ? '(중성화 O)' : (dog.is_neutered ? '(중성화 X)' : '')}</div>
                            <div style="font-size: 16px; color: #5D5D5D;">${dog.address != null ? dog.address : '지정된 산책로가 없습니다.'}</div>
                            <div class="documents-container">
                                ${dog.blood_test_certified === "Y" ? `<div class="documents-item"><img src="/img/icon/check.svg" alt="check-icon"> 혈통서</div>` : ""}
                                ${dog.vaccination_certified === "Y" ? `<div class="documents-item"><img src="/img/icon/check.svg" alt="check-icon"> 예방접종 증명서</div>` : ""}
                                ${dog.health_record_certified === "Y" ? `<div class="documents-item"><img src="/img/icon/check.svg" alt="check-icon"> 건강기록 증명서</div>` : ""}
                                ${dog.blood_test_certified === "N" && dog.vaccination_certified === "N" && dog.health_record_certified === "N" ? 
                                    `<div class="documents-item" style="font-size: 16px; color: #5D5D5D;"><img src="/img/icon/dog-filter/document.svg" alt="document-icon" style="margin-right: 5px;">
                                    등록된 서류가 없어요!</div>` : ""}
                            </div>
                            <div style="font-size: 18px;" class="card-dog-intro">
                                ${ dog.dog_intro != null
                                ? (dog.dog_intro.length > 60
                                    ? dog.dog_intro.substring(0,60) + '...'
                                    : dog.dog_intro)
                                : '' }
                            </div>
                        </div>
                        <div class="card-bottom-web">
                            <div class="card-bottom-web-item">
                                <img id="likeIcon" src="${dog.liked ? '/img/icon/mating-push.svg' : '/img/icon/mating.svg'}" alt="mating-icon" data-target="${dog.dog_id}" onclick="likeToggle(this)">
                            </div>
                            <div class="card-bottom-web-item">
                                <img src="/img/icon/partner-messege.svg" alt="messege-icon" data-dog-id="${dog.dog_id}" data-username="${dog.username}" onclick="chatForm(this)">
                            </div>
                            <div class="card-bottom-web-item">
                                <img src="/img/icon/partner-wechat-logo.svg" alt="wechat-logo-icon" data-dog-id="${dog.dog_id}" data-username="${dog.username}" onclick="openModal('groupChat'); getGroupChatRoom(this)">
                            </div>
                            <div class="card-bottom-web-item">
                                <img src="/img/icon/alarm-warning-line.svg" alt="alarm-warning-line-icon" data-target="${dog.dog_id}" onclick="openModal('declarationForm'); declarationForm(this)">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        container.appendChild(card);
        container.appendChild(cardWeb);
    }
}
