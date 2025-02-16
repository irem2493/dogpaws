function confirmDelete(button) {
    let dogId = button.getAttribute("data-dogid");
    let username = button.getAttribute("data-username");

    if (confirm("정말 삭제하시겠습니까?")) {
        api.delete(`/api/dog/${dogId}/${username}` )
            .then(data => {
                console.log('Response Data:', data);  // 응답 데이터 출력

                // 응답의 body.body가 '1단계 저장 완료'인지 확인
                if (data.body?.body === '강아지 삭제 완료') {
                    alert("강아지 정보 삭제 완료");
                    location.href = '/dog/mypage/dogList';
                } else {
                    alert("강아지 삭제 실패");
                }
            })
            .catch(error => {
                console.error("오류:", error);
                alert("강아지 삭제 중 오류.");
            });
    }
}