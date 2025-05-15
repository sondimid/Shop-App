import React, { useState, useRef, useEffect } from "react";
import axios from "axios";

function ChatWidget() {
  const [showChat, setShowChat] = useState(true);
  const [inputMessage, setInputMessage] = useState("");
  const [messages, setMessages] = useState([
    { from: "bot", text: "👋 Xin chào! Bạn cần hỗ trợ gì không?" },
  ]);
  const messagesEndRef = useRef(null);

  // Tự động scroll xuống cuối khi có tin nhắn mới
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages, showChat]);

  const handleSend = async () => {
    if (inputMessage.trim() === "") return;
    setMessages((prev) => [...prev, { from: "user", text: inputMessage }]);
    const userMessage = inputMessage;
    setInputMessage("");
    try {
      const response = await axios.get("http://localhost:8080/api/v1/chat", {
        params: { message: userMessage },
      });
      setMessages((prev) => [...prev, { from: "bot", text: response.data }]);
    } catch (error) {
      setMessages((prev) => [
        ...prev,
        {
          from: "bot",
          text: "Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau!",
        },
      ]);
    }
  };
  const handleInputKeyDown = (e) => {
    if (e.key === "Enter") handleSend();
  };

  return (
    <>
      <button
        className="chat-bounce"
        style={{
          position: "fixed",
          bottom: "30px",
          right: "30px",
          zIndex: 9999,
          background: "linear-gradient(135deg, #f6891a 60%, #f6891a 100%)",
          color: "#fff",
          border: "none",
          borderRadius: "50%",
          width: "64px",
          height: "64px",
          boxShadow: "0 4px 16px rgba(0,0,0,0.18)",
          fontSize: "32px",
          cursor: "pointer",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          transition: "box-shadow 0.2s",
        }}
        onClick={() => setShowChat((prev) => !prev)}
        aria-label="Chat"
      >
        💬
      </button>

      {showChat && (
        <div
          style={{
            position: "fixed",
            bottom: "110px",
            right: "36px",
            width: "340px",
            height: "440px",
            background: "#fff",
            borderRadius: "18px",
            boxShadow: "0 8px 32px rgba(0,0,0,0.18)",
            zIndex: 9999,
            display: "flex",
            flexDirection: "column",
            overflow: "hidden",
            border: "1.5px solid #e3e3e3",
            animation: "fadeInChat 0.3s",
          }}
        >
          <div
            style={{
              background: "linear-gradient(135deg, #f6891a 60%, #f6891a 100%)",
              color: "#fff",
              padding: "16px",
              fontWeight: "bold",
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              fontSize: "18px",
              letterSpacing: "0.5px",
            }}
          >
            Chat hỗ trợ
            <button
              style={{
                background: "transparent",
                border: "none",
                color: "#fff",
                fontSize: "22px",
                cursor: "pointer",
                marginLeft: "8px",
              }}
              onClick={() => setShowChat(false)}
              aria-label="Đóng chat"
            >
              ×
            </button>
          </div>
          <div
            className="chat-scrollbar"
            style={{
              flex: 1,
              padding: "16px",
              overflowY: "auto",
              background: "#f8fafd",
              fontSize: "15px",
              display: "flex",
              flexDirection: "column",
              gap: "12px",
            }}
          >
            {messages.map((msg, idx) =>
              msg.from === "bot" ? (
                <div
                  key={idx}
                  style={{
                    alignSelf: "flex-start",
                    background: "#e9f3ff",
                    color: "#f6891a",
                    padding: "10px 16px",
                    borderRadius: "16px 16px 16px 4px",
                    maxWidth: "80%",
                    marginBottom: "4px",
                    boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
                  }}
                >
                  {msg.text}
                </div>
              ) : (
                <div
                  key={idx}
                  style={{
                    alignSelf: "flex-end",
                    background: "#f6891a",
                    color: "#fff",
                    padding: "10px 16px",
                    borderRadius: "16px 16px 4px 16px",
                    maxWidth: "80%",
                    marginBottom: "4px",
                    boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
                  }}
                >
                  {msg.text}
                </div>
              )
            )}
            {/* ref này sẽ luôn ở cuối danh sách */}
            <div ref={messagesEndRef} />
          </div>
          <div
            style={{
              padding: "12px",
              borderTop: "1px solid #e3e3e3",
              background: "#fff",
              display: "flex",
              alignItems: "center",
              gap: "8px",
            }}
          >
            <input
              type="text"
              placeholder="Nhập tin nhắn..."
              className="chat-input"
              value={inputMessage}
              onChange={(e) => setInputMessage(e.target.value)}
              onKeyDown={handleInputKeyDown}
              style={{
                flex: 1,
                padding: "8px 12px",
                borderRadius: "8px",
                border: "1.5px solid #d1d5db",
                fontSize: "15px",
                transition: "border 0.2s, box-shadow 0.2s",
                background: "#f8fafd",
              }}
            />
            <button
              className="chat-send-btn"
              style={{
                background:
                  "linear-gradient(135deg, #f6891a 60%, #f6891a 100%)",
                color: "#fff",
                border: "none",
                borderRadius: "8px",
                padding: "8px 18px",
                fontWeight: "bold",
                fontSize: "15px",
                cursor: "pointer",
                boxShadow: "0 2px 8px rgba(0,0,0,0.08)",
                transition: "background 0.2s",
              }}
              onClick={handleSend}
            >
              Gửi
            </button>
          </div>
        </div>
      )}
    </>
  );
}

export default ChatWidget;
