import React from "react";
import { useState } from "react";
import { getAccessToken } from "../utils/AuthUtils";
import Loading from "./Loading";
import SuccessModal from "./SuccessModal";
import axiosInstance from "../utils/RefreshToken";

function ChangePassword() {
  const [currentPassword, setCurrentPassword] = useState(null);
  const [newPassword, setNewPassword] = useState(null);
  const [confirmPassword, setConfirmPassword] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const accessToken = getAccessToken();
      const response = await axiosInstance.put(
        "/users/password",
        {
          currentPassword,
          newPassword,
          confirmPassword,
        },
        {
          headers: {
            Authorization: `Bear ${accessToken}`,
          },
        }
      );
      setMessage(response.data);
      setIsLoading(false);
    } catch (error) {
      console.log(error.response);
      setMessage(error.response.data);
      setIsLoading(false);
    }
    setIsModalOpen(true);
  };
  if (isLoading) {
    return <Loading />;
  }
  return (
    <>
      {isModalOpen && (
        <SuccessModal
          message={message}
          onClose={() => setIsModalOpen(false)}
          onConfirm={() => (window.location.href = "/account")}
        />
      )}
      <main>
        <section class="my-50 d-flex justify-content-center align-items-center">
          <div class="container">
            <div class="row justify-content-center align-items-center m">
              <div class="col-12 col-md-8 ">
                <div>
                  <div class="mb-5">
                    <h1 class="mb-2 h2 fw-bold">Đổi Mật Khẩu</h1>
                  </div>
                  <form onSubmit={handleSubmit}>
                    <div class="row g-3">
                      <div class="col-12">
                        <label
                          for="formForgetEmail"
                          class="form-label visually-hidden"
                        >
                          Old Password
                        </label>
                        <input
                          type="password"
                          class="form-control"
                          id="formForgetEmail"
                          placeholder="Mật Khẩu Hiện Tại"
                          value={currentPassword}
                          onChange={(e) => setCurrentPassword(e.target.value)}
                          required
                        />
                      </div>
                      <div class="col-12">
                        <label
                          for="formForgetEmail1"
                          class="form-label visually-hidden"
                        >
                          New Password
                        </label>
                        <input
                          type="password"
                          class="form-control"
                          id="formForgetEail"
                          placeholder="Mật Khẩu Mới"
                          value={newPassword}
                          onChange={(e) => setNewPassword(e.target.value)}
                          required
                        />
                      </div>
                      <div class="col-12">
                        <label
                          for="formForgetEmail"
                          class="form-label visually-hidden"
                        >
                          Email address
                        </label>
                        <input
                          type="password"
                          class="form-control"
                          id="formForgetEmail1"
                          placeholder="Xác Nhân Mật Khẩu"
                          value={confirmPassword}
                          onChange={(e) => setConfirmPassword(e.target.value)}
                          required
                        />
                      </div>

                      <div class="col-12 d-grid gap-2">
                        <button type="submit" class="btn btn-primary">
                          Đổi Mật Khẩu
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
    </>
  );
}

export default ChangePassword;
