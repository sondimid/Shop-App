/* eslint-disable jsx-a11y/role-supports-aria-props */
import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import Footer from "../components/Footer";
import Cookies from "js-cookie";
import { Navigate, useLocation } from "react-router-dom";
import axiosInstance from "../utils/RefreshToken";
import Loading from "../components/Loading";
import { useNavigate } from "react-router-dom";
function OrderPage() {
  const [user, setUser] = useState(null);
  const location = useLocation();
  const { selectedProductDetails } = location.state;
  const [order, setOrder] = useState(null);
  const [accessToken, setAccessToken] = useState(null);
  const [loading, setLoading] = useState(false);

  const fetchUser = () => {
    const userData = Cookies.get("user");
    setAccessToken(Cookies.get("accessToken"));
    console.log(selectedProductDetails);
    setUser(JSON.parse(userData));
  };
  useEffect(() => {
    fetchUser();
  }, []);

  const handlePayment = async () => {
    setLoading(true);
    const code = parseInt(Date.now().toString().slice(-6));
    const orderDetailDTOs = selectedProductDetails.map((product) => ({
      name: product.name,
      productId: product.id,
      numberOfProducts: product.quantity,
      price: product.price,
      totalMoney: product.totalPrice,
      discount: product.totalDiscount,
    }));

    const newOrder = {
      ...order,
      fullName: user.fullName,
      email: user.email,
      phoneNumber: user.phoneNumber,
      address: user.address,
      shippingAddress: user.address,
      orderDetailDTOs: orderDetailDTOs,
      code: code,
      totalMoney: selectedProductDetails.reduce(
        (total, product) => total + product.price * product.quantity,
        5
      ),
    };

    try {
      const response = await axiosInstance.post(
        "/checkout/create-payment-link",
        newOrder,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      Cookies.set("orderCode", code);
      console.log(response.data);
      window.location.href = response.data
    } catch (error) {
      console.log(error.response);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <Loading />;
  }
  return (
    <>
      <Header />
      <main>
        <div class="product-checkout">
          <div class="mt-4">
            <div class="container">
              <div class="row">
                <div class="col-12">
                  <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                      <li class="breadcrumb-item">
                        <a href="#!">Home</a>
                      </li>
                      <li class="breadcrumb-item">
                        <a href="#!">My Cart</a>
                      </li>
                      <li class="breadcrumb-item active" aria-current="page">
                        Checkout
                      </li>
                    </ol>
                  </nav>
                </div>
              </div>

              <div class="row mt-50">
                <div class="col-lg-7">
                  <h4 class="title-2 mb-4">Chi Tiết Hoá Đơn</h4>
                  <div class="checkout-info">
                    <form action="#">
                      <h6>Thông Tin Cá Nhân</h6>
                      <div class="row">
                        <div class="col-md-12 mb-4">
                          <div class="input-item input-item-name gs_input_area">
                            <input
                              type="text"
                              name="gs_name"
                              placeholder="Họ Và Tên Người Nhận"
                              value={user?.fullName}
                              onChange={(e) => {
                                setUser({
                                  ...user,
                                  fullName: e.target.value,
                                });
                              }}
                            />
                          </div>
                        </div>

                        <div class="col-md-12 mb-4">
                          <div class="input-item input-item-phone gs_input_area">
                            <input
                              type="text"
                              name="gs_phone"
                              placeholder="Số Điện Thoại"
                              value={user?.phoneNumber}
                              onChange={(e) => {
                                setUser({
                                  ...user,
                                  phoneNumber: e.target.value,
                                });
                              }}
                            />
                          </div>
                        </div>

                        <div class="col-md-12 mb-4">
                          <div class="input-item input-item-email gs_input_area">
                            <input
                              type="email"
                              name="gs_email"
                              placeholder="Email"
                              value={user?.email}
                              onChange={(e) => {
                                setUser({
                                  ...user,
                                  email: e.target.value,
                                });
                              }}
                            />
                          </div>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-lg-12 col-md-12">
                          <h6>Address</h6>
                          <div class="row">
                            <div class="col-md-12 mb-4">
                              <div class="input-item gs_input_area">
                                <input
                                  type="text"
                                  placeholder="Địa Chỉ Cụ Thể"
                                  required
                                  value={user?.address}
                                  onChange={(e) => {
                                    setUser({
                                      ...user,
                                      address: e.target.value,
                                    });
                                  }}
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                      <h6>Ghi Chú (Không Bắt Buộc)</h6>
                      <div class="input-item input-item-textarea gs_input_area">
                        <textarea
                          name="gs_message"
                          rows="6"
                          placeholder="Notes about your order, e.g. Special notes for Delivery:"
                          required=""
                          onChange={(e) => {
                            setOrder({
                              ...order,
                              note: e.target.value,
                            });
                          }}
                        ></textarea>
                      </div>
                    </form>
                  </div>
                </div>
                <div class="col-lg-5 mt-5">
                  <div class="card bg-white">
                    <div class="card-header bg-white px-4">
                      <h2>Đơn Hàng Của Bạn</h2>
                    </div>
                    <div class="card-body p-0">
                      <div class="d-flex justify-content-between px-4 py-3">
                        <span class="p-summary-title">
                          <b>Sản Phẩm</b>
                        </span>
                        <span class="p-summary-price">
                          <b>Giá Tiền</b>
                        </span>
                      </div>

                      {selectedProductDetails.map((product, index) => (
                        <div className="cart-item" key={index}>
                          <div className="d-flex justify-content-between border-t">
                            <span className="p-summary-title">
                              {product.name} x {product.quantity}
                            </span>
                            <span className="p-summary-price">
                              {product.price * product.quantity}$
                            </span>
                          </div>
                        </div>
                      ))}

                      <div className="d-flex justify-content-between border-t">
                        <span className="p-summary-title">Phí Vận Chuyển</span>
                        <span className="p-summary-price">5$</span>
                      </div>

                      <div className="d-flex justify-content-between mt-2 border-t">
                        <span className="p-summary-total-title">Giảm Giá</span>
                        <span className="p-summary-price text-danger">
                          -
                          {selectedProductDetails.reduce(
                            (total, product) => total + product.totalDiscount,
                            0
                          )}
                          $
                        </span>
                      </div>

                      <div className="d-flex justify-content-between mt-2 border-t">
                        <span className="p-summary-total-title">Tổng Tiền</span>
                        <span className="p-summary-total-price">
                          {selectedProductDetails.reduce(
                            (total, product) =>
                              total + product.price * product.quantity,
                            5
                          )}
                          $
                        </span>
                      </div>
                    </div>
                  </div>

                  <div class="card mt-4">
                    <div class="card-header bg-white px-4">
                      <h2>Phương Thức Thanh Toán</h2>
                    </div>
                    <div class="border-top border-width-3 border-color-1 pt-3 mb-3">
                      <div class="accordion" id="paymentAccordion">
                        <div class="border-bottom border-color-1 border-dotted-bottom">
                          <div class="p-3" id="vnpayHeading">
                            <div class="custom-control custom-radio">
                              <input
                                type="radio"
                                class="custom-control-input"
                                id="vnpayRadio"
                                name="paymentMethod"
                                data-bs-toggle="collapse"
                                data-bs-target="#vnpayCollapse"
                                aria-expanded="false"
                                aria-controls="vnpayCollapse"
                                onClick={() => {
                                  setOrder({
                                    ...order,
                                    paymentMethod: "VNPAY",
                                  });
                                }}
                              />
                              <label
                                class="custom-control-label form-label"
                                for="vnpayRadio"
                              >
                                <img
                                  src="/assets/images/download.png"
                                  alt="VNPay"
                                  style={{ height: "20px", marginRight: "8px" }}
                                />
                                Chuyển Khoản Trực Tiếp (VNPay)
                              </label>
                            </div>
                          </div>
                        </div>

                        <div class="border-color-1 border-dotted-bottom">
                          <div class="p-3" id="codHeading">
                            <div class="custom-control custom-radio">
                              <input
                                type="radio"
                                class="custom-control-input"
                                id="codRadio"
                                name="paymentMethod"
                                data-bs-toggle="collapse"
                                data-bs-target="#codCollapse"
                                aria-expanded="false"
                                aria-controls="codCollapse"
                                onClick={() => {
                                  setOrder({
                                    ...order,
                                    paymentMethod: "COD",
                                  });
                                }}
                              />
                              <label
                                class="custom-control-label form-label"
                                for="codRadio"
                              >
                                Thanh toán khi nhận hàng (COD)
                              </label>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="checkout-btn mx-3 my-4">
                      <button
                        onClick={handlePayment}
                        class="btn btn-primary btn-lg w-100"
                      >
                        Đặt hàng
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </>
  );
}

export default OrderPage;
