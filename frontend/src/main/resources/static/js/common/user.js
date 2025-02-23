function handleClick(event) {
    const clickedId = event.target.id;
    const currentPath = window.location.pathname;

    // 메인 페이지(`/`)에서는 `localStorage`를 삭제하여 active 해제
    if (currentPath === "/") {
        localStorage.removeItem("activeMenu");
    } else {
        // localStorage에 클릭한 메뉴의 id 값을 저장
        localStorage.setItem("activeMenu", clickedId);
    }

    // 모든 메뉴에서 active 클래스 제거
    const menuItems = document.querySelectorAll('.menu p');
    menuItems.forEach(item => item.classList.remove('active'));

    // 클릭한 메뉴에 active 클래스 추가
    event.target.classList.add('active');

<<<<<<< HEAD
    // 페이지 이동 처리
    if (clickedId === 'fmatching') {
        window.location.href = '/matching/friend';
    } else if (clickedId === 'pmatching') {
        window.location.href = '/matching/partner';
    } else if (clickedId === 'header-chat') {
        window.location.href = '/chat-room';
    } else if (clickedId === 'map') {
        window.location.href = '/dog/nearbyDogMap';
    } else if (clickedId === 'store') {
        window.location.href = '/store';
    } else if (clickedId === 'board') {
        window.location.href = '/board/F';
    }
=======
    // localStorage에 클릭한 메뉴의 id 값을 저장
    localStorage.setItem('activeMenu', clickedId);

        if(clickedId === 'fmatching'){
            window.location.href = '/matching/friend';
        }else if(clickedId === 'pmatching'){
            window.location.href = '/matching/partner';
        }else if(clickedId === 'chat'){
            window.location.href = '/chat-room';
        } else if(clickedId === 'map'){
            window.location.href = '/dog/nearbyDogMap';
        } else if(clickedId === 'store'){
            window.location.href = '/store';
        } else if(clickedId === 'board'){
            window.location.href = '/board/F';
        }
>>>>>>> origin/REQ-68-관리자
}

window.onload = function () {
    const activeMenu = localStorage.getItem('activeMenu');
    const currentPath = window.location.pathname;

    // 메인 페이지(`/`)에서는 active 제거!
    if (currentPath === "/") {
        localStorage.removeItem("activeMenu");
        const menuItems = document.querySelectorAll('.menu p');
        menuItems.forEach(item => item.classList.remove('active'));
        return; // 더 이상 실행하지 않고 함수 종료
    }

    // 기존 localStorage 데이터 적용 (요소가 존재할 때만)
    if (activeMenu) {
        const activeItem = document.getElementById(activeMenu);
        if (activeItem) {
            activeItem.classList.add('active');
        }
    }
};

function bellClick(){
    document.getElementById('header-bell-block').style.display = "block";
    document.getElementById('header-redbell-block').style.display = "none";
}

function toggleDropdown() {
    const dropdownMenu = document.getElementById('dropdown-menu');
    // 드롭다운의 display 스타일을 토글
    if (dropdownMenu.style.display === 'none' || dropdownMenu.style.display === '') {
        dropdownMenu.style.display = 'block';
    } else {
        dropdownMenu.style.display = 'none';
    }
}

function handleOption(option) {
    console.log(`${option} 클릭됨`);
    alert(`${option} 메뉴를 선택했습니다.`);
    // 드롭다운 닫기
    document.getElementById('dropdown-menu').style.display = 'none';
}

document.addEventListener("DOMContentLoaded", function () {
    const sidebarLinks = document.querySelectorAll(".sidebar-menu a");

    // 현재 페이지 URL 가져오기
    const currentPath = window.location.pathname;
    const savedActive = localStorage.getItem("activeSidebar");

    // 모든 링크에서 active 클래스 제거
    sidebarLinks.forEach(link => link.classList.remove("active"));

    let isActiveSet = false;

    sidebarLinks.forEach(link => {
        const linkHref = link.getAttribute("href");

        // 저장된 경로와 현재 URL 중 하나만 active 적용
        if (linkHref === currentPath) {
            link.classList.add("active");
            isActiveSet = true;
        }
    });

    // 현재 URL에 해당하는 active가 없으면, 저장된 값 적용 (단, 중복 방지)
    if (!isActiveSet && savedActive) {
        sidebarLinks.forEach(link => {
            if (link.getAttribute("href") === savedActive) {
                link.classList.add("active");
            }
        });
    }

    // 클릭 이벤트 추가
    sidebarLinks.forEach(link => {
        link.addEventListener("click", function () {
            // 모든 링크에서 active 클래스 제거
            sidebarLinks.forEach(l => l.classList.remove("active"));

            // 클릭한 링크에 active 클래스 추가
            this.classList.add("active");

            // 선택한 메뉴를 로컬스토리지에 저장 (새로고침 시 유지)
            localStorage.setItem("activeSidebar", this.getAttribute("href"));
        });
    });
});

