document.addEventListener('DOMContentLoaded', function() {
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
        select.addEventListener('change', function() {
            loadFilteredProducts(0, true);
        });
    });

    // 검색 버튼 클릭 이벤트
    document.getElementById('searchBtn').addEventListener('click', function() {
        loadFilteredProducts(0, true);
    });

    // 검색어 입력 필드에서 엔터 키 이벤트
    document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            loadFilteredProducts(0, true);
        }
    });
    // 페이지네이션 클릭 이벤트
    document.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            loadFilteredProducts(page, true);
        });
    });

    addEventListeners();

});





// 필터링된 상품 로드
async function loadFilteredProducts(page = 0, useAjax = false) {
    const category = document.getElementById('mainCategory').value;
    const status = document.getElementById('status').value;
    const sortBy = document.getElementById('stockSort').value;
    const keyword = document.getElementById('searchKeyword').value;

    // URL 파라미터 설정
    const params = new URLSearchParams({
        category: category,
        status: status,
        sortBy: sortBy,
        keyword: keyword,
        page: page
    });

    // URL 업데이트 (페이지 새로고침 없이)
    window.history.pushState({}, '', `${window.location.pathname}?${params}`);

    if (useAjax) {
        try {
            // AJAX 요청
            const response = await fetch(`/admin/product/manage?${params}`);
            const html = await response.text();

            // 새로운 HTML에서 테이블 본문만 추출
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const newTableBody = doc.querySelector('.product-table tbody');
            const newPagination = doc.querySelector('.pagination');

            // 테이블 본문 업데이트
            if (newTableBody) {
                document.querySelector('.product-table tbody').innerHTML = newTableBody.innerHTML;
            }

            // 페이지네이션 업데이트
            if (newPagination) {
                document.querySelector('.pagination').innerHTML = newPagination.innerHTML;
            }

            // 페이지네이션 이벤트 리스너 다시 설정
            document.querySelectorAll('.page-link').forEach(link => {
                link.addEventListener('click', function(e) {
                    e.preventDefault();
                    const newPage = this.getAttribute('data-page');
                    loadFilteredProducts(newPage, true);
                });
            });

        } catch (error) {
            console.error('데이터 로드 실패:', error);
            // 에러 발생 시 페이지 새로고침
            window.location.reload();
        }
    } else {
        // 기존 방식 (페이지 새로고침)
        window.location.href = `${window.location.pathname}?${params}`;
    }
}

// 이벤트 리스너 등록
function addEventListeners() {

    // 재고 버튼 클릭 이벤트
    document.querySelectorAll('.btn-normal[data-product-id]').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            initializeStockModal(productId);
        });
    });

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

// 재고 조회
function getStock(productId, optionId) {
    return api.get(`/api/admin/products/${productId}/stock`)
        .then(response => {
            if (response.status === 'SUCCESS') {
                return response.body;
            }
            throw new Error('재고 조회 실패');
        });
}

// 재고 수정
function updateStock(productId, optionId, quantity, isIncrease) {
    return api.post(`/api/admin/products/${productId}/stock`, {
        optionId: optionId,
        quantity: quantity,
        isIncrease: isIncrease
    })
        .then(response => {
            if (response.status === 'SUCCESS') {
                loadFilteredProducts(0, true);
                return response.body;
            }
            throw new Error('재고 수정 실패');
        });
}


// 재고 변경 버튼 클릭 시 처리
function handleStockUpdate(productId) {
    const quantity = document.getElementById('stockQuantity').value;
    const isIncrease = true; // 입고는 true, 출고는 false

    if (!quantity || isNaN(quantity) || quantity <= 0) {
        alert('올바른 수량을 입력해주세요.');
        return;
    }

    updateStock(productId, null, parseInt(quantity), isIncrease)
        .then(result => {
            alert('재고가 성공적으로 수정되었습니다.');
            closeModal('stockModal');
        })
        .catch(error => {
            console.error('재고 수정 실패:', error);
            alert('재고 수정에 실패했습니다.');
        });
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



//모달랜더링
// 재고 모달 초기화 함수
// 재고 모달 초기화 함수
async function initializeStockModal(productId) {
    try {
        // 재고 정보 조회
        const stockInfo = await getStock(productId);

        // 모달에 정보 표시
        const modal = document.getElementById('stockModal');
        const stockInfoDiv = modal.querySelector('.stock-info');
        const optionSelect = document.getElementById('optionSelect');
        const selectedOptionsDiv = modal.querySelector('.selected-options');

        // 상품 정보 및 전체 재고 표시
        stockInfoDiv.innerHTML = `
            <h4>${stockInfo.body.productName}</h4>
            <p>전체 재고: <span class="total-stock">${stockInfo.body.totalStock}</span>개</p>
        `;

        // 옵션 셀렉트박스 초기화
        optionSelect.innerHTML = `
            <option value="">옵션 선택</option>
            ${stockInfo.body.options.map(option => `
                <option value="${option.optionId}" 
                        data-stock="${option.optionStock}"
                        data-name="${option.optionName}">
                    ${option.optionName} (현재: ${option.optionStock}개)
                </option>
            `).join('')}
        `;

        // 선택된 옵션들 저장
        const selectedOptions = new Set();

        openModal('stockModal');

        // 옵션 선택 이벤트
        optionSelect.addEventListener('change', function() {
            const optionId = this.value;
            if (!optionId) return;

            // 이미 선택된 옵션인지 확인
            if (selectedOptions.has(optionId)) {
                alert('이미 선택된 옵션입니다.');
                this.value = '';
                return;
            }

            const option = this.options[this.selectedIndex];
            const optionName = option.dataset.name;
            const currentStock = option.dataset.stock;

            // 선택된 옵션 추가
            selectedOptions.add(optionId);

            // 옵션 행 추가
            const optionRow = document.createElement('div');
            optionRow.className = 'option-row';
            optionRow.dataset.optionId = optionId;
            optionRow.innerHTML = `
                <div class="option-content">
                    <div class="option-info">
                        ${optionName}
                        <span class="current-stock">(현재: ${currentStock}개)</span>
                    </div>
                    <input type="number" class="quantity-input" placeholder="수량 입력 (음수=출고)">
                </div>
                <button type="button" class="remove-option">×</button>
            `;

            selectedOptionsDiv.appendChild(optionRow);

            // 삭제 버튼 이벤트
            optionRow.querySelector('.remove-option').addEventListener('click', function() {
                selectedOptions.delete(optionId);
                optionRow.remove();
            });

            // 셀렉트박스 초기화
            this.value = '';
        });

        // 확인 버튼 이벤트 리스너
        document.getElementById('confirmStock').onclick = async () => {
            const updates = [];
            const optionRows = selectedOptionsDiv.querySelectorAll('.option-row');

            optionRows.forEach(row => {
                const quantity = parseInt(row.querySelector('.quantity-input').value);
                if (!isNaN(quantity) && quantity !== 0) {
                    updates.push({
                        optionId: row.dataset.optionId,
                        quantity: Math.abs(quantity),
                        isIncrease: quantity > 0
                    });
                }
            });

            if (updates.length === 0) {
                alert('변경할 재고 수량을 입력해주세요.');
                return;
            }

            try {
                // 모든 재고 업데이트 요청을 순차적으로 처리
                for (const update of updates) {
                    await updateStock(productId, update.optionId, update.quantity, update.isIncrease);
                }
                alert('재고가 성공적으로 수정되었습니다.');
                closeModalById('stockModal');
                loadFilteredProducts(0, true);
            } catch (error) {
                console.error('재고 수정 실패:', error);
                alert('재고 수정에 실패했습니다.');
            }
        };


    } catch (error) {
        console.error('재고 정보 조회 실패:', error);
        alert('재고 정보를 불러오는데 실패했습니다.');
    }
}
