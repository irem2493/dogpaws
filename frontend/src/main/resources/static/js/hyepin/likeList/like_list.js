document.addEventListener("DOMContentLoaded", function () {

    const friendTab = document.getElementById("friend-tab");
    const matingTab = document.getElementById("mating-tab");
    let likeList = [];
<<<<<<< HEAD
    let listType;
=======
>>>>>>> origin/REQ-68-관리자

    const container = document.getElementById("like-list-container-content");

    const sessionDogId = document.getElementById("sessionDogId");
<<<<<<< HEAD
    const myDogId = sessionDogId.value;

    function toggleList(selectedTab) {
        friendTab.classList.remove("title-s");
        matingTab.classList.remove("title-s");
        friendTab.classList.add("title-d");
        matingTab.classList.add("title-d");
        selectedTab.classList.remove("title-d");
        selectedTab.classList.add("title-s");

        listType = selectedTab === friendTab ? "F" : "P";
        console.log("🔥🔥🔥 updateContent() 실행됨, 현재 listType:", listType);
        fetchListData(listType);
    }

=======
    const myDogId = 1;
>>>>>>> origin/REQ-68-관리자

    function fetchListData(likeCode) {
        api.get('/api/like?myDogId=' + myDogId + '&likeCode=' + likeCode)
            .then(data => {
                likeList = data.body || []; // body가 없으면 빈 배열 할당
                console.log('fetchListData loaded:', likeList); // 배열 확인
                updateContent();
            })
            .catch(error => {
                console.error(error);
                alert("오류가 발생했습니다.");
            });
    }

<<<<<<< HEAD
=======
    function toggleList(selectedTab) {
        friendTab.classList.remove("title-s");
        matingTab.classList.remove("title-s");
        friendTab.classList.add("title-d");
        matingTab.classList.add("title-d");
        selectedTab.classList.remove("title-d");
        selectedTab.classList.add("title-s");

        let listType = selectedTab === friendTab ? "F" : "P";
        fetchListData(listType);
    }
>>>>>>> origin/REQ-68-관리자

    function updateContent() {
        const newContentContainer = document.createElement("div");
        const newMoblieContentContainer = document.createElement("div");
        newContentContainer.classList.add("content-item-container-web-box");
        newMoblieContentContainer.classList.add("like-list-container-content-mobile");

        newContentContainer.innerHTML = likeList.map(item => `
            <div class="content-item-container-web">
<<<<<<< HEAD
                <div class="item-profile"><a href="/dog/detail/${item.dog_id}"> <img src="${item.profile_url}" alt="강아지 프로필"
                            onerror="this.onerror=null; this.src='/img/로고.jpg';" style="cursor: pointer;"> </a>
=======
                <div class="item-profile"> <img src="${item.profile_url}" alt="강아지 프로필"
                            onerror="this.onerror=null; this.src='/img/로고.jpg';">
>>>>>>> origin/REQ-68-관리자
                </div>
                <div class="content-items">
                    <div class="content-item">
                        <div class="item-name">${item.dog_name}</div>
                        <div class="item-breed">| ${item.breed} <span class="item-mix">(${item.is_mix ? "믹스" : "순종"})</span></div>
<<<<<<< HEAD
                        <div class="item-gender">| ${item.gender == "F" ? "여" : " 남"} <span class="item-neutered">(${item.is_neutered ? "중성화 O" : "중성화 X"})</span></div>
=======
                        <div class="item-gender">| ${item.gender} <span class="item-neutered">(${item.is_neutered ? "중성화 O" : "중성화 X"})</span></div>
>>>>>>> origin/REQ-68-관리자
                        <div class="item-birth">
                            ${item.birth_year && item.birth_month ? `<div class="dog-filter-calendar">
                                <img src="/img/icon/dog-filter/birth.svg" alt="calendar-icon">
                            </div>` : ""} 
                            ${item.birth_year ? `${item.birth_year}년 ` : ""} 
                            ${item.birth_month ? `${item.birth_month}월생` : ""}
                        </div>
                    </div>
                    <div>
                        <div class="dog-filter">
                            <img src="/img/icon/dog-filter/bone.svg" alt="bone-icon">성격 유형
                        </div>
                    </div>
                    <div class="item-personal">
                        ${item.dog_personal_gbn_cds_list?.length > 0
                        ? item.dog_personal_gbn_cds_list.map(personal => `<span class='trait-tag'>${personal}</span>`).join(" ")
                        : "정보 없음"}
                    </div>
                    <div>
                        <div class="dog-filter">
                            <img src="/img/icon/dog-filter/dribbble-ball.svg" alt="dribbble-ball-icon">좋아하는 놀이
                        </div>
                    </div>
                    <div class="item-play">
                        ${item.dog_play_gbn_cds_list?.length > 0
                        ? item.dog_play_gbn_cds_list.map(play => `<span class='play-tag'>${play}</span>`).join(" ")
                        : "정보 없음"}
                    </div>
                </div>
<<<<<<< HEAD
                <div class="item-like">
                    <img src="/img/icon/${item.liked ? (listType === 'F' ? 'like-push.svg' : 'mating-push.svg')
                            : (listType === 'F' ? 'like.svg' : 'mating.svg')}" 
                         alt="좋아요 아이콘" 
                         data-dog-id="${item.dog_id}" 
                         data-like-code="${listType}" 
                         onclick="likeToggle(this)">
                </div>
=======
                <div class="item-like"><img src="/img/icon/like-push.svg" alt="좋아요 아이콘"> </div>
>>>>>>> origin/REQ-68-관리자
            </div>
            <hr class="custom-hr">
        `).join("");

        newMoblieContentContainer.innerHTML = likeList.map(item => `
            <div class="mobile-container">
                <div class="content-item-container">
<<<<<<< HEAD
                    <div class="item-profile"><a href="/dog/detail/${item.dog_id}"> <img src="${item.profile_url}" alt="강아지 프로필"
                            onerror="this.onerror=null; this.src='/img/로고.jpg';" style="cursor: pointer;"> </a>
                    </div>
                    <div class="content-items-moblie">
                        <div class="item-name">${item.dog_name}</div>
                        <div class="item-breed">| ${item.breed}<span class="item-mix">(${item.is_mix ? "믹스" : "순종"})</span></div>
                        <div>
                            <div class="item-gender">| ${item.gender == "F" ? "여" : " 남"} <span class="item-neutered">(${item.is_neutered ? "중성화 O" : "중성화 X"})</span></div>
=======
                    <div class="item-profile"> <img src="${item.profile_url}" alt="강아지 프로필"
                            onerror="this.onerror=null; this.src='/img/로고.jpg';">
                    </div>
                    <div class="content-items-moblie">
                        <div class="content-item">
                            <div class="item-name">${item.dog_name}</div>
                            <div class="item-breed">| ${item.breed}<span class="item-mix">(${item.is_mix ? "믹스" : "순종"})</span></div>
                        </div>
                        <div>
                            <div class="item-gender">| ${item.gender} <span class="item-neutered">(${item.is_neutered ? "중성화 O" : "중성화 X"})</span></div>
>>>>>>> origin/REQ-68-관리자
                        </div>
                        <div class="item-birth">
                            ${item.birth_year && item.birth_month ? `<div class="dog-filter-calendar">
                                <img src="/img/icon/dog-filter/birth.svg" alt="calendar-icon">
                            </div>` : ""} 
                            ${item.birth_year ? `${item.birth_year}년 ` : ""} 
                            ${item.birth_month ? `${item.birth_month}월생` : ""}
                        </div>
                    </div>
<<<<<<< HEAD
                    <div class="item-like">
                        <img src="/img/icon/${item.liked ? (listType === 'F' ? 'like-push.svg' : 'mating-push.svg')
                            : (listType === 'F' ? 'like.svg' : 'mating.svg')}" 
                             alt="좋아요 아이콘" 
                             data-dog-id="${item.dog_id}" 
                             data-like-code="${listType}" 
                             onclick="likeToggle(this)">
                    </div>
=======
                    <div class="item-like"><img src="/img/icon/like-push.svg" alt="좋아요 아이콘"  onclick="likeToggle(this)"></div>
>>>>>>> origin/REQ-68-관리자
                </div>
                <div> <div class="dog-filter">
                            <img src="/img/icon/dog-filter/bone.svg" alt="bone-icon">성격 유형
                        </div>
                </div>
                <div class="item-personal">
                                ${item.dog_personal_gbn_cds_list?.length > 0
                    ? item.dog_personal_gbn_cds_list.map(personal => `<span class='trait-tag'>${personal}</span>`).join(" ")
                    : "정보 없음"}
                </div>
                <div>
                    <div class="dog-filter">
                            <img src="/img/icon/dog-filter/dribbble-ball.svg" alt="dribbble-ball-icon">좋아하는 놀이
                    </div>
                </div>
                <div class="item-play-mobile">
                                ${item.dog_play_gbn_cds_list?.length > 0
                    ? item.dog_play_gbn_cds_list.map(play => `<span class='play-tag'>${play}</span>`).join(" ")
                    : "정보 없음"}
                </div>
            </div>
            <hr class="custom-hr">
        `).join("");

        container.innerHTML = "";
        container.appendChild(newContentContainer);
        container.appendChild(newMoblieContentContainer);
    }

    friendTab.addEventListener("click", function() { toggleList(friendTab); });
    matingTab.addEventListener("click", function() { toggleList(matingTab); });

    toggleList(friendTab);
});


//좋아요 토글
function likeToggle(element) {
<<<<<<< HEAD
    let isLiked = element.src.includes("push.svg");


    //dogId 가져오기
    const sessionDogId = document.getElementById("sessionDogId");
    const myDogId = sessionDogId.value;
    const dogId = element.dataset.dogId;
    const likeCode = element.dataset.likeCode;
    let baseIcon = likeCode === 'F' ? 'like' : 'mating';
    let newSrc = isLiked ? `/img/icon/${baseIcon}.svg` : `/img/icon/${baseIcon}-push.svg`;

=======
    let icon = document.getElementById("likeIcon");
    let isLiked = icon.src.includes("like-push.svg");

    const username = sessionUsername.value;
    const myDogId = sessionDogId.value;
    const dogId = element.dataset.target;
>>>>>>> origin/REQ-68-관리자
    console.log("dogId:" + dogId);

    //숫자와 char 형식은 변환이 필요하기 때문에 폼데이터로 보내겠습니다.
    const LikeDto = {
        "myDogId": myDogId,
        "dogId": parseInt(dogId), // <-- 숫자로 변환
<<<<<<< HEAD
        "likeCode": likeCode.charAt(0) // <-- char 변환
=======
        "likeCode": "F".charAt(0) // <-- char 변환
>>>>>>> origin/REQ-68-관리자
    }

    const formData = new FormData();
    formData.append("myDogId", myDogId);
    formData.append("dogId", dogId);
<<<<<<< HEAD
    formData.append("likeCode", likeCode);
=======
    formData.append("likeCode", "F");
>>>>>>> origin/REQ-68-관리자

    api.post('/api/likes/toggle', formData, {})
        .then(res => {
            if (res.body.body == '성공') {  // res.body.body 로 받아야합니다..
<<<<<<< HEAD
                element.src = newSrc;
                element.dataset.liked = isLiked ? "false" : "true";
=======
                icon.src = isLiked ? "/img/icon/like.svg" : "/img/icon/like-push.svg";
                matchList.forEach(dog => {
                    if (dog.dog_id === parseInt(dogId)) {
                        console.log("dog.dog_id: " + dog.dog_id + "dogId: " + dogId + "찾았다 dogId")
                        dog.liked = !dog.liked;
                    }
                });
                updateCards();
>>>>>>> origin/REQ-68-관리자
            } else {
                alert("좋아요 실패!");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("저장 오류");
        });
}