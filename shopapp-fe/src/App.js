import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./Pages/LoginPage";
import HomePage from "./Pages/HomePage";
import RegisterPage from "./Pages/RegisterPage";
import VerifyAccountPage from "./Pages/VerifyAccountPage";
import ForgotPasswordPage from "./Pages/ForgotPasswordPage";
import ResetPasswordPage from "./Pages/ResetPasswordPage";
import ChangePasswordPage from "./components/ChangePassword";
import AuthenticateOAuth2 from "./components/AuthenticateOAuth2";
import ProductList from "./components/ProductList";
import ShopSingle from "./Pages/ShopSingle";
import ProductDetail from "./components/ProductDetail";
import AccountPage from "./Pages/AccountPage";
import UserRoute from "./utils/UserRoute";
import CartPage from "./Pages/CartPage";
import CategoryPage from "./Pages/CategoryPage";
function App() {
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
          <Route path="/product/:id" element={<ProductDetail />} />
          <Route path="/category/:id" element={<CategoryPage />} />
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
         
          <Route path="*" element={<HomePage />} />
        </Routes>
      </>
    </BrowserRouter>
  );
}

export default App;
