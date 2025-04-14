import React from "react";
import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";

function AdminRoute({ children }) {
  const user = JSON.parse(Cookies.get("user"));

  if (user.role !== "ADMIN") {
    window.location.href = "/";
  }

  return children;
}

export default AdminRoute;