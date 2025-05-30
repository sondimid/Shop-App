/* eslint-disable react/jsx-no-duplicate-props */
/* eslint-disable jsx-a11y/anchor-is-valid */
import React, { useState } from "react";
import { handleLogout } from "../utils/AuthUtils";
import Cookies from "js-cookie";
import Loading from "./Loading";
import axios from "axios";
import axiosInstance from "../utils/RefreshToken";
import ChatAdminWidget from "./ChatAdminWidget";

function Header() {
  const [loading, setLoading] = useState(false);
  const [showMessages, setShowMessages] = useState(false);
  const [messages, setMessages] = useState([]);
  const [messagesLoading, setMessagesLoading] = useState(false);
  const [chatWidgetInfo, setChatWidgetInfo] = useState(null);

  let isAdmin = false;
  let username = "";
  let userId = null;
  try {
    const user = Cookies.get("user");
    if (user) {
      const userObj = JSON.parse(user);
      isAdmin = (userObj.role || "").toLowerCase() === "admin";
      username = userObj.fullName || "";
      userId = userObj.id;
    }
  } catch (e) {}

  const fetchMessages = async () => {
    if (!userId) return;
    setMessagesLoading(true);
    try {
      const accessToken = Cookies.get("accessToken");
      const res = await axiosInstance.get("/chat/all", {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      setMessages(res.data || []);
      console.log(res.data);
    } catch (e) {
      setMessages([]);
    }
    setMessagesLoading(false);
  };

  React.useEffect(() => {
    fetchMessages();
  }, [userId]);

  const handleShowMessages = () => {
    setShowMessages((v) => {
      if (!v) fetchMessages();
      return !v;
    });
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <>
      <header>
        <div
          className="border-bottom"
          style={{
            background: "#fff",
            boxShadow: "0 2px 8px rgba(0,0,0,0.04)",
          }}
        >
          <div className="bg-light py-1" style={{ background: "#f8f9fa" }}>
            <div className="container">
              <div
                className="d-flex flex-column flex-md-row align-items-center justify-content-between"
                style={{ minHeight: 70 }}
              >
                {/* Logo đã bỏ */}
                {isAdmin && (
                  <div
                    className="text-center mx-auto"
                    style={{
                      marginTop: 0,
                      marginBottom: 0,
                    }}
                  >
                    <span
                      style={{
                        fontWeight: 700,
                        fontSize: "22px",
                        color: "#f6891f",
                        letterSpacing: "0.5px",
                        background: "#fff7f0",
                        borderRadius: "12px",
                        padding: "10px 32px",
                        boxShadow: "0 2px 8px rgba(246,137,31,0.10)",
                        display: "inline-block",
                        border: "1.5px solid #f6891f",
                      }}
                    >
                      Chào mừng quản trị viên
                      {username ? `, ${username}` : ""}!
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>
          <div
            className="py-4"
            style={{
              background: "#fff",
              borderBottomLeftRadius: "14px",
              borderBottomRightRadius: "14px",
            }}
          >
            <div className="container" style={{ padding: 0, maxWidth: "100%" }}>
              <div
                style={{
                  width: "100vw",
                  maxWidth: "100vw",
                  display: "flex",
                  justifyContent: "flex-end",
                  alignItems: "center",
                  minHeight: 48,
                  position: "relative",
                  paddingRight: "32px",
                }}
              >
                <div
                  className="d-flex align-items-center"
                  style={{ gap: "22px", position: "relative" }}
                >
                  {/* Badge Admin nếu là admin */}

                  {/* Nút Messages và Dropdown */}
                  <div style={{ position: "relative" }}>
                    <button
                      className="btn"
                      style={{
                        fontSize: "17px",
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                        background: "#fff",
                        border: "1.5px solid #f6891f",
                        color: "#f6891f",
                        borderRadius: "10px",
                        fontWeight: 600,
                        boxShadow: "0 1px 4px rgba(246,137,31,0.07)",
                        padding: "8px 22px",
                        transition: "background 0.2s, color 0.2s, border 0.2s",
                      }}
                      onClick={handleShowMessages}
                      // Xóa onBlur để tránh mất focus khi click vào message
                    >
                      <i className="bi bi-chat-dots"></i>
                      Messages
                    </button>
                    {showMessages && (
                      <div
                        className="card shadow"
                        style={{
                          position: "absolute",
                          top: "110%",
                          right: 0,
                          minWidth: "270px",
                          maxWidth: "420px",
                          zIndex: 100,
                          border: "1.5px solid #f6891f",
                          borderRadius: "12px",
                          overflow: "hidden",
                          boxShadow: "0 4px 24px rgba(246,137,31,0.13)",
                        }}
                      >
                        <div
                          className="card-header py-2 px-3 d-flex justify-content-between align-items-center"
                          style={{
                            borderBottom: "1px solid #ffe2c2",
                            background: "#fff7f0",
                            color: "#f6891f",
                            fontWeight: 700,
                            fontSize: "17px",
                          }}
                        >
                          <strong>Messages</strong>
                          <button
                            type="button"
                            className="btn-close"
                            aria-label="Close"
                            style={{ fontSize: "10px" }}
                            onClick={() => setShowMessages(false)}
                          ></button>
                        </div>
                        <ul
                          className="list-group list-group-flush"
                          style={{
                            maxHeight: "420px",
                            minWidth: "340px",
                            maxWidth: "420px",
                            overflowY: "auto",
                            background: "#fff",
                            fontSize: "17px",
                          }}
                        >
                          {messagesLoading ? (
                            <li className="list-group-item py-2 px-3 text-muted">
                              Đang tải tin nhắn...
                            </li>
                          ) : messages.length > 0 ? (
                            messages.map((msg) => (
                              <li
                                key={msg.id}
                                className="list-group-item py-2 px-3 message-hover"
                                style={{
                                  border: "none",
                                  borderBottom: "1px solid #ffe2c2",
                                  fontSize: "17px",
                                  transition: "background 0.2s, color 0.2s",
                                  borderRadius: 0,
                                  cursor: "pointer",
                                  position: "relative",
                                  background: "#fff",
                                  padding: "18px 18px",
                                  minHeight: "56px",
                                }}
                                onClick={() => {
                                  setChatWidgetInfo({
                                    senderId: userId,
                                    recipientId:
                                      Number(msg.senderId) === Number(userId)
                                        ? msg.recipientId
                                        : msg.senderId,
                                  });
                                  setShowMessages(false);
                                  setMessages((prev) =>
                                    prev.map((m) =>
                                      m.id === msg.id
                                        ? { ...m, isRead: true }
                                        : m
                                    )
                                  );
                                }}
                              > 
                                <div
                                  style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: "center",
                                  }}
                                >
                                  <span>
                                    <span style={{ fontWeight: 600 }}>
                                      {msg.senderName}:
                                    </span>{" "}
                                    {msg.content}
                                  </span>
                                  <span
                                    style={{
                                      fontSize: "12px",
                                      color: "#aaa",
                                      marginLeft: "12px",
                                      whiteSpace: "nowrap",
                                    }}
                                  >
                                    {msg.createdAt
                                      ? new Date(msg.createdAt).toLocaleString()
                                      : ""}
                                  </span>
                                </div>
                              </li>
                            ))
                          ) : (
                            <li
                              className="list-group-item py-2 px-3 text-muted"
                              style={{ border: "none" }}
                            >
                              Không có tin nhắn.
                            </li>
                          )}
                        </ul>
                        <style>
                          {`
                            .message-hover:hover {
                              background: #f6891f !important;
                              color: #fff !important;
                              cursor: pointer;
                            }
                          `}
                        </style>
                      </div>
                    )}
                  </div>
                  <button
                    className="btn"
                    style={{
                      fontSize: "17px",
                      display: "flex",
                      alignItems: "center",
                      gap: "8px",
                      background: "#f6891f",
                      color: "#fff",
                      border: "none",
                      borderRadius: "10px",
                      fontWeight: 600,
                      boxShadow: "0 1px 4px rgba(246,137,31,0.13)",
                      padding: "8px 26px",
                      transition: "background 0.2s, color 0.2s",
                    }}
                    onClick={handleLogout}
                  >
                    <i className="bi bi-box-arrow-right"></i>
                    Logout
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </header>

      {chatWidgetInfo && (
        <div
          style={{
            position: "fixed",
            bottom: "30px",
            right: "36px",
            zIndex: 9999,
          }}
        >
          <ChatAdminWidget
            senderId={chatWidgetInfo.senderId}
            recipientId={chatWidgetInfo.recipientId}
            onClose={() => setChatWidgetInfo(null)}
          />
        </div>
      )}
    </>
  );
}

export default Header;
