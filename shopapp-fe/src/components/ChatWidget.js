import React, { useState, useRef, useEffect } from "react";
import axios from "axios";

function ChatWidget() {
  const [products, setProducts] = useState([]);
  const [showChat, setShowChat] = useState(() => {
    const saved = localStorage.getItem("showChat");
    return saved === null ? true : saved === "true";
  });
  const [inputMessage, setInputMessage] = useState("");
  const [messages, setMessages] = useState([
    { from: "bot", text: "👋 Xin chào! Bạn cần hỗ trợ gì không?" },
  ]);
  const [selectedProductIds, setSelectedProductIds] = useState([]);
  const [isBotTyping, setIsBotTyping] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    if (messagesEndRef.current) {
      window.requestAnimationFrame(() => {
        messagesEndRef.current.scrollIntoView({ behavior: "auto" });
      });
    }
    localStorage.setItem("showChat", showChat);
  }, [messages, showChat]);

  const handleSend = async () => {
    if (inputMessage.trim() === "") return;

    const userMessage = inputMessage;
    setMessages((prev) => [...prev, { from: "user", text: userMessage }]);
    setInputMessage("");
    setIsBotTyping(true);

    const simpleProducts = products.map((p) => ({
      id: p.id,
      name: p.name,
      price: p.finalPrice,
      image: p.imageResponses?.[0]?.url || "",
    }));

    try {
      const response = await axios.post(
        "https://openrouter.ai/api/v1/chat/completions",
        {
          model: "openai/gpt-3.5-turbo",
          messages: [
            {
              role: "system",
              content:
                [
                  "Bạn là trợ lý bán hàng cho một cửa hàng chuyên về các sản phẩm điện tử như điện thoại, laptop, tai nghe, và máy tính bảng.",
                  "Hãy trả lời tất cả các câu hỏi liên quan đến những sản phẩm này.",
                  "Chỉ chọn ra duy nhất 1 sản phẩm phù hợp nhất với yêu cầu người dùng.",
                  "Khi trả lời về sản phẩm, hãy trả về đúng cấu trúc JSON sau (không thêm bất kỳ ký tự nào khác ngoài JSON này):",
                  '{"id": ..., "name": ..., "price": ..., "image": ...}',
                  "Khi lọc sản phẩm, hãy chú ý đến yêu cầu cụ thể của người dùng, ví dụ: nếu người dùng hỏi điện thoại Samsung thì chỉ trả về sản phẩm Samsung phù hợp nhất. Cấm trả về Iphone. Tìm điện thoại thì chỉ đc hiện ra điện thoại.",
                  "Khi không tìm thấy sản phẩm nào không phù hợp thì thông báo không tìm thấy sản phẩm. Khi khách hàng hỏi câu không liên quan đến đồ điện tử thì trả lời khéo léo nhưng vẫn cố gắng nhồi nhét thông tin liên quan đến cửa hàng , dài 1 chút",
                ].join("\n") +
                "\n" +
                JSON.stringify(simpleProducts),
            },
            { role: "user", content: userMessage },
          ],
        },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer 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`,
          },
        }
      );

      const botReply = response.data.choices[0].message.content;
      setMessages((prev) => [...prev, { from: "bot", text: botReply }]);
    } catch (error) {
      console.error(error);
      setMessages((prev) => [
        ...prev,
        {
          from: "bot",
          text: "Xin lỗi, hệ thống đang bận. Vui lòng thử lại sau!",
        },
      ]);
    } finally {
      setIsBotTyping(false);
    }
  };

  const handleInputKeyDown = (e) => {
    if (e.key === "Enter") handleSend();
  };

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/products/all"
        );
        console.log(response.data.content);
        setProducts(response.data.content);
      } catch (error) {
        console.log(error);
      }
    };

    fetchProducts();
  }, []);

  const getSelectedProducts = () => {
    return products.filter((p) => selectedProductIds.includes(p.id));
  };

  const handleProductSelect = (productId) => {
    setSelectedProductIds((prev) => [...prev, productId]);
    setMessages((prev) => [
      ...prev,
      { from: "system", text: `Bạn đã chọn sản phẩm có id: ${productId}` },
    ]);
  };

  useEffect(() => {
    const saved = localStorage.getItem("chatMessages");
    if (saved) setMessages(JSON.parse(saved));
  }, []);

  let openWidgets = [];
  try {
    openWidgets = JSON.parse(localStorage.getItem("openChatWidgets")) || [];
  } catch (e) {
    openWidgets = [];
  }
  let myIndex = openWidgets
    .filter((w) => w === "bot" || w === "admin")
    .indexOf("bot");
  if (!showChat) myIndex = -1;
  const rightPos = myIndex >= 0 ? 36 + myIndex * 370 : 36;

  const handleOpenChat = () => {
    let widgets = [];
    try {
      widgets = JSON.parse(localStorage.getItem("openChatWidgets")) || [];
    } catch (e) {
      widgets = [];
    }
    widgets = widgets.filter(
      (w) => w !== "bot" && (w === "bot" || w === "admin")
    );
    widgets.push("bot");
    localStorage.setItem("openChatWidgets", JSON.stringify(widgets));
    localStorage.setItem("showChat", true);
    setShowChat(true);
  };

  return (
    <>
      <button
        className="chat-bounce"
        style={{
          position: "fixed",
          bottom: "30px",
          right: 30,
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
            right: 36,
            width: "340px",
            height: "440px",
            background: "#fff",
            borderRadius: "18px",
            boxShadow: "0 8px 32px rgba(0,0,0,0.18)",
            zIndex: 9999,
            display: "flex",
            flexDirection: "column",
            overflow: "hidden",
            border: "1.5px solid #f6891a",
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
              position: "relative",
            }}
          >
            Chat hỗ trợ
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <button
                style={{
                  background: "transparent",
                  border: "none",
                  color: "#fff",
                  fontSize: "22px",
                  cursor: "pointer",
                  marginLeft: "8px",
                  padding: 0,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  width: 32,
                  height: 32,
                  borderRadius: "50%",
                  transition: "background 0.2s",
                }}
                title="Làm mới chat"
                onClick={() => {
                  localStorage.removeItem("chatMessages");
                  setMessages([
                    {
                      from: "bot",
                      text: "👋 Xin chào! Bạn cần hỗ trợ gì không?",
                    },
                  ]);
                }}
                aria-label="Làm mới chat"
              >
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                  <path
                    d="M10 3v2.5M10 3a7 7 0 1 1-7 7"
                    stroke="#fff"
                    strokeWidth="2"
                    strokeLinecap="round"
                  />
                  <path
                    d="M3 3v4h4"
                    stroke="#fff"
                    strokeWidth="2"
                    strokeLinecap="round"
                  />
                </svg>
              </button>
              <button
                style={{
                  background: "transparent",
                  border: "none",
                  color: "#fff",
                  fontSize: "22px",
                  cursor: "pointer",
                  marginLeft: "8px",
                  padding: 0,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  width: 32,
                  height: 32,
                  borderRadius: "50%",
                  transition: "background 0.2s",
                }}
                onClick={() => setShowChat(false)}
                aria-label="Đóng chat"
              >
                ×
              </button>
            </div>
          </div>
          <div
            className="chat-scrollbar"
            style={{
              flex: 1,
              padding: "16px",
              overflowY: "auto",
              background: "#fff8f1",
              fontSize: "15px",
              display: "flex",
              flexDirection: "column",
              gap: "12px",
            }}
          >
            {messages.map((msg, idx) => {
              if (msg.from === "bot") {
                // Nếu message là JSON sản phẩm
                let productObj = null;
                try {
                  productObj = JSON.parse(msg.text);
                } catch (e) {}
                if (
                  productObj &&
                  productObj.id != null &&
                  productObj.name &&
                  productObj.price != null &&
                  productObj.image
                ) {
                  return (
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
                      <div style={{ fontWeight: "bold", marginBottom: 8 }}>
                        Sản phẩm gợi ý:
                      </div>
                      <div
                        style={{
                          display: "flex",
                          alignItems: "center",
                          gap: 12,
                          background: "#fff",
                          borderRadius: 8,
                          padding: 8,
                          cursor: "pointer",
                          border: "1px solid #f6891a",
                          transition: "box-shadow 0.2s, border 0.2s",
                        }}
                        onClick={() => {
                          localStorage.setItem(
                            "chatMessages",
                            JSON.stringify(messages)
                          );
                          window.location.href = `/product/${productObj.id}`;
                        }}
                        title="Xem chi tiết sản phẩm này"
                      >
                        <img
                          src={productObj.image}
                          alt={productObj.name}
                          style={{
                            width: 56,
                            height: 56,
                            objectFit: "cover",
                            borderRadius: 8,
                          }}
                        />
                        <div>
                          <div
                            style={{
                              fontWeight: "bold",
                              color: "#f6891a",
                              fontSize: 16,
                            }}
                          >
                            {productObj.name}
                          </div>
                          <div style={{ color: "#333", fontSize: 14 }}>
                            Giá: {productObj.price} $
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                }
                // Regex cho dạng markdown có ảnh
                const productRegex = /\*\*\*\*\*([\s\S]*?)\*\*\*\*\*/g;
                const matches = [...msg.text.matchAll(productRegex)];
                if (matches.length > 0) {
                  // Parse từng block *****...*****
                  return (
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
                      <div style={{ fontWeight: "bold", marginBottom: 8 }}>
                        Sản phẩm gợi ý:
                      </div>
                      {matches.map((m, i) => {
                        // Tách các trường từ block *****...*****
                        const nameMatch = m[1].match(/Tên sản phẩm:\s*(.*)/i);
                        const priceMatch = m[1].match(
                          /Giá:\s*([\d,.]+)\s*USD?/i
                        );
                        const imgMatch = m[1].match(/!\[(.*?)\]\((.*?)\)/);
                        const name = nameMatch ? nameMatch[1].trim() : "";
                        const price = priceMatch ? priceMatch[1].trim() : "";
                        const imgUrl = imgMatch ? imgMatch[2] : "";
                        return (
                          <div
                            key={i}
                            style={{
                              display: "flex",
                              alignItems: "center",
                              gap: 12,
                              marginBottom: 10,
                              background: "#fff",
                              borderRadius: 8,
                              padding: 8,
                              cursor: "pointer",
                              border: "1px solid #f6891a",
                              transition: "box-shadow 0.2s, border 0.2s",
                            }}
                            onClick={() => handleProductSelect(name)}
                            title="Chọn sản phẩm này"
                          >
                            {imgUrl && (
                              <img
                                src={imgUrl}
                                alt={name}
                                style={{
                                  width: 56,
                                  height: 56,
                                  objectFit: "cover",
                                  borderRadius: 8,
                                }}
                              />
                            )}
                            <div>
                              <div
                                style={{
                                  fontWeight: "bold",
                                  color: "#f6891a",
                                  fontSize: 16,
                                }}
                              >
                                {name}
                              </div>
                              <div style={{ color: "#333", fontSize: 14 }}>
                                Giá: {price} USD
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  );
                }
                // Regex cho dạng markdown có ảnh
                const simpleProductRegex =
                  /\*\*(.*?)\*\* - Giá: (\d+\$) !\[(.*?)\]\((.*?)\)/g;
                const simpleMatches = [
                  ...msg.text.matchAll(simpleProductRegex),
                ];
                if (simpleMatches.length > 0) {
                  return (
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
                      <div style={{ fontWeight: "bold", marginBottom: 8 }}>
                        Danh sách sản phẩm:
                      </div>
                      {simpleMatches.map((m, i) => (
                        <div
                          key={i}
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: 12,
                            marginBottom: 10,
                            background: "#fff",
                            borderRadius: 8,
                            padding: 8,
                            cursor: "pointer",
                            border: "1px solid #f6891a",
                            transition: "box-shadow 0.2s, border 0.2s",
                          }}
                          onClick={() => handleProductSelect(m[1])}
                          title="Chọn sản phẩm này"
                        >
                          <img
                            src={m[4]}
                            alt={m[1]}
                            style={{
                              width: 56,
                              height: 56,
                              objectFit: "cover",
                              borderRadius: 8,
                            }}
                          />
                          <div>
                            <div
                              style={{
                                fontWeight: "bold",
                                color: "#f6891a",
                                fontSize: 16,
                              }}
                            >
                              {m[1]}
                            </div>
                            <div style={{ color: "#333", fontSize: 14 }}>
                              Giá: {m[2]}
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  );
                }
                // Regex cho dạng text: 1. Tên - giá$
                const textProductRegex = /\d+\.\s*([^-\n]+)-\s*(\d+\$)/g;
                const textMatches = [...msg.text.matchAll(textProductRegex)];
                if (textMatches.length > 0) {
                  return (
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
                      <div style={{ fontWeight: "bold", marginBottom: 8 }}>
                        Danh sách sản phẩm:
                      </div>
                      {textMatches.map((m, i) => (
                        <div
                          key={i}
                          style={{
                            display: "flex",
                            alignItems: "center",
                            gap: 12,
                            marginBottom: 10,
                            background: "#fff",
                            borderRadius: 8,
                            padding: 8,
                            cursor: "pointer",
                            border: "1px solid #f6891a",
                            transition: "box-shadow 0.2s, border 0.2s",
                          }}
                          onClick={() => handleProductSelect(m[1].trim())}
                          title="Chọn sản phẩm này"
                        >
                          <div
                            style={{
                              fontWeight: "bold",
                              color: "#f6891a",
                              fontSize: 16,
                            }}
                          >
                            {m[1].trim()}
                          </div>
                          <div
                            style={{
                              color: "#333",
                              fontSize: 14,
                              marginLeft: 8,
                            }}
                          >
                            Giá: {m[2]}
                          </div>
                        </div>
                      ))}
                    </div>
                  );
                }
                // Nếu có sản phẩm được chọn, hiển thị sản phẩm đầu tiên trong danh sách đã chọn
                const selectedList = getSelectedProducts();
                if (selectedList.length > 0 && idx === messages.length - 1) {
                  const prod = selectedList[0];
                  return (
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
                      <div style={{ display: "flex", alignItems: "center" }}>
                        <img
                          src={prod.imageResponses?.[0]?.url}
                          alt={prod.name}
                          style={{
                            width: 56,
                            height: 56,
                            objectFit: "cover",
                            borderRadius: 8,
                          }}
                        />
                        <div>
                          <div
                            style={{
                              fontWeight: "bold",
                              color: "#f6891a",
                              fontSize: 16,
                            }}
                          >
                            {prod.name}
                          </div>
                          <div
                            style={{
                              color: "#333",
                              margin: "4px 0",
                              fontSize: 14,
                            }}
                          >
                            Giá: {prod.price?.toLocaleString()} $
                          </div>
                        </div>
                      </div>
                    </div>
                  );
                }
                return (
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
                );
              } else if (msg.from === "system") {
                return (
                  <div
                    key={idx}
                    style={{
                      alignSelf: "center",
                      background: "#fffbe7",
                      color: "#f6891a",
                      padding: "8px 14px",
                      borderRadius: "12px",
                      maxWidth: "80%",
                      marginBottom: "4px",
                      fontStyle: "italic",
                      fontSize: 14,
                    }}
                  >
                    {msg.text}
                  </div>
                );
              } else {
                return (
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
                );
              }
            })}
            {isBotTyping && (
              <div
                style={{
                  alignSelf: "flex-start",
                  background: "#e9f3ff",
                  color: "#f6891a",
                  padding: "10px 16px",
                  borderRadius: "16px 16px 16px 4px",
                  maxWidth: "80%",
                  marginBottom: "4px",
                  boxShadow: "0 1px 4px rgba(0,0,0,0.04)",
                  fontStyle: "italic",
                  fontSize: 18,
                  letterSpacing: 2,
                  display: "flex",
                  alignItems: "center",
                  minHeight: 32,
                }}
              >
                <span
                  style={{
                    display: "inline-flex",
                    alignItems: "center",
                    height: 24,
                  }}
                >
                  <span
                    className="chat-typing-dot"
                    style={{
                      display: "inline-block",
                      width: 8,
                      height: 8,
                      borderRadius: "50%",
                      background: "#f6891a",
                      marginRight: 4,
                      animation: "chat-bounce-dot 0.9s infinite alternate",
                    }}
                  ></span>
                  <span
                    className="chat-typing-dot"
                    style={{
                      display: "inline-block",
                      width: 8,
                      height: 8,
                      borderRadius: "50%",
                      background: "#f6891a",
                      marginRight: 4,
                      animation:
                        "chat-bounce-dot 0.9s 0.18s infinite alternate",
                    }}
                  ></span>
                  <span
                    className="chat-typing-dot"
                    style={{
                      display: "inline-block",
                      width: 8,
                      height: 8,
                      borderRadius: "50%",
                      background: "#f6891a",
                      animation:
                        "chat-bounce-dot 0.9s 0.36s infinite alternate",
                    }}
                  ></span>
                </span>
                <style>
                  {`
                  @keyframes chat-bounce-dot {
                    0% { transform: translateY(0);}
                    30% { transform: translateY(-8px);}
                    60% { transform: translateY(0);}
                    100% { transform: translateY(0);}
                  }
                  `}
                </style>
              </div>
            )}
            {/* ref này sẽ luôn ở cuối danh sách */}
            <div ref={messagesEndRef} />
          </div>
          <div
            style={{
              padding: "12px",
              borderTop: "1.5px solid #ffd6a0",
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
                border: "1.5px solid #f6891a",
                fontSize: "15px",
                transition: "border 0.2s, box-shadow 0.2s",
                background: "#fff8f1",
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
                boxShadow: "0 2px 8px rgba(246,137,26,0.08)",
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
