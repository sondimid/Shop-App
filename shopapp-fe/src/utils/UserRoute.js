import React from "react";
import { Navigate } from "react-router-dom";
import Cookies from "js-cookie";

function UserRoute({ children }) {
  const userData = Cookies.get("user");
  const user = JSON.parse(userData)

  if(user.role === "USER" || user.role === "ADMIN"){
    return children
  }

  window.location.href = "/"
  
}

export default UserRoute;