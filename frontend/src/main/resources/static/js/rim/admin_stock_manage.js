// 상품 검색
function searchProducts(page = 0) {
    const mainCategory = document.getElementById('mainCategory').value;
    const searchKeyword = document.getElementById('searchKeyword').value;

    return api.get('/api/admin/products/manage', {
        mainCategory: mainCategory,
        searchKeyword: searchKeyword,
        page: page
    }).then(response => {
        if (response.status === 'SUCCESS') {
            renderProductList(response.body.content);
        }
    });
}

// 입고 처리
function processStockIn(productId, optionId, quantity, costPrice) {
    return api.post(`/api/admin/products/${productId}/stock`, {
        optionId: optionId,
        quantity: quantity,
        costPrice: costPrice,
        isIncrease: true
    }).then(response => {
        if (response.status === 'SUCCESS') {
            alert('입고 처리가 완료되었습니다.');
            searchProducts();
        } else {
            throw new Error(response.message || '입고 처리 실패');
        }
    });
}

// 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
    // 전역 변수 및 상수 정의
    const INBOUND_ITEM_TEMPLATE = document.querySelector('.inbound-item').cloneNode(true);
    const inboundItems = document.querySelector('.inbound-items');

    // 초기화
    initializeEventListeners();

    // 이벤트 리스너 초기화
    function initializeEventListeners() {
        // 카테고리 변경 이벤트
        document.getElementById('mainCategory').addEventListener('change', searchProducts);

        // 검색 버튼 클릭 이벤트
        document.getElementById('searchBtn').addEventListener('click', searchProducts);

        // 엔터키 검색
        document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                searchProducts();
            }
        });

        // 입고 항목 추가 버튼
        document.getElementById('addInboundItem').addEventListener('click', addInboundItem);

        // 입고 처리 버튼
        document.getElementById('saveInbound').addEventListener('click', saveInbound);

        // 첫 번째 입고 항목의 이벤트 리스너 설정
        setupInboundItemListeners(document.querySelector('.inbound-item'));
    }

    // 입고 항목의 이벤트 리스너 설정
    function setupInboundItemListeners(item) {
        const productSelect = item.querySelector('.product-select');
        const optionSelect = item.querySelector('.option-select');
        const removeBtn = item.querySelector('.btn-remove');

        productSelect.addEventListener('change', function() {
            loadProductOptions(this);
        });

        removeBtn.addEventListener('click', function() {
            if (document.querySelectorAll('.inbound-item').length > 1) {
                item.remove();
            } else {
                alert('최소 하나의 입고 항목이 필요합니다.');
            }
        });

        // 숫자 입력 필드 이벤트
        const numericInputs = item.querySelectorAll('.numeric-input');
        numericInputs.forEach(input => {
            input.addEventListener('input', function() {
                this.value = this.value.replace(/[^0-9]/g, '');
            });
        });
    }

    // 상품 검색
    async function searchProducts() {
        const category = document.getElementById('mainCategory').value;
        const keyword = document.getElementById('searchKeyword').value;

        try {
            const response = await fetch(`/api/admin/products/search?category=${category}&keyword=${keyword}`);
            const data = await response.json();

            if (!response.ok) throw new Error(data.message || '상품 목록을 불러오는데 실패했습니다.');

            // 모든 상품 선택 드롭다운 업데이트
            const productSelects = document.querySelectorAll('.product-select');
            productSelects.forEach(select => {
                const currentValue = select.value;
                select.innerHTML = '<option value="">상품 선택</option>';

                data.products.forEach(product => {
                    const option = document.createElement('option');
                    option.value = product.product_id;
                    option.textContent = product.name;
                    select.appendChild(option);
                });

                // 기존 선택값 유지
                if (currentValue && select.querySelector(`option[value="${currentValue}"]`)) {
                    select.value = currentValue;
                }
            });
        } catch (error) {
            console.error('상품 검색 에러:', error);
            alert(error.message);
        }
    }

    // 상품 옵션 로드
    async function loadProductOptions(productSelect) {
        const productId = productSelect.value;
        const optionSelect = productSelect.closest('.inbound-item').querySelector('.option-select');

        optionSelect.innerHTML = '<option value="">옵션 선택</option>';

        if (!productId) return;

        try {
            const response = await fetch(`/api/admin/products/${productId}/options`);
            const data = await response.json();

            if (!response.ok) throw new Error(data.message || '옵션 목록을 불러오는데 실패했습니다.');

            data.options.forEach(option => {
                const optionElement = document.createElement('option');
                optionElement.value = option.option_id;
                optionElement.textContent = option.name;
                optionSelect.appendChild(optionElement);
            });
        } catch (error) {
            console.error('옵션 로드 에러:', error);
            alert(error.message);
        }
    }

    // 새 입고 항목 추가
    function addInboundItem() {
        const newItem = INBOUND_ITEM_TEMPLATE.cloneNode(true);
        setupInboundItemListeners(newItem);
        inboundItems.appendChild(newItem);
    }

    // 입고 처리
    async function saveInbound() {
        const items = document.querySelectorAll('.inbound-item');
        const inboundData = [];

        // 데이터 유효성 검사
        for (const item of items) {
            const optionId = item.querySelector('.option-select').value;
            const quantity = item.querySelector('.quantity').value;
            const costPrice = item.querySelector('.cost-price').value;

            if (!optionId || !quantity || !costPrice) {
                alert('모든 필수 항목을 입력해주세요.');
                return;
            }

            inboundData.push({
                option_id: parseInt(optionId),
                quantity: parseInt(quantity),
                cost_price: parseInt(costPrice)
            });
        }

        try {
            const response = await fetch('/api/admin/products/inbound', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(inboundData)
            });

            const data = await response.json();

            if (!response.ok) throw new Error(data.message || '입고 처리에 실패했습니다.');

            alert('입고 처리가 완료되었습니다.');
            location.reload();
        } catch (error) {
            console.error('입고 처리 에러:', error);
            alert(error.message);
        }
    }
});