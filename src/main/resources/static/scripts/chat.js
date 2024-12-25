let stompClient = null;
let currentChatRoomId = null;
let currentUser = null;

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

function fetchCurrentUser() {
    return fetch('/api/user/current')
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Failed to fetch current user');
        })
        .then(user => {
            currentUser = user;
            document.getElementById('username-span').textContent = user.username; // Example: update UI with username
        })
        .catch(error => {
            console.error('Error fetching user:', error);
        });
}

function loadChatRooms() {
    fetch('/api/chat/rooms')
        .then(response => response.json())
        .then(chatRooms => {
            const chatRoomsList = document.getElementById('chat-rooms');
            chatRoomsList.innerHTML = '';

            chatRooms.forEach(room => {
                const listItem = document.createElement('li');
                if(currentUser.id === room.user1Id) {
                    listItem.textContent = `Chat Room with user ${room.user2Name}`;
                } else {
                    listItem.textContent = `Chat Room with user ${room.user1Name}`;
                }
                listItem.onclick = () => openChatRoom(room);
                chatRoomsList.appendChild(listItem);
            });
        });
}

// Open a chat room and load its messages
function openChatRoom(chatRoom) {
    currentChatRoomId = chatRoom.id;
    document.getElementById('chat-window').innerHTML = '';  // Clear previous messages

    // Fetch messages for the selected chat room
    fetch(`/api/chat/rooms/${chatRoom.id}/messages`)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(msg => {
                displayMessage(msg, chatRoom);
            });
        });
}

// Display a message in the chat window
function displayMessage(message, chatRoom) {
    const chatWindow = document.getElementById('chat-window');
    const messageElement = document.createElement('div');
    const timestamp = new Date(message.timestamp).toLocaleTimeString();

    const senderId = +message.sender;

    let sender;
    if (senderId === chatRoom.user1Id) {
        sender = chatRoom.user1Name || 'User 1';
    } else if (senderId === chatRoom.user2Id) {
        sender = chatRoom.user2Name || 'User 2';
    } else {
        sender = 'Unknown';
    }

    const isCurrentUser = senderId === chatRoom.user1Id;
    messageElement.classList.add('message', isCurrentUser ? 'you' : 'other');

    messageElement.textContent = `[${timestamp}] ${sender}: ${message.content}`;
    chatWindow.appendChild(messageElement);

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
    fetchCurrentUser();
    // Set up event listener for sending messages
    document.getElementById('sendButton').addEventListener('click', sendMessage);
});
