document.addEventListener('DOMContentLoaded', function() {
<<<<<<< HEAD
    // URL 파라미터에서 현재 필터 상태 가져오기
    const urlParams = new URLSearchParams(window.location.search);

    // 셀렉트 박스 초기값 설정
    const mainCategory = document.getElementById('mainCategory');
    const status = document.getElementById('status');
    const stockSort = document.getElementById('stockSort');
    const searchKeyword = document.getElementById('searchKeyword');

    // URL 파라미터 값으로 셀렉트 박스 설정
    if (urlParams.get('category')) {
        mainCategory.value = urlParams.get('category');
    }
    if (urlParams.get('status')) {
        status.value = urlParams.get('status');
    }
    if (urlParams.get('sortBy')) {
        stockSort.value = urlParams.get('sortBy');
    }
    if (urlParams.get('keyword')) {
        searchKeyword.value = urlParams.get('keyword');
    }
    
    // 자동 제출 이벤트 리스너 추가
    document.querySelectorAll('.auto-submit').forEach(select => {
        select.addEventListener('change', () => loadFilteredProducts(0, true));
    });

    // 검색 버튼 클릭 이벤트
    document.getElementById('searchBtn').addEventListener('click', () => loadFilteredProducts(0, true));

    // 검색어 입력 필드에서 엔터 키 이벤트
    document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') loadFilteredProducts(0, true);
    });
});

// 필터링된 상품 목록 로드
async function loadFilteredProducts(page = 0, useAjax = false) {
    const category = document.getElementById('mainCategory').value;
    const status = document.getElementById('status').value;
    const sortBy = document.getElementById('stockSort').value;
    const keyword = document.getElementById('searchKeyword').value.trim();

    // URL 파라미터 설정
    const params = new URLSearchParams();
    
    if (category) params.append('mainCategory', category); 
    if (status) params.append('status', status);
    if (sortBy) params.append('sortBy', sortBy);
    if (keyword) params.append('keyword', keyword);

    // URL 업데이트
    const queryString = params.toString();
    const newUrl = queryString ? `${window.location.pathname}?${queryString}` : window.location.pathname;
    window.history.pushState({}, '', newUrl);

    if (useAjax) {
        try {
            // AJAX 요청
            const response = await fetch(`/admin/products/manage?${params}`);
            const html = await response.text();

            // 새로운 HTML에서 테이블 본문만 추출
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const newTableBody = doc.querySelector('.product-table tbody');

            // 테이블 본문 업데이트
            if (newTableBody) {
                document.querySelector('.product-table tbody').innerHTML = newTableBody.innerHTML;
            }

        } catch (error) {
            console.error('데이터 로드 실패:', error);
            window.location.reload();
        }
    } else {
        // 페이지 새로고침
        window.location.href = newUrl;
    }
}

=======
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
>>>>>>> origin/REQ-68-관리자
