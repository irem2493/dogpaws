// 모달 열기
function openModal(modalId, isEmptyBg = false) {
    const modal = document.getElementById(modalId);
    if (modal) {
        // 오버레이 생성 및 추가
        const overlay = document.createElement('div');
        overlay.className = 'pawsModal-overlay';

        // 오버레이 배경색 없는걸 원할경우
        if (isEmptyBg) {
            overlay.style.backgroundColor = 'transparent';
        } else {
            overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
        }

        document.body.appendChild(overlay);

        // 모달을 오버레이 안으로 이동
        overlay.appendChild(modal);

        // 표시
        overlay.style.display = 'flex';
        modal.style.display = 'flex';
    }
}

// 모달 닫기
function closeModal(button) {
    const modal = button.closest('.pawsModal');
    const overlay = modal.parentElement;
    if (modal && overlay.classList.contains('pawsModal-overlay')) {
        // 모달을 원래 위치로 되돌림
        document.body.appendChild(modal);
        // 오버레이 제거
        overlay.remove();
        modal.style.display = 'none';
    }
}

// ESC 키로 모달 닫기
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        const overlay = document.querySelector('.pawsModal-overlay');
        if (overlay) {
            const modal = overlay.querySelector('.pawsModal');
            if (modal) {
                document.body.appendChild(modal);
                overlay.remove();
                modal.style.display = 'none';
            }
        }
    }
});