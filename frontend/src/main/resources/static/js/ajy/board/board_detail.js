
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

document.addEventListener("DOMContentLoaded", function () {

    const boardId = document.getElementById('boardId').value;
    const category = document.getElementById('category').value;

    document.querySelectorAll(".delete-comment-btn").forEach(button => {
        button.addEventListener("click", function (event) {
            event.preventDefault(); // 기본 동작 방지
            const commentId = this.getAttribute("data-comment-id"); // commentId 가져오기
            if (confirm("댓글을 삭제하시겠습니까?")) {
                deleteComment(commentId);
            }
        });
    });

    function deleteComment(commentId) {
        api.delete(`/api/comment/${commentId}`)
            .then(data => {
                console.log('Response Data:', data);  // 응답 데이터 출력

                // 응답의 body.body가 '1단계 저장 완료'인지 확인
                if (data.body?.body === '댓글 삭제 성공') {
                    alert("댓글 삭제 완료");
                    location.href = `/board/boardDetail/${boardId}/${category}`;
                } else {
                    alert("댓글 삭제 실패");
                }
            })
            .catch(error => {
                console.error("오류:", error);
                alert("게시글 삭제 중 오류.");
            });
    }
});

function editComment(commentId) {
    let commentDiv = document.getElementById("comment" + commentId);
    let commentInput = document.getElementById("commentInput" + commentId);

    // 기존 댓글 숨기기
    commentDiv.style.display = "none";

    // input 필드 보이기 및 기존 댓글 내용을 input에 넣기
    commentInput.style.display = "inline-block";
    commentInput.value = commentDiv.innerText.trim(); // 기존 댓글 값 유지

    // 수정 버튼 숨기고 저장 버튼 보이기
    document.getElementById("editBtn" + commentId).style.display = "none";
    document.getElementById("saveBtn" + commentId).style.display = "inline-block";
}

function saveComment(commentId) {
    let commentDiv = document.getElementById("comment" + commentId);
    let commentInput = document.getElementById("commentInput" + commentId);
    let updatedText = commentInput.value.trim(); // 수정된 댓글 가져오기

    if (!updatedText) {
        // 빈 값이면 기존 댓글 값 유지
        alert("빈 값은 입력할 수 없습니다. 기존 댓글이 유지됩니다.");
        commentInput.value = commentDiv.innerText; // 기존 값 복원
        return;
    }

    // 기존 댓글 div 업데이트
    commentDiv.innerText = updatedText;

    // 다시 원래대로 돌리기
    commentDiv.style.display = "inline-block";
    commentInput.style.display = "none";

    // 저장 버튼 숨기고 수정 버튼 다시 보이기
    document.getElementById("editBtn" + commentId).style.display = "inline-block";
    document.getElementById("saveBtn" + commentId).style.display = "none";

    // 실제 서버에 저장하는 로직
    updateCommentInServer(commentId, updatedText);
}

function updateCommentInServer(commentId, updatedText) {
    const category = document.getElementById('category').value;
    const boardId = document.getElementById('boardId').value;
    const data = {
        comment: updatedText
    };

    api.put(`/api/comment/${commentId}`, data)
        .then(async response => {
            if (response.status === 'SUCCESS') {
                alert("댓글이 수정되었습니다.");
                location.href = `/board/boardDetail/${boardId}/${category}`;
            } else {
                alert("댓글 수정 실패");
            }
        })
        .catch(error => {
            console.error("API 요청 오류:", error);
            alert("서버 오류가 발생했습니다.");
        });
}
