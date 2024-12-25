let stompClient = null;

function connect(username) {
    const socket = new SockJS('/ws'); // Connect to WebSocket endpoint
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');
        console.log(username)

        // Subscribe to user's message queue
        stompClient.subscribe('/user/queue/messages', (message) => {
            const chatMessage = JSON.parse(message.body);
            displayMessage(chatMessage.sender, chatMessage.content);
        });
    }, (error) => {
        console.error('WebSocket connection failed:', error);
        alert('Failed to connect to WebSocket server.');
    });
}

function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const messageContent = messageInput.value;

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: document.getElementById('username-span').textContent, // Use the logged-in username
            receiver: document.getElementById('receiverInput').value, // Dynamic receiver
            content: messageContent
        };

        console.log('Sending message:', chatMessage);

        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    } else {
        alert('Please enter a message and select a recipient.');
    }
}

function displayMessage(sender, content) {
    const chatWindow = document.getElementById('chat-window');
    const messageElement = document.createElement('div');
    const timestamp = new Date().toLocaleTimeString();
    const username = document.getElementById('username-span').textContent;

    if (sender === username) {
        messageElement.classList.add('text-right', 'text-primary');
        messageElement.textContent = `[${timestamp}] You: ${content}`;
    } else {
        messageElement.classList.add('text-left', 'text-secondary');
        messageElement.textContent = `[${timestamp}] ${sender}: ${content}`;
    }

    chatWindow.appendChild(messageElement);
    chatWindow.scrollTop = chatWindow.scrollHeight; // Auto-scroll to the latest message
}


document.addEventListener('DOMContentLoaded', () => {
    const username = document.querySelector('span').textContent; // Logged-in username
    connect(username);

    document.getElementById('sendButton').addEventListener('click', sendMessage);
});