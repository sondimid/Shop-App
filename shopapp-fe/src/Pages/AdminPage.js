import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import AdminUser from "../components/AdminUser";
import AdminProduct from "../components/AdminProduct";
import AdminCategory from "../components/AdminCategory";


function AdminPage() {
  const [activeMenu, setActiveMenu] = useState("Quản Lý Người Dùng");

  const renderContent = () => {
    if(activeMenu === "Quản Lý Người Dùng"){
      return <AdminUser/>
    }
    if(activeMenu === "Quản Lý Sản Phẩm"){
      return <AdminProduct/>
    }
    if(activeMenu === "Quản Lý Danh Mục"){
      return <AdminCategory/>
    }
  }

  return (
    <>
      <Header />
      <div className="profile-container">
        <div className="sidebar">
          <ul className="menu">
            <li
              className={activeMenu === "Quản Lý Người Dùng" ? "active" : ""}
              onClick={() => setActiveMenu("Quản Lý Người Dùng")}
            >
              Quản Lý Người Dùng
            </li>
            <li
              className={activeMenu === "Quản Lý Sản Phẩm" ? "active" : ""}
              onClick={() => setActiveMenu("Quản Lý Sản Phẩm")}
            >
              Quản Lý Sản Phẩm
            </li>
            <li
              className={activeMenu === "Quản Lý Danh Mục" ? "active" : ""}
              onClick={() => setActiveMenu("Quản Lý Danh Mục")}
            >
              Quản Lý Danh Mục  
            </li>
            <li>
              Quản Lý Đơn Hàng
              <ul className="submenu">
                <li
                  className={activeMenu === "Đơn Chờ Xác Nhận" ? "active" : ""}
                  onClick={() => setActiveMenu("Đơn Chờ Xác Nhận")}
                >
                  Đơn Chờ Xác Nhận
                </li>
                <li
                  className={activeMenu === "Đơn Đã Xác Nhận" ? "active" : ""}
                  onClick={() => setActiveMenu("Đơn Đã Xác Nhận")}
                >
                  Đơn Đã Xác Nhận
                </li>
                <li
                  className={activeMenu === "Đơn Đã Đóng Gói" ? "active" : ""}
                  onClick={() => setActiveMenu("Đơn Đã Đóng Gói")}
                >
                  Đơn Đã Đóng Gói
                </li>
                <li
                  className={activeMenu === "Đơn Đang Giao" ? "active" : ""}
                  onClick={() => setActiveMenu("Đơn Đang Giao")}
                >
                  Đơn Đang Giao
                </li>
                <li
                  className={
                    activeMenu === "Đơn Đã Giao Thành Công" ? "active" : ""
                  }
                  onClick={() => setActiveMenu("Đơn Đã Giao Thành Công")}
                >
                  Đơn Đã Giao Thành Công
                </li>
                <li
                  className={activeMenu === "Đơn Đã Hủy" ? "active" : ""}
                  onClick={() => setActiveMenu("Đơn Đã Hủy")}
                >
                  Đơn Đã Hủy
                </li>
              </ul>
            </li>
          </ul>
        </div>
        <div className="content">
        {renderContent()}
        </div>
      </div>

      <Footer />
    </>
  );
}

export default AdminPage;
