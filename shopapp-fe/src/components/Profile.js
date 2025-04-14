import React from "react";
import "../css/profile.css";
import { useEffect } from "react";
import { useState } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import Loading from "./Loading";
import { getAccessToken } from "../utils/AuthUtils";
import axiosInstance from "../utils/RefreshToken";

function Profile() {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [fullName, setFullName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");

  useEffect(() => {
    function fetchUserProfile() {
      const userData = Cookies.get("user");
      const parsedUser = JSON.parse(userData);
      setUser(parsedUser);
      setFullName(parsedUser.fullName);
      setPhoneNumber(parsedUser.phoneNumber);
      setAddress(parsedUser.address);
      setIsLoading(false);
    }
    fetchUserProfile();
  }, []);

  function handleUpdateProfile(e) {
    e.preventDefault();

    const accessToken = Cookies.get("accessToken");
    if (accessToken) {
      axiosInstance
        .put(
          "users/profiles",
          {
            fullName,
            phoneNumber,
            address,
          },
          {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          }
        )
        .then((response) => {
          setUser(response.data);
          Cookies.set("user", JSON.stringify(response.data));
          alert("Cập nhật thông tin thành công!");
          ;
        })
        .catch((error) => {
          alert(error.response);
        });
    }
  }

  function handleAvatarChange(e) {
    setIsLoading(true);
    const file = e.target.files[0];
    if (file) {
      const formData = new FormData();
      formData.append("file", file);

      const accessToken = Cookies.get("accessToken");
      if (accessToken) {
        axiosInstance
          .put("/users/avatar", formData, {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              "Content-Type": "multipart/form-data",
            },
          })
          .then((response) => {
            const updatedUser = { ...user, avatar: response.data };
            setUser(updatedUser);
            Cookies.set("user", JSON.stringify(updatedUser));
            alert("Cập nhật ảnh đại diện thành công!");
            window.location.reload()
          })
          .catch((error) => {
            alert(error.response.data);
          });
      }
    }
    setIsLoading(false);
  }

  if (isLoading) {
    return <Loading />;
  }

  return (
    <div className="profile-container">
      <div className="profile-content">
        <div className="profile-left">
          <h1 class="mb-2 h2 fw-bold">Thông Tin Cá Nhân</h1>
          <form className="profile-form" onSubmit={handleUpdateProfile}>
            <div className="form-group">
              <label htmlFor="username">Tên Người Dùng:</label>
              <input
                type="text"
                id="username"
                name="username"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="email">Email:</label>
              <input
                type="email"
                id="email"
                name="email"
                value={user.email}
                disabled
              />
            </div>
            <div className="form-group">
              <label htmlFor="phone">Số Điện Thoại:</label>
              <input
                type="tel"
                id="phone"
                name="phone"
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
              />
            </div>
            <div className="form-group">
              <label htmlFor="address">Địa Chỉ:</label>
              <input
                type="text"
                id="address"
                name="address"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
              />
            </div>
            <button type="submit" className="btn-save">
              Lưu Thay Đổi
            </button>
          </form>
        </div>
        <div className="profile-right">
          <h1 class="mb-2 h2 fw-bold">Ảnh Đại Diện</h1>
          <div className="avatar-container">
            <img
              src={user.avatar || "/assets/images/avatar/avatar-default.png"}
              alt="Avatar"
              className="avatar-image"
            />
            <input
              type="file"
              id="avatar"
              name="avatar"
              accept="image/*"
              style={{ display: "none" }}
              onChange={handleAvatarChange}
            />
            <button
              type="button"
              className="btn-save"
              onClick={() => document.getElementById("avatar").click()}
            >
              Thay Đổi Ảnh Đại Diện
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
