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
        select.addEventListener('change', () => loadFilteredProducts(0, true));
    });

    // 검색 버튼 클릭 이벤트
    document.getElementById('searchBtn').addEventListener('click', () => loadFilteredProducts(0, true));

    // 검색어 입력 필드에서 엔터 키 이벤트
    document.getElementById('searchKeyword').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') loadFilteredProducts(0, true);
    });

    // 페이지네이션 이벤트 리스너 설정
    initializePaginationListeners();

    initializeEventListeners();
});

// 페이지네이션 이벤트 리스너 초기화
function initializePaginationListeners() {
    document.querySelectorAll('.page-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const newPage = this.getAttribute('data-page');
            loadFilteredProducts(newPage, true);
        });
    });
}

// 필터링된 상품 목록 로드
async function loadFilteredProducts(page = 0, useAjax = false) {
    const category = document.getElementById('mainCategory').value;
    const status = document.getElementById('status').value;
    const sortBy = document.getElementById('stockSort').value;
    const keyword = document.getElementById('searchKeyword').value.trim();

    // URL 파라미터 설정
    const params = new URLSearchParams();
    
    if (category) params.append('category', category);
    if (status) params.append('status', status);
    if (sortBy) params.append('sortBy', sortBy);
    if (keyword) params.append('keyword', keyword);
    if (page > 0) params.append('page', page);

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
            const newPagination = doc.querySelector('.pagination');

            // 테이블 본문 업데이트
            if (newTableBody) {
                document.querySelector('.product-table tbody').innerHTML = newTableBody.innerHTML;
            }

            // 페이지네이션 업데이트
            if (newPagination) {
                document.querySelector('.pagination').innerHTML = newPagination.innerHTML;
            }

            // 이벤트 리스너 재설정
            initializeEventListeners();
            initializePaginationListeners();

        } catch (error) {
            console.error('데이터 로드 실패:', error);
            window.location.reload();
        }
    } else {
        // 페이지 새로고침
        window.location.href = newUrl;
    }
}

// 이벤트 리스너 초기화
function initializeEventListeners() {
    // 수정 버튼 클릭 이벤트
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            initializeEditModal(productId);
        });
    });

    // 상태 변경 버튼 이벤트
    document.querySelectorAll('[data-current-status]').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            const currentStatus = this.getAttribute('data-current-status');
            handleStatusChange(productId, currentStatus);
        });
    });

    // 삭제 버튼 이벤트
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            handleDelete(productId);
        });
    });
}

// 상품 수정 모달 초기화
async function initializeEditModal(productId) {
    try {
        const data = await api.get(`/api/admin/products/${productId}`);
        
        if (data.status === 'SUCCESS') {
            const product = data.body;
            
            document.getElementById('editName').value = product.name;
            document.getElementById('editPrice').value = product.price;
            document.getElementById('editStatus').value = product.status;
            document.getElementById('editDescription').value = product.description;
            
            document.getElementById('editModal').style.display = 'block';
            
            document.getElementById('confirmEdit').onclick = async () => {
                const updatedProduct = {
                    name: document.getElementById('editName').value,
                    price: parseInt(document.getElementById('editPrice').value),
                    status: document.getElementById('editStatus').value,
                    description: document.getElementById('editDescription').value
                };
                await updateProduct(productId, updatedProduct);
            };
        }
    } catch (error) {
        console.error('상품 정보 로드 실패:', error);
        alert('상품 정보를 불러오는데 실패했습니다.');
    }
}

// 상품 수정 처리
async function updateProduct(productId, productData) {
    try {
        const data = await api.put(`/api/admin/products/${productId}/simple`, productData);
        
        if (data.status === 'SUCCESS') {
            alert('상품이 성공적으로 수정되었습니다.');
            closeModal(document.querySelector('#editModal .pawsModal-close'));
            loadFilteredProducts();
        } else {
            alert('상품 수정에 실패했습니다.');
        }
    } catch (error) {
        console.error('상품 수정 실패:', error);
        alert('상품 수정 중 오류가 발생했습니다.');
    }
}

// 상품 상태 변경 처리
async function handleStatusChange(productId, currentStatus) {
    const statusModal = document.getElementById('statusModal');
    statusModal.style.display = 'block';
    
    document.getElementById('confirmStatus').onclick = async () => {
        try {
            const newStatus = getNextStatus(currentStatus);
            const data = await api.put(`/api/admin/products/${productId}/status`, { status: newStatus });
            
            if (data.status === 'SUCCESS') {
                alert('상품 상태가 변경되었습니다.');
                closeModal(document.querySelector('#statusModal .pawsModal-close'));
                loadFilteredProducts();
            } else {
                alert('상태 변경에 실패했습니다.');
            }
        } catch (error) {
            console.error('상태 변경 실패:', error);
            alert('상태 변경 중 오류가 발생했습니다.');
        }
    };
    
    document.getElementById('cancelStatus').onclick = () => {
        closeModal(document.querySelector('#statusModal .pawsModal-close'));
    };
}

// 상품 삭제 처리
async function handleDelete(productId) {
    const deleteModal = document.getElementById('deleteModal');
    deleteModal.style.display = 'block';
    
    document.getElementById('confirmDelete').onclick = async () => {
        try {
            const data = await api.delete(`/api/admin/products/${productId}`);
            
            if (data.status === 'SUCCESS') {
                alert('상품이 삭제되었습니다.');
                closeModal(document.querySelector('#deleteModal .pawsModal-close'));
                loadFilteredProducts();
            } else {
                alert('삭제에 실패했습니다.');
            }
        } catch (error) {
            console.error('삭제 실패:', error);
            alert('삭제 중 오류가 발생했습니다.');
        }
    };
    
    document.getElementById('cancelDelete').onclick = () => {
        closeModal(document.querySelector('#deleteModal .pawsModal-close'));
    };
}

// 상태 순환 함수
function getNextStatus(currentStatus) {
    const statusCycle = {
        'O': 'S',  // 판매중 -> 품절
        'S': 'D',  // 품절 -> 판매중지
        'D': 'O'   // 판매중지 -> 판매중
    };
    return statusCycle[currentStatus] || 'O';
}