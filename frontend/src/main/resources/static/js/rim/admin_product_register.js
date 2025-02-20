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
        const mainCat = mainCategory.value;
        const optionItem = document.createElement('div');
        optionItem.className = 'option-item';

        // 기본 필수 옵션 필드
        let optionFields = `
        <div class="option-basic row">
            <div class="col-md-4">
                <input type="text" placeholder="옵션명" class="option-name form-control" required>
            </div>
            <div class="col-md-4">
                <input type="number" placeholder="추가금액" class="option-price form-control" required>
            </div>
            <div class="col-md-4">
                <input type="number" placeholder="재고수량" class="option-stock form-control" required>
            </div>
        </div>
    `;

        // 추가 옵션 필드 선택 영역
        optionFields += `<div class="option-additional mt-3">
        <div class="option-selectors row">
            <div class="col-12">
                <p class="mb-2">변경할 옵션 선택:</p>
                <div class="row">`;

        // 카테고리별 선택 가능한 추가 옵션
        if (mainCat === 'F' || mainCat === 'N') {
            optionFields += `
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="expiration-date" id="expiration-date-${Date.now()}">
                            <label class="form-check-label" for="expiration-date-${Date.now()}">유통기한</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="weight" id="weight-${Date.now()}">
                            <label class="form-check-label" for="weight-${Date.now()}">무게</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="storage-info" id="storage-info-${Date.now()}">
                            <label class="form-check-label" for="storage-info-${Date.now()}">보관방법</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="manufacturer" id="manufacturer-${Date.now()}">
                            <label class="form-check-label" for="manufacturer-${Date.now()}">제조사</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="origin" id="origin-${Date.now()}">
                            <label class="form-check-label" for="origin-${Date.now()}">원산지</label>
                        </div>
                    </div>
            `;
        } else if (mainCat === 'T') {
            optionFields += `
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="size" id="size-${Date.now()}">
                            <label class="form-check-label" for="size-${Date.now()}">크기</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="color" id="color-${Date.now()}">
                            <label class="form-check-label" for="color-${Date.now()}">색상</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="weight" id="weight-${Date.now()}">
                            <label class="form-check-label" for="weight-${Date.now()}">무게</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="material" id="material-${Date.now()}">
                            <label class="form-check-label" for="material-${Date.now()}">재질</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="manufacturer" id="manufacturer-${Date.now()}">
                            <label class="form-check-label" for="manufacturer-${Date.now()}">제조사</label>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-check">
                            <input type="checkbox" class="form-check-input option-field-toggle" data-field="origin" id="origin-${Date.now()}">
                            <label class="form-check-label" for="origin-${Date.now()}">원산지</label>
                        </div>
                    </div>
            `;
        }

        optionFields += `
                </div>
            </div>
        </div>`;

        // 추가 옵션 입력 필드 영역
        optionFields += `
        <div class="option-additional-fields mt-3">
            <div class="row">
                <div class="col-md-6 option-expiration-date" style="display:none">
                    <input type="date" placeholder="유통기한" class="form-control option-expiration-date-input">
                </div>
                <div class="col-md-6 option-weight" style="display:none">
                    <input type="text" placeholder="무게" class="form-control option-weight-input">
                </div>
                <div class="col-md-6 option-storage-info" style="display:none">
                    <input type="text" placeholder="보관방법" class="form-control option-storage-info-input">
                </div>
                <div class="col-md-6 option-size" style="display:none">
                    <input type="text" placeholder="크기" class="form-control option-size-input">
                </div>
                <div class="col-md-6 option-color" style="display:none">
                    <input type="text" placeholder="색상" class="form-control option-color-input">
                </div>
                <div class="col-md-6 option-material" style="display:none">
                    <input type="text" placeholder="재질" class="form-control option-material-input">
                </div>
                <div class="col-md-6 option-manufacturer" style="display:none">
                    <input type="text" placeholder="제조사" class="form-control option-manufacturer-input">
                </div>
                <div class="col-md-6 option-origin" style="display:none">
                    <input type="text" placeholder="원산지" class="form-control option-origin-input">
                </div>
            </div>
        </div>
    </div>`;

        // 삭제 버튼
        optionFields += `
        <div class="row mt-3">
            <div class="col-12 text-end">
                <button type="button" class="btn btn-danger remove-option">삭제</button>
            </div>
        </div>`;

        optionItem.innerHTML = optionFields;

        // 체크박스 이벤트 리스너 추가
        optionItem.querySelectorAll('.option-field-toggle').forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const fieldName = this.dataset.field;
                const fieldContainer = optionItem.querySelector(`.option-${fieldName}`);
                fieldContainer.style.display = this.checked ? 'block' : 'none';
            });
        });

        optionList.insertBefore(optionItem, addOptionBtn);
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
            manufacturer: document.getElementById('manufacturer').value,

            // 조건부 필드
            expirationDate: (mainCat === 'F' || mainCat === 'N') ? document.getElementById('expirationDate').value || null : null,
            weight: (mainCat === 'F' || mainCat === 'N' || mainCat === 'T') ? document.getElementById('weight').value || null : null,
            storageInfo: (mainCat === 'F' || mainCat === 'N') ? document.getElementById('storageInfo').value || null : null,
            size: mainCat === 'T' ? document.getElementById('size').value || null : null,
            color: mainCat === 'T' ? document.getElementById('color').value || null : null
        };
        console.log('상품 기본 정보:', productData);

        const options = [];
        document.querySelectorAll('.option-item').forEach(item => {
            // 기본 필수 필드
            const optionData = {
                optionName: item.querySelector('.option-name').value,
                optionPrice: parseInt(item.querySelector('.option-price').value),
                optionStock: parseInt(item.querySelector('.option-stock').value)
            };

            // 선택된 추가 필드만 데이터 수집
            item.querySelectorAll('.option-field-toggle:checked').forEach(checkbox => {
                const fieldName = checkbox.dataset.field;
                const fieldValue = item.querySelector(`.option-${fieldName}-input`).value;
                optionData[`option${fieldName.charAt(0).toUpperCase() + fieldName.slice(1).replace(/-\w/g, m => m[1].toUpperCase())}`] = fieldValue;
            });

            options.push(optionData);
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