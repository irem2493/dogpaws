console.log('hello product register.js')
document.addEventListener('DOMContentLoaded', function (){

    // 카테고리 대분류/소분류 표시
    const mainCategory = document.getElementById('mainCategory');
    const subCategory = document.getElementById('subCategory');
    const foodFields = document.getElementById('foodFields');
    const toyFields = document.getElementById('toyFields');

    subCategory.style.display = 'none';

    // 카테고리에 따른 폼 표시 제어
    mainCategory.addEventListener('change', function() {
        // 모든 필드 숨기기
        foodFields.style.display = 'none';
        toyFields.style.display = 'none';

        // 선택된 카테고리에 따라 해당 필드 표시
        switch(this.value) {
            case 'F': // 사료
                foodFields.style.display = 'block';
                subCategory.style.display = 'block'; // 서브카테고리 표시
                break;
            case 'N': // 간식
                foodFields.style.display = 'block';
                subCategory.style.display = 'none'; // 서브카테고리 숨김
                subCategory.value = '';
                break;
            case 'T': // 장난감
                toyFields.style.display = 'block';
                subCategory.style.display = 'none';
                subCategory.value = '';
                break;
            default:
                subCategory.style.display = 'none';
                subCategory.value = '';
        }
    });




    // 이미지 미리보기 기능
    function setupImagePreview(inputId, previewId) {
        const input = document.getElementById(inputId);
        const preview = document.getElementById(previewId);

        input.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    preview.innerHTML = `<img src="${e.target.result}" alt="Preview">`;
                };
                reader.readAsDataURL(file);
            }
        });
    }

    setupImagePreview('thumbnailImage', 'thumbnailPreview');
    setupImagePreview('detailImage', 'detailPreview');

    // 옵션 추가/삭제 기능
    const optionList = document.getElementById('optionList');
    const addOptionBtn = document.getElementById('addOption');

    // 옵션 추가 버튼 클릭 이벤트
    addOptionBtn.addEventListener('click', function() {
        const optionItem = document.createElement('div');
        optionItem.className = 'option-item';
        optionItem.innerHTML = `
            <input type="text" placeholder="옵션명" class="option-name">
            <input type="number" placeholder="추가금액" class="option-price">
            <input type="number" placeholder="재고수량" class="option-stock">
            <button type="button" class="remove-option">삭제</button>
        `;
        optionList.insertBefore(optionItem, addOptionBtn); // 추가 버튼 앞에 새 옵션 추가
    });

    // 옵션 삭제 버튼 클릭 이벤트 (이벤트 위임 사용)
    optionList.addEventListener('click', function(e) {
        if (e.target.classList.contains('remove-option')) {
            e.target.closest('.option-item').remove();
        }
    });

    // 폼 제출
    const form = document.getElementById('productRegisterForm');

    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const mainCat = mainCategory.value;

        const formData = new FormData();

        // 상품 기본 정보
        const productData = {
            // 공통 필드
            mainCategory: mainCat,
            subCategory: document.getElementById('subCategory').value,
            name: document.getElementById('productName').value,
            price: parseInt(document.getElementById('price').value),
            stockQuantity: parseInt(document.getElementById('stockQuantity').value),
            description: document.getElementById('description').value,
            status: document.getElementById('status').value,
            origin: document.getElementById('origin').value || null,

            // 조건부 필드
            expirationDate: (mainCat === 'F' || mainCat === 'N') ? document.getElementById('expirationDate').value || null : null,
            weight: (mainCat === 'F' || mainCat === 'N' || mainCat === 'T') ? document.getElementById('weight').value || null : null,
            storageInfo: (mainCat === 'F' || mainCat === 'N') ? document.getElementById('storageInfo').value || null : null,
            size: mainCat === 'T' ? document.getElementById('size').value || null : null,
            color: mainCat === 'T' ? document.getElementById('color').value || null : null
        };
        console.log('상품 기본 정보:', productData);

        // 옵션 정보
        const options = [];
        document.querySelectorAll('.option-item').forEach(item => {
            options.push({
                optionName: item.querySelector('.option-name').value,
                optionPrice: parseInt(item.querySelector('.option-price').value),
                optionStock: parseInt(item.querySelector('.option-stock').value)
            });
        });
        console.log('옵션 정보:', options);

        // JSON 문자열로 변환하여 FormData에 추가
        formData.append('productDtoString', JSON.stringify(productData));
        formData.append('optionDtosString', JSON.stringify(options));

        // 이미지 파일
        const thumbnailFile = document.getElementById('thumbnailImage').files[0];
        const detailFile = document.getElementById('detailImage').files[0];

        if (thumbnailFile) formData.append('thumbnailImage', thumbnailFile);
        if (detailFile) formData.append('detailImage', detailFile);

        try {
            const response = await adminApi.post('/products', formData);

            if (response.data.status === 'SUCCESS') {
                alert('상품이 등록되었습니다.');
            }
        } catch (error) {
            console.error('상품 등록 실패:', error);
            alert('상품 등록에 실패했습니다.');
        }
    });
});