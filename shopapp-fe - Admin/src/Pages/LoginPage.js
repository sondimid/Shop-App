import React, { useState } from "react";
import axiosInstance from "../utils/RefreshToken";
import Cookies from "js-cookie";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useEffect } from "react";
import { Link, Navigate } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import Loading from "../components/Loading";
import axios from "axios";
import ReCAPTCHA from "react-google-recaptcha";

function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [recaptchaToken, setRecaptchaToken] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please enter both phone and password!");
      return;
    }
    if (!recaptchaToken) {
      setError("Please verify the reCAPTCHA!");
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/admin/login",
        {
          email,
          password,
        }
      );
      Cookies.set("accessToken", response.data.accessToken);
      Cookies.set("refreshToken", response.data.refreshToken);

      const userRes = await axiosInstance.get("/users/profiles", {
        headers: {
          Authorization: `Bearer ${response.data.accessToken}`,
        },
      });
      Cookies.set("user", JSON.stringify(userRes.data));

      window.location.href = "/";
    } catch (error) {
      console.log(error);
      setError(error.response.data || "Login failed. Try again!");
    }
  };

  return (
    <>
      <Header />
      <main>
        <section className="my-100">
          <div className="container">
            <div className="row justify-content-center align-items-center px-3">
              <div className="col-12 col-md-6 col-lg-4">
                <div className="mb-5">
                  <h1 className="mb-1 h2 fw-bold">Login</h1>
                  <p>Welcome back! Enter your account to get started.</p>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}

                <form onSubmit={handleLogin}>
                  <div className="row g-3">
                    <div className="col-12">
                      <label
                        htmlFor="formSigninPhone"
                        className="form-label visually-hidden"
                      >
                        Phone
                      </label>
                      <input
                        type="tel"
                        className="form-control"
                        id="formSigninPhone"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                      />
                    </div>
                    <div className="col-12">
                      <div className="password-field position-relative">
                        <label
                          htmlFor="formSigninPassword"
                          className="form-label visually-hidden"
                        >
                          Password
                        </label>
                        <input
                          type="password"
                          className="form-control"
                          id="formSigninPassword"
                          placeholder="*******"
                          value={password}
                          onChange={(e) => setPassword(e.target.value)}
                          required
                        />
                      </div>
                    </div>
                    <div className="d-flex justify-content-between">
                      <div className="form-check">
                        <input
                          className="form-check-input"
                          type="checkbox"
                          id="flexCheckDefault"
                        />
                        <label
                          className="form-check-label"
                          htmlFor="flexCheckDefault"
                        >
                          Remember me
                        </label>
                      </div>
                      <div>
                        Forgot password?{" "}
                        <a href="/forgot-password" className="text-primary">
                          Reset It
                        </a>
                      </div>
                    </div>
                    {/* Đảm bảo ReCAPTCHA nằm trong col-12 để không bị ẩn bởi flex hoặc row */}
                    <div className="col-12 mb-3 d-flex justify-content-center">
                      <ReCAPTCHA
                        sitekey="6LfxtT8rAAAAAB9iQfAR3bTs39i2thLBr8N0qcgO"
                        onChange={(token) => setRecaptchaToken(token)}
                      />
                    </div>
                    <div className="col-12 d-grid">
                      <button type="submit" className="btn btn-primary">
                        Sign in
                      </button>
                    </div>
                  </div>
                  {/* Nếu vẫn không hiển thị, kiểm tra lại npm install react-google-recaptcha và đảm bảo không bị chặn bởi adblock/vpn */}
                </form>
              </div>
            </div>
          </div>
        </section>
      </main>

      <Footer />
    </>
  );
}

export default LoginPage;
