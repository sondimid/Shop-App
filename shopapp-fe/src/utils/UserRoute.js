import React from "react";
import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";

function UserRoute({ children }) {
  const user = Cookies.get("user");

  if (!user) {
    window.location.href = "/";
  }

  return children;
}

export default UserRoute;