document.addEventListener('DOMContentLoaded', function() {
    const optionSelect = document.getElementById('productOption');
    const selectedOptions = document.getElementById('selectedOptions');
    const totalPriceElement = document.getElementById('totalPrice');
    const addToCartButton = document.getElementById('addToCart');

    let selectedItems = new Map(); // 선택된 옵션들 관리

    // 기본 가격
    const basePrice = parseInt(document.querySelector('.price').textContent.replace(/[^0-9]/g, ''));

    // 옵션 선택 시
    optionSelect.addEventListener('change', function() {
        if (!this.value) return;

        if (selectedItems.has(this.value)) {
            alert('이미 선택된 옵션입니다.');
            return;
        }

        const selectedOption = this.options[this.selectedIndex];
        const optionId = selectedOption.value;
        const optionName = selectedOption.getAttribute('data-name');
        const optionPrice = parseInt(selectedOption.getAttribute('data-price'));
        const totalOptionPrice = basePrice + optionPrice; // 기본 가격 + 옵션 가격

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
                totalQuantity += item.quantity;  // 각 옵션의 수량을 합산
                options.push({
                    optionId: parseInt(optionId),
                    quantity: item.quantity      // 각 옵션의 개별 수량
                });
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
                alert(response.body.body.message);
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