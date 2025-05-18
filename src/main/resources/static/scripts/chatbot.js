const chatToggle = document.getElementById("chat-toggle");
const chatbox = document.getElementById("chatbox");
const closeChat = document.getElementById("close-chat");
const input = document.getElementById("input");
const messages = document.getElementById("messages");

chatToggle.onclick = () => {
    chatbox.style.display = (chatbox.style.display === "flex") ? "none" : "flex";
};
closeChat.onclick = () => chatbox.style.display = "none";

input.addEventListener("keydown", async function (e) {
    if (e.key === "Enter" && input.value.trim() !== "") {
        const userMsg = input.value.trim();
        addMessage(userMsg, "user");

        input.value = "";

        const botMsg = addMessage("...", "bot");
        const stopAnimation = animateDots(botMsg);

        const reply = await getReply(userMsg.toLowerCase());

        stopAnimation();
        simulateTyping(reply, botMsg);
    }
});

function addMessage(text, sender) {
    const msg = document.createElement("div");
    msg.className = `msg ${sender}`;
    msg.textContent = text;
    messages.appendChild(msg);
    messages.scrollTop = messages.scrollHeight;
    return msg;
}

async function getReply(prompt) {
    const response = await fetch("http://localhost:8080/api/chat", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt })
    });

    const data = await response.json();
    return data.choices?.[0]?.message?.content || "Nie rozumiem 😕";
}


function simulateTyping(text, element, delay = 20) {
    element.textContent = "";
    let index = 0;
    const interval = setInterval(() => {
        element.textContent += text.charAt(index);
        index++;
        messages.scrollTop = messages.scrollHeight;
        if (index >= text.length) clearInterval(interval);
    }, delay);
}

function animateDots(element) {
    let dots = 0;
    const interval = setInterval(() => {
        dots = (dots + 1) % 4;
        element.textContent = ".".repeat(dots);
        messages.scrollTop = messages.scrollHeight;
    }, 400);

    return () => clearInterval(interval);
}