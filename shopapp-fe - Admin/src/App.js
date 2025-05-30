import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./Pages/LoginPage";
import HomePage from "./Pages/HomePage";
import AuthenticateOAuth2 from "./components/AuthenticateOAuth2";
import AdminRoute from "./utils/AdminRoute";
import AdminPage from "./Pages/AdminPage";
import ChatAdminWidget from "./components/ChatAdminWidget";
import { getUser } from "./utils/AuthUtils";

function App() {
  const userInfo = getUser();
  const isLoggedIn = !!userInfo;
  const adminId = 1;

  console.log("User Info:", userInfo);
  console.log("Is Logged In:", isLoggedIn);

  const showChat = isLoggedIn;

  return (
    <BrowserRouter>
      <>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/authenticate" element={<AuthenticateOAuth2 />} />
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminPage />
              </AdminRoute>
            }
          />

          <Route path="*" element={<HomePage />} />
        </Routes>
      </>
    </BrowserRouter>
  );
}

export default App;
