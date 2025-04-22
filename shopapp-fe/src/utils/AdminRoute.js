import React from "react";
import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";

function AdminRoute({ children }) {
  const userData = Cookies.get("user");
    const user = JSON.parse(userData)
  
    if (user.role === "ADMIN") {
      return children
    }

  window.location.href = "/"
}

export default AdminRoute;