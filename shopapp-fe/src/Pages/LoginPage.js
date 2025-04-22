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
function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Please enter both phone and password!");
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
      Cookies.set("refreshToken", response.data.refreshToken);
      const accessToken = response.data.accessToken;
      if (accessToken) {
        try {
          const response = await axiosInstance.get(
            "/users/profiles",
            {
              headers: {
                Authorization: `Bearer ${accessToken}`,
              },
            }
          );

          console.log(response.data);
          Cookies.set("user", JSON.stringify(response.data));
          Cookies.set("localLogin", "true");
          window.location.href = '/'
        } catch (error) {
          setError(error.message || "Login failed. Try again!");
        }
      }
      
    } catch (error) {
      setError(error.message || "Login failed. Try again!");
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
                    <div className="col-12 d-grid">
                      <button type="submit" className="btn btn-primary">
                        Sign in
                      </button>
                    </div>

                    {/* Social Login Buttons */}
                    <div className="col-12 mt-3">
                      <div className="d-flex gap-2">
                        <button
                          type="button"
                          className="btn btn-white border w-50 d-flex align-items-center justify-content-center gap-2 shadow-sm"
                          onClick={handleGoogleLogin}
                          style={{
                            backgroundColor: "white",
                            color: "#5F6368",
                            borderColor: "#DADCE0",
                          }}
                        >
                          <img
                            src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg"
                            alt="Google logo"
                            width="20"
                            height="20"
                          />
                          Google
                        </button>
                        <button
                          type="button"
                          className="btn btn-white border w-50 d-flex align-items-center justify-content-center gap-2 shadow-sm"
                          onClick={handleFacebookLogin}
                          style={{
                            backgroundColor: "white",
                            color: "#1877F2",
                            borderColor: "#DADCE0",
                          }}
                        >
                          <img
                            src="https://upload.wikimedia.org/wikipedia/commons/0/05/Facebook_Logo_%282019%29.png"
                            alt="Facebook logo"
                            width="20"
                            height="20"
                          />
                          Facebook
                        </button>
                      </div>
                    </div>

                    <div className="col-12 text-center mt-3">
                      Don't have an account?{" "}
                      <Link to="/register" className="text-primary">
                        Sign Up
                      </Link>
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
