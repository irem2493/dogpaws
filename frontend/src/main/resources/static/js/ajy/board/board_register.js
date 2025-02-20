const {
    ClassicEditor,
    AutoImage,
    Autosave,
    BlockQuote,
    Bold,
    CKBox,
    CKBoxImageEdit,
    CloudServices,
    Essentials,
    Heading,
    ImageBlock,
    ImageCaption,
    ImageInline,
    ImageInsert,
    ImageInsertViaUrl,
    ImageResize,
    ImageStyle,
    ImageTextAlternative,
    ImageToolbar,
    ImageUpload,
    Indent,
    IndentBlock,
    Italic,
    Link,
    LinkImage,
    Paragraph,
    PictureEditing,
    Table,
    TableCaption,
    TableCellProperties,
    TableColumnResize,
    TableProperties,
    TableToolbar,
    Underline
} = window.CKEDITOR;

const LICENSE_KEY =
    'eyJhbGciOiJFUzI1NiJ9.eyJleHAiOjE3NDA5NTk5OTksImp0aSI6IjY3MGQ4YmFhLTVjNzUtNDg5Ny05ZjM4LTFkNzg5OGQ4YTM5NiIsInVzYWdlRW5kcG9pbnQiOiJodHRwczovL3Byb3h5LWV2ZW50LmNrZWRpdG9yLmNvbSIsImRpc3RyaWJ1dGlvbkNoYW5uZWwiOlsiY2xvdWQiLCJkcnVwYWwiLCJzaCJdLCJ3aGl0ZUxhYmVsIjp0cnVlLCJsaWNlbnNlVHlwZSI6InRyaWFsIiwiZmVhdHVyZXMiOlsiKiJdLCJ2YyI6ImNlN2RjMTdiIn0.B5obsHWU2Zhn9i7D_T0EROc5CKfY8EXiWQIgxPuri1wT14Q0O5bhKsYlVd5Lc5O_vpDQeW_EEJQfOX5BkIWFBA';

const CLOUD_SERVICES_TOKEN_URL =
    'https://jjkg5zjkklym.cke-cs.com/token/dev/8b65a8fa23375014e8d59e3754515103671cb563e31a4ebc90ffa182437f?limit=10';

const editorConfig = {
    ckfinder:{
        uploadUrl: "http://localhost:2000/uploads/"
    },
    toolbar: {
        items: [
            'heading',
            '|',
            'bold',
            'italic',
            'underline',
            '|',
            'link',
            'insertImage',
            'ckbox',
            'insertTable',
            'blockQuote',
            '|',
            'outdent',
            'indent'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AutoImage,
        Autosave,
        BlockQuote,
        Bold,
        CKBox,
        CKBoxImageEdit,
        CloudServices,
        Essentials,
        Heading,
        ImageBlock,
        ImageCaption,
        ImageInline,
        ImageInsert,
        ImageInsertViaUrl,
        ImageResize,
        ImageStyle,
        ImageTextAlternative,
        ImageToolbar,
        ImageUpload,
        Indent,
        IndentBlock,
        Italic,
        Link,
        LinkImage,
        Paragraph,
        PictureEditing,
        Table,
        TableCaption,
        TableCellProperties,
        TableColumnResize,
        TableProperties,
        TableToolbar,
        Underline
    ],
    cloudServices: {
        tokenUrl: CLOUD_SERVICES_TOKEN_URL
    },
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    image: {
        toolbar: [
            'toggleImageCaption',
            'imageTextAlternative',
            '|',
            'imageStyle:inline',
            'imageStyle:wrapText',
            'imageStyle:breakText',
            '|',
            'resizeImage',
            '|',
            'ckboxImageEdit'
        ]
    },
    initialData: '',
    language: 'ko',
    licenseKey: LICENSE_KEY,
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },
    placeholder: 'Type or paste your content here!',
    table: {
        contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
    }
};

configUpdateAlert(editorConfig);

let editorInstance;

const content = document.getElementById('content').value;
ClassicEditor.create(document.querySelector('#editor'), editorConfig)
    .then(editor => {
        editorInstance = editor; // 에디터 인스턴스를 전역 변수에 저장

        if (content && content.trim() !== "") {
            console.log('여기?');
            editorInstance.setData(content);  // 서버에서 받은 content를 에디터에 넣기
        } else {
            console.log('여기로 들어가나용?');
            editorInstance.setData('<p></p>');  // content가 비어있으면 빈 paragraph 넣기
        }
    })
    .catch(error => {
        console.error("CKEditor 로드 오류:", error);
    });

// 입력된 값 가져오기
function getEditorData() {
    const data = editorInstance.getData();
    console.log("입력된 값:", data);
    alert("입력된 값:\n" + data);
}

/**
 * This function exists to remind you to update the config needed for premium features.
 * The function can be safely removed. Make sure to also remove call to this function when doing so.
 */
function configUpdateAlert(config) {
    if (configUpdateAlert.configUpdateAlertShown) {
        return;
    }

    const isModifiedByUser = (currentValue, forbiddenValue) => {
        if (currentValue === forbiddenValue) {
            return false;
        }

        if (currentValue === undefined) {
            return false;
        }

        return true;
    };

    const valuesToUpdate = [];

    configUpdateAlert.configUpdateAlertShown = true;

    if (!isModifiedByUser(config.cloudServices?.tokenUrl, '<YOUR_CLOUD_SERVICES_TOKEN_URL>')) {
        valuesToUpdate.push('CLOUD_SERVICES_TOKEN_URL');
    }

    if (valuesToUpdate.length) {
        window.alert(
            [
                'Please update the following values in your editor config',
                'to receive full access to Premium Features:',
                '',
                ...valuesToUpdate.map(value => ` - ${value}`)
            ].join('\n')
        );
    }
}


//등록
document.getElementById('boardForm').addEventListener('submit', function(event) {
    event.preventDefault();  // 기본 폼 제출 방지

    const username = document.getElementById('username').value;
    const nickname=document.getElementById('nickname').value;
    const titleInput = document.getElementById('title');
    const title = titleInput.value;

    const contentData = document.getElementById('content').value;
    const boardId = document.getElementById('boardId').value;

    const editorData = editorInstance.getData();
    const category = document.getElementById('category').value;



   // if(!boardId){
        const value = title.trim();
        if (!value) {
            alert(`필수 입력 항목을 모두 채워주세요: 제목`);
            if (titleInput) {
                titleInput.focus();  // 빈 필드에 포커스 설정
            }
            return;
        }

        const content = editorData.trim();
        if (!content || content === "" || content === "<p></p>") {
            console.error("에디터 내용이 비어 있습니다.");
            alert("내용을 입력해주세요.");
            return;
        }
    /*}
    else{
        // 기존 값 저장
        const fields = ["title", "content"];
        const previousValues = {};
        fields.forEach(field => {
            const element = document.getElementById(field);
            previousValues[field] = element.getAttribute("th:value") || "";
        });

        fields.forEach(field => {
            const inputElement = document.getElementById(field);
            if (inputElement) {
                const currentValue = inputElement.value.trim();
                const prevValue = inputElement.getAttribute("data-prev-value") || "";

                // 값이 비어 있으면 이전 값으로 설정
                if (!currentValue) {
                    inputElement.value = prevValue;
                }
            }
        });
    }*/

    const boardData = {
        username,
        nickname,
        title,
        content : content,
        category
    };

    console.log(boardId);
    if(!boardId){
        api.post('/api/board', boardData)
            .then(async response => {

                if (response.status === 'SUCCESS') {
                    alert("게시글이 저장되었습니다.");
                    location.href=`/board/${category}`;
                }else{
                    alert("게시글 저장 실패");
                }
            })
            .catch(error => {
                console.error("API 요청 오류:", error);
                alert("서버 오류가 발생했습니다.");
            });
    }else{
        api.put(`/api/board/${boardId}`, boardData)
            .then(async response => {

                if (response.status === 'SUCCESS') {
                    alert("게시글이 수정되었습니다.");
                    location.href=`/board/${category}`;
                }else{
                    alert("게시글 수정 실패");
                }
            })
            .catch(error => {
                console.error("API 요청 오류:", error);
                alert("서버 오류가 발생했습니다.");
            });
    }


});
/*
document.addEventListener("DOMContentLoaded", function() {
    const content = document.getElementById('content').value;  // hidden input에서 content 값 가져오기

    // CKEditor 초기화
    ClassicEditor
        .create(
            document.querySelector('#editor'),editorConfig
        )
        .then(editor => {
            // 에디터 초기화 후에는 #editor 요소를 숨기기 전에 값을 설정합니다.
            if (content && content.trim() !== "") {
                console.log('여기?');
                editor.setData(content);  // 서버에서 받은 content를 에디터에 넣기
            } else {
                console.log('여기로 들어가나용?');
                editor.setData('<p></p>');  // content가 비어있으면 빈 paragraph 넣기
            }

            // 초기화가 완료된 후에 에디터를 숨깁니다.
            document.querySelector('#editor-container').style.display = 'none';
        })
        .catch(error => {
            console.error("CKEditor 초기화 실패: ", error);
        });
});*/


