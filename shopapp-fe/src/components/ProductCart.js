import React from "react";

const ProductCard = ({ product }) => {
  return (
    <div className="card product-card mx-2">
      <a href="shop-single.html">
        <img
          src="/assets/images/product/4.png"
          className="card-img-top"
          alt="eCommerce Template"
        />
      </a>
      <div className="card-body">
        <h5 className="card-title">
          <a href="shop-single.html">SAMSUNG Galaxy Book4 Intel Core 5 120U</a>
        </h5>
        <div className="d-block d-sm-block d-md-flex d-lg-flex justify-content-between align-items-center">
          <span className="text-muted strike-through">
            <s>$400.00</s>
          </span>
          <span className="sell-price">$300.00</span>
        </div>
        <div className="icons">
          <a
            href="#"
            data-bs-toggle="tooltip"
            data-bs-placement="left"
            title="Wishlist"
          >
            <i className="bi bi-heart"></i>{" "}
          </a>
          <a
            href="#"
            data-bs-toggle="tooltip"
            data-bs-placement="left"
            title="Quick view"
          >
            <i className="bi bi-eye"></i>
          </a>
          <a
            href="#"
            data-bs-toggle="tooltip"
            data-bs-placement="left"
            title="Compare"
          >
            <i className="bi bi-arrow-left-right"></i>{" "}
          </a>
        </div>
        <span className="discount-badge">10% OFF</span>
      </div>
      <a
        href="cart.html"
        className="d-block rounded py-2 text-center border mx-3 mb-3 btn-cart"
      >
        Add to Cart
      </a>
    </div>
  );
};

export default ProductCard;
