/* eslint-disable jsx-a11y/anchor-is-valid */
import React from "react";
import { useState, useEffect } from "react";
import Loading from "./Loading";
import Header from "./Header";
import Footer from "./Footer";
import { formatDate } from "../utils/AuthUtils";
import axiosInstance from "../utils/RefreshToken";
import Cookies from "js-cookie";
import { handleAddToCart } from "../utils/AddToCart";
import axios from "axios";

function ProductDetail() {
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [relatedProducts, setRelatedProducts] = useState(null);
  const [mainImage, setMainImage] = useState(null);
  const [numberOfProduct, setNumberOfProduct] = useState(1);

  useEffect(() => {
    const storedProduct = JSON.parse(localStorage.getItem("product"));
    localStorage.setItem("productId", storedProduct.id);
    setProduct(storedProduct);
    setMainImage(storedProduct.imageResponses[0]?.url || "/assets/images/product/3.png");
  }, []);

  useEffect(() => {
    const fetchRelatedProducts = async () => {
      if (!product) return;

      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/products",
          {
            params: {
              categoryId: product.categoryId,
              limit: 10,
            },
          }
        );
        setRelatedProducts(response.data);
        setLoading(false);
      } catch (error) {
        console.log(error.response);
        setLoading(false);
      }
    };

    fetchRelatedProducts();
  }, [product]);

  useEffect(() => {
    const initScripts = () => {
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
      initScripts();
    }
  }, [loading]);

  const handleIncrement = () => {
    setNumberOfProduct((prev) => prev + 1);
  };

  const handleDecrement = () => {
    setNumberOfProduct((prev) => (prev > 1 ? prev - 1 : prev));
  };

  const handleAddToCart1 = async (e) => {
    e.preventDefault();
    const cartDTO = {
      cartDetailDTOS: [
        {
          productId: product.id,
          numberOfProducts: numberOfProduct,
          color: "blue",
        },
      ],
      idDeletes: [],
    };

    try {
      const accessToken = await Cookies.get("accessToken");
      if (!accessToken) {
        alert("Vui lòng đăng nhập trước khi thêm sản phẩm vào giỏ hàng!");
        window.location.href = "/login";
      }
      const response = await axiosInstance.put("/carts", cartDTO, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      alert("Thêm sản phẩm vào giỏ hàng thành công!");
    } catch (error) {
      alert(error.response.data);
    }
  };

  if (loading) {
    return <Loading />;
  }

  return (
    <>
      <Header />
      <div className="product-details">
        <main>
          <div className="mt-4">
            <div className="container">
              <div className="row">
                <div className="col-12">
                  <nav aria-label="breadcrumb">
                    <ol className="breadcrumb mb-0">
                      <li className="breadcrumb-item">
                        <a href="#">Home</a>
                      </li>
                      <li className="breadcrumb-item">
                        <a href={`/category/${product.category}`}
                        onClick={() =>{
                          localStorage.setItem("category", product.category)
                          localStorage.setItem("categoryId", product.categoryId)
                        }}>
                          {product.category}
                        </a>
                      </li>
                      <li
                        className="breadcrumb-item active"
                        aria-current="page"
                      >
                        {product.name}
                      </li>
                    </ol>
                  </nav>
                </div>
              </div>
            </div>
          </div>
          <section className="mt-5">
            <div className="container">
              <div className="row">
                <div className="col-md-5 col-xl-6 pe-lg-50">
                  <div
                    className="zoom border rounded"
                    id="product-img-zoom"
                    data-zoom-image={mainImage}
                  >
                    <img
                      src={mainImage}
                      className="w-100 rounded"
                      alt="eCommerce Template"
                    />
                  </div>
                  <div className="product-tools mt-3">
                    <div
                      className="thumbnails row g-3 slider-nav"
                      id="productThumbnails"
                      aria-label="Carousel Pagination"
                    >
                      {product.imageResponses.map((image, index) => (
                        <div className="col-3" key={index}>
                          <div className="thumbnails-img border rounded">
                            <img
                              src={image.url}
                              className="w-100 rounded"
                              alt={`Thumbnail ${index + 1}`}
                              onClick={() => setMainImage(image.url)}
                              style={{ cursor: "pointer" }}
                            />
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
                <div className="col-md-7 col-xl-6">
                  <div className="ps-lg-10 mt-5 mt-md-0">
                    <h1 className="mb-1">{product.name}</h1>
                    <div className="mb-4">
                      <small className="text-warning">
                        <i className="bi bi-star-fill"></i>
                        <i className="bi bi-star-fill"></i>
                        <i className="bi bi-star-fill"></i>
                        <i className="bi bi-star-fill"></i>
                        <i className="bi bi-star-half"></i>
                      </small>
                      <a href="#" className="ms-2">
                        {product.comments.length} Reviews
                      </a>
                    </div>
                    <div className="fs-4">
                      <span className="fw-bold text-dark">
                        {product.finalPrice}${" "}
                      </span>
                      <span className="text-muted">
                        <s className="ml-1">{product.price}$</s>
                      </span>
                      <span>
                        <small className="fs-6 ms-2 text-danger">
                          {product.discount}% Off
                        </small>
                      </span>
                    </div>

                    <hr className="my-4" />
                    <div className="mb-5">
                      <p className="mb-0">{product.description}</p>
                    </div>

                    <div className="product-action">
                      <div className="qty-container">
                        <button
                          className="qty-btn-minus count-decreament"
                          type="button"
                          onClick={handleDecrement}
                        >
                          <i className="bi bi-dash"></i>
                        </button>
                        <input
                          type="number"
                          name="qty"
                          value={numberOfProduct}
                          className="input-qty input-cornered"
                          onChange={(e) =>
                            setNumberOfProduct(Number(e.target.value))
                          }
                        />
                        <button
                          className="qty-btn-plus count-increament"
                          type="button"
                          onClick={handleIncrement}
                        >
                          <i className="bi bi-plus"></i>
                        </button>
                      </div>

                      <ul className="d-flex list-unstyled mt-4">
                        <li>
                          <a
                            className="btn btn-cart"
                            onClick={handleAddToCart1}
                          >
                            <i className="bi bi-bag me-2"></i>Add to cart
                          </a>
                        </li>
                        <li>
                          <a
                            className="btn btn-light"
                            href="#"
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                            aria-label="Compare"
                            data-bs-original-title="Compare"
                          >
                            <i className="bi bi-arrow-left-right"></i>
                          </a>
                        </li>
                        <li>
                          <a
                            className="btn btn-light"
                            href="shop-wishlist.html"
                            data-bs-toggle="tooltip"
                            data-bs-placement="top"
                            aria-label="Wishlist"
                            data-bs-original-title="Wishlist"
                          >
                            <i className="bi bi-heart"></i>
                          </a>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </section>
          <section className="mt-100">
            <div className="container">
              <div className="row">
                <div className="col-md-12">
                  <ul
                    className="nav nav-pills nav-lb-tab"
                    id="myTab"
                    role="tablist"
                  >
                    <li className="nav-item" role="presentation">
                      <button
                        className="nav-link"
                        data-bs-toggle="tab"
                        data-bs-target="#reviews-tab-pane"
                        type="button"
                        role="tab"
                        aria-controls="reviews-tab-pane"
                        aria-selected="false"
                        tabindex="-1"
                      >
                        Reviews
                      </button>
                    </li>
                  </ul>

                  <div className="tab-content" id="myTabContent">
                    <div
                      className="tab-pane fade show active"
                      role="tabpanel"
                      aria-labelledby="product-tab"
                      tabindex="0"
                    >
                      <div className="my-5">
                        <div className="row">
                          {product.comments.map((comment, index) => (
                            <div className="col-md-12" key={index}>
                              <div className="mb-5">
                                <div className="d-flex border-bottom pb-2 mb-2">
                                  <div className="me-3">
                                    <img
                                      src={
                                        comment.avatar ||
                                        "/assets/images/avatar/avatar-default.png"
                                      }
                                      alt={comment.userName}
                                      className="rounded-circle"
                                      style={{
                                        width: "50px",
                                        height: "50px",
                                        objectFit: "cover",
                                        border: "1px solid #ddd",
                                      }}
                                    />
                                  </div>
                                  <div>
                                    <h6 className="mb-1">{comment.userName}</h6>
                                    <p className="small">
                                      <span className="text-muted">
                                        {formatDate(comment.createdAt)}
                                      </span>
                                      <span className="text-primary ms-3 fw-bold">
                                        Verified
                                      </span>
                                    </p>
                                    <p>{comment.content}</p>
                                    <div className="comment-images mt-3">
                                      <div className="row g-2">
                                        <div className="col-3">
                                          <img
                                            src={comment.image}
                                            alt={comment.content}
                                            className="img-fluid rounded border"
                                          />
                                        </div>
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
                  </div>
                </div>
              </div>
            </div>
          </section>

          <section className="my-5">
            <div className="container">
              <div className="row">
                <div className="col-12">
                  <h3>Sản Phẩm Tương Tự</h3>
                </div>
              </div>
              <div className="product mt-4">
                <div className="owl-carousel product-slider">
                  {relatedProducts.content.map((product) => (
                    <div className="card product-card mx-2" key={product.id}>
                      <a
                        href={`/product/${product.id}`}
                        onClick={() =>
                          localStorage.setItem(
                            "product",
                            JSON.stringify(product)
                          )
                        }
                      >
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
          </section>
        </main>
      </div>
      <Footer />
    </>
  );
}

export default ProductDetail;
