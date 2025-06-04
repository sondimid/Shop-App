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
    axios.defaults.withCredentials = true;
    e.preventDefault();
    if (!email || !password) {
      setError("Please enter both phone and password!");
      return;
    }
    if (!recaptchaToken) {
      setError("Vui lòng xác nhận bạn không phải là robot!");
      return;
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/users/login",
        {
          email,
          password,
        }
      );
      Cookies.set("accessToken", response.data.accessToken);
      const accessToken = response.data.accessToken;
      if (accessToken) {
        try {
          const response = await axiosInstance.get("/users/profiles", {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          });

          Cookies.set("user", JSON.stringify(response.data));
          Cookies.set("localLogin", "true");
          window.location.href = "/";
        } catch (error) {
          console.log(error);
          setError(error.response.data || "Login failed. Try again!");
        }
      }
    } catch (error) {
      setError(error.response.data || "Login failed. Try again!");
    }
  };
  const handleGoogleLogin = () => {
    window.location.href =
      "https://accounts.google.com/o/oauth2/v2/auth" +
      "?redirect_uri=http://localhost:3000/authenticate" +
      "&response_type=code" +
      "&client_id=567381700445-7gr2kvq4posh4f1fhca0fu3pv5qsvdmn.apps.googleusercontent.com" +
      "&scope=openid%20email%20profile" +
      "&access_type=offline" +
      "&state=google";
  };

  const handleFacebookLogin = () => {
    window.location.href =
      "https://www.facebook.com/v18.0/dialog/oauth?client_id=639375768955190&redirect_uri=http://localhost:3000/authenticate&scope=email,public_profile&response_type=code&state=facebook";
  };

  return (
    <>
      <Header />
      <main>
        <section className="my-100">
          <div className="container">
            <div className="row justify-content-center align-items-center px-3">
              <div className="col-12 col-md-6 col-lg-4">
                <div className="mb-5 text-center">
                  <h1 className="mb-1 h2 fw-bold">Welcome Back!</h1>
                  <p className="text-muted">
                    Enter your credentials to access your account
                  </p>
                </div>
                {error && (
                  <div className="alert alert-danger" role="alert">
                    <i className="bi bi-exclamation-circle me-2"></i>
                    {error}
                  </div>
                )}
                <form
                  onSubmit={handleLogin}
                  className="card shadow-sm border-0"
                >
                  <div className="card-body p-4">
                    <div className="mb-3">
                      <input
                        type="email"
                        className="form-control"
                        id="formSigninPhone"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        autoFocus
                      />
                    </div>
                    <div className="mb-3">
                      <input
                        type="password"
                        className="form-control"
                        id="formSigninPassword"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                      />
                    </div>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                      <div className="form-check m-0">
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
                      <a
                        href="/forgot-password"
                        className="text-primary text-decoration-none"
                      >
                        Forgot password?
                      </a>
                    </div>
                    <div className="mb-3 d-flex justify-content-center">
                      <ReCAPTCHA
                        sitekey="6LfxtT8rAAAAAB9iQfAR3bTs39i2thLBr8N0qcgO"
                        onChange={(token) => setRecaptchaToken(token)}
                      />
                    </div>
                    <div className="d-grid mb-3">
                      <button
                        type="submit"
                        className="btn btn-primary py-2"
                        disabled={!recaptchaToken}
                      >
                        Sign in
                      </button>
                    </div>
                    <div className="d-flex align-items-center my-3">
                      <hr className="flex-grow-1" />
                      <span
                        className="mx-3 text-muted"
                        style={{ fontSize: 14 }}
                      >
                        or continue with
                      </span>
                      <hr className="flex-grow-1" />
                    </div>
                    <div className="d-flex gap-2 mb-3 justify-content-center">
                      <button
                        type="button"
                        className="btn btn-light border w-50 d-flex align-items-center justify-content-center gap-2"
                        onClick={handleGoogleLogin}
                      >
                        <img
                          src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                          alt="Google logo"
                          width="20"
                          height="20"
                          style={{ display: "block", margin: "0 auto" }}
                        />
                        Google
                      </button>
                      <button
                        type="button"
                        className="btn btn-light border w-50 d-flex align-items-center justify-content-center gap-2"
                        onClick={handleFacebookLogin}
                      >
                        <img
                          src="https://upload.wikimedia.org/wikipedia/commons/0/05/Facebook_Logo_%282019%29.png"
                          alt="Facebook logo"
                          width="20"
                          height="20"
                          style={{ display: "block", margin: "0 auto" }}
                        />
                        Facebook
                      </button>
                    </div>
                    <div className="text-center mt-2">
                      <p className="mb-0" style={{ fontSize: 15 }}>
                        Don't have an account?{" "}
                        <Link
                          to="/register"
                          className="text-primary text-decoration-none"
                        >
                          Sign Up
                        </Link>
                      </p>
                    </div>
                  </div>
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
