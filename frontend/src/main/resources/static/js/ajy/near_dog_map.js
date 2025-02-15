document.addEventListener("DOMContentLoaded", function() {
    let fixedLat = document.getElementById("latitude").value || 37.5665; // 기본값: 서울
    let fixedLng = document.getElementById("longitude").value || 126.9780;
    let dogImageUrl = document.getElementById("dogMarkerImage").src; // 강아지 이미지 URL

    const sliderThumb = document.querySelector(".slider-thumb");
    const sliderFill = document.querySelector(".slider-fill");
    const sliderTrack = document.querySelector(".slider-track");
    const labels = document.querySelectorAll(".slider-label");

    const distances = [0, 50, 100]; // 1km, 3km, 5km 위치

    let currentIndex = 0; // 현재 거리 인덱스

    // 슬라이더 이동 함수
    function moveSlider(index) {
        sliderThumb.style.left = `${distances[index]}%`;
        sliderFill.style.width = `${distances[index]}%`;
        currentIndex = index;
    }

    // 거리 값(1Km, 3Km, 5Km) 클릭 시 이동
    labels.forEach(label => {
        label.addEventListener("click", function () {
            const index = parseInt(this.getAttribute("data-index"));
            moveSlider(index);
        });
    });

    // 슬라이더 바 클릭 시 가장 가까운 거리로 이동
    sliderTrack.addEventListener("click", function (event) {
        const trackRect = sliderTrack.getBoundingClientRect();
        const clickX = event.clientX - trackRect.left;
        const trackWidth = trackRect.width;

        let closestIndex = 0;
        let minDiff = Math.abs((clickX / trackWidth) * 100 - distances[0]);

        distances.forEach((dist, index) => {
            const diff = Math.abs((clickX / trackWidth) * 100 - dist);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = index;
            }
        });

        moveSlider(closestIndex);
    });

    // 초기 값 설정
    moveSlider(currentIndex);

    console.log("📌 지도 로딩 시작...");
    console.log("위도:", fixedLat, "경도:", fixedLng);

    // 지도 컨테이너 확인
    let mapContainer = document.getElementById("map-container");
    let mapElement = document.getElementById("kakao-map");

    if (!mapContainer || !mapElement) {
        console.error("❌ 지도 컨테이너 또는 #kakao-map 요소를 찾을 수 없습니다!");
        return;
    }

    // 지도의 크기가 자동으로 적용되도록 설정
    mapContainer.style.width = "100%";
    mapContainer.style.height = "500px";
    mapElement.style.width = "100%";
    mapElement.style.height = "100%";

    // 지도를 생성하기 전에 부모 요소가 화면에 존재하는지 확인
    if (mapContainer.offsetParent === null) {
        console.warn("⚠️ 지도 컨테이너가 화면에 보이지 않습니다. 부모 요소 확인 필요!");
    }

    // 지도 초기화
    setTimeout(() => {
        initializeMapWithDog(fixedLat, fixedLng, dogImageUrl);
    }, 300);
});

function initializeMapWithDog(lat, lng, dogImageUrl) {
    let mapContainer = document.getElementById("kakao-map");
    if (!mapContainer) {
        console.error("❌ Map container not found!");
        return;
    }

    var map = new kakao.maps.Map(mapContainer, {
        center: new kakao.maps.LatLng(lat, lng), // 지도 중심
        level: 4
    });

    // 📌 강아지 개수 하드코딩 (`+9` 고정)
    let dogCount = 9;

    // 📌 강아지 프로필과 버튼을 포함하는 HTML 구조
    var content = `
        <div class="custom-marker">
            <div class="marker-wrapper">
                <div class="marker-image">
                    <img src="${dogImageUrl}" alt="강아지 프로필">
                </div>
                <div class="marker-badge">+${dogCount}</div>
            </div>
            <button class="marker-button">내 주변 강아지 보기</button>
        </div>
    `;

    // 📌 커스텀 오버레이 생성
    var customOverlay = new kakao.maps.CustomOverlay({
        position: new kakao.maps.LatLng(lat, lng),
        content: content,
        yAnchor: 1.2
    });
    customOverlay.setMap(map);

    // 📌 반경 1km 원 추가
    let circle = new kakao.maps.Circle({
        center: new kakao.maps.LatLng(lat, lng),
        radius: 1000, // 1km 반경
        strokeWeight: 2,
        strokeColor: "#ff5a5f",
        strokeOpacity: 0.8,
        fillColor: "#ffb6c1",
        fillOpacity: 0.3
    });
    circle.setMap(map);

    console.log("✅ 지도 로딩 완료! 강아지 개수:", dogCount);
}
