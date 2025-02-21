importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

firebase.initializeApp({
    apiKey: "AIzaSyBNqsnBfUpdLzD5O62ixY8Yyk6RUdUNFVo",
    authDomain: "paws2-6ce51.firebaseapp.com",
    projectId: "paws2-6ce51",
    storageBucket: "paws2-6ce51.firebasestorage.app",
    messagingSenderId: "978825288307",
    appId: "1:978825288307:web:f04f5a168e8aa4aba0a806"
});

const messaging = firebase.messaging();

// 백그라운드 메시지 수신
messaging.onBackgroundMessage((payload) => {
    console.log('백그라운드 메시지 수신:', payload);

    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body,
        icon: '/images/logo.png'
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});