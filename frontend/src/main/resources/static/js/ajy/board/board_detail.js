document.addEventListener("DOMContentLoaded", function() {



});

window.onload = function() {
    const editButton = document.getElementById('editButton');

    if (editButton) {
        editButton.addEventListener('click', function() {
            const boardId = this.getAttribute('data-board-id');
            const category = this.getAttribute('data-category');
            window.location.href = '/board/boardEdit/' + boardId + '/' + category;
        });
    }
}

//등록
document.getElementById('commentForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    const boardId = document.getElementById('boardId').value;
    const username = document.getElementById('username').value;
    const nickname = document.getElementById('nickname').value;
    const category = document.getElementById('category').value;

    const commentInput = document.getElementById('comment');

    const comment = commentInput.value.trim();
    if (!comment) {
        alert(`필수 입력 항목을 모두 채워주세요: 댓글 내용`);
        if (commentInput) {
            commentInput.focus();  // 빈 필드에 포커스 설정
        }
        return;
    }

    const commentData = {
        boardId : Number(boardId),
        username,
        nickname,
        comment,
        category
    };

    console.log(commentData);

    api.post(`/api/comment`, commentData)
        .then(async response => {

        if (response.status === 'SUCCESS') {
            alert("댓글이 저장되었습니다.");
            location.href=`/board/boardDetail/${boardId}/${category}`;
        }else{
            alert("댓글 저장 실패");
        }
    })
        .catch(error => {
            console.error("API 요청 오류:", error);
            alert("서버 오류가 발생했습니다.");
        });
});

document.getElementById("deleteBoardBtn").addEventListener("click", function () {
    if (confirm("게시글을 삭제 하시겠습니까?")) {
        deleteBoard(); // ✅ 회원탈퇴 진행
    }
});

function deleteBoard(){
    const boardId = document.getElementById('boardId').value;
    const category = document.getElementById('category').value;

    api.delete(`/api/board/${boardId}`)
        .then(data => {
            console.log('Response Data:', data);  // 응답 데이터 출력

            // 응답의 body.body가 '1단계 저장 완료'인지 확인
            if (data.body?.body === '게시글 삭제 성공') {
                alert("게시글 삭제 완료");
                location.href = `/board/${category}`;
            } else {
                alert("게시글 삭제 실패");
            }
        })
        .catch(error => {
            console.error("오류:", error);
            alert("게시글 삭제 중 오류.");
        });

}