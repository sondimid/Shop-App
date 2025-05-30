import React, { useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import Cookies from "js-cookie";
import Header from "./Header";
import Footer from "./Footer";
import { useSearchParams, useNavigate } from "react-router-dom";

function OrderSuccess() {
  const [searchParams] = useSearchParams();
  const status = searchParams.get("status");
  const orderCode = searchParams.get("orderCode");
  const navigate = useNavigate();

  useEffect(() => {
    if (status === "PAID" && orderCode) {
      const accessToken = Cookies.get("accessToken");
      axiosInstance
        .put("/orders/success", null, {
          params: {
            orderId: parseInt(orderCode),
          },
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        })
        .then((res) => {
          console.log("Order confirmed:", res.data);
        })
        .catch((err) => {
          console.error("Failed to confirm order:", err.response);
        });
    }
  }, [status, orderCode]);

  return (
    <>
      <Header />
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        <h2>🎉 Thanh toán thành công!</h2>
        <p>Đơn hàng của bạn đã được gửi thông qua email. Vui lòng kiểm tra email để biết thêm chi tiết.</p>
        <div style={{ marginTop: "30px" }}>
          <button
            onClick={() => navigate("/")}
            style={{
              padding: "10px 20px",
              marginRight: "10px",
              backgroundColor: "#f6891a",
              color: "#fff",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
            }}
          >
            Về Trang Chủ
          </button>
          <button
            onClick={() => navigate("/order")}
            style={{
              padding: "10px 20px",
              backgroundColor: "#f6891a",
              color: "#fff",
              border: "none",
              borderRadius: "5px",
              cursor: "pointer",
            }}
          >
            Xem Đơn Mua
          </button>
        </div>
      </div>
      <Footer />
    </>
  );
}

export default OrderSuccess;