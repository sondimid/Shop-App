import React, { useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { Link, useNavigate } from "react-router-dom";
import Loading from "../components/Loading";
import axios from "axios";

function RegisterPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    fullName: "",
    password: "",
    confirmPassword: ""
  });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (!formData.email || !formData.fullName || !formData.password || !formData.confirmPassword) {
      setError("Please enter all fields!");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match!");
      return;
    }

    try {
      setIsLoading(true);
      await axios.post(
        "http://localhost:8080/api/v1/users/register",
        formData
      );
      navigate(`/verify-account?email=${encodeURIComponent(formData.email)}`);
    } catch (error) {
      setError(error.response?.data || "Signup failed. Try again!");
    } finally {
      setIsLoading(false);
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
                  <p>Welcome! Enter your details to create an account.</p>
                </div>

                {error && <div className="alert alert-danger">{error}</div>}

                <form onSubmit={handleRegister}>
                  <div className="row g-3">
                    <div className="col-12">
                      <input
                        type="text"
                        name="fullName"
                        className="form-control"
                        placeholder="Full Name"
                        required
                        value={formData.fullName}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12">
                      <input
                        type="email"
                        name="email"
                        className="form-control"
                        placeholder="Email"
                        required
                        value={formData.email}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12">
                      <input
                        type="password"
                        name="password"
                        className="form-control"
                        placeholder="Password"
                        required
                        value={formData.password}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12">
                      <input
                        type="password"
                        name="confirmPassword"
                        className="form-control"
                        placeholder="Confirm Password"
                        required
                        value={formData.confirmPassword}
                        onChange={handleChange}
                      />
                    </div>
                    <div className="col-12 d-grid">
                      <button
                        type="submit"
                        className="btn btn-primary"
                        disabled={isLoading}
                      >
                        {isLoading ? "Registering..." : "Register"}
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
    </>
  );
}

export default RegisterPage;
