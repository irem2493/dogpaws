document.addEventListener('DOMContentLoaded', function() {
    const optionSelect = document.getElementById('productOption');
    const selectedOptions = document.getElementById('selectedOptions');
    const totalPriceElement = document.getElementById('totalPrice');
    const addToCartButton = document.getElementById('addToCart');

    let selectedItems = new Map(); // 선택된 옵션들 관리

    // 기본 가격
    const basePrice = parseInt(document.querySelector('.price').textContent.replace(/[^0-9]/g, ''));
    const productName = document.querySelector('.product-name').textContent;


    // 기본 옵션 자동 선택 및 화면에 추가
    const baseOption = optionSelect.querySelector('option[data-is-base="true"]');
    if (baseOption) {
        const optionId = baseOption.value;
        const optionName = baseOption.getAttribute('data-name');
        const optionPrice = parseInt(baseOption.getAttribute('data-price'));
        const totalOptionPrice = optionPrice;

        // 기본 옵션을 selectedItems에 추가
        selectedItems.set(optionId, {
            name: optionName,
            price: optionPrice,
            quantity: 1,
            isBase: true
        });

        // 화면에 기본 옵션 추가
        addSelectedOption(optionId, optionName, totalOptionPrice);
        updateTotalPrice();
    }


    // 옵션 선택 시
    optionSelect.addEventListener('change', function() {
        if (!this.value) return;

        const selectedOption = this.options[this.selectedIndex];
        const optionId = selectedOption.value;
        const isBaseOption = optionId === baseOptionId;

        if (selectedItems.has(optionId) ||
            (isBaseOption && Array.from(selectedItems.values()).some(item => item.isBase))) {
            alert('이미 선택된 옵션입니다.');
            this.selectedIndex = 0;
            return;
        }

        const optionName = selectedOption.getAttribute('data-name');
        const optionPrice = parseInt(selectedOption.getAttribute('data-price'));
        const totalOptionPrice = optionPrice; // 기본 가격 + 옵션 가격

        addSelectedOption(optionId, optionName, totalOptionPrice);
    });

    // 선택된 옵션 추가
    function addSelectedOption(optionId, name, totalPrice) {
        const optionDiv = document.createElement('div');
        optionDiv.className = 'selected-option';
        optionDiv.dataset.optionId = optionId;

        optionDiv.innerHTML = `
            <div>${name}</div>
            <div class="quantity-control">
                <button type="button" class="decrease">-</button>
                <input type="number" value="1" min="1" readonly>
                <button type="button" class="increase">+</button>
                <span class="option-price">${formatPrice(totalPrice)}원</span>
                <button type="button" class="remove">×</button>
            </div>
        `;

        selectedOptions.appendChild(optionDiv);
        selectedItems.set(optionId, { name, price: totalPrice, quantity: 1 });
        updateTotalPrice();

        // 수량 조절 이벤트
        optionDiv.querySelector('.decrease').addEventListener('click', () => updateQuantity(optionId, -1));
        optionDiv.querySelector('.increase').addEventListener('click', () => updateQuantity(optionId, 1));
        optionDiv.querySelector('.remove').addEventListener('click', () => removeOption(optionId));
    }

    // 수량 업데이트
    function updateQuantity(optionId, change) {
        const item = selectedItems.get(optionId);
        const newQuantity = item.quantity + change;

        if (newQuantity < 1) return;

        item.quantity = newQuantity;
        const optionDiv = document.querySelector(`[data-option-id="${optionId}"]`);
        optionDiv.querySelector('input').value = newQuantity;
        updateTotalPrice();
    }

    // 옵션 제거
    function removeOption(optionId) {
        selectedItems.delete(optionId);
        document.querySelector(`[data-option-id="${optionId}"]`).remove();
        updateTotalPrice();
    }

    // 총 가격 업데이트
    function updateTotalPrice() {
        let total = 0;
        selectedItems.forEach(item => {
            total += item.price * item.quantity;
        });
        totalPriceElement.textContent = formatPrice(total) + '원';
    }

    // 가격 포맷팅
    function formatPrice(price) {
        return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    }

// 장바구니 담기
    addToCartButton.addEventListener('click', async function() {
        try {

            const username = sessionStorage.getItem('username');

            if(!username){
                alert('로그인 후 이용해주세요.');
                window.location.href = '/login';
                return;
            }


            // 사용자 권한 확인
            // if (!await verifyUserRole()) {
            //     return;
            // }

            if(!sessionStorage.getItem('role') === 'ROLE_USER'){
                return;
            }

            // 선택된 상품이 없는 경우
            if (selectedItems.size === 0) {
                alert('상품을 선택해주세요.');
                return;
            }

            const productId = Number(document.getElementById('productId').value);
            const options = [];
            let totalQuantity = 0;  // 전체 수량 계산

            selectedItems.forEach((item, optionId) => {
                options.push({
                    optionId: parseInt(optionId),
                    quantity: item.quantity
                });
                totalQuantity += item.quantity;
            });

            // CartRequestDto 형식에 맞게 데이터 구성
            const requestData = {
                productId: productId,
                quantity: totalQuantity,         // 전체 수량으로 변경
                isBaseProduct: false,
                options: options,
                username: username
            };

            console.log('requestData : ', requestData);

            const response = await api.post('/api/cart', requestData);

            if (response.status === 'SUCCESS') {

                openModal('cartModal');

                // alert(response.body.body.message);
                // 선택된 옵션 초기화
                selectedItems.clear();
                selectedOptions.innerHTML = '';
                updateTotalPrice();
            } else {
                alert('장바구니 추가에 실패했습니다.');
            }
        } catch (error) {
            console.error('장바구니 추가 실패:', error);
            alert('장바구니 추가에 실패했습니다.');
        }
    });

});

// 장바구니 페이지로 이동
function goToCart() {
    window.location.href = '/cart/list';
}