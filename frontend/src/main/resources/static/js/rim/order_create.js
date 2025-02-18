// 결제 위젯 초기화에 필요한 변수들
const clientKey = 'test_ck_ex6BJGQOVDkMPRAZyZ6a3W4w2zNb';
let paymentWidget;

// 요소 초기화 함수
function initializeElements() {
    return {
        orderForm: document.getElementById('orderForm'),
        paymentButton: document.getElementById('payment-button'),
        orderName: document.getElementById('orderName'),
        orderPhone: document.getElementById('orderPhone'),
        shippingZipcode: document.getElementById('shippingZipcode'),
        shippingAddress1: document.getElementById('shippingAddress1'),
        shippingAddress2: document.getElementById('shippingAddress2'),
        shippingMemo: document.getElementById('shippingMemo'),
        addressSearchBtn: document.getElementById('addressSearchBtn'),
        orderSubmitBtn: document.getElementById('orderSubmitBtn'),
        sameAsOrderer: document.getElementById('sameAsOrderer'),
        receiverName: document.getElementById('receiverName'),
        receiverPhone: document.getElementById('receiverPhone'),
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

// 주문 생성 API 호출 함수
async function createOrder(orderData) {
    try {
        const response = await myapi.post('/api/order', orderData);
        return response.data;
        return response.data;
    } catch (error) {
        console.error('주문 생성 실패:', error);
        throw error;
    }
}

// 결제 요청 처리 함수
// 결제 요청 처리 함수 수정
async function requestPayment(qlId, orderData, orderId) {
    try {
        const tossPayments = await loadTossPayments(clientKey);
        paymentWidget = tossPayments.widgets();

        await paymentWidget.setAmount({ value: totalAmount, currency: 'KRW' });

        await Promise.all([
            paymentWidget.renderPaymentMethods({
                selector: '#payment-method',
                variantKey: 'DEFAULT',
            })
        ]);

        // 결제하기 버튼 이벤트
        const paymentButton = document.getElementById('payment-button');
        paymentButton.addEventListener('click', async () => {
            try {
                await paymentWidget.requestPayment({
                    orderId: qlId,
                    orderName: '상품 주문',
                    customerName: orderData.orderName,
                    customerEmail: orderData.email || '',
                    customerMobilePhone: orderData.orderPhone,
                    successUrl: `${window.location.origin}/payments/success?orderId=${orderId}`,
                    failUrl: `${window.location.origin}/payments/fail?orderId=${orderId}`
                });
            } catch (error) {
                console.error('결제 요청 실패:', error);
                alert('결제 처리 중 오류가 발생했습니다.');
            }
        });
    } catch (error) {
        console.error('결제 위젯 초기화 실패:', error);
        alert('결제 시스템을 불러오는데 실패했습니다.');
    }
}

// 주문 데이터 검증 함수
function validateOrderData(elements) {
    // 간단한 검증 로직 추가 (필요 시 확장)
    if (!elements.orderName.value.trim()) {
        alert('주문자명을 입력해주세요.');
        return false;
    }
    if (!elements.orderPhone.value.trim()) {
        alert('연락처를 입력해주세요.');
        return false;
    }
    if (!elements.receiverName.value.trim()) {
        alert('수령인 이름을 입력해주세요.');
        elements.receiverName.focus();
        return false;
    }
    if (!elements.receiverPhone.value.trim()) {
        alert('수령인 연락처를 입력해주세요.');
        elements.receiverPhone.focus();
        return false;
    }
    if (!elements.shippingZipcode.value.trim()) {
        alert('우편번호를 입력해주세요.');
        return false;
    }
    if (!elements.shippingAddress1.value.trim() || !elements.shippingAddress2.value.trim()) {
        alert('주소를 모두 입력해주세요.');
        return false;
    }
    return true;
}

// 초기화 및 이벤트 리스너 설정
window.addEventListener('DOMContentLoaded', async () => {
    console.log('DOM 로드됨');

    // DOM 요소 초기화
    const elements = initializeElements();
    console.log('DOM 요소 초기화됨:', elements);

    // 주문자 정보와 동일 체크박스 이벤트
    elements.sameAsOrderer.addEventListener('change', (e) => {
        if (e.target.checked) {
            elements.receiverName.value = elements.orderName.value;
            elements.receiverPhone.value = elements.orderPhone.value;
            elements.receiverName.disabled = true;
            elements.receiverPhone.disabled = true;
        } else {
            elements.receiverName.disabled = false;
            elements.receiverPhone.disabled = false;
        }
    });

    // 주소 검색 버튼 이벤트 설정
    const addressSearchBtn = elements.addressSearchBtn;
    if (addressSearchBtn) {
        addressSearchBtn.addEventListener('click', searchAddress);
    }

    // 주문하기 버튼 클릭 이벤트 설정
    elements.orderSubmitBtn.addEventListener('click', async () => {
        if (!validateOrderData(elements)) return;

        // 장바구니 데이터 확인
        console.log('cartItemsData before:', cartItemsData);

        // form 데이터 수집
        const orderData = {
            username: document.querySelector('input[name="username"]').value,
            total_price: Number(totalAmount),
            order_name: elements.orderName.value,
            order_phone: elements.orderPhone.value,
            shipping_zipcode: elements.shippingZipcode.value,
            shipping_address1: elements.shippingAddress1.value,
            shipping_address2: elements.shippingAddress2.value,
            shipping_memo: elements.shippingMemo.value,
            receiver_name: elements.receiverName.value,
            receiver_phone: elements.receiverPhone.value,
            cart_items: cartItemsData,
        };


        console.log('orderData >> ', orderData);
        try {
            // 주문 생성 API 호출
            // const orderResponse = await createOrder(orderData);
            // console.log('주문 생성 성공:', orderResponse);

            // 결제 모달 열기
            openModal('paymentModal');

            // 주문번호(qlId) 생성
            const qlId = generateTossOrderId();

            // 결제 요청 처리
            await requestPayment(qlId, orderData);
        } catch (error) {
            alert('주문 생성 또는 결제 요청 중 오류가 발생했습니다.');
        }
    });
});