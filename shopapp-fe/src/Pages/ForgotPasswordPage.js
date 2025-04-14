import React, { useEffect } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useState } from "react";
import axiosInstance from "../utils/RefreshToken";
import SuccessModal from "../components/SuccessModal";
import Loading from "../components/Loading";

function ForgotPasswordPage() {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("")
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)

  const handleForgotPassword = async (e) => {
    e.preventDefault();
    setIsLoading(true)
    try {
      const response = await axiosInstance.post(
        "/users/forgot-password",
        {
          email,
        }
      );

      setMessage(response.data);
      } catch (error) {
        setMessage(error.response.data);
    }
    setIsLoading(false)
    setIsModalOpen(true);
    };
    
    return (
      <>
        {isModalOpen && (
          <SuccessModal
            message={message}
            onClose={() => setIsModalOpen(false)}
            onConfirm={()=>(setIsModalOpen(false))}
          />
        )}
        <Header />
        <main>
          {isLoading && <Loading/>}
          <section className="my-100">
            <div className="container">
              <div className="row justify-content-center align-items-center px-3">
                <div className="col-12 col-md-6 col-lg-4">
                  <div>
                    <div className="mb-5">
                      <h1 className="mb-2 h2 fw-bold">Forgot your password?</h1>
                      <p>
                        Kindly provide the email address linked to your account,
                        and we'll send you a link via email to reset your
                        password.
                      </p>
                    </div>
                    <form onSubmit={handleForgotPassword}>
                      <div className="row g-3">
                        <div className="col-12">
                          <label
                            for="formForgetEmail"
                            className="form-label visually-hidden"
                          >
                            Email address
                          </label>
                          <input
                            type="email"
                            className="form-control"
                            id="formForgetEmail"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                          />
                        </div>
                        <div className="col-12 d-grid gap-2">
                          <button type="submit" className="btn btn-primary">
                            Reset Password
                          </button>
                        </div>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </main>
        <Footer />
      </>
    );
}

export default ForgotPasswordPage