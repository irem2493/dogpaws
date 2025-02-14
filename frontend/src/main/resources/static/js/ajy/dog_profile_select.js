document.addEventListener("DOMContentLoaded", function () {

    // ✅ URL에서 Query Parameters 가져오기
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get("accessToken");
    const username = params.get("username");
    const role = params.get("role");
    const nickname = params.get("nickname");

    // ✅ 토큰과 사용자 정보가 존재하면 세션 스토리지에 저장
    if (accessToken && username && role && nickname) {
        sessionStorage.setItem("accessToken", accessToken);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("role", role);
        sessionStorage.setItem("nickname", nickname);

        console.log("✅ 소셜 로그인 성공! sessionStorage 저장 완료.");
    }

    // ✅ 기존 URL에서 Query Parameters 제거 (보안을 위해)
    window.history.replaceState({}, document.title, window.location.pathname);



    // Thymeleaf에서 데이터를 가져옴
    const profileContainer = document.getElementById('dogProfiles');
    let dogProfilesStr = profileContainer.getAttribute('data-dogs');

    console.log('원본 프로필 데이터:', dogProfilesStr);

    let dogProfiles = convertToJSON(dogProfilesStr);

    // 2. 프로필 정보 렌더링
    renderDogProfiles(dogProfiles);

    function renderDogProfiles(dogList) {
        profileContainer.innerHTML = '';  // 기존 내용 초기화

        // 프로필 렌더링
        dogList.forEach(profile => {
            const profileItem = document.createElement('div');
            profileItem.classList.add('profile-item');

            profileItem.innerHTML = `
                <img src="${profile.profile_url}" alt="프로필 이미지" onerror="this.src='/img/dog_foot2.png';">
                <div class="profile-name" >${profile.dog_name}</div>
            `;

            // 프로필 선택 클릭 이벤트 추가
            profileItem.addEventListener('click', function () {
                saveSelectedProfile(profile);
            });

            profileContainer.appendChild(profileItem);
        });

        // 남은 슬롯에 추가 버튼 렌더링
        const remainingSlots = 5 - dogList.length;
        for (let i = 0; i < remainingSlots; i++) {
            const addProfileItem = document.createElement('div');
            addProfileItem.classList.add('add-profile');
            addProfileItem.innerHTML = '+';
            addProfileItem.addEventListener('click', function () {
                location.href="/dogProfileRegister";
            });

            profileContainer.appendChild(addProfileItem);
        }
    }
});

function convertToJSON(str) {
    // Key에 쌍따옴표 추가
    str = str.replace(/([a-zA-Z0-9_]+)=/g, '"$1":');

    // 값에 대해 쌍따옴표로 감싸기 처리
    str = str.replace(/:\s*([^,\]}]+)/g, function (match, value) {
        // null과 숫자는 그대로 유지
        if (value === 'null' || value.match(/^-?\d+(\.\d+)?$/)) {
            return ':' + value;
        }

        // 이미 따옴표로 묶인 문자열일 경우 그대로 유지
        if (value.startsWith('"') && value.endsWith('"')) {
            return ':' + value;
        }

        // URL 값 처리
        if (value.startsWith('http://') || value.startsWith('https://')) {
            return ':"' + value + '"';
        }

        // **콤마(,)가 포함된 경우 반드시 문자열로 묶기**
        if (value.includes(',')) {
            return ':"' + value + '"';
        }

        return ':"' + value + '"';  // 일반 문자열 처리
    });

    console.log('변환된 JSON 문자열:', str);

    // JSON 파싱 시도
    return JSON.parse(str);
}

function saveSelectedProfile(profile) {

    // FormData 객체 생성
    /*const formData = new FormData();

    // 선택한 프로필 정보를 FormData에 추가
    formData.append('dog_name', profile.dog_name);
    formData.append('profile_url', profile.profile_url);*/

    axios({
        method: 'post',
        url: 'http://localhost:2000/dog/saveProfile', // 서버의 정확한 URL
        data: JSON.stringify(profile),  // JSON으로 전송
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            console.log('Response Data:', response);  // 응답 데이터 출력

            if (response.data === '프로필 선택 완료') {
                alert('프로필이 선택되었습니다.');
                location.href = '/';
            } else {
                alert("프로필 선택 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("프로필 저장 중 오류.");
        });
}