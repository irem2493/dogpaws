// <script type="module">
    // Import the functions you need from the SDKs you need
    import { initializeApp } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-app.js";
    import { getAnalytics } from "https://www.gstatic.com/firebasejs/11.2.0/firebase-analytics.js";
    // TODO: Add SDKs for Firebase products that you want to use
    // https://firebase.google.com/docs/web/setup#available-libraries

    // Your web app's Firebase configuration
    // For Firebase JS SDK v7.20.0 and later, measurementId is optional
    const firebaseConfig = {
    apiKey: "AIzaSyBNqsnBfUpdLzD5O62ixY8Yyk6RUdUNFVo",
    authDomain: "paws2-6ce51.firebaseapp.com",
    projectId: "paws2-6ce51",
    storageBucket: "paws2-6ce51.firebasestorage.app",
    messagingSenderId: "978825288307",
    appId: "1:978825288307:web:f04f5a168e8aa4aba0a806",
    measurementId: "G-WVVKWQP8RV"
};

    // Initialize Firebase
    const app = initializeApp(firebaseConfig);
    const analytics = getAnalytics(app);

// `db` 객체를 내보내기(export)
export { analytics };
