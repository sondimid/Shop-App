/* eslint-disable jsx-a11y/anchor-is-valid */
import React from "react";
import { useState } from "react";
import { useEffect } from "react";
import axios from "axios"
import Loading from "./Loading";
import { handleAddToCart } from "../utils/AddToCart";

function DealOfTheDays() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/products/best-discount",
        );
        setProducts(response.data);
        setLoading(false);
      } catch (error) {
        console.error(error);
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  useEffect(() => {
    const initializeCarousel = () => {
      const $ = window.$;
      $(".product-slider").owlCarousel({
        responsiveClass: true,
        autoplay: true,
        dots: false,
        nav: true,
        responsive: {
          0: {
            items: 2,
          },
          600: {
            items: 3,
          },
          1000: {
            items: 5,
          },
        },
      });
    };

    if (!loading) {
      initializeCarousel();
    }
  }, [loading, products]);
  if (loading) {
    return <Loading />;
  }
  return (
    <>
      <div className="product mt-100">
        <div className="container">
          <div className="my-4 d-block d-sm-block d-md-flex d-lg-flex justify-content-between border-bottom pb-3">
            <div>
              <span className="section-title">Giảm Giá Hot</span>
            </div>
          </div>
          <div>
            <div className="owl-carousel product-slider">
              {products.map((product) => (
                <div className="card product-card mx-2" key={product.id}>
                  <a href={`/product/${product.id}`}
                    onClick={() => localStorage.setItem("product", JSON.stringify(product))}>
                      <img
                        src={product.imageResponses[0].url || "/assets/images/product/3.png"}
                        className="card-img-top"
                        alt={product.name}
                      />
                    </a>
                  <div className="body-card">
                    <div className="card-body">
                      <h5 className="card-title">
                        <a href="shop-single.html">{product.name}</a>
                      </h5>
                      <div className="d-block d-sm-block d-md-flex d-lg-flex justify-content-between align-items-center">
                        <span className="text-muted strike-through">
                          <s>{product.price}$</s>
                        </span>
                        <span className="sell-price">
                          {product.finalPrice}$
                        </span>
                      </div>
                      <div className="icons">
                        <a
                          href="#"
                          data-bs-toggle="tooltip"
                          data-bs-placement="left"
                          title="Wishlist"
                        >
                          <i className="bi bi-heart"></i>
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
                          <i className="bi bi-arrow-left-right"></i>
                        </a>
                      </div>
                      <span className="discount-badge">
                        {product.discount}% OFF
                      </span>
                    </div>
                    <a
                      style={{
                        cursor: "pointer",
                      }}
                      className="d-block rounded py-2 text-center border mx-3 mb-3 btn-cart"
                      onClick={() => handleAddToCart(product.id, 1)}
                    >
                      Add to Cart
                    </a>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default DealOfTheDays;
