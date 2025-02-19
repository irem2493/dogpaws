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