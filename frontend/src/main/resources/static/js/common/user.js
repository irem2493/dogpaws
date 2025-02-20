function handleClick(event) {
    const clickedId = event.target.id;

    const menuItems = document.querySelectorAll('.menu p');
    menuItems.forEach(item => item.classList.remove('active'));
    event.target.classList.add('active');

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
            window.location.href = '';
        } else if(clickedId === 'board'){
            window.location.href = '/board/F';
        }
}

window.onload = function () {
    const activeMenu = localStorage.getItem('activeMenu');
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