import React from "react";
import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";

function AdminRoute({ children }) {
  const userData = Cookies.get("user");
  if (!userData) {
    return <Navigate to="/" replace />;
  }

  let user;
  try {
    user = JSON.parse(userData);
  } catch (e) {
    return <Navigate to="/" replace />;
  }

  if (user.role === "ADMIN") {
    return children;
  }

  return <Navigate to="/" replace />;
}

export default AdminRoute;
