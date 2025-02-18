
//스와이프
document.getElementById("next").addEventListener("click", () => {
    if (startIndex + 1 < matchList.length) {
        startIndex += 1;
        updateCards();
    }
});

document.getElementById("prev").addEventListener("click", () => {
    if (startIndex - 1 >= 0) {
        startIndex -= 1;
        updateCards();
    }
});

//스와이프 감지
let touchStartX = 0; // 터치 또는 마우스 시작 X 좌표
let touchEndX = 0;   // 터치 또는 마우스 끝 X 좌표
let isSwiping = false; // 스와이프가 진행 중인지 체크하는 플래그
const swipeThreshold = 50; // 최소 스와이프 거리 (50px 이상만 넘어가게)

const cardContainer = document.getElementById("cardContainer");

// 터치 시작 (모바일)
cardContainer.addEventListener("touchstart", (event) => {
    touchStartX = event.touches[0].clientX; // 터치 시작 X 좌표
    isSwiping = true;  // 스와이프 시작
});

// 터치 끝 (모바일)
cardContainer.addEventListener("touchend", (event) => {
    touchEndX = event.changedTouches[0].clientX; // 터치 끝 X 좌표
    handleSwipe();
    isSwiping = false; // 스와이프 종료
});

// 마우스 다운 (PC)
cardContainer.addEventListener("mousedown", (event) => {
    touchStartX = event.clientX; // 마우스 시작 X 좌표
    isSwiping = true;  // 스와이프 시작
});

// 마우스 업 (PC)
cardContainer.addEventListener("mouseup", (event) => {
    touchEndX = event.clientX; // 마우스 끝 X 좌표
    handleSwipe();
    isSwiping = false; // 스와이프 종료
});

// 스와이프 처리 함수
function handleSwipe() {
    const swipeDistance = touchEndX - touchStartX; // 스와이프 거리 계산
    console.log('Swipe distance:', swipeDistance); // 디버깅용

    // 스와이프가 threshold 이상일 경우에만 카드 넘어가도록
    if (isSwiping && Math.abs(swipeDistance) > swipeThreshold) {
        if (swipeDistance > 0) {
            // 오른쪽 스와이프 (이전 카드로 넘어가기)
            console.log('Swipe right: Previous card');
            if (startIndex - 1 >= 0) {
                startIndex -= 1;
                updateCards();
            }
        } else {
            // 왼쪽 스와이프 (다음 카드로 넘어가기)
            console.log('Swipe left: Next card');
            if (startIndex + 1 < matchList.length) {
                startIndex += 1;
                updateCards();
            }
        }
        cardContainer.style.userSelect = 'none'; // 글자 드래그 방지
    }
}
