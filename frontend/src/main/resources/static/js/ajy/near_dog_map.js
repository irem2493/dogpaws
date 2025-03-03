let nearbyDogs = [];
let radiusCircle = null;
let currentRadius = 1000;
let markers = [];
let infoWindows = [];
let labels;
let sliderThumb, sliderFill, sliderTrack;
let dogCountOverlay = null;

document.addEventListener("DOMContentLoaded", function () {
    let username = document.getElementById("username").value;
    let lat = parseFloat(document.getElementById("latitude").value) || 37.5665;
    let lng = parseFloat(document.getElementById("longitude").value) || 126.9780;
    let dogImageUrl1 = document.getElementById("dogMarkerImage").src;

    // 📌 슬라이더 요소 가져오기
    labels = document.querySelectorAll(".slider-label");
    sliderThumb = document.querySelector(".slider-thumb");
    sliderFill = document.querySelector(".slider-fill");
    sliderTrack = document.querySelector(".slider-track");

    api.get('/api/dog/nearbyDog/' + username)
        .then(data => {
            console.log('Response Data:', data);
            nearbyDogs = data.body?.body || [];
            console.log("🐶 강아지 목록 저장 완료!", nearbyDogs);
            updateMapWithDogs(nearbyDogs, lat, lng, dogImageUrl1);
        })
        .catch(error => console.error("❌ 강아지 데이터를 불러오는 중 오류 발생:", error));

    // 📌 슬라이더 이벤트 등록
    labels.forEach(label => {
        label.addEventListener("click", function () {
            const index = parseInt(this.getAttribute("data-index"));
            moveSlider(index, window.kakaoMap, lat, lng, nearbyDogs);
        });
    });

    sliderTrack.addEventListener("click", function (event) {
        const trackRect = sliderTrack.getBoundingClientRect();
        const clickX = event.clientX - trackRect.left;
        const trackWidth = trackRect.width;

        let closestIndex = 0;
        let minDiff = Math.abs((clickX / trackWidth) * 100 - sliderPositions[0]);

        sliderPositions.forEach((pos, index) => {
            const diff = Math.abs((clickX / trackWidth) * 100 - pos);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = index;
            }
        });

        moveSlider(closestIndex, window.kakaoMap, lat, lng, nearbyDogs);
    });
});

function updateMapWithDogs(dogs, lat, lng, dogImageUrl1) {
    let mapContainer = document.getElementById("kakao-map");
    if (!mapContainer) {
        console.error("❌ 지도 컨테이너를 찾을 수 없습니다.");
        return;
    }

    if (!window.kakaoMap) {
        window.kakaoMap = new kakao.maps.Map(mapContainer, {
            center: new kakao.maps.LatLng(lat, lng),
            level: 4
        });
    }

    let map = window.kakaoMap;

    var content = `
        <div class="custom-marker">
            <div class="marker-wrapper">
                <div class="marker-image">
                    <img src="${dogImageUrl1}" alt="강아지 프로필" onerror="this.src='/img/dog_foot2.png';">
                </div>
            </div>
        </div>
    `;

    var customOverlay = new kakao.maps.CustomOverlay({
        position: new kakao.maps.LatLng(lat, lng),
        content: content,
        yAnchor: 1.2
    });
    customOverlay.setMap(map);

    updateRadiusCircle(map, lat, lng, currentRadius, dogs);

    markers = [];
    infoWindows = [];

    let positionMap = {};
    let baseOffset = 0.000005;

    dogs.forEach(dog => {
        if (!dog.latitude || !dog.longitude) {
            console.warn(`⚠️ 강아지 ${dog.dog_name}의 위도/경도가 없습니다.`);
            return;
        }

        let key = `${dog.latitude},${dog.longitude}`;
        if (!positionMap[key]) {
            positionMap[key] = [];
        }
        positionMap[key].push(dog);
    });

    Object.keys(positionMap).forEach((key) => {
        let dogsInSameSpot = positionMap[key];
        let baseLat = parseFloat(key.split(',')[0]);
        let baseLng = parseFloat(key.split(',')[1]);

        dogsInSameSpot.forEach((dog, index) => {
            let angle = (360 / dogsInSameSpot.length) * index;
            let radian = (Math.PI / 180) * angle;
            let randomOffset = baseOffset + Math.random() * 0.000005;
            let adjustedLat = baseLat + randomOffset * Math.cos(radian);
            let adjustedLng = baseLng + randomOffset * Math.sin(radian);

            let markerPosition = new kakao.maps.LatLng(adjustedLat, adjustedLng);
            let marker = new kakao.maps.Marker({
                position: markerPosition,
                map: map
            });

            let profileImg = dog.profile_url ? dog.profile_url : "/img/dog_foot2.png";
            let infoContent = `
                <div style="text-align:center; padding:10px; position:relative;">
                    <button onclick="closeInfoWindow(${infoWindows.length})" 
                            style="position:absolute; top:5px; right:5px; background:#ff5a5f; color:white; border:none; padding:2px 6px; border-radius:50%;">
                        ✖
                    </button>
                    <a href="/dog/detail/${dog.dog_id}">
                       <img src="${profileImg}" width="50" height="50" style="border-radius:50%;" onerror="this.src='/img/dog_foot2.png';">
                    </a>
                    <br><strong>${dog.dog_name}</strong>
                </div>
            `;

            let infoWindow = new kakao.maps.InfoWindow({
                content: infoContent
            });

            kakao.maps.event.addListener(marker, 'click', function () {
                infoWindows.forEach(win => win.close());
                infoWindow.open(map, marker);
            });

            markers.push(marker);
            infoWindows.push(infoWindow);
        });
    });

    let clusterer = new kakao.maps.MarkerClusterer({
        map: map,
        markers: markers,
        gridSize: 50,
        minLevel: 4,
        averageCenter: true
    });
}

function updateRadiusCircle(map, lat, lng, radius, dogs = []) {
    if (radiusCircle) {
        radiusCircle.setMap(null);
    }

    radiusCircle = new kakao.maps.Circle({
        center: new kakao.maps.LatLng(lat, lng),
        radius: radius,
        strokeWeight: 2,
        strokeColor: "#ff5a5f",
        strokeOpacity: 0.8,
        fillColor: "#ffb6c1",
        fillOpacity: 0.3
    });

    radiusCircle.setMap(map);

    let count = (dogs || []).filter(dog => {
        if (!dog.latitude || !dog.longitude) return false;
        let distance = getDistance(lat, lng, dog.latitude, dog.longitude);
        return distance <= radius;
    }).length;

    if (dogCountOverlay) {
        dogCountOverlay.setMap(null);
    }

    let countContent = `<div class="marker-badge">+${count}</div>`;
    /*
    let countContent = `<div class="marker-badge">+${count}</div>`;*/

    dogCountOverlay = new kakao.maps.CustomOverlay({
        position: new kakao.maps.LatLng(lat, lng),
        content: countContent,
        yAnchor: -0.2
    });

    dogCountOverlay.setMap(map);
}


// 📌 거리 설정
var distances = [1000, 3000, 5000];
let currentIndex = 0;
const sliderPositions = [0, 50, 100];

function moveSlider(index, map, lat, lng, dogs) {
    sliderThumb.style.left = `${sliderPositions[index]}%`;
    sliderFill.style.width = `${sliderPositions[index]}%`;
    currentIndex = index;

    let newRadius = distances[index];
    currentRadius = newRadius;
    updateRadiusCircle(map, lat, lng, newRadius, dogs);
}

// 📌 X 버튼 클릭 시 정보 창 닫기
function closeInfoWindow(index) {
    infoWindows[index].close();
}

// 📌 거리 계산 함수 (Haversine 공식)
function getDistance(lat1, lng1, lat2, lng2) {
    const R = 6371 * 1000;
    const dLat = (lat2 - lat1) * (Math.PI / 180);
    const dLng = (lng2 - lng1) * (Math.PI / 180);
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(lat1 * (Math.PI / 180)) * Math.cos(lat2 * (Math.PI / 180)) *
        Math.sin(dLng/2) * Math.sin(dLng/2);
    return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
}
