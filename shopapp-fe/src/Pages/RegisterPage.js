import React, { useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import axiosInstance from "../utils/RefreshToken";
import SuccessModal from "../components/SuccessModal";
import { Link } from "react-router-dom";
import Loading from "../components/Loading";
import axios from "axios";
function RegisterPage() {
  const [email, setEmail] = useState("");
  const [fullName, setFullName] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false); 
  const [message, setSuccessMessage] = useState(""); 
  const [isLoading, setIsLoading] = useState(false)

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (!email || !fullName || !password || !confirmPassword) {
      setError("Please enter all fields!");
      return;
    }

    try {
      setIsLoading(true)
      const response = await axios.post("http://localhost:8080/api/v1/users/register", {
        email,
        fullName,
        password,
        confirmPassword,
      });
      setIsLoading(false)
      window.location.href = `/verify-account?email=${encodeURIComponent(email)}`;
    } catch (error) {
      setIsLoading(false)
      setError(error.response?.data || "Signup failed. Try again!");
    }
  };

  return (
    <>
      <Header />
      <main>
        {isLoading && <Loading />}
        <section className="my-100">
          <div className="container">
            <div className="row justify-content-center align-items-center px-3">
              <div className="col-12 col-md-6 col-lg-4">
                <div className="mb-5">
                  <h1 className="mb-1 h2 fw-bold">Signup</h1>
                  <p>Welcome! Enter your phone number to create an account.</p>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}
                <form onSubmit={handleRegister}>
                  <div className="row g-3">
                    <div className="col-12">
                      <input
                        type="text"
                        className="form-control"
                        placeholder="Full Name"
                        required
                        value={fullName}
                        onChange={(e) => setFullName(e.target.value)}
                      />
                    </div>
                    <div className="col-12">
                      <input
                        type="email"
                        className="form-control"
                        placeholder="Email"
                        required
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                      />
                    </div>
                    <div className="col-12">
                      <input
                        type="password"
                        className="form-control"
                        placeholder="Password"
                        required
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                      />
                    </div>
                    <div className="col-12">
                      <input
                        type="password"
                        className="form-control"
                        placeholder="Confirm Password"
                        required
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                      />
                    </div>
                    <div className="col-12 d-grid">
                      <button type="submit" className="btn btn-primary">
                        Register
                      </button>
                    </div>
                  </div>
                  <div className="col-12 text-center mt-3">
                    Have an account?{" "}
                    <Link to="/login" className="text-primary">
                      Sign In
                    </Link>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </section>
      </main>
      <Footer />

      {isModalOpen && (
        <SuccessModal
          message={message}
          onClose={() => setIsModalOpen(false)}
          onConfirm={() => (window.location.href = "/login")}
        />
      )}
    </>
  );
}

export default RegisterPage;
