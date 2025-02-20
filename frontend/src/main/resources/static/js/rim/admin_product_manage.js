document.addEventListener('DOMContentLoaded', function() {
    // 초기 데이터 로드
    loadProducts();

    // 이벤트 리스너 등록
    document.getElementById('mainCategory').addEventListener('change', handleMainCategoryChange);
    document.getElementById('subCategory').addEventListener('change', loadProducts);
    document.getElementById('status').addEventListener('change', loadProducts);
    document.getElementById('stockSort').addEventListener('change', loadProducts);
    document.getElementById('searchBtn').addEventListener('click', loadProducts);
});

// 상품 목록 로드
function loadProducts() {
    const params = {
        mainCategory: document.getElementById('mainCategory').value,
        subCategory: document.getElementById('subCategory').value,
        status: document.getElementById('status').value,
        sortBy: document.getElementById('stockSort').value,
        searchKeyword: document.getElementById('searchKeyword').value,
        page: 0,
        size: 10
    };

    api.get('/api/admin/products/manage', params)
        .then(response => {
            if (response.status === 'SUCCESS') {
                renderProductList(response.body.content);
            }
        })
        .catch(error => {
            console.error('상품 목록 로드 실패:', error);
        });
}

// 대분류 카테고리 변경 처리
function handleMainCategoryChange() {
    const mainCategory = document.getElementById('mainCategory').value;
    const subCategory = document.getElementById('subCategory');

    if (mainCategory === 'F') { // 사료 카테고리
        subCategory.style.display = 'inline-block';
    } else {
        subCategory.style.display = 'none';
        subCategory.value = '';
    }

    loadProducts();
}

// 상품 목록 렌더링
function renderProductList(products) {
    const tbody = document.getElementById('productList');
    tbody.innerHTML = '';

    products.forEach(product => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${product.name}</td>
            <td>${product.price.toLocaleString()}원</td>
            <td>
                <select class="status-select" data-product-id="${product.productId}">
                    <option value="O" ${product.status === 'O' ? 'selected' : ''}>판매중</option>
                    <option value="S" ${product.status === 'S' ? 'selected' : ''}>품절</option>
                    <option value="D" ${product.status === 'D' ? 'selected' : ''}>판매중지</option>
                </select>
            </td>
            <td class="stock-cell" data-product-id="${product.productId}">
                ${product.stockQuantity}
                <button type="button" class="add-stock-btn">입고</button>
            </td>
            <td>
                <button type="button" class="edit-btn" data-product-id="${product.productId}">수정</button>
                <button type="button" class="delete-btn" data-product-id="${product.productId}">삭제</button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    // 이벤트 리스너 등록
    addEventListeners();
}

// 이벤트 리스너 등록
function addEventListeners() {
    // 상태 변경 이벤트
    document.querySelectorAll('.status-select').forEach(select => {
        select.addEventListener('change', function() {
            const productId = this.dataset.productId;
            const newStatus = this.value;

            openModal('statusModal');

            document.getElementById('confirmStatus').onclick = () => {
                updateProductStatus(productId, newStatus);
                closeModal(document.getElementById('confirmStatus'));
            };

            document.getElementById('cancelStatus').onclick = () => {
                this.value = this.getAttribute('data-original-value');
                closeModal(document.getElementById('cancelStatus'));
            };
        });
    });

    // 입고 버튼 이벤트
    document.querySelectorAll('.add-stock-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.closest('.stock-cell').dataset.productId;
            openModal('stockModal');

            document.getElementById('confirmStock').onclick = () => {
                const quantity = document.getElementById('addStock').value;
                addStock(productId, quantity);
                closeModal(document.getElementById('confirmStock'));
            };
        });
    });

    // 삭제 버튼 이벤트
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.dataset.productId;
            openModal('deleteModal');

            document.getElementById('confirmDelete').onclick = () => {
                deleteProduct(productId);
                closeModal(document.getElementById('confirmDelete'));
            };
        });
    });
}

// API 호출 함수들
function updateProductStatus(productId, status) {
    api.put(`/api/admin/products/${productId}/status`, { status })
        .then(response => {
            if (response.status === 'SUCCESS') {
                loadProducts();
            }
        })
        .catch(error => console.error('상태 변경 실패:', error));
}

function addStock(productId, quantity) {
    api.post(`/api/admin/products/${productId}/stock`, { quantity })
        .then(response => {
            if (response.status === 'SUCCESS') {
                loadProducts();
            }
        })
        .catch(error => console.error('재고 추가 실패:', error));
}

function deleteProduct(productId) {
    api.delete(`/api/admin/products/${productId}`)
        .then(response => {
            if (response.status === 'SUCCESS') {
                loadProducts();
            }
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                alert('해당 상품에 대한 주문이 진행되고있어 삭제가 불가능합니다.');
            } else {
                console.error('상품 삭제 실패:', error);
            }
        });
}