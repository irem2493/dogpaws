// 닉네임을 클릭했을 때 모달 열기
document.querySelectorAll('.username').forEach(function(usernameElement) {
    usernameElement.addEventListener('click', function() {
        var username = usernameElement.getAttribute('data-username');
        var boardId = usernameElement.getAttribute('data-boardId');
        var modalId = 'modal-' + username + '-'+boardId; // 해당 사용자에 맞는 모달 ID

        // 모든 모달을 닫음
        document.querySelectorAll('.modal').forEach(function(modal) {
            modal.style.display = 'none';
        });

        // 클릭된 사용자의 모달을 보이도록 설정
        var modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'block';
        }
    });
});

// 닉네임을 클릭했을 때 모달 열기
document.querySelectorAll('.nickname').forEach(function(nicknameElement) {
    nicknameElement.addEventListener('click', function() {
        event.stopPropagation();
        var username = nicknameElement.getAttribute('data-username');
        var commentId = nicknameElement.getAttribute('data-commentId');
        var modalId = 'modal-' + username + '-'+commentId; // 해당 사용자에 맞는 모달 ID

        // 모든 모달을 닫음
        document.querySelectorAll('.modal').forEach(function(modal) {
            modal.style.display = 'none';
        });

        // 클릭된 사용자의 모달을 보이도록 설정
        var modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'block';
        }
    });
});


// 페이지의 다른 곳을 클릭하면 모달을 닫기
document.addEventListener('click', function(event) {
    if (!event.target.closest('.username') && !event.target.closest('.modal')) {
        // 모달이 보이지 않도록 설정
        document.querySelectorAll('.modal').forEach(function(modal) {
            modal.style.display = 'none';
        });
    }
});