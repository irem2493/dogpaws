<<<<<<< HEAD

    // 전역 변수로 옵션 데이터 저장
    const availableOptionsMap = {};
    const cartSummary = /*[[${cartSummary}]]*/ null;

    console.log('cartSummary', cartSummary);
    console.log('사용가능 옵션', cartSummary.cart_items[0].availableOptions);
    // 총 금액 계산 함수를 전역으로 이동
    function updateTotalPrice() {
    const optionList = document.getElementById('optionList');
    const options = Array.from(optionList.querySelectorAll('.option-item'));
    let total = 0;

    options.forEach((option, index) => {
    const quantity = parseInt(option.querySelector('.quantity-input').value);
    const optionPrice = parseInt(option.dataset.optionPrice) || 0;

    if (index === 0) {
    // 첫 번째 옵션은 기본가격만 적용
    total += basePrice * quantity;
} else {
    // 두 번째 옵션부터는 옵션가격만 적용
    total += optionPrice * quantity;
}
});

    document.getElementById('modalTotalPrice').textContent = total.toLocaleString();
}

    // 체크박스 변경 시 선택된 상품 정보 업데이트
    function updateSelectedInfo() {
    const checkedItems = document.querySelectorAll('input[name="cartItemIds"]:checked');
    const selectedCount = checkedItems.length;
    let totalProductPrice = 0;
    let deliveryFee = selectedCount > 0 ? 3000 : 0; // 선택된 상품이 있을 경우 배송비 3000원 추가

    checkedItems.forEach(item => {
    const cardBody = item.closest('.card-body');
    const priceText = cardBody.querySelector('.text-end strong').textContent;
    totalProductPrice += parseInt(priceText.replace(/,/g, ''));
});

    const totalPrice = totalProductPrice + deliveryFee;

    // 금액 표시 업데이트
    document.getElementById('totalProductPrice').textContent = totalProductPrice.toLocaleString();
    document.getElementById('deliveryFee').textContent = deliveryFee.toLocaleString();
    document.getElementById('totalOrderPrice').textContent = totalPrice.toLocaleString();

    // 선택된 상품 정보 업데이트
    document.getElementById('selectedItemCount').textContent = selectedCount;
    document.getElementById('selectedTotalPrice').textContent = totalPrice.toLocaleString();
}

    // 전체 선택 체크박스 이벤트 수정
    document.getElementById('selectAll').addEventListener('change', function () {
    const checkboxes = document.getElementsByClassName('item-checkbox');
    for (let checkbox of checkboxes) {
    checkbox.checked = this.checked;
}
    updateSelectedInfo();
});

    // 개별 체크박스 이벤트 수정
    const itemCheckboxes = document.getElementsByClassName('item-checkbox');
    for (let checkbox of itemCheckboxes) {
    checkbox.addEventListener('change', function () {
        const selectAll = document.getElementById('selectAll');
        const allChecked = Array.from(itemCheckboxes).every(cb => cb.checked);
        selectAll.checked = allChecked;
        updateSelectedInfo();
    });
}

    // 페이지 로드 시 초기 선택 정보 업데이트
    document.addEventListener('DOMContentLoaded', function() {
    // 전체 선택 체크박스 체크
    const selectAllCheckbox = document.getElementById('selectAll');
    if (selectAllCheckbox) {
    selectAllCheckbox.checked = true;
}

    // 모든 개별 체크박스 체크
    const checkboxes = document.getElementsByClassName('item-checkbox');
    for (let checkbox of checkboxes) {
    checkbox.checked = true;
}

    // 선택된 상품 정보 업데이트
    updateSelectedInfo();
});

    // 수량 업데이트 함수
    async function updateQuantity(cartItemId, optionId, change, input) {
    try {
    const newQuantity = parseInt(input.value) + change;

    if (newQuantity < 1) {
    alert('수량은 1개 이상이어야 합니다.');
    return;
}

    input.value = newQuantity;
} catch (error) {
    console.error('수량 업데이트 실패:', error);
    alert('수량 변경에 실패했습니다.');
}
}

    // 옵션 삭제 함수도 수정
    optionList.querySelectorAll('.temp-delete-btn').forEach(btn => {
    btn.addEventListener('click', function() {
        const optionRow = this.closest('.option-item');
        const remainingOptions = optionList.querySelectorAll('.option-item');

        if (remainingOptions.length <= 1) {
            alert('최소 1개의 옵션은 유지해야 합니다.');
            return;
        }

        if (confirm('이 옵션을 삭제하시겠습니까?')) {
            // 삭제할 옵션 정보 저장
            const cartItemId = optionRow.dataset.cartItemId;
            const optionId = optionRow.dataset.optionId;

            // 삭제 대기 목록에 추가
            if (!window.deletions) window.deletions = [];
            window.deletions.push({ cartItemId, optionId });

            // UI에서 임시 제거
            optionRow.remove();
            updateTotalPrice(); // 삭제 후 총 금액 업데이트
        }
    });
});

    // 장바구니 상품 삭제
    function deleteCartItem(cartItemId) {
    if (confirm('선택한 상품을 장바구니에서 삭제하시겠습니까?')) {
    myapi.delete(`/api/cart/selected`, {
    data: [cartItemId]
})
    .then(() => {
    alert('삭제되었습니다.');
    location.reload();
})
    .catch(error => {
    console.error('삭제 실패:', error);
    alert('삭제에 실패했습니다.');
});
}
}

    async function updateCartOption() {
    try {
    const modalContent = document.getElementById('optionList');
    const options = modalContent.querySelectorAll('.option-item');

    // 최소 1개 옵션 체크
    if (options.length < 1) {
    alert('최소 1개의 옵션이 필요합니다.');
    return;
}

    // 1. 먼저 삭제 작업 수행
    if (window.deletions && window.deletions.length > 0) {
    for (const deletion of window.deletions) {
    try {
    await myapi.delete(`/api/cart/option/${deletion.cartItemId}/${deletion.optionId}`);
} catch (error) {
    console.error('옵션 삭제 실패:', error);
    throw new Error('옵션 삭제 중 오류가 발생했습니다.');
}
}
}

    // 2. 새로운 옵션 추가
    if (window.additions && window.additions.length > 0) {
    for (const addition of window.additions) {
    try {
    await myapi.post(`/api/cart/option/${addition.cartItemId}`, {
    optionId: addition.optionId,
    quantity: addition.quantity
});
} catch (error) {
    console.error('옵션 추가 실패:', error);
    throw new Error('옵션 추가 중 오류가 발생했습니다.');
}
}
}

    // 3. 기존 옵션 수량 업데이트
    const updates = [];
    options.forEach(option => {
    const cartItemId = option.dataset.cartItemId;
    const optionId = option.dataset.optionId;
    const quantity = parseInt(option.querySelector('.quantity-input').value);

    // 새로 추가된 옵션이 아닌 경우에만 업데이트
    const isNewlyAdded = window.additions?.some(
    addition => addition.cartItemId === cartItemId && addition.optionId === optionId
    );
    if (!isNewlyAdded) {
    updates.push({
    cartItemId: cartItemId,
    optionId: optionId,
    quantity: quantity
});
}
});

    // 수량 업데이트 요청 실행
    for (const update of updates) {
    try {
    await myapi.put('/api/cart/option/quantity', update);
} catch (error) {
    console.error('수량 업데이트 실패:', error);
    throw new Error('수량 업데이트 중 오류가 발생했습니다.');
}
}

    alert('수정이 완료되었습니다.');
    location.reload();
} catch (error) {
    console.error('수정 실패:', error);
    console.error('에러 상세:', error.response?.data || error.message);
    alert(error.message || '수정에 실패했습니다. 다시 시도해주세요.');
}
}

    // 주문 전 유효성 검사
    function validateOrder() {
    const checkedItems = document.querySelectorAll('input[name="cartItemIds"]:checked');
    if (checkedItems.length === 0) {
    alert('주문할 상품을 선택해주세요.');
    return false;
}
    return true;
}

    // DOM이 로드된 후 실행
    document.addEventListener('DOMContentLoaded', function() {
    const optionList = document.getElementById('optionList');

    // 수정 버튼 클릭 이벤트 리스너
    document.querySelectorAll('.modify-option-btn').forEach(button => {
    button.addEventListener('click', function(e) {
    e.preventDefault();
    // 모달 열 때 전역 변수 초기화
    window.additions = [];
    window.deletions = [];

    const container = this.closest('.option-container');
    basePrice = parseInt(container.dataset.basePrice);

    console.log('container:', container);

    const cartItemId = container.dataset.cartItemId;
    console.log('cartItemId:', cartItemId);

    const productName = container.dataset.productName;
    console.log('productName:', productName);

    const imageUrl = container.dataset.imageUrl;
    console.log('imageUrl:', imageUrl);

    const productId = container.dataset.productId;
    console.log('productId:', productId);


    let currentItem = null;
    // cart_items 배열 순회
    for(let i = 0; i < cartSummary.cart_items.length; i++) {
    console.log('비교:', cartSummary.cart_items[i].product_id, productId);
    if(String(cartSummary.cart_items[i].product_id) === String(productId)) {
    currentItem = cartSummary.cart_items[i];
    break;
}
}

    console.log('현재 상품:', currentItem);

    // 총 금액 계산 함수 제거 (전역으로 이동했으므로)

    // 초기 총 금액 계산 및 표시
    updateTotalPrice();

    // 옵션 데이터 수집
    const options = Array.from(container.querySelectorAll('.option-data')).map(optionDiv => ({
    optionId: optionDiv.dataset.optionId,
    optionName: optionDiv.dataset.optionName,
    optionPrice: parseInt(optionDiv.dataset.optionPrice) || 0,
    quantity: parseInt(optionDiv.dataset.quantity)
}));

    // 모달 내용 설정
    const modalProductInfo = document.getElementById('modalProductInfo');
    modalProductInfo.innerHTML = `
                        <div class="d-flex align-items-start mb-3">
                            <img src="${imageUrl}" alt="상품 이미지" style="width: 100px; height: 100px; object-fit: cover; margin-right: 15px;">
                            <div>
                                <h5>${productName}</h5>
                                <p class="mb-1">기본 가격: ${basePrice.toLocaleString()}원</p>
                            </div>
                        </div>
                    `;


    optionList.innerHTML = `
                        ${options.map((option, index) => `
                            <div class="d-flex align-items-center mb-2 option-item"
                                 data-cart-item-id="${cartItemId}"
                                 data-option-id="${option.optionId}"
                                 data-option-price="${index === 0 ? 0 : option.optionPrice}">
                                <span class="me-2">${option.optionName}</span>
                                <span class="text-muted me-2">
                                    ${index === 0 ? '' : option.optionPrice ? `(+${option.optionPrice.toLocaleString()}원)` : ''}
                                </span>
                                <div class="input-group" style="width: 150px;">
                                    <button type="button" class="btn btn-outline-secondary btn-sm quantity-btn"
                                            data-change="-1">-</button>
                                    <input type="number" class="form-control form-control-sm text-center quantity-input"
                                           value="${option.quantity}" readonly>
                                    <button type="button" class="btn btn-outline-secondary btn-sm quantity-btn"
                                            data-change="1">+</button>
                                </div>
                                <button type="button" class="btn-icon temp-delete-btn ms-2" style="display: ${options.length > 1 ? 'block' : 'none'}">
                                    <img src="/img/icon/button/cancel.svg" alt="삭제" />
                                </button>
                            </div>
                        `).join('')}
                    `;

    // 수량 변경 버튼 이벤트
    optionList.querySelectorAll('.quantity-btn').forEach(btn => {
    btn.addEventListener('click', function() {
    const input = this.parentElement.querySelector('input');
    const change = parseInt(this.dataset.change);
    const newQuantity = parseInt(input.value) + change;

    if (newQuantity < 1) {
    alert('수량은 1개 이상이어야 합니다.');
    return;
}

    input.value = newQuantity;
    updateTotalPrice(); // 수량 변경 시 총 금액 업데이트
});
});

    // 삭제 버튼 이벤트
    optionList.querySelectorAll('.temp-delete-btn').forEach(btn => {
    btn.addEventListener('click', function() {
    const optionRow = this.closest('.option-item');
    const remainingOptions = optionList.querySelectorAll('.option-item');

    if (remainingOptions.length <= 1) {
    alert('최소 1개의 옵션은 유지해야 합니다.');
    return;
}

    if (confirm('이 옵션을 삭제하시겠습니까?')) {
    // 삭제할 옵션 정보 저장
    const cartItemId = optionRow.dataset.cartItemId;
    const optionId = optionRow.dataset.optionId;

    // 삭제 대기 목록에 추가
    if (!window.deletions) window.deletions = [];
    window.deletions.push({ cartItemId, optionId });

    // UI에서 임시 제거
    optionRow.remove();
    updateTotalPrice();
}
});
});

    // 새 옵션 선택 셀렉트박스 초기화
    const newOptionSelect = document.getElementById('newOptionSelect');
    newOptionSelect.innerHTML = '<option value="">새 옵션 추가하기</option>';


    // 사용 가능한 옵션 목록 추가
    if (currentItem && currentItem.availableOptions) {
    currentItem.availableOptions.forEach(option => {
    const optEl = document.createElement('option');
    optEl.value = option.optionId;
    optEl.dataset.price = option.optionPrice;
    optEl.textContent = `${option.optionName} ${option.optionPrice > 0 ?
    `(+${option.optionPrice.toLocaleString()}원)` : ''}`;
    newOptionSelect.appendChild(optEl);
});
}


    // 초기 총 금액 계산
    updateTotalPrice();
    // 모달 열기 (modal.js의 함수 사용)
    openModal('cartOptionModal', false);
});
});




});


    // newOptionSelect 이벤트 리스너 수정
    document.getElementById('newOptionSelect').addEventListener('change', function() {
    if (this.value) {
    addSelectedOption();
}
});

    // 삭제 버튼 표시 여부를 업데이트하는 함수 추가
    function updateDeleteButtons() {
    const optionList = document.getElementById('optionList');
    const options = optionList.querySelectorAll('.option-item');
    const hasMultipleOptions = options.length > 1;

    options.forEach(option => {
    const deleteBtn = option.querySelector('.temp-delete-btn');
    if (deleteBtn) {
    deleteBtn.style.display = hasMultipleOptions ? 'block' : 'none';
}
});
}

    // 새 옵션 추가 함수 수정
    async function addSelectedOption() {
    const select = document.getElementById('newOptionSelect');
    const optionList = document.getElementById('optionList');
    const option = select.options[select.selectedIndex];

    if (!option.value) return;

    const optionPrice = parseInt(option.dataset.price) || 0;
    const cartItemId = document.querySelector('.option-item')?.dataset.cartItemId ||
    document.querySelector('.option-container')?.dataset.cartItemId;

    // 이미 선택된 옵션인지 확인
    const existingOption = optionList.querySelector(`[data-option-id="${option.value}"]`);
    if (existingOption) {
    // 이미 선택된 옵션이면 수량만 증가
    const quantityInput = existingOption.querySelector('.quantity-input');
    quantityInput.value = parseInt(quantityInput.value) + 1;
    updateTotalPrice();
    select.value = ''; // select 초기화
    return;
}

    // 만약 이 옵션이 이전에 삭제된 옵션이라면 deletions 배열에서 제거
    if (window.deletions) {
    window.deletions = window.deletions.filter(
    item => !(item.cartItemId === cartItemId && item.optionId === option.value)
    );
}

    // 새로 추가된 옵션 정보를 additions 배열에 추가
    if (!window.additions) window.additions = [];
    window.additions.push({
    cartItemId: cartItemId,
    optionId: option.value,
    quantity: 1
});

    // 현재 옵션 개수 확인
    const currentOptions = optionList.querySelectorAll('.option-item');
    const willHaveMultipleOptions = currentOptions.length + 1 > 1;

    // 새 옵션 UI 추가
    const newOptionHtml = `
        <div class="d-flex align-items-center mb-2 option-item"
             data-cart-item-id="${cartItemId}"
             data-option-id="${option.value}"
             data-option-price="${optionPrice}">
            <span class="me-2">${option.text}</span>
            <div class="input-group" style="width: 150px;">
                <button type="button" class="btn btn-outline-secondary btn-sm quantity-btn"
                        data-change="-1">-</button>
                <input type="number" class="form-control form-control-sm text-center quantity-input"
                       value="1" readonly>
                <button type="button" class="btn btn-outline-secondary btn-sm quantity-btn"
                        data-change="1">+</button>
            </div>
            <button type="button" class="btn-icon temp-delete-btn ms-2" style="display: ${willHaveMultipleOptions ? 'block' : 'none'}">
                <img src="/img/icon/button/cancel.svg" alt="삭제" />
            </button>
        </div>
    `;

    optionList.insertAdjacentHTML('beforeend', newOptionHtml);

    // 새로 추가된 옵션에 이벤트 리스너 추가
    const newOption = optionList.lastElementChild;
    addOptionEventListeners(newOption);

    // 삭제 버튼 표시 여부 업데이트
    updateDeleteButtons();

    // 총 금액 업데이트
    updateTotalPrice();

    // select 초기화
    select.value = '';
}

    // 옵션 이벤트 리스너 추가 함수 수정
    function addOptionEventListeners(optionElement) {
    // 수량 변경 버튼 이벤트
    optionElement.querySelectorAll('.quantity-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input');
            const change = parseInt(this.dataset.change);
            const newQuantity = parseInt(input.value) + change;

            if (newQuantity < 1) {
                alert('수량은 1개 이상이어야 합니다.');
                return;
            }

            input.value = newQuantity;

            // 새로 추가된 옵션의 수량 업데이트
            const optionRow = this.closest('.option-item');
            const cartItemId = optionRow.dataset.cartItemId;
            const optionId = optionRow.dataset.optionId;

            if (window.additions) {
                const addedOption = window.additions.find(
                    item => item.cartItemId === cartItemId && item.optionId === optionId
                );
                if (addedOption) {
                    addedOption.quantity = newQuantity;
                }
            }

            updateTotalPrice();
        });
    });

    // 삭제 버튼 이벤트
    const deleteBtn = optionElement.querySelector('.temp-delete-btn');
    if (deleteBtn) {
    deleteBtn.addEventListener('click', function() {
    const optionRow = this.closest('.option-item');
    const remainingOptions = document.querySelectorAll('.option-item');

    if (remainingOptions.length <= 1) {
    alert('최소 1개의 옵션은 유지해야 합니다.');
    return;
}

    if (confirm('이 옵션을 삭제하시겠습니까?')) {
    const cartItemId = optionRow.dataset.cartItemId;
    const optionId = optionRow.dataset.optionId;

    // 만약 이 옵션이 방금 추가된 옵션이라면 additions 배열에서 제거
    if (window.additions) {
    window.additions = window.additions.filter(
    item => !(item.cartItemId === cartItemId && item.optionId === optionId)
    );
}

    // 기존 옵션이라면 deletions 배열에 추가
    if (!window.deletions) window.deletions = [];
    window.deletions.push({ cartItemId, optionId });

    optionRow.remove();
    updateDeleteButtons(); // 삭제 후 버튼 표시 여부 업데이트
    updateTotalPrice();
}
});
}
}

    // 기존의 수량 변경 버튼 이벤트 리스너 부분 수정
    optionList.querySelectorAll('.option-item').forEach(option => {
    addOptionEventListeners(option);
});

    // 모달 닫힐 때 전역 변수 초기화를 위한 이벤트 리스너 추가
    document.addEventListener('DOMContentLoaded', function() {
    const modalOverlay = document.querySelector('.pawsModal-overlay');
    if (modalOverlay) {
    modalOverlay.addEventListener('click', function(e) {
    if (e.target === modalOverlay) {
    window.additions = [];
    window.deletions = [];
}
});
}

    // ESC 키 이벤트에 대한 처리도 추가
    document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
    window.additions = [];
    window.deletions = [];
}
});
});

    // 선택된 상품 삭제
    async function deleteSelectedItems() {
    const checkedItems = document.querySelectorAll('input[name="cartItemIds"]:checked');
    if (checkedItems.length === 0) {
    alert('삭제할 상품을 선택해주세요.');
    return;
}

    if (!confirm('선택한 상품을 장바구니에서 삭제하시겠습니까?')) {
    return;
}
    const username = sessionStorage.getItem('username');
    const cartItemIds = Array.from(checkedItems).map(item => item.value);

    try {
    const response = await fetch('http://localhost:8080/api/cart/selected', {
    method: 'DELETE',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify({
    username: username,
    cartItemIds: cartItemIds
})
});

    console.log(response);
    if (response.status === 200) {
    alert('선택한 상품이 삭제되었습니다.');
    location.reload();
} else {
    throw new Error(result.body.message || '삭제 실패');
}
} catch (error) {
    console.error('장바구니 상품 삭제 중 오류 발생:', error);
    alert('상품 삭제 중 오류가 발생했습니다.');
}
}
=======
document.addEventListener('DOMContentLoaded', function() {
    const selectAll = document.getElementById('selectAll');
    const itemCheckboxes = document.querySelectorAll('.item-checkbox');

    // 전체 선택 처리
    selectAll.addEventListener('change', function() {
        itemCheckboxes.forEach(checkbox => {
            checkbox.checked = selectAll.checked;
        });
        updateTotalPrice();
    });

    // 수량 변경 처리
    document.querySelectorAll('.quantity-control').forEach(control => {
        const decreaseBtn = control.querySelector('.decrease');
        const increaseBtn = control.querySelector('.increase');
        const quantityInput = control.querySelector('input');
        const cartItemId = control.closest('.cart-item').querySelector('.item-checkbox').value;

        decreaseBtn.addEventListener('click', () => updateQuantity(cartItemId, -1));
        increaseBtn.addEventListener('click', () => updateQuantity(cartItemId, 1));
    });

    // 상품 삭제 처리
    document.querySelectorAll('.delete-item').forEach(button => {
        button.addEventListener('click', async function() {
            const cartItemId = this.dataset.itemId;
            try {
                const response = await fetch(`/api/cart/${cartItemId}`, {
                    method: 'DELETE'
                });
                if (response.ok) {
                    this.closest('.cart-item').remove();
                    updateTotalPrice();
                }
            } catch (error) {
                console.error('삭제 실패:', error);
            }
        });
    });

    // 수량 업데이트 함수
    async function updateQuantity(cartItemId, change) {
        const quantityControl = document.querySelector(`[data-item-id="${cartItemId}"]`)
            .closest('.cart-item')
            .querySelector('.quantity-control input');
        const newQuantity = parseInt(quantityControl.value) + change;

        if (newQuantity < 1) return;

        try {
            const response = await fetch(`/api/cart/${cartItemId}/quantity?quantity=${newQuantity}`, {
                method: 'PATCH'
            });

            if (response.ok) {
                quantityControl.value = newQuantity;
                updateTotalPrice();
            }
        } catch (error) {
            console.error('수량 변경 실패:', error);
        }
    }

    // 총 가격 업데이트
    function updateTotalPrice() {
        let total = 0;
        let itemCount = 0;

        document.querySelectorAll('.cart-item').forEach(item => {
            if (item.querySelector('.item-checkbox').checked) {
                const price = parseInt(item.querySelector('.item-total').textContent.replace(/[^0-9]/g, ''));
                total += price;
                itemCount++;
            }
        });

        const shippingFee = 3000;
        document.getElementById('totalProductPrice').textContent = total.toLocaleString() + '원';
        document.getElementById('totalOrderPrice').textContent = (total + shippingFee).toLocaleString() + '원';
        document.getElementById('totalItemCount').textContent = itemCount;
    }
});
>>>>>>> origin/REQ-68-관리자
