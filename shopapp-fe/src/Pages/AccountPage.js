import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import Profile from "../components/Profile";
import Cookies from "js-cookie";
import Loading from "../components/Loading";
import ChangePasswordPage from "../components/ChangePassword";
import UserOrder from "../components/UserOrder";

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
        return <UserOrder status={"PENDING"} header={"Đơn Hàng Chờ Xác Nhận"}/>;
      case "Đang Vận Chuyển":
        return <UserOrder status={"IN_TRANSIT"} header={"Đơn Hàng Đang Vận Chuyển"}/>;
      case "Đơn Đã Giao Hàng":
        return <UserOrder status={"COMPLETED"} header={"Đơn Hàng Giao Thành Công"}/>;
      case "Đã Huỷ":
        return <UserOrder status={"CANCELLED"} header={"Đơn Hàng Đã Huỷ"}/>;
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
          <ul className="menu">
            <li>
               Tài Khoản Của Tôi
              <ul className="submenu">
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
               Đơn Hàng
              <ul className="submenu">
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
