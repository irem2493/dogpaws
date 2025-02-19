document.addEventListener("DOMContentLoaded", async function () {

    const friendTab = document.getElementById("friend-tab");
    const matingTab = document.getElementById("mating-tab");
    const contentContainer = document.querySelector(".like-list-container-content");

    const sessionDogId = document.getElementById("sessionDogId");
    //const myDogId = sessionDogId.value;
    const myDogId = 1;

    async function fetchListData(likeCode) {
        api.get('/api/like?myDogId=' + myDogId + '&likeCode=' + likeCode)
            .then(data => {
                likeList = data.body;  // body 속성의 배열을 할당
                console.log('likeList loaded:', likeList);  // 배열 확인
                return likeList;
            })
            .catch(error => {
                console.error(error);
                alert("오류가 발생했습니다.");
            });
    }

    async function toggleList(selectedTab) {
        // 모든 탭에서 title-d 제거하고 title-s로 변경
        friendTab.classList.remove("title-d");
        matingTab.classList.remove("title-d");
        friendTab.classList.add("title-s");
        matingTab.classList.add("title-s");

        // 선택된 탭만 title-d로 변경
        selectedTab.classList.remove("title-s");
        selectedTab.classList.add("title-d");

        // API 데이터 가져오기
        listType = selectedTab === friendTab ? "F" : "P";
        const listData = await fetchListData(listType);


        // 콘텐츠 변경
        contentContainer.innerHTML = listData.map(item => `
            <div class="content-item-container-web-box">
                <div class="content-item-container-web">
                    <div class="item-profile"> <img src="${item.profile_url}" alt="강아지 프로필"> </div>
                    <div>
                        <div class="content-item">
                            <div class="item-name">${item.dog_name}</div>
                            <div class="item-breed">| ${item.breed} <span class="item-mix">(${item.is_mix ? "믹스" : "순종"})</span></div>
                            <div class="item-gender">| ${item.gender} <span class="item-neutered">(${item.is_neutered ? "중성화 O" : "중성화 X"})</span></div>
                            <div class="item-birth">${item.birthDate || "생년월일 정보 없음"}</div>
                        </div>
                        <div>성격 유형</div>
                        <div class="item-personal">${item.dogPersonalGbnCdsList.length > 0 ? item.dogPersonalGbnCdsList.map(personal => `<span class='trait-tag'>${personal}</span>`).join(" ") : "정보 없음"}</div>
                        <div>좋아하는 놀이</div>
                        <div class="item-play">${item.dogPlayGbnCdsList.length > 0 ? item.dogPlayGbnCdsList.map(play => `<span class='play-tag'>${play}</span>`).join(" ") : "정보 없음"}</div>
                    </div>
                    <div class="item-like"><img src="${item.liked ? '/img/icon/like-push.svg' : '/img/icon/like.svg'}" alt="좋아요 아이콘"> 좋아요</div>
                </div>
                <hr class="custom-hr">
            </div>
        `).join("");
    }

    friendTab.addEventListener("click", function() { toggleList(friendTab); });
    matingTab.addEventListener("click", function() { toggleList(matingTab); });

    // 기본적으로 친구후보 리스트 API 호출하여 표시
    toggleList(friendTab);
});
