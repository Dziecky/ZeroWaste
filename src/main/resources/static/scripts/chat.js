let stompClient = null;
let currentChatRoomId = null;
let currentUser = null;
let currentChatRoom = null;

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');

        stompClient.subscribe('/user/queue/messages', (message) => {
            const chatMessage = JSON.parse(message.body);
            displayMessage(chatMessage);
        });

        loadChatRooms();
    });
}

async function fetchCurrentUser() {
    try {
        const response = await fetch('/api/user/current');
        if (!response.ok) {
            throw new Error('Failed to fetch current user');
        }
        currentUser = await response.json();
    } catch (error) {
        console.error('Error fetching user:', error);
    }
}

async function handleChatRoomClick(room) {
    await fetchCurrentUser();
    await openChatRoom(room);
}

function loadChatRooms() {
    fetch('/api/chat/rooms')
        .then(response => response.json())
        .then(chatRooms => {
            const chatRoomsList = document.getElementById('chat-rooms');
            chatRoomsList.innerHTML = '';

            chatRooms.forEach(room => {
                const listItem = document.createElement('li');
                if (currentUser.id === room.user1Id) {
                    listItem.textContent = `Chat Room with user ${room.user2Name}`;
                } else {
                    listItem.textContent = `Chat Room with user ${room.user1Name}`;
                }
                listItem.onclick = () => handleChatRoomClick(room);
                chatRoomsList.appendChild(listItem);
            });
        });
}

async function openChatRoom(chatRoom) {
    currentChatRoom = chatRoom;
    currentChatRoomId = chatRoom.id;
    document.getElementById('chat-window').innerHTML = '';

    await fetchCurrentUser();

    const chatWithUser = (currentUser.id === chatRoom.user1Id)
        ? chatRoom.user2Name
        : chatRoom.user1Name;

    const chatHeading = document.getElementById('chat-heading');
    chatHeading.textContent = `Chat with ${chatWithUser}`;

    fetch(`/api/chat/rooms/${chatRoom.id}/messages`)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(msg => {
                displayMessage(msg, chatRoom);
            });
        });
}

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

function sendMessage() {
    const messageContent = document.getElementById('messageInput').value;

    let receiverName;
    if(currentChatRoom.user1Name === currentUser.username) {
        receiverName = currentChatRoom.user2Name;
    } else {
        receiverName = currentChatRoom.user1Name;
    }
    console.log(receiverName)

    if (messageContent && currentChatRoomId !== null) {
        const chatMessage = {
            sender: currentUser.username,
            receiver: receiverName,
            content: messageContent
        };

        stompClient.send('/app/chat', {}, JSON.stringify(chatMessage));
        document.getElementById('messageInput').value = '';  // Clear input field

        setTimeout(() => {
            openChatRoom(currentChatRoom);
        }, 400);
    } else {
        alert('Please select a chat room and type a message.');
    }
}

document.addEventListener('DOMContentLoaded', async () => {
    await fetchCurrentUser();
    connect();
    document.getElementById('sendButton').addEventListener('click', sendMessage);
});
