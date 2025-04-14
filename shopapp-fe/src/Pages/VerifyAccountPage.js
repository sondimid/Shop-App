import React, { useState } from "react";
import OtpInput from "react-otp-input";
import Footer from "../components/Footer";
import Header from "../components/Header";
import { useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import SuccessModal from "../components/SuccessModal";
import axios from "axios";
function VerifyAccountPage() {
  const [otp, setOtp] = useState("");
  const [message, setMessage] = useState("");
  const [email, setEmail] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  
  useEffect(() => {
    const queryParams = new URLSearchParams(window.location.search);
    const emailFromQuery = queryParams.get("email");
    if (emailFromQuery) {
      setEmail(decodeURIComponent(emailFromQuery));
    }
  }, []);
    
  const handleVerify = async (e) => {
    e.preventDefault();
      if (otp.length === 6) {
          try {
              const response = await axios.post("http://localhost:8080/api/v1/users/verify-account", {
                email,
                otp  
              })
              setMessage(response.data)
              setIsModalOpen(true)
          } catch (error) {
              setMessage(error.data?.message)
              setIsModalOpen(true)
          }
    }
  };

  const handleResend = () => {
    console.log("Resend OTP");
    setOtp(""); 

  };

  return (
    <>
      <Header />
      <main>
        <section className="my-100">
          <div className="container">
            <div className="row justify-content-center align-items-center px-3">
              <div className="col-12 col-md-6 col-lg-4">
                <div>
                  <div className="mb-5 text-center">
                    <h1 className="mb-2 h2 fw-bold">Verify Your Account</h1>
                    <p className="text-muted">
                      Enter the 6-digit code sent to your email/phone
                    </p>
                  </div>
                  <form onSubmit={handleVerify}>
                    <div className="row g-3">
                      <div className="col-12">
                        <div className="d-flex justify-content-center mb-4">
                          <OtpInput
                            value={otp}
                            onChange={setOtp}
                            numInputs={6}
                            renderSeparator={<span style={{ width: "1rem" }} />}
                            renderInput={(props) => <input {...props} />}
                            inputStyle={{
                              width: "2.5rem",
                              height: "2.5rem",
                              fontSize: "1.25rem",
                              borderRadius: "4px",
                              border: "1px solid #ced4da",
                              textAlign: "center",
                              outline: "none",
                              margin: "0 0.25rem",
                              transition: "border-color 0.2s",
                            }}
                            inputType="tel"
                            shouldAutoFocus
                          />
                        </div>
                      </div>
                      <div className="col-12 d-grid gap-2">
                        <button
                          type="submit"
                          className="btn btn-primary"
                          disabled={otp.length !== 6}
                        >
                          Verify
                        </button>
                        <div className="text-center">
                          <p className="text-muted mb-0">
                            Didn't receive the code?{" "}
                            <button
                              type="button"
                              onClick={handleResend}
                              className="btn btn-link p-0 text-primary"
                            >
                              Resend OTP
                            </button>
                          </p>
                        </div>
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

export default VerifyAccountPage;
