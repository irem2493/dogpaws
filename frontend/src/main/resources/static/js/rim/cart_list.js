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