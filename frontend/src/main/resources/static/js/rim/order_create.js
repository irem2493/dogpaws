function initializeElements() {
    return {
        orderForm: document.getElementById('orderForm'),
        paymentButton: document.getElementById('payment-button'),
        ordererName: document.getElementById('ordererName'),
        ordererPhone: document.getElementById('ordererPhone'),
        shippingZipcode: document.getElementById('shippingZipcode'),
        shippingAddress1: document.getElementById('shippingAddress1'),
        shippingAddress2: document.getElementById('shippingAddress2'),
        addressSearchBtn: document.getElementById('addressSearchBtn'),
        orderSubmitBtn: document.getElementById('orderSubmitBtn')
    };
}

// 주소 검색 함수
function searchAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('shippingZipcode').value = data.zonecode;
            document.getElementById('shippingAddress1').value = data.address;
            document.getElementById('shippingAddress2').focus();
        }
    }).open();
}


// 초기화 및 이벤트 리스너 설정
window.addEventListener('DOMContentLoaded', async () => {
    console.log('DOM 로드됨');

    // DOM 요소 초기화
    const elements = initializeElements();
    console.log('DOM 요소 초기화됨:', elements);

    // 주소 검색 버튼
    const addressSearchBtn = document.getElementById('addressSearchBtn');
    if (addressSearchBtn) {
        addressSearchBtn.addEventListener('click', searchAddress);
    }
});