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

