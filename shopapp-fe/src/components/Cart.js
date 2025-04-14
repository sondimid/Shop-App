/* eslint-disable jsx-a11y/anchor-has-content */
/* eslint-disable no-undef */
/* eslint-disable jsx-a11y/anchor-is-valid */
import React from "react";
import { useState } from "react";
import Cookies from "js-cookie";
import { useEffect, useCallback } from "react";
import axiosInstance from "../utils/RefreshToken";
import Loading from "./Loading";
import { set } from "lodash";

function Cart() {
  const [isLoading, setIsLoading] = useState(true);
  const [cart, setCart] = useState(null);
  const [selectedProducts, setSelectedProducts] = useState([]);

  useEffect(() => {
    const fetchCart = async () => {
      const accessToken = Cookies.get("accessToken");
      try {
        const response = await axiosInstance.get("/carts", {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        setCart(response.data);
        setIsLoading(false);
      } catch (error) {
        alert(error.response);
        setIsLoading(false);
      }
    };
    fetchCart();
  }, []);

  const handleNumberOfProductChange = async (productId, newQuantity) => {
    const accessToken = Cookies.get("accessToken");

    try {
      const response = await axiosInstance.put(
        "/carts/number-of-product",
        {
          productId: productId,
          numberOfProduct: newQuantity,
        },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      setCart((prevCart) => {
        const updatedCart = { ...prevCart };
        const itemIndex = updatedCart.cartDetailResponses.findIndex(
          (item) => item.productResponse.id === productId
        );
        if (itemIndex !== -1) {
          updatedCart.cartDetailResponses[itemIndex].numberOfProducts =
            newQuantity;
        }
        return updatedCart;
      });
    } catch (error) {
      alert("Có lỗi xảy ra khi cập nhật số lượng sản phẩm.");
    }
  };

  const handleDeleteProduct = async (productId) => {
    const accessToken = Cookies.get("accessToken");

    try {
      const response = await axiosInstance.delete(`/carts/${productId}`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      setCart((prevCart) => {
        return {
          ...prevCart,
          cartDetailResponses: prevCart.cartDetailResponses.filter(
            (item) => item.productResponse.id !== productId
          ),
        };
      });
      console.log(response.data);
    } catch (error) {
      alert(error.response.data);
    }
  };

  const handleSelectProduct = (productId) => {
    setSelectedProducts((prevSelected) =>
      prevSelected.includes(productId)
        ? prevSelected.filter((id) => id !== productId)
        : [...prevSelected, productId]
    );
  };

  const handleSelectAll = () => {
    if (selectedProducts.length === cart.cartDetailResponses.length) {
      setSelectedProducts([]);
    } else {
      setSelectedProducts(
        cart.cartDetailResponses.map((item) => item.productResponse.id)
      );
    }
  };

  const handleDeleteSelectedProducts = async () => {
    const accessToken = Cookies.get("accessToken");

    try {
      const response = await axiosInstance.delete(
        `/carts/multi/${selectedProducts}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      setCart((prevCart) => {
        return {
          ...prevCart,
          cartDetailResponses: prevCart.cartDetailResponses.filter(
            (item) => !selectedProducts.includes(item.productResponse.id)
          ),
        };
      });
      alert("Xoá sản phẩm thành công");
    } catch (error) {
      alert(error.response.data);
    }
  };

  const calculateOriginalPrice = () => {
    return cart.cartDetailResponses
      .filter((item) => selectedProducts.includes(item.productResponse.id))
      .reduce((total, item) => {
        return total + item.productResponse.price * item.numberOfProducts;
      }, 0)
      .toFixed(2);
  };

  const calculateDiscountPrice = () => {
    return cart.cartDetailResponses
      .filter((item) => selectedProducts.includes(item.productResponse.id))
      .reduce((total, item) => {
        return (
          total +
          ((item.productResponse.price * item.productResponse.discount) / 100) *
            item.numberOfProducts
        );
      }, 0)
      .toFixed(2);
  };

  const calculateTotalAmount = () => {
    return calculateOriginalPrice() - calculateDiscountPrice();
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <main>
      <div className="product-cart">
        <div className="mt-4">
          <div className="container">
            <div className="row">
              <div className="col-12">
                <nav aria-label="breadcrumb">
                  <ol className="breadcrumb mb-0">
                    <li className="breadcrumb-item">
                      <a href="#!">Home</a>
                    </li>
                    <li className="breadcrumb-item active" aria-current="page">
                      My Cart
                    </li>
                  </ol>
                </nav>
              </div>
            </div>
          </div>
        </div>
        <style></style>
        <section className="mt-5 mb-5">
          <div className="container">
            <div className="row">
              <div className="col-lg-8">
                <h2>My Cart ({cart.cartDetailResponses.length})</h2>
                <div className="card">
                  <div className="card-header bg-white px-4 d-flex justify-content-between align-items-center">
                    <div>
                      <input
                        type="checkbox"
                        checked={
                          selectedProducts.length ===
                          cart.cartDetailResponses.length
                        }
                        onChange={handleSelectAll}
                        style={{
                          cursor: "pointer",
                          width: "15px",
                          height: "15px",
                        }}
                      />
                      <label className="ms-2">Chọn Tất Cả</label>
                    </div>
                  </div>
                  <div className="card-body p-0">
                    {cart.cartDetailResponses.map((item, index) => (
                      <div className="cart-item" key={index}>
                        <div className="d-flex align-items-center">
                          <input
                            type="checkbox"
                            checked={selectedProducts.includes(
                              item.productResponse.id
                            )}
                            onChange={() =>
                              handleSelectProduct(item.productResponse.id)
                            }
                          />
                          <div>
                            <a
                              href={`/product/${item.productResponse.name}`}
                              onClick={() =>
                                localStorage.setItem(
                                  "product",
                                  JSON.stringify(item.productResponse)
                                )
                              }
                            >
                              <img
                                src={item.productResponse.imageResponses[0].url}
                                alt="eCommerce HTML Template"
                              />
                            </a>
                          </div>
                          <div>
                            <div className="px-4">
                              <div>
                                <p className="p-title">
                                  {item.productResponse.name}
                                </p>
                              </div>
                              <div className="d-flex mt-3">
                                <span className="p-price">
                                  {item.productResponse.finalPrice}$
                                </span>
                                <span className="mx-2 text-muted p-discount">
                                  <s>
                                    {item.productResponse.price *
                                      item.numberOfProducts}
                                    $
                                  </s>
                                </span>
                                <span className="text-success p-off">
                                  {item.productResponse.discount}% Off
                                </span>
                              </div>
                              <div className="d-flex mt-4">
                                <div className="qty-container">
                                  <button
                                    className="qty-btn-minus count-decreament"
                                    type="button"
                                    onClick={() =>
                                      handleNumberOfProductChange(
                                        item.productResponse.id,
                                        item.numberOfProducts - 1
                                      )
                                    }
                                    disabled={item.numberOfProducts <= 1}
                                    style={{
                                      backgroundColor: "#f6891a",
                                    }}
                                  >
                                    <i className="bi bi-dash"></i>
                                  </button>
                                  <input
                                    type="number"
                                    name="qty"
                                    value={item.numberOfProducts}
                                    className="input-qty input-cornered"
                                    onChange={(e) =>
                                      handleNumberOfProductChange(
                                        item.productResponse.id,
                                        e.target.value
                                      )
                                    }
                                  />
                                  <button
                                    className="qty-btn-plus count-increament"
                                    type="button"
                                    onClick={() =>
                                      handleNumberOfProductChange(
                                        item.productResponse.id,
                                        item.numberOfProducts + 1
                                      )
                                    }
                                    style={{
                                      backgroundColor: "#f6891a",
                                    }}
                                  >
                                    <i className="bi bi-plus"></i>
                                  </button>
                                </div>
                                <div className="mx-4 remove-item">
                                  <a
                                    href="#"
                                    onClick={() =>
                                      handleDeleteProduct(
                                        item.productResponse.id
                                      )
                                    }
                                    className="text-muted"
                                    data-bs-toggle="tooltip"
                                    data-bs-placement="top"
                                    aria-label="Delete"
                                    data-bs-original-title="Delete"
                                  >
                                    <i className="bi bi-trash"></i>
                                  </a>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
              <div className="col-lg-4">
                <div className="card bg-white">
                  <div className="card-header bg-white px-4">
                    <h2>Chi Tiết Thanh Toán</h2>
                  </div>
                  <div className="card-body p-0">
                    <div className="px-4 py-3">
                      <div className="d-flex justify-content-between">
                        <span className="p-summary-title">Giá Gốc </span>
                        <span className="p-summary-price">
                          {calculateOriginalPrice()}$
                        </span>
                      </div>
                      <div className="d-flex justify-content-between mt-2">
                        <span className="p-summary-title">Giảm Giá</span>
                        <span className="p-summary-price text-danger">
                          -{calculateDiscountPrice()}$
                        </span>
                      </div>
                    </div>
                    <div className="d-flex justify-content-between mt-2 border-t">
                      <span className="p-summary-total-title">
                        Tổng Thanh Toán
                      </span>
                      <span className="p-summary-total-price">
                        {calculateTotalAmount()}$
                      </span>
                    </div>
                  </div>
                </div>
                <div className="checkout-btn mt-5">
                  <a
                    href="checkout.html"
                    className="btn btn-primary btn-lg w-100"
                  >
                    Thanh Toán
                  </a>
                </div>
              </div>
            </div>
            {selectedProducts.length > 0 && (
              <div className="d-flex justify-content-between mt-4">
                <button
                  className="btn btn-danger"
                  onClick={() => handleDeleteSelectedProducts()}
                >
                  Xoá Sản Phẩm
                </button>
              </div>
            )}
          </div>
        </section>
      </div>
    </main>
  );
}

export default Cart;
