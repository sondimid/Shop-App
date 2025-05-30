import React, { useState, useRef, useEffect } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { MdSend } from "react-icons/md";
import { timeAgo } from "../utils/helper";

function ChatAdminWidget({ senderId, recipientId, onClose }) {
  const [showChat, setShowChat] = useState(true);
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState("");
  const stompClientRef = useRef(null);
  const chatBoxRef = useRef(null);

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/chat");
    socket.onopen = () => {
      console.log("SockJS connection opened");
    };
    socket.onclose = () => {
      console.log("SockJS connection closed");
    };
    socket.onerror = (e) => {
      console.error("SockJS error", e);
    };

    const stompClient = Stomp.over(socket);

    stompClient.onConnect = () => {
      console.log("STOMP connected");
      stompClient.subscribe(
        `/topic/room/${senderId + recipientId}`,
        (message) => {
          console.log("Received message:", message);
          try {
            const msg = JSON.parse(message.body);
            setMessages((prev) => [...prev, msg]);
          } catch (err) {
            console.error("Error parsing message body:", message.body, err);
          }
        },
        {
          id: `sub-${senderId}-${Date.now()}`,
          ack: "auto",
        }
      );
    };

    stompClient.onStompError = (frame) => {
      console.error("STOMP Error:", frame);
    };

    stompClient.onWebSocketError = (evt) => {
      console.error("WebSocket error:", evt);
    };

    stompClient.onWebSocketClose = (evt) => {
      console.log("WebSocket closed:", evt);
    };

    stompClientRef.current = stompClient;
    stompClient.activate();

    return () => {
      if (stompClientRef.current && stompClientRef.current.connected) {
        console.log("Deactivating STOMP client");
        stompClientRef.current.deactivate();
      }
    };
  }, [senderId, recipientId]);

  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scroll({
        top: chatBoxRef.current.scrollHeight,
        behavior: "auto",
      });
    }
  }, [messages]);

  useEffect(() => {
    async function loadMessages() {
      try {
        const response = await fetch(
          `http://localhost:8080/api/v1/chat/${senderId}/${recipientId}`
        );
        const data = await response.json();
        const messageData = Array.isArray(data)
          ? data.map((msg) => ({
              ...msg,
              senderId: Number(msg.senderId),
              recipientId: Number(msg.recipientId),
              content: msg.content || msg.text,
              timestamp: msg.createdAt || msg.timestamp,
            }))
          : [];
        setMessages(messageData);
      } catch (error) {
        setMessages([]);
      }
    }
    if (showChat && senderId && recipientId) {
      loadMessages();
    }
  }, [showChat, senderId, recipientId]);

  const sendMessage = () => {
    const stompClient = stompClientRef.current;
    if (inputMessage.trim() !== "" && stompClient && stompClient.connected) {
      const msg = {
        senderId: Number(senderId),
        recipientId: Number(recipientId),
        content: inputMessage,
        timestamp: new Date().toISOString(),
      };
      console.log("Sending message:", msg);
      stompClient.send(
        `/app/sendMessage/${senderId + recipientId}`,
        {},
        JSON.stringify(msg)
      );
      setInputMessage("");
    } else {
      console.log(
        "Cannot send message. stompClient:",
        stompClient,
        "connected:",
        stompClient && stompClient.connected
      );
    }
  };

  const handleInputKeyDown = (e) => {
    if (e.key === "Enter") sendMessage();
  };

  // Đóng chat widget
  const handleCloseChat = () => {
    if (onClose) onClose();
  };

  let openWidgets = JSON.parse(localStorage.getItem("openChatWidgets")) || [];
  openWidgets = openWidgets.filter((w) => w === "bot" || w === "admin");
  const myIndex = showChat ? openWidgets.indexOf("admin") : -1;
  const rightPos = myIndex >= 0 ? 36 + myIndex * 370 : 36;

  return (
    <>
      {showChat && (
        <div
          style={{
            position: "fixed",
            bottom: "110px",
            right: rightPos,
            width: "340px",
            height: "440px",
            background: "#fffdfa",
            borderRadius: "18px",
            boxShadow: "0 8px 32px rgba(246,137,26,0.18)",
            zIndex: 9999,
            display: "flex",
            flexDirection: "column",
            overflow: "hidden",
            border: "2px solid #f6891a",
          }}
        >
          <div
            style={{
              background: "linear-gradient(135deg, #f6891a 60%, #f6b01a 100%)",
              color: "#fff",
              padding: "16px",
              fontWeight: "bold",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              fontSize: "18px",
            }}
          >
            {/* Hiển thị tên người nhận nếu có */}
            Chat với{" "}
            {messages.length > 0
              ? (() => {
                  // Tìm tên người nhận từ messages
                  const firstMsg = messages.find(
                    (msg) => Number(msg.senderId) !== Number(senderId)
                  );
                  return firstMsg
                    ? firstMsg.senderName || firstMsg.sender || "người dùng"
                    : "người dùng";
                })()
              : "người dùng"}
            <button
              style={{
                background: "transparent",
                border: "none",
                color: "#fff",
                fontSize: "22px",
                cursor: "pointer",
              }}
              onClick={handleCloseChat}
              aria-label="Đóng chat admin"
            >
              ×
            </button>
          </div>
          <div
            ref={chatBoxRef}
            style={{
              flex: 1,
              padding: "16px",
              overflowY: "auto",
              background: "#fff8ef",
              display: "flex",
              flexDirection: "column",
              gap: "12px",
            }}
          >
            {Array.isArray(messages) &&
              messages.map((msg, idx) => {
                const isCurrentUserMessage =
                  Number(msg.senderId) === Number(senderId);
                return (
                  <div
                    key={idx}
                    style={{
                      alignSelf: isCurrentUserMessage
                        ? "flex-end"
                        : "flex-start",
                      background: isCurrentUserMessage ? "#f6891a" : "#fff3e0",
                      color: isCurrentUserMessage ? "#fff" : "#f6891a",
                      padding: "10px 16px",
                      borderRadius: isCurrentUserMessage
                        ? "16px 16px 4px 16px"
                        : "16px 16px 16px 4px",
                      maxWidth: "80%",
                      boxShadow: "0 1px 4px rgba(246,137,26,0.08)",
                      marginBottom: "8px",
                      border: isCurrentUserMessage
                        ? "1.5px solid #f6891a"
                        : "1.5px solid #ffe3c2",
                    }}
                  >
                    <div>{msg.content}</div>
                    <div
                      style={{
                        fontSize: "12px",
                        opacity: 0.7,
                        marginTop: "4px",
                        color: isCurrentUserMessage ? "#fffbe6" : "#f6b01a",
                      }}
                    >
                      {timeAgo(msg.timestamp || msg.createdAt)}
                    </div>
                  </div>
                );
              })}
            <div ref={chatBoxRef} />
          </div>
          <div
            style={{
              padding: "12px",
              borderTop: "2px solid #f6891a",
              background: "#fffdfa",
              display: "flex",
              alignItems: "center",
              gap: "8px",
            }}
          >
            <input
              type="text"
              placeholder="Nhập tin nhắn..."
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              onKeyDown={handleInputKeyDown}
              style={{
                flex: 1,
                padding: "8px 12px",
                borderRadius: "8px",
                border: "2px solid #f6891a",
                fontSize: "15px",
                background: "#fff8ef",
                color: "#333",
              }}
            />
            <button
              onClick={sendMessage}
              style={{
                background:
                  "linear-gradient(135deg, #f6891a 60%, #f6b01a 100%)",
                color: "#fff",
                border: "none",
                borderRadius: "8px",
                padding: "8px",
                cursor: "pointer",
                boxShadow: "0 2px 8px #f6891a22",
              }}
            >
              <MdSend size={20} />
            </button>
          </div>
        </div>
      )}
    </>
  );
}

export default ChatAdminWidget;
