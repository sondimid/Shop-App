import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import Profile from "../components/Profile";
import { isOauth } from "../utils/AuthUtils";
import Cookies from "js-cookie";
import Loading from "../components/Loading";
import ChangePasswordPage from "../components/ChangePassword";

function AccountPage() {
  const [activeMenu, setActiveMenu] = useState("Hồ Sơ");
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  useEffect(() => {
    const fetchUser = async () => {
      const userData = Cookies.get("user");
      setUser(JSON.parse(userData));
      setIsLoading(false);
    };
    fetchUser();
  }, []);
  const renderContent = () => {
    switch (activeMenu) {
      case "Hồ Sơ":
        return <Profile />;
      case "Đổi Mật Khẩu":
        return <ChangePasswordPage />;
      case "Chờ Xác Nhận":
        return <div>Đây là danh sách đơn hàng Chờ Xác Nhận.</div>;
      case "Đang Vận Chuyển":
        return <div>Đây là danh sách đơn hàng Đang Vận Chuyển.</div>;
      case "Đơn Đã Giao Hàng":
        return <div>Đây là danh sách đơn hàng Đã Giao Hàng.</div>;
      case "Đã Huỷ":
        return <div>Đây là danh sách đơn hàng Đã Huỷ.</div>;
      default:
        return <div>Chọn một mục để xem nội dung.</div>;
    }
  };
  if (isLoading) {
    return <Loading />;
  }
  return (
    <>
      <Header />
      <div className="profile-container">
        <div className="sidebar">
          <div className="avatar"></div>
          <ul className="menu">
            <li>
              👤 Tài Khoản Của Tôi
              <ul>
                <li
                  className={activeMenu === "Hồ Sơ" ? "active" : ""}
                  onClick={() => setActiveMenu("Hồ Sơ")}
                >
                  Hồ Sơ
                </li>
                {1 && (
                  <li
                    className={activeMenu === "Đổi Mật Khẩu" ? "active" : ""}
                    onClick={() => setActiveMenu("Đổi Mật Khẩu")}
                  >
                    Đổi Mật Khẩu
                  </li>
                )}
              </ul>
            </li>
            <li>
              📦 Đơn Hàng
              <ul>
                <li
                  className={activeMenu === "Chờ Xác Nhận" ? "active" : ""}
                  onClick={() => setActiveMenu("Chờ Xác Nhận")}
                >
                  Chờ Xác Nhận
                </li>
                <li
                  className={activeMenu === "Đang Vận Chuyển" ? "active" : ""}
                  onClick={() => setActiveMenu("Đang Vận Chuyển")}
                >
                  Đang Vận Chuyển
                </li>
                <li
                  className={activeMenu === "Đơn Đã Giao Hàng" ? "active" : ""}
                  onClick={() => setActiveMenu("Đơn Đã Giao Hàng")}
                >
                  Đơn Đã Giao Hàng
                </li>
                <li
                  className={activeMenu === "Đã Huỷ" ? "active" : ""}
                  onClick={() => setActiveMenu("Đã Huỷ")}
                >
                  Đã Huỷ
                </li>
              </ul>
            </li>
          </ul>
        </div>
        <div className="content">{renderContent()}</div>
      </div>
      <Footer />
    </>
  );
}

export default AccountPage;
