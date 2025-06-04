/* eslint-disable react/jsx-no-duplicate-props */
/* eslint-disable jsx-a11y/anchor-is-valid */
import React from "react";
import { handleLogout, hasToken } from "../utils/AuthUtils";
import Cookies from "js-cookie";
import { useState, useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import axios from "axios";
import Loading from "./Loading";

function Header() {
  const [user, setUser] = useState(null);
  const [categories, setCategories] = useState([]);
  const [cart, setCart] = useState({
    quantityProduct: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    function fetchUser() {
      const userData = Cookies.get("user");
      if (userData) {
        setUser(JSON.parse(userData));
      }
    }
    fetchUser();
  }, []);

  useEffect(() => {
    async function fetchCategories() {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/categories"
        );
        setCategories(response.data);
      } catch (error) {
        alert(error.response.data);
      }
    }
    fetchCategories();
  }, []);

  useEffect(() => {
    const fetchCart = async () => {
      const accessToken = Cookies.get("accessToken");
      if (accessToken) {
        try {
          const response = await axiosInstance.get("/carts", {
            headers: {
              Authorization: `Bearer ${accessToken}`,
            },
          });
          setCart(response.data);
          setLoading(false);
        } catch (error) {
          alert(error.response.data);
          setLoading(false);
        }
      }
      setLoading(false);
    };
    fetchCart();
  }, []);

  if (loading) {
    return <Loading />;
  }

  return (
    <>
      <header>
        <div className="border-bottom">
          <div className="bg-light py-1">
            <div className="container">
              <div className="row">
                <div className="col-md-6 col-12 text-center text-md-start">
                  <span>Super Deals Today - 10% Off All Category </span>
                </div>
              </div>
            </div>
          </div>
          <div className="py-4">
            <div className="container">
              <div className="row w-100 align-items-center gx-lg-2 gx-0">
                <div className="col-xxl-2 col-lg-3 col-md-6 col-5">
                  <a className="navbar-brand d-none d-lg-block" href="/">
                    <img
                      src="/assets/images/logo.png"
                      alt="eCommerce HTML Template"
                    />
                  </a>
                  <div className="d-flex justify-content-between w-100 d-lg-none">
                    <a className="navbar-brand" href="/">
                      <img
                        src="/assets/images/logo.png"
                        alt="eCommerce HTML Template"
                      />
                    </a>
                  </div>
                </div>
                <div className="col-xxl-8 col-lg-7 d-none d-lg-block"></div>
                <div className="col-lg-2 col-xxl-2 text-end col-md-6 col-7">
                  <div className="list-inline">
                    <div className="list-inline-item me-3 me-lg-4">
                      <a
                        href={Cookies.get("user") ? "/account" : "/login"}
                        className="login-btn"
                      >
                        {user && user.avatar ? (
                          <img
                            src={user.avatar}
                            alt="Avatar"
                            className="avatar-icon"
                            style={{
                              width: "30px",
                              height: "30px",
                              borderRadius: "50%",
                              objectFit: "cover",
                              display: "flex",
                            }}
                          />
                        ) : (
                          <i
                            className="bi bi-person"
                            style={{ fontSize: "30px", color: "black" }}
                          ></i>
                        )}
                      </a>
                    </div>
                    <div className="list-inline-item me-3 me-lg-4">
                      <a
                        className="text-muted position-relative"
                        href={Cookies.get("user") ? "/cart" : "/login"}
                      >
                        <i
                          className="bi bi-cart"
                          style={{ fontSize: "30px", display: "flex" }}
                        ></i>
                        <span
                          className="position-absolute top-0 start-100 translate-middle badge rounded-pill"
                          style={{ backgroundColor: "#f6891a" }}
                        >
                          {cart.quantityProduct || ""}
                        </span>
                      </a>
                    </div>
                    {Cookies.get("user") && (
                      <div className="list-inline-item me-3 me-lg-0">
                        <button
                          className="text-muted position-relative"
                          style={{
                            background: "none",
                            border: "none",
                            cursor: "pointer",
                          }}
                          onClick={handleLogout}
                        >
                          <i
                            className="bi bi-box-arrow-right"
                            style={{
                              fontSize: "30px",
                              display: "flex",
                              color: "black",
                            }}
                          ></i>
                        </button>
                      </div>
                    )}
                    <div className="list-inline-item d-lg-none">
                      <button
                        className="navbar-toggler collapsed"
                        type="button"
                        data-bs-toggle="offcanvas"
                        data-bs-target="#navbar-default"
                        aria-controls="navbar-default"
                        aria-label="Toggle navigation"
                      >
                        <i className="bi bi-text-indent-left"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <nav
            className="navbar navbar-expand-lg navbar-light navbar-default py-0 pb-lg-2"
            aria-label="Offcanvas navbar large"
          >
            <div className="container">
              <div
                className="offcanvas offcanvas-start"
                tabIndex={-1}
                id="navbar-default"
                aria-labelledby="navbar-defaultLabel"
              >
                <div className="offcanvas-header pb-1">
                  <a href="/">
                    <img
                      src="/assets/images/logo.png"
                      alt="eCommerce HTML Template"
                    />
                  </a>
                  <button
                    type="button"
                    className="btn-close"
                    data-bs-dismiss="offcanvas"
                    aria-label="Close"
                  ></button>
                </div>
                <div className="offcanvas-body">
                  <div className="d-block d-lg-none mb-4">
                    <form action="#"></form>
                  </div>
                  <div className="d-block d-lg-none mb-4">
                    <a
                      className="btn btn-primary w-100 d-flex justify-content-center align-items-center collapsed"
                      data-bs-toggle="collapse"
                      href="#collapseExample"
                      role="button"
                      aria-expanded="false"
                      aria-controls="collapseExample"
                    >
                      <span className="me-2">
                        <i className="bi bi-grid"></i>
                      </span>
                      All Categories
                    </a>
                    <div className="mt-2 collapse" id="collapseExample">
                      <div className="card card-body">
                        <ul className="mb-0 list-unstyled">
                          <li>
                            <a className="dropdown-item" href="/category/lap">
                              Laptop
                            </a>
                          </li>
                        </ul>
                      </div>
                    </div>
                  </div>
                  <div className="dropdown me-3 d-none d-lg-block">
                    <button
                      className="btn btn-primary px-6"
                      type="button"
                      id="dropdownMenuButton1"
                      data-bs-toggle="dropdown"
                      aria-expanded="false"
                    >
                      <span className="me-1">
                        <i className="bi bi-grid"></i>
                      </span>
                      All Categories
                    </button>
                    <ul
                      className="dropdown-menu"
                      aria-labelledby="dropdownMenuButton1"
                    >
                      {categories.map((category) => (
                        <li key={category.id}>
                          <a
                            className="dropdown-item"
                            href={`/category/${category.name}`}
                            onClick={() => {
                              localStorage.setItem("category", category.name);
                              localStorage.setItem("categoryId", category.id);
                            }}
                          >
                            {category.name}
                          </a>
                        </li>
                      ))}
                    </ul>
                  </div>
                  <div>
                    <ul className="navbar-nav align-items-center">
                      <li className="nav-item dropdown w-100 w-lg-auto">
                        <a className="nav-link" href="/">
                          Home
                        </a>
                      </li>
                      <li className="nav-item dropdown w-100 w-lg-auto">
                        <a
                          className="nav-link dropdown-toggle"
                          href="#"
                          role="button"
                          data-bs-toggle="dropdown"
                          aria-expanded="false"
                        >
                          Shop
                        </a>
                        <ul className="dropdown-menu">
                          <li>
                            <a className="dropdown-item" href="shop.html">
                              Shop Page - Filter
                            </a>
                          </li>
                          <li>
                            <a
                              className="dropdown-item"
                              href="shop-single.html"
                            >
                              Shop Single
                            </a>
                          </li>
                          <li>
                            <a className="dropdown-item" href="wishlist.html">
                              Shop Wishlist
                            </a>
                          </li>
                          <li>
                            <a className="dropdown-item" href="cart.html">
                              Shop Cart
                            </a>
                          </li>
                          <li>
                            <a className="dropdown-item" href="checkout.html">
                              Shop Checkout
                            </a>
                          </li>
                        </ul>
                      </li>
                      <li className="nav-item dropdown w-100 w-lg-auto dropdown-fullwidth">
                        <a
                          className="nav-link dropdown-toggle"
                          href="#"
                          role="button"
                          data-bs-toggle="dropdown"
                          aria-expanded="false"
                        >
                          Mega menu
                        </a>
                        <div className="dropdown-menu pb-0">
                          <div className="row p-2 p-lg-4">
                            <div className="col-lg-3 col-12 mb-4 mb-lg-0">
                              <h6 className="text-primary ps-3">
                                Laptop Accessories
                              </h6>
                              <a className="dropdown-item" href="#">
                                Mouse
                              </a>
                              <a className="dropdown-item" href="#">
                                Keyboard
                              </a>
                              <a className="dropdown-item" href="#">
                                USB
                              </a>
                              <a className="dropdown-item" href="#">
                                Data Cards
                              </a>
                              <a className="dropdown-item" href="#">
                                Router
                              </a>
                              <a className="dropdown-item" href="#">
                                Battery
                              </a>
                              <a className="dropdown-item" href="#">
                                Adapter
                              </a>
                              <a className="dropdown-item" href="#">
                                UPS
                              </a>
                            </div>
                            <div className="col-lg-3 col-12 mb-4 mb-lg-0">
                              <h6 className="text-primary ps-3">
                                Mobile Accessories
                              </h6>
                              <a className="dropdown-item" href="#">
                                Screen Guards
                              </a>
                              <a className="dropdown-item" href="#">
                                Plain Cases
                              </a>
                              <a className="dropdown-item" href="#">
                                Mobile Cables
                              </a>
                              <a className="dropdown-item" href="#">
                                Mobile Charger
                              </a>
                              <a className="dropdown-item" href="#">
                                Camera Lens
                              </a>
                              <a className="dropdown-item" href="#">
                                Mobile Flash
                              </a>
                              <a className="dropdown-item" href="#">
                                Power Bank
                              </a>
                              <a className="dropdown-item" href="#">
                                Mobile USB
                              </a>
                            </div>
                            <div className="col-lg-3 col-12 mb-4 mb-lg-0">
                              <h6 className="text-primary ps-3">
                                Camera Accessories
                              </h6>
                              <a className="dropdown-item" href="#">
                                Instant Camera
                              </a>
                              <a className="dropdown-item" href="#">
                                Drone
                              </a>
                              <a className="dropdown-item" href="#">
                                DSLR
                              </a>
                              <a className="dropdown-item" href="#">
                                Flashes
                              </a>
                              <a className="dropdown-item" href="#">
                                Sports & Action
                              </a>
                              <a className="dropdown-item" href="#">
                                Camera Lens
                              </a>
                              <a className="dropdown-item" href="#">
                                Camera tripods
                              </a>
                              <a className="dropdown-item" href="#">
                                Point & Shoot
                              </a>
                            </div>
                            <div className="col-lg-3 col-12 mb-4 mb-lg-0">
                              <div className="position-relative">
                                <img
                                  src="/assets/images/category/special-offer.png"
                                  className="rounded w-100"
                                  alt="eCommerce HTML Template"
                                />
                                <div className="position-absolute ps-6 mt-8 top-0 p-4">
                                  <h5 className="mb-0">
                                    Dont miss this
                                    <br />
                                    offer today.
                                  </h5>
                                  <a
                                    href="#"
                                    className="btn btn-cart btn-sm mt-3"
                                  >
                                    Shop Now
                                  </a>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </li>

                      <li className="nav-item dropdown w-100 w-lg-auto">
                        <a
                          className="nav-link dropdown-toggle"
                          href="#"
                          role="button"
                          data-bs-toggle="dropdown"
                          aria-expanded="false"
                        >
                          Account
                        </a>

                        <ul className="dropdown-menu">
                          {!hasToken() ? (
                            <>
                              <li>
                                <a href="/login" className="dropdown-item">
                                  Sign in
                                </a>
                              </li>
                              <li>
                                <a href="/register" className="dropdown-item">
                                  Sign up
                                </a>
                              </li>
                              <li>
                                <a
                                  className="dropdown-item"
                                  href="/forgot-password"
                                >
                                  Forgot Password
                                </a>
                              </li>
                            </>
                          ) : (
                            <>
                              <li>
                                <a
                                  href="/login"
                                  className="dropdown-item"
                                  onClick={handleLogout}
                                >
                                  Logout
                                </a>
                              </li>
                              <li>
                                <a
                                  href="/change-password"
                                  className="dropdown-item"
                                >
                                  Change Password
                                </a>
                              </li>
                            </>
                          )}
                        </ul>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
          </nav>
        </div>
      </header>
    </>
  );
}

export default Header;
