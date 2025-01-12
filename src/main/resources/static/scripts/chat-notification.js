document.addEventListener('DOMContentLoaded', () => {
    let stompClient = null;

    function connect() {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, (frame) => {
            console.log('Connected to WebSocket: ' + frame);

            stompClient.subscribe('/user/queue/notifications', (message) => {
                onNotificationReceived(message);
            });
        });
    }

    function onNotificationReceived(message) {
        console.log('Notification received:', message);
        const notification = JSON.parse(message.body);
        displayNotification(notification);
    }

    function displayNotification(notification) {
        const notificationList = document.getElementById('notifications-list');
        const notificationCount = document.getElementById('notification-count');

        const newNotification = document.createElement('div');
        newNotification.textContent = notification.message;
        newNotification.classList.add('dropdown-item');

        notificationList.textContent = '';
        notificationList.appendChild(newNotification);

        const currentCount = parseInt(notificationCount.textContent) || 0;
        notificationCount.textContent = currentCount + 1;
        notificationCount.style.display = 'inline';
    }

    connect();
});
