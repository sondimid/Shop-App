/* eslint-disable jsx-a11y/anchor-is-valid */
import React from "react";
import { useState } from "react";
import { useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import Loading from "./Loading";
import { set } from "lodash";
import Select from "react-select";
import axios from "axios";

function CategoryDetail() {
  const [isLoading, setIsLoading] = useState(true);
  const [products, setProducts] = useState([]);
  const [sortBy, setSortBy] = useState("DESC");
  const [sortField, setSortField] = useState("price");
  const [keyword, setKeyword] = useState("");
  const [selectedOption, setSelectedOption] = useState(null);
  const [fromPrice, setFromPrice] = useState(0);
  const [toPrice, setToPrice] = useState(2000);
  const [page, setPage] = useState({
    pageNumber: 0,
    totalPages: 1
  })

  const options = [
    { value: "high-to-low-price", label: "Giá: Cao-Thấp" },
    { value: "low-to-high-price", label: "Giá: Thấp-Cao" },
    { value: "hot-discount", label: "Giảm Giá Hot" },
    { value: "new-arrivals", label: "Hàng Mới" },
  ];

  useEffect(() => {
    setIsLoading(true);
    const fetchProducts = async () => {
      try {
        const categoryId = localStorage.getItem("categoryId");
        const response = await axios.get(
          "http://localhost:8080/api/v1/products",
          {
            params: {
              categoryId: categoryId,
              page: page.pageNumber,
              limit: 6,
              sort: sortBy,
              sortField: sortField,
              keyword: keyword,
              toPrice: toPrice,
              fromPrice: fromPrice,
            },
          }
        );
        setProducts(response.data.content);
        setPage(() => {
          return {
            pageNumber: response.data.pageNumber,
            totalPages: response.data.totalPages
          }
        });
        setIsLoading(false);
      } catch (error) {
        alert(error.response);
        setIsLoading(false);
      }
    };
    fetchProducts();
  }, [sortBy, sortField, page.pageNumber]);

  const fetchProductsByKeyWord = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/products",
        {
          params: {
            categoryId: localStorage.getItem("categoryId"),
            limit: 6,
            page: 0,
            sort: sortBy,
            sortField: sortField,
            keyword: keyword,
            fromPrice: fromPrice,
            toPrice: toPrice,
          },
        }
      );
      setProducts(response.data.content);
      setPage(() => {
        return {
          pageNumber: response.data.pageNumber,
          totalPages: response.data.totalPages
        }
      });
      setIsLoading(false);
    } catch (error) {
      alert(error.response);
      setIsLoading(false);
    }
  };

  const handleSortChange = (selectedOption) => {
    const value = selectedOption.value;
    setSelectedOption(selectedOption);
    if (value === "high-to-low-price") {
      setSortBy("DESC");
      setSortField("finalPrice");
    } else if (value === "low-to-high-price") {
      setSortBy("ASC");
      setSortField("finalPrice");
    } else if (value === "hot-discount") {
      setSortBy("DESC");
      setSortField("discount");
    } else if (value === "new-arrivals") {
      setSortBy("DESC");
      setSortField("createdAt");
    }
  };

  const handlePreviousPage = () => {
    if (page.pageNumber > 0) {
      setPage((prev) => ({
        ...prev,
        pageNumber: prev.pageNumber - 1,
      }));
    }
  };
  
  const handleNextPage = () => {
    if (page.pageNumber < page.totalPages - 1) {
      setPage((prev) => ({
        ...prev,
        pageNumber: prev.pageNumber + 1,
      }));
    }
  };

  if (isLoading) {
    return <Loading />;
  }
  return (
    <>
      <main>
        <div className="shop-section">
          <div className="mt-4">
            <div className="container">
              <div className="row">
                <div className="col-12">
                  <nav aria-label="breadcrumb">
                    <ol className="breadcrumb mb-0">
                      <li className="breadcrumb-item">
                        <a href="/">Home</a>
                      </li>
                      <li className="breadcrumb-item">
                        <a href="" onClick={() => window.location.reload}>
                          {localStorage.getItem("category")}
                        </a>
                      </li>
                    </ol>
                  </nav>
                </div>
              </div>
            </div>
          </div>
          <div className="mb-lg-14 mb-5">
            <div className="container">
              <div className="row gx-10">
                <aside className="col-lg-3 col-md-4 mb-2">
                  <div
                    className="offcanvas offcanvas-start offcanvas-collapse w-md-50"
                    tabindex="-1"
                    id="offcanvasCategory"
                    aria-labelledby="offcanvasCategoryLabel"
                  >
                    <div className="offcanvas-header d-lg-none border-bottom">
                      <h5
                        className="offcanvas-title"
                        id="offcanvasCategoryLabel"
                      >
                        Filter
                      </h5>
                      <button
                        type="button"
                        className="btn-close"
                        data-bs-dismiss="offcanvas"
                        aria-label="Close"
                      ></button>
                    </div>
                    <div className="offcanvas-body ps-lg-2 pt-lg-0">
                      <div className="my-4">
                        <h5 className="mb-3">
                          {localStorage.getItem("category")}
                        </h5>
                      </div>
                      <div className="my-4 border-bottom pb-3">
                        <h5 className="mb-3">Giá</h5>
                        <div>
                          <form action="#">
                            <div className="input-group">
                              <input
                                className="form-control"
                                type="number"
                                placeholder="Từ"
                                value={fromPrice}
                                onChange={(e) => {
                                  setFromPrice(e.target.value);
                                }}
                              />
                              <input
                                className="form-control ms-3"
                                type="number"
                                placeholder="Đến"
                                value={toPrice}
                                onChange={(e) => {
                                  setToPrice(e.target.value);
                                }}
                              />
                            </div>
                          </form>
                        </div>
                      </div>
                      <div className="mt-4">
                        <h5 className="mb-3">Từ Khoá</h5>
                        <div>
                          <div className="col-12 ">
                            <form action="#">
                              <div className="input-group">
                                <input
                                  className="form-control"
                                  type="search"
                                  placeholder="Từ Khoá Tìm Kiếm"
                                  value={keyword}
                                  onChange={(e) => {
                                    setKeyword(e.target.value);
                                  }}
                                />
                              </div>
                            </form>
                          </div>
                        </div>
                      </div>
                      <a
                        href="cart.html"
                        className="d-block rounded py-2 text-center border mt-3 btn-cart"
                        style={{ width: "100px" }}
                        onClick={fetchProductsByKeyWord}
                      >
                        Lọc
                      </a>
                    </div>
                  </div>
                </aside>
                <section className="col-lg-9 col-md-12">
                  <div className="d-lg-flex justify-content-between align-items-center">
                    <div className="d-md-flex justify-content-between align-items-center">
                      <div className="d-flex mt-2 mt-lg-0">
                        <div className="me-2 flex-grow-1"></div>
                        <div>
                          <Select
                            options={options}
                            value={selectedOption}
                            onChange={handleSortChange}
                            className="react-select-container"
                            classNamePrefix="Sort"
                            defaultValue={options[0]}
                          />
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="product">
                    <div className="row g-4 row-cols-xl-3 row-cols-lg-3 row-cols-2 row-cols-md-2 mt-1">
                      {products.map((product) => (
                        <div className="col-md-3" key={product.id}>
                          <div className="card product-card">
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
                                src={
                                  product.imageResponses[0].url ||
                                  "/assets/images/default-product.png"
                                }
                                className="card-img-top"
                                alt={product.name}
                              />
                            </a>
                            <div className="body-card">
                              <div className="card-body">
                                <h5 className="card-title">
                                  <a
                                    href={`/product/${product.id}`}
                                    onClick={() =>
                                      localStorage.setItem(
                                        "product",
                                        JSON.stringify(product)
                                      )
                                    }
                                  >
                                    {product.name}
                                  </a>
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
                                {product.discount && (
                                  <span className="discount-badge">
                                    {product.discount}% OFF
                                  </span>
                                )}
                              </div>
                              <a
                                href="#"
                                className="d-block rounded py-2 text-center border mx-3 mb-3 btn-cart"
                              >
                                Add to Cart
                              </a>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="row mt-5">
                    <div className="col">
                      <nav>
                        <ul className="pagination">
                          {/* Nút Previous */}
                          <li
                            className={`page-item ${
                              page.pageNumber === 0 ? "disabled" : ""
                            }`}
                          >
                            <a
                              className="page-link mx-1"
                              href="#"
                              aria-label="Previous"
                              onClick={(e) => {
                                e.preventDefault();
                                handlePreviousPage();
                              }}
                            >
                              <i className="bi bi-arrow-left-short"></i>
                            </a>
                          </li>

                          {/* Hiển thị số trang hiện tại */}
                          <li className="page-item">
                            <a className="page-link mx-1 active" href="#">
                              {page.pageNumber + 1} /{" "}
                              {page.totalPages}
                            </a>
                          </li>

                          {/* Nút Next */}
                          <li
                            className={`page-item ${
                              page.pageNumber >= page.totalPages - 1
                                ? "disabled"
                                : ""
                            }`}
                          >
                            <a
                              className="page-link mx-1"
                              href="#"
                              aria-label="Next"
                              onClick={(e) => {
                                e.preventDefault();
                                handleNextPage();
                              }}
                            >
                              <i className="bi bi-arrow-right-short"></i>
                            </a>
                          </li>
                        </ul>
                      </nav>
                    </div>
                  </div>
                </section>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}

export default CategoryDetail;
