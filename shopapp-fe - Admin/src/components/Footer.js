import React from "react";

function Footer() {
  return (
    <>
      <footer className="mt-50">
        <div className="container">
          <div className="footer">
            <div className="row">
              <div className="col-6 col-sm-6 col-md-3 col-lg-3 col-xl-3 mb-4 mb-sm-4 mb-md-4 mb-lg-0">
                <p className="m-0 p-0 fw-medium mb-2 text-uppercase ">
                  Navigate
                </p>
                <ul className="m-0 p-0 list-unstyled">
                  <li>
                    <a href="#">About us</a>
                  </li>
                  <li>
                    <a href="#">Shipping & Returns</a>
                  </li>
                  <li>
                    <a href="#">Contact Us</a>
                  </li>
                  <li>
                    <a href="#">Blog</a>
                  </li>
                </ul>
              </div>
              <div className="col-6 col-sm-6 col-md-3 col-lg-3 col-xl-3 mb-4 mb-sm-4 mb-md-4 mb-lg-0">
                <p className="m-0 p-0 fw-medium mb-2 text-uppercase">
                  Categories
                </p>
                <ul className="m-0 p-0 list-unstyled">
                  <li>
                    <a href="#">Laptop Accessories</a>
                  </li>
                  <li>
                    <a href="#">Mobile Accessories</a>
                  </li>
                  <li>
                    <a href="#">Camera Accessories</a>
                  </li>
                  <li>
                    <a href="#">Game Accessories</a>
                  </li>
                </ul>
              </div>
              <div className="col-6 col-sm-6 col-md-3 col-lg-3 col-xl-3 mb-4 mb-sm-4 mb-md-4 mb-lg-0">
                <p className="m-0 p-0 fw-medium mb-2 text-uppercase">Help</p>
                <ul className="m-0 p-0 list-unstyled">
                  <li>
                    <a href="#">Contact</a>
                  </li>
                  <li>
                    <a href="#">FAQ</a>
                  </li>
                  <li>
                    <a href="#">Career</a>
                  </li>
                  <li>
                    <a href="#">Events</a>
                  </li>
                </ul>
              </div>
              <div className="col-6 col-sm-6 col-md-3 col-lg-3 col-xl-3 mb-4 mb-sm-4 mb-md-4 mb-lg-0">
                <p className="m-0 p-0 fw-medium mb-2 text-uppercase">Address</p>
                <span>
                  Widgetify Inc. <br />
                  456 Gadget Avenue <br />
                  Techtown, TX 67890 <br />
                  United States of America
                </span>
              </div>
            </div>
            <hr />
            <div className="d-block d-sm-block d-md-flex d-lg-flex justify-content-between bottom-footer">
              <div className="mb-4 mb-sm-4 mb-md-4 mb-lg-0">
                <img
                  src="/assets/images/payment-method.png"
                  alt="eCommerce Html Template"
                />
              </div>
              <div>
                <a href="#">
                  <img
                    src="/assets/images/google-store.webp"
                    alt="eCommerce Html Template"
                  />
                </a>
                <a href="#">
                  <img
                    src="/assets/images/apple-store.webp"
                    alt="eCommerce Html Template"
                  />
                </a>
              </div>
            </div>
          </div>
        </div>
      </footer>
    </>
  );
}

export default Footer;
