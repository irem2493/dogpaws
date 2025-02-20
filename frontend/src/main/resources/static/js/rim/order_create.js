// 전역 변수 선언
let elements;

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

// 주소 검색
function searchAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분
            let fullAddress = ''; // 최종 주소
            let extraAddress = ''; // 조합형 주소 변수

            // 사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다
            if (data.userSelectedType === 'R') { // 도로명 주소
                fullAddress = data.roadAddress;
            } else { // 지번 주소
                fullAddress = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddress += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddress += (extraAddress !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 조합형주소의 유무에 따라 양쪽에 괄호를 추가하여 최종 주소를 만든다.
                if (extraAddress !== '') {
                    extraAddress = ' (' + extraAddress + ')';
                }

                // 조합된 참고항목을 주소 필드에 추가한다.
                fullAddress += extraAddress;
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.querySelector('#shippingZipcode').value = data.zonecode;
            document.querySelector('#shippingAddress1').value = fullAddress;

            // 커서를 상세주소 필드로 이동한다.
            document.querySelector('#shippingAddress2').focus();
        }
    }).open();
}



// 토스페이먼츠 위젯 초기화
async function main() {
    const clientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    const customerKey = generateRandomString();
    const tossPayments = TossPayments(clientKey);

    const widgets = tossPayments.widgets({
        customerKey,
    });

    // 결제 금액 설정
    await widgets.setAmount({
        currency: "KRW",
        value: Number(totalAmount),
    });

    // 결제 UI 렌더링
    await Promise.all([
        widgets.renderPaymentMethods({
            selector: "#payment-method",
            variantKey: "DEFAULT"
        }),
        widgets.renderAgreement({
            selector: "#agreement",
            variantKey: "AGREEMENT",
        }),
    ]);

    // 결제하기 버튼에 이벤트 연결
    const paymentButton = document.getElementById('payment-button');
    paymentButton.addEventListener('click', () => processOrder(widgets));

    return widgets;
}

// 주문 및 결제 처리
async function processOrder(widgets) {

    console.log('cartItemsData>>',cartItemsData);

    // 2. 주문 데이터 생성
    const orderData = {
        username: sessionStorage.getItem('username'),
        total_price: Number(totalAmount),
        order_name: elements.orderName.value,
        order_phone: elements.orderPhone.value,
        shipping_zipcode: elements.shippingZipcode.value,
        shipping_address1: elements.shippingAddress1.value,
        shipping_address2: elements.shippingAddress2.value,
        shipping_memo: elements.shippingMemo.value,
        receiver_name: elements.receiverName.value,
        receiver_phone: elements.receiverPhone.value,
        order_items: convertCartItemsToOrderItems(cartItemsData),
        cart_item_ids: cartItemsData.map(item => item.cart_item_id)
    };

    console.log('orderData >>', orderData);

    // 3. 먼저 주문 생성 (상태: READY)
    const order = await api.post('/api/order', orderData);

    console.log('order',order);

    const qlId = order.body.body.ql_id; // 서버에서 생성된 주문번호

    try {

        console.log('orderData.totalPrice', orderData.totalPrice);
        console.log('orderData.orderPhone', orderData.orderPhone);
        console.log('cartItemsData',cartItemsData);

        // 3. 결제 요청
        const paymentResult = await widgets.requestPayment({
            orderId: qlId,
            orderName: `${cartItemsData[0].product_name} 외 ${cartItemsData.length}건`,
            successUrl: `${window.location.origin}/orders/success`,  // 성공 시 리다이렉트 URL
            failUrl: `${window.location.origin}/orders/fail`,        // 실패 시 리다이렉트 URL
            customerEmail: orderData.username,
            customerName: orderData.orderName,
            customerMobilePhone: orderData.orderPhone,
        });

    } catch (error) {
        console.error('주문 처리 중 오류 발생:', error);
        if (error.code === 'USER_CANCEL') {
            alert('결제가 취소되었습니다.');
        } else {
            alert('결제 처리 중 오류가 발생했습니다.');
        }
    }
}

function generateRandomString() {
    return window.btoa(Math.random()).slice(0, 20);
}
// 총 수량 계산 함수
function getTotalQuantity(options) {
    return options.reduce((sum, option) => sum + option.quantity, 0);
}

// 총 가격 계산 함수
function getTotalPrice(cartItem) {
    const optionsPrice = cartItem.cart_options.reduce((sum, option) =>
        sum + (option.option_price * option.quantity), 0);
    return optionsPrice + cartItem.product_price;  // 기본 상품 가격 + 옵션 가격
}

// 장바구니 아이템을 주문 아이템으로 변환
function convertCartItemsToOrderItems(cartItems) {
    console.log('Converting cart items:', cartItems);

    return cartItems.map(cartItem => ({
        order_item_id : cartItem.cart_item_id,
        product_id: cartItem.product_id,
        product_name : cartItem.product_name,
        manufacturer : cartItem.manufacturer,
        image_url : cartItem.image_url,
        amount: getTotalQuantity(cartItem.cart_options),  // 모든 옵션의 수량 합계
        item_price: getTotalPrice(cartItem),  // 모든 옵션의 가격 합계
        options: cartItem.cart_options.map(option => ({
            option_id: option.option_id,
            option_item_id: option.option_item_id,
            option_name: option.option_name,
            option_price:  cartItem.product_price + option.option_price,
            quantity: option.quantity
        })),
    }));

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


// 배송메모 직접입력 처리
document.getElementById('shippingMemo').addEventListener('change', function() {
    const directInput = document.getElementById('directShippingMemo');
    directInput.style.display = this.value === 'direct' ? 'block' : 'none';
});

// 배송정보 저장
function saveShippingInfo() {
    // 필수 입력값 검증
    const receiverName = document.querySelector('#receiverName').value;
    const receiverPhone = document.querySelector('#receiverPhone').value;
    const shippingZipcode = document.querySelector('#shippingZipcode').value;
    const shippingAddress1 = document.querySelector('#shippingAddress1').value;
    const shippingAddress2 = document.querySelector('#shippingAddress2').value;

    // 필수 입력값 검증
    if (!receiverName) {
        alert('받는 사람을 입력해주세요.');
        return;
    }
    if (!receiverPhone) {
        alert('연락처를 입력해주세요.');
        return;
    }
    if (!shippingZipcode || !shippingAddress1) {
        alert('주소를 입력해주세요.');
        return;
    }
    if (!shippingAddress2) {
        alert('상세주소를 입력해주세요.');
        return;
    }

    // 화면에 표시
    document.querySelector('#displayReceiverName').textContent = receiverName;
    document.querySelector('#displayReceiverPhone').textContent = receiverPhone;
    document.querySelector('#displayShippingZipcode').textContent = shippingZipcode;
    document.querySelector('#displayShippingAddress1').textContent = shippingAddress1;
    document.querySelector('#displayShippingAddress2').textContent = shippingAddress2;

    // 모달 닫기

    // 모달 닫기
    // 모달 닫기
    const modalElement = document.querySelector('#shippingModal');
    const closeButton = modalElement.querySelector('.pawsModal-close');
    closeModal(closeButton);
}



// 초기화 및 이벤트 리스너 설정
window.addEventListener('DOMContentLoaded', async () => {
    console.log('DOM 로드됨');
    // DOM 요소 초기화
    elements = initializeElements();
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

    console.log('elements', elements);

    // 주소 검색 버튼 이벤트 설정
    const addressSearchBtn = elements.addressSearchBtn;
    if (addressSearchBtn) {
        addressSearchBtn.addEventListener('click', searchAddress);
    }



    // 주문하기 버튼 클릭 이벤트
    elements.orderSubmitBtn.addEventListener('click', async () => {
        // 1. 주문 데이터 검증
        if (!validateOrderData(elements)) return;

        // 결제 모달 열기
        openModal('paymentModal');
        // 토스페이먼츠 위젯 초기화
        await main();
    });
});