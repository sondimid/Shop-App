import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./Pages/LoginPage";
import HomePage from "./Pages/HomePage";
import RegisterPage from "./Pages/RegisterPage";
import VerifyAccountPage from "./Pages/VerifyAccountPage";
import ForgotPasswordPage from "./Pages/ForgotPasswordPage";
import ResetPasswordPage from "./Pages/ResetPasswordPage";
import AuthenticateOAuth2 from "./components/AuthenticateOAuth2";
import ProductDetail from "./components/ProductDetail";
import AccountPage from "./Pages/AccountPage";
import UserRoute from "./utils/UserRoute";
import CartPage from "./Pages/CartPage";
import CategoryPage from "./Pages/CategoryPage";
import AdminRoute from "./utils/AdminRoute";
import AdminPage from "./Pages/AdminPage";
import OrderPage from "./Pages/OrderPage";
import OrderSuccess from "./components/OrderSuccess";
import ChatWidget from "./components/ChatWidget";
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
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/verify-account" element={<VerifyAccountPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />
          <Route path="/reset-password" element={<ResetPasswordPage />} />
          <Route path="/authenticate" element={<AuthenticateOAuth2 />} />
          <Route path="/product/:productId" element={<ProductDetail />} />
          <Route path="/category/:categoryId" element={<CategoryPage />} />
          <Route
            path="/account"
            element={
              <UserRoute>
                <AccountPage />
              </UserRoute>
            }
          />
          <Route
            path="/cart"
            element={
              <UserRoute>
                <CartPage />
              </UserRoute>
            }
          />
          <Route
            path="/order"
            element={
              <UserRoute>
                <OrderPage />
              </UserRoute>
            }
          />
          <Route
            path="/order/success"
            element={
              <UserRoute>
                <OrderSuccess />
              </UserRoute>
            }
          />

          <Route path="*" element={<HomePage />} />
        </Routes>
        <ChatWidget />
        {showChat && (
          <ChatAdminWidget
            isLoggedIn={isLoggedIn}
            senderId={userInfo.id}
            recipientId={adminId}
          />
        )}
      </>
    </BrowserRouter>
  );
}

export default App;
