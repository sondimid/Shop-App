import Cookies from "js-cookie";
import axios from "axios";

export const deleteUser = () => {
  Cookies.remove("user");
};

export const getUser = () => {
  return Cookies.get("user");
};
export const hasToken = () => {
  if (Cookies.get("accessToken") != null) {
    return true;
  }
  return false;
};

export const isOauth = () => {
  return localStorage.getItem("isOauth2") !== "false";
}

export const getAccessToken = () => {
  return Cookies.get("accessToken");
};

export const getRefreshToken = () => {
  return Cookies.get("refreshToken");
};

export const deleteToken = () => {
  Cookies.remove("accessToken");
  Cookies.remove("refreshToken");
};

export const handleLogout = (e) => {
    e.preventDefault();
    const accessToken = getAccessToken();
    const refreshToken = getRefreshToken();
    deleteToken();
    deleteUser();
    
    axios.post(
      "http://localhost:8080/api/v1/users/logout",
      {},
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          refresh_token: refreshToken,
        },
      }
    );
    window.location.href = "/login";
};

export const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString("vi-VN");
};

export const truncateText = (text, maxLength) => {
  return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
}; 