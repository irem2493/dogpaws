document.addEventListener("DOMContentLoaded", function () {
    const profileDropdown = document.querySelector(".profile-dropdown");
    const profileModal = document.getElementById("profileModal");

    // 강아지 이름 클릭 시 모달 열기
    profileDropdown.addEventListener("click", function (event) {
        event.stopPropagation(); // 이벤트 버블링 방지
        profileModal.style.display = profileModal.style.display === "block" ? "none" : "block";
    });

    // 모달 바깥 클릭 시 닫기
    document.addEventListener("click", function (event) {
        if (!profileDropdown.contains(event.target)) {
            profileModal.style.display = "none";
        }
    });

});

function selectDogProfile(element) {
    const profileImg = element.querySelector('img').getAttribute('src');
    const profileName = element.querySelector('span').innerText;
    const profileId = element.querySelector('input[type="hidden"]').value;

    const selectedProfile = {
        dog_id: profileId,
        dog_name: profileName,
        profile_url: profileImg
    };

    console.log("선택한 프로필:", selectedProfile);

    // 선택한 프로필을 서버에 저장
    saveSelectedProfile(selectedProfile);
}