document.addEventListener('DOMContentLoaded', function() {

    // 운송장번호 입력 버튼 클릭 이벤트
    document.querySelectorAll('.tracking-btn').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const qlId = this.dataset.qlId;
            openTrackingModal(qlId);
        });
    });

    // 운송장번호 모달 열기
    function openTrackingModal(qlId) {
        const modal = document.getElementById('trackingModal');
        const trackingInput = document.getElementById('trackingNumber');
        
        // 모달 초기화
        trackingInput.value = '';
        modal.style.display = 'block';
        
        // 입력 필드에 포커스
        setTimeout(() => trackingInput.focus(), 100);
        
        // 확인 버튼 클릭 이벤트
        document.getElementById('confirmTracking').onclick = async function() {
            const trackingNumber = trackingInput.value.trim();
            if (!trackingNumber) {
                alert('운송장번호를 입력해주세요.');
                trackingInput.focus();
                return;
            }
            
            try {
                const response = await api.put(`/api/order/${qlId}/tracking?trackingNumber=${trackingNumber}`);
                
                if (response.status === 'SUCCESS') {
                    alert('운송장번호가 등록되었습니다.');
                    location.reload();
                } else {
                    throw new Error(response.message);
                }
            } catch (error) {
                console.error('운송장번호 등록 실패:', error);
                alert('운송장번호 등록에 실패했습니다.');
            }
            
            closeModal(modal);
        };

        // Enter 키 입력 처리
        trackingInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                document.getElementById('confirmTracking').click();
            }
        });
    }
});