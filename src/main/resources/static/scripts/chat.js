let stompClient = null;
let currentChatRoomId = null;

// Connect to WebSocket
function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');

        // Subscribe to user-specific message queue
        stompClient.subscribe('/user/queue/messages', (message) => {
            const chatMessage = JSON.parse(message.body);
            displayMessage(chatMessage);
        });

        loadChatRooms();  // Load existing chat rooms when connected
    });
}

// Load chat rooms from backend (replace this with real API if needed)
function loadChatRooms() {
    fetch('/api/chat/rooms')
        .then(response => response.json())
        .then(chatRooms => {
            const chatRoomsList = document.getElementById('chat-rooms');
            chatRoomsList.innerHTML = '';

            console.log(chatRooms)
            chatRooms.forEach(room => {
                const listItem = document.createElement('li');
                listItem.textContent = `Chat Room with user: ${room.user1} and ${room.user2Id}`;
                listItem.onclick = () => openChatRoom(room.id);
                chatRoomsList.appendChild(listItem);
            });
        });
}

// Open a chat room and load its messages
function openChatRoom(chatRoomId) {
    currentChatRoomId = chatRoomId;
    document.getElementById('chat-window').innerHTML = '';  // Clear previous messages

    // Fetch messages for the selected chat room
    fetch(`/api/chat/rooms/${chatRoomId}/messages`)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(msg => {
                displayMessage(msg);
            });
        });
}

// Display a message in the chat window
function displayMessage(message) {
    const chatWindow = document.getElementById('chat-window');
    const messageElement = document.createElement('div');
    const timestamp = new Date(message.timestamp).toLocaleTimeString();
    const sender = message.senderId === 1 ? 'You' : 'Other';  // Adjust sender check based on actual logic

    messageElement.classList.add('message', sender === 'You' ? 'you' : 'other');
    messageElement.textContent = `[${timestamp}] ${sender}: ${message.content}`;
    chatWindow.appendChild(messageElement);

    // Auto-scroll to the bottom of the chat window
    chatWindow.scrollTop = chatWindow.scrollHeight;
}

// Send a message to the selected chat room
function sendMessage() {
    const messageContent = document.getElementById('messageInput').value;

    currentChatRoomId = 1; // TODO: Remove this, stubbing for test purposes
    if (messageContent && currentChatRoomId !== null) {
        const chatMessage = {
            sender: document.getElementById('username-span').textContent,
            receiver: document.getElementById('receiverInput').value,
            content: messageContent,
            chatRoomId: currentChatRoomId
        };

        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        document.getElementById('messageInput').value = '';  // Clear input field
    } else {
        alert('Please select a chat room and type a message.');
    }
}

// Initialize connection on page load
document.addEventListener('DOMContentLoaded', () => {
    connect();  // Connect to WebSocket

    // Set up event listener for sending messages
    document.getElementById('sendButton').addEventListener('click', sendMessage);
});
