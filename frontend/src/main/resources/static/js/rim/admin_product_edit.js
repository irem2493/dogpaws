// 상수 정의
const SELECTORS = {
    form: '#productEditForm',
    mainCategory: '#mainCategory',
    subCategory: '#subCategory',
    foodFields: '#foodFields',
    toyFields: '#toyFields',
    optionsContainer: '.options-container',
    addOptionBtn: '#addOptionBtn'
};

// 옵션 템플릿 통합
function createOptionElement(isNew = true) {
    const optionDiv = document.createElement('div');
    optionDiv.className = `option-item ${isNew ? 'new-option' : ''}`;
    optionDiv.innerHTML = `
        <input type="text" class="optionName" placeholder="옵션명">
        <input type="number" class="optionPrice" placeholder="가격">
        <input type="text" class="optionWeight" placeholder="무게">
        ${isNew ? `
            <button type="button" class="save-new-option">저장</button>
            <button type="button" class="cancel-new-option">취소</button>
        ` : `
            <button type="button" class="update-option">수정</button>
            <button type="button" class="delete-option">삭제</button>
        `}
    `;
    return optionDiv;
}

// 카테고리 변경에 따른 필드 표시/숨김
function toggleCategoryFields(category) {
    const { foodFields, toyFields, subCategory } = this.elements;

    foodFields.style.display = 'none';
    toyFields.style.display = 'none';
    subCategory.style.display = 'none';

    if (category === 'F' || category === 'N') {
        foodFields.style.display = 'block';
        if (category === 'F') subCategory.style.display = 'block';
    } else if (category === 'T') {
        toyFields.style.display = 'block';
    }
}

// 이미지 프리뷰 처리
function handleImagePreview(input, previewElement) {
    const reader = new FileReader();
    reader.onload = (e) => {
        previewElement.innerHTML = `<img src="${e.target.result}" alt="이미지 미리보기">`;
    };
    reader.readAsDataURL(input.files[0]);
}

// 폼 데이터 수집
function collectFormData() {
    const formData = new FormData();
    const basicFields = [
        'mainCategory', 'subCategory', 'name', 'price', 'origin',
        'size', 'weight', 'manufacturer', 'description', 'status'
    ];

    // 기본 필드 추가
    basicFields.forEach(field => {
        formData.append(field, document.getElementById(field).value);
    });

    // 카테고리별 추가 필드
    const mainCategory = document.getElementById('mainCategory').value;
    if (mainCategory === 'F' || mainCategory === 'N') {
        formData.append('expirationDate', document.getElementById('expirationDate').value);
        formData.append('storageInfo', document.getElementById('storageInfo').value);
    }
    if (mainCategory === 'T') {
        formData.append('color', document.getElementById('color').value);
    }

    // 이미지 파일
    const thumbnailFile = document.getElementById('thumbnailImage').files[0];
    const detailFile = document.getElementById('detailImage').files[0];
    if (thumbnailFile) formData.append('thumbnailImage', thumbnailFile);
    if (detailFile) formData.append('detailImage', detailFile);

    // 옵션 정보 수집
    const existingOptions = Array.from(document.querySelectorAll('.option-item.existing-option'))
    .map(item => ({
        optionId: item.querySelector('.optionId').value,
        optionName: item.querySelector('.optionName').value,
        optionPrice: parseInt(item.querySelector('.optionPrice').value),
        optionWeight: item.querySelector('.optionWeight').value
    }));

    const newOptions = Array.from(document.querySelectorAll('.option-item.new-option'))
        .filter(item => item.querySelector('.optionName').value)
        .map(item => ({
            optionName: item.querySelector('.optionName').value,
            optionPrice: item.querySelector('.optionPrice').value,
            optionWeight: item.querySelector('.optionWeight').value,
            optionStock: item.querySelector('.optionStock').value
        }));

    formData.append('existingOptions', JSON.stringify(existingOptions));
    formData.append('newOptions', JSON.stringify(newOptions));

    return formData;
}

// 옵션 수정 처리
async function handleOptionUpdate(optionId, productId) {
    const optionElement = document.querySelector(`.option-item[data-option-id="${optionId}"]`);
    const optionData = {
        optionId: optionId,
        optionName: optionElement.querySelector('.optionName').value,
        optionPrice: parseInt(optionElement.querySelector('.optionPrice').value),
        optionWeight: optionElement.querySelector('.optionWeight').value
    };

    try {
        const response = await adminApi.put(`/products/${productId}/options/${optionId}`, optionData);
        
        if (response.data.status === 'SUCCESS') {
            alert('옵션이 수정되었습니다.');
        }
    } catch (error) {
        console.error('옵션 수정 실패:', error);
        alert('옵션 수정에 실패했습니다.');
    }
}

// 새 옵션 저장
async function handleNewOptionSave(productId) {
    const newOptionElement = document.querySelector('.new-option');
    const optionData = {
        productId: productId,
        optionName: newOptionElement.querySelector('.optionName').value,
        optionPrice: parseInt(newOptionElement.querySelector('.optionPrice').value),
        optionWeight: newOptionElement.querySelector('.optionWeight').value
    };

    try {
        const response = await adminApi.post(`/products/${productId}/options`, optionData);
        
        if (response.data.status === 'SUCCESS') {
            alert('새 옵션이 추가되었습니다.');
            // 새로 추가된 옵션으로 UI 업데이트
            updateOptionsUI(response.data.body);
        }
    } catch (error) {
        console.error('옵션 추가 실패:', error);
        alert('옵션 추가에 실패했습니다.');
    }
}

// 이벤트 위임을 통한 옵션 관리
document.querySelector('.options-container').addEventListener('click', async function(e) {
    const productId = window.location.pathname.split('/').pop();
    const optionItem = e.target.closest('.option-item');
    
    if (!optionItem) return;

    if (e.target.classList.contains('save-new-option')) {
        await handleNewOptionSave(productId);
    } else if (e.target.classList.contains('cancel-new-option')) {
        optionItem.remove();
    } else if (e.target.classList.contains('update-option')) {
        const optionId = optionItem.dataset.optionId;
        await handleOptionUpdate(optionId, productId);
    } else if (e.target.classList.contains('delete-option')) {
        const optionId = optionItem.dataset.optionId;
        await handleOptionDelete(optionId, productId);
    }
});

// 메인 초기화 함수
document.addEventListener('DOMContentLoaded', () => {
    const elements = Object.fromEntries(
        Object.entries(SELECTORS).map(([key, selector]) => [key, document.querySelector(selector)])
    );

    // 기존 옵션 삭제 버튼 이벤트
    document.querySelectorAll('.existing-option .removeOption').forEach(button => {
        button.addEventListener('click', () => {
            if (confirm('이 옵션을 삭제하시겠습니까?\n삭제된 옵션은 복구할 수 없습니다.')) {
                button.closest('.option-item').remove();
            }
        });
    });

    // 새 옵션 추가 버튼
    elements.addOptionBtn.addEventListener('click', () => {
        elements.optionsContainer.insertBefore(
            createOptionElement(),
            elements.addOptionBtn
        );
    });

    // 카테고리 변경 이벤트
    elements.mainCategory.addEventListener('change', () => {
        toggleCategoryFields.call({ elements }, elements.mainCategory.value);
    });

    // 이미지 프리뷰 이벤트
    ['thumbnailImage', 'detailImage'].forEach(id => {
        const input = document.getElementById(id);
        input.addEventListener('change', () => {
            if (input.files[0]) {
                handleImagePreview(input, document.getElementById(`${id.replace('Image', '')}Preview`));
            }
        });
    });

    // 폼 제출
    elements.form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const productId = window.location.pathname.split('/').pop();

        try {
            const response = await api.put("/api/admin/products", { productId }, { formData: collectFormData() });

            if (response.status === 'SUCCESS') {
                alert('상품이 성공적으로 수정되었습니다.');
                window.location.href = '/admin/products/manage';
            } else {
                throw new Error(response.message || '상품 수정 실패');
            }
        } catch (error) {
            console.error('Error:', error);
            alert(error.message || '상품 수정 중 오류가 발생했습니다.');
        }
    });

    // 초기 카테고리 필드 상태 설정
    elements.mainCategory.dispatchEvent(new Event('change'));
});