import React, { useEffect } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useState } from "react";
import axiosInstance from "../utils/RefreshToken";
import SuccessModal from "../components/SuccessModal";
import Loading from "../components/Loading";
import axios from "axios";

function ResetPasswordPage() {
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [resetToken, setResetToken] = useState("");
  const [message, setMessage] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    setResetToken(urlParams.get("token"));
  }, []);

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/users/reset-password",
        {
          resetToken,
          newPassword,
          confirmPassword,
        }
      );
      setMessage(response.data);
    } catch (error) {
      setMessage(error.response.data);
    }
    setIsLoading(false);
    setIsModalOpen(true);
  };
  if(isLoading) {
    return <Loading />;
  }
  return (
    <>
      {isModalOpen && (
        <SuccessModal
          message={message}
          onClose={() => setIsModalOpen(false)}
          onConfirm={() => window.location.href = "/login"}
        />
      )}
      <Header />
      <main>
        
        <section className="my-100">
          <div className="container">
            <div className="row justify-content-center align-items-center px-3">
              <div className="col-12 col-md-6 col-lg-4">
                <div>
                  <div className="mb-5">
                    <h1 className="mb-2 h2 fw-bold">Reset your password</h1>
                  </div>
                  <form onSubmit={handleResetPassword}>
                    <div className="row g-3">
                      <div className="col-12">
                        <label
                          for="formForgetEmail"
                          className="form-label visually-hidden"
                        >
                          Mật Khẩu Mới
                        </label>
                        <input
                          type="password"
                          className="form-control"
                          id="formForgetEmail"
                          placeholder="New Password"
                          value={newPassword}
                          onChange={(e) => setNewPassword(e.target.value)}
                          required
                        />
                      </div>
                      <div className="col-12">
                        <label
                          for="formForgetEmail"
                          className="form-label visually-hidden"
                        >
                          Mật khẩu mới
                        </label>
                        <input
                          type="password"
                          className="form-control"
                          id="formForgetEmail1"
                          placeholder="Confirm Pasword"
                          value={confirmPassword}
                          onChange={(e) => setConfirmPassword(e.target.value)}
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

export default ResetPasswordPage;
