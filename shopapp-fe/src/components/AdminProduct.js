import React, { useState, useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import axios from "axios";
import Loading from "./Loading";
import Select from "react-select";
import Cookies from "js-cookie";
import Pagination from "./Pagination";
import { Form, Modal, Button } from "react-bootstrap";

function AdminProduct() {
  const [loading, setLoading] = useState(true);
  const [productList, setProductList] = useState([]);
  const [accessToken, setAccessToken] = useState(null);
  const [keyword, setKeyword] = useState("");
  const [sort, setSort] = useState("ASC");
  const [sortField, setSortField] = useState("id");
  const [selectedOption, setSelectedOption] = useState(null);
  const [fromPrice, setFromPrice] = useState(0);
  const [toPrice, setToPrice] = useState(200000);
  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);
  const [page, setPage] = useState({
    pageNumber: 0,
    totalPages: 1,
  });
  const options = [
    { value: "high-to-low-price", label: "Giá: Cao-Thấp" },
    { value: "low-to-high-price", label: "Giá: Thấp-Cao" },
    { value: "hot-discount", label: "Giảm Giá Hot" },
    { value: "new-arrivals", label: "Hàng Mới" },
  ];

  const [categories, setCategories] = useState([]);
  const fetchCategories = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/categories"
      );
      setCategories(response.data);
    } catch (error) {
      alert("Không thể tải danh sách danh mục.");
    }
  };

  const fetchProducts = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/products/all",
        {
          params: {
            page: page.pageNumber,
            limit: 5,
            keyword: keyword,
            fromPrice: fromPrice,
            toPrice: toPrice,
            sort: sort,
            sortField: sortField,
          },
        }
      );
      setProductList(response.data.content);
      setPage({
        pageNumber: response.data.pageNumber,
        totalPages: response.data.totalPages,
      });
      setLoading(false);
    } catch (error) {
      alert("Không thể tải danh sách sản phẩm.");
      setLoading(false);
    }
  };

  const handleDeleteProduct = (productId) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
      axiosInstance
        .delete(`/products/${productId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        })
        .then(() => {
          alert("Xóa sản phẩm thành công!");
          fetchProducts();
        })
        .catch(() => {
          alert("Không thể xóa sản phẩm.");
        });
    }
  };

  const handleEditProduct = (product) => {
    const matchedCategory = categories.find(
      (category) => category.name === product.category
    );

    setEditingProduct({
      ...product,
      category: matchedCategory ? matchedCategory.id : "",
    });

    setShowModal(true);
  };

  useEffect(() => {
    setAccessToken(Cookies.get("accessToken"));
    fetchProducts();
    fetchCategories();
  }, [page.pageNumber, keyword, sort, sortField]);

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

  const handleSortChange = (selectedOption) => {
    const value = selectedOption.value;
    setSelectedOption(selectedOption);
    setPage((prev) => ({
      ...prev,
      pageNumber: 0,
    }));
    if (value === "high-to-low-price") {
      setSort("DESC");
      setSortField("price");
    } else if (value === "low-to-high-price") {
      setSort("ASC");
      setSortField("price");
    } else if (value === "hot-discount") {
      setSort("DESC");
      setSortField("discount");
    } else if (value === "new-arrivals") {
      setSort("DESC");
      setSortField("createdAt");
    }
  };

  if (loading) {
    return <Loading />;
  }

  const handleSaveChanges = () => {
    const formData = new FormData();

    formData.append("name", editingProduct.name);
    formData.append("categoryId", editingProduct.categoryId);
    formData.append("price", editingProduct.price);
    formData.append("discount", editingProduct.discount);
    formData.append("description", editingProduct.description);

    if (editingProduct.id) {
      formData.append("id", editingProduct.id);
    }
    if (
      editingProduct.IdImageDelete &&
      editingProduct.IdImageDelete.length > 0
    ) {
      formData.append("IdImageDelete", editingProduct.IdImageDelete);
    }

    if (editingProduct.images && editingProduct.images.length > 0) {
      editingProduct.images.forEach((image, index) => {
        if (image.file) {
          formData.append(`images[${index}]`, image.file);
        }
      });
    }

    console.log(formData);
    axiosInstance
      .put("/products", formData, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          "Content-Type": "multipart/form-data",
        },
      })
      .then((response) => {
        alert("Cập nhật sản phẩm thành công!");
        console.log(response.data.message);
        setShowModal(false);
        fetchProducts();
      })
      .catch(() => {
        alert("Không thể cập nhật sản phẩm.");
      });
  };

  const handleDeleteImage = (imageId) => {
    setEditingProduct((prev) => ({
      ...prev,
      IdImageDelete: [...(prev.IdImageDelete || []), imageId],
      imageResponses: prev.imageResponses.filter(
        (image) => image.id !== imageId
      ),
    }));
  };

  const handleImageUpload = (e) => {
    const files = Array.from(e.target.files);
    const newImages = files.map((file) => {
      return {
        url: URL.createObjectURL(file),
        file: file,
      };
    });

    setEditingProduct({
      ...editingProduct,
      images: [...(editingProduct.images || []), ...newImages],
    });
  };

  return (
    <>
      <style>
        {`
    .table-container {
      display: flex;
      flex-direction: column;
      min-height: calc(100vh - 200px); /* Trừ chiều cao của header và footer */
    }

    .table-responsive {
      flex-grow: 1; /* Để bảng chiếm toàn bộ không gian còn lại */
      overflow-y: auto; /* Thêm thanh cuộn dọc nếu nội dung vượt quá chiều cao */
    }

    .table {
      width: 100%;
      border-collapse: collapse;
      background-color: #fff;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .table th,
    .table td {
      padding: 12px 15px;
      text-align: center;
      border-bottom: 1px solid #ddd;
    }

    .table th {
      background-color: #f4f4f4;
      font-weight: bold;
      color: #333;
    }

    .table tr:hover {
      background-color: #f9f9f9;
    }

    .table td img {
      border-radius: 4px;
      transition: transform 0.2s ease-in-out;
    }

    .table td img:hover {
      transform: scale(1.1);
      cursor: pointer;
    }

    .btn {
      padding: 6px 12px;
      font-size: 14px;
      border-radius: 4px;
      transition: background-color 0.3s ease;
    }

    .btn-primary {
      background-color: #007bff;
      color: #fff;
      border: none;
    }

    .btn-primary:hover {
      background-color: #0056b3;
    }

    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  `}
      </style>
      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh Sửa Sản Phẩm</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {editingProduct && (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Tên Sản Phẩm</Form.Label>
                <Form.Control
                  type="text"
                  value={editingProduct.name}
                  onChange={(e) =>
                    setEditingProduct({
                      ...editingProduct,
                      name: e.target.value,
                    })
                  }
                />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Danh Mục</Form.Label>
                <Form.Select
                  value={editingProduct.categoryId || ""}
                  onChange={(e) =>
                    setEditingProduct({
                      ...editingProduct,
                      categoryId: e.target.value,
                    })
                  }
                >
                  <option value="">Chọn danh mục</option>{" "}
                  {categories.map((category) => (
                    <option key={category.id} value={category.id}>
                      {category.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
              <div className="row">
                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Giá Gốc</Form.Label>
                    <Form.Control
                      type="number"
                      value={editingProduct.price}
                      onChange={(e) =>
                        setEditingProduct({
                          ...editingProduct,
                          price: e.target.value,
                        })
                      }
                    />
                  </Form.Group>
                </div>

                <div className="col-md-6">
                  <Form.Group className="mb-3">
                    <Form.Label>Giảm Giá</Form.Label>
                    <Form.Control
                      type="number"
                      value={editingProduct.discount}
                      onChange={(e) =>
                        setEditingProduct({
                          ...editingProduct,
                          discount: e.target.value,
                        })
                      }
                    />
                  </Form.Group>
                </div>
              </div>
              <Form.Group className="mb-3">
                <Form.Label>Mô Tả</Form.Label>
                <Form.Control
                  as="textarea"
                  rows={7}
                  value={editingProduct.description}
                  onChange={(e) =>
                    setEditingProduct({
                      ...editingProduct,
                      description: e.target.value,
                    })
                  }
                />
              </Form.Group>

              {editingProduct.imageResponses.length > 0 && (
                <Form.Group className="mb-3">
                  <Form.Label>Hình Ảnh Hiện Tại</Form.Label>
                  <div className="d-flex flex-wrap">
                    {editingProduct.imageResponses?.map((image) => (
                      <div
                        key={image.id}
                        className="me-2 mb-2"
                        style={{ position: "relative" }}
                      >
                        <img
                          src={image.url}
                          alt={`Hình ảnh ${image.id + 1}`}
                          style={{
                            width: "100px",
                            height: "100px",
                            objectFit: "cover",
                            border: "1px solid #ddd",
                            borderRadius: "5px",
                          }}
                        />
                        <button
                          type="button"
                          className="btn btn-danger btn-sm position-absolute"
                          style={{
                            top: "2px",
                            right: "2px",
                            padding: "4px",
                          }}
                          onClick={() => handleDeleteImage(image.id)}
                        >
                          &times;
                        </button>
                      </div>
                    ))}
                  </div>
                </Form.Group>
              )}

              <Form.Group className="mb-3">
                <Form.Label>Tải Lên Hình Ảnh</Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  multiple
                  onChange={(e) => handleImageUpload(e)}
                />
                <div className="d-flex flex-wrap">
                  {(editingProduct.images || []).map((image, index) => (
                    <div key={index} className="me-2 mb-2">
                      <img
                        src={image.url}
                        alt={`Hình ảnh mới ${index + 1}`}
                        style={{
                          width: "100px",
                          height: "100px",
                          objectFit: "cover",
                          border: "1px solid #ddd",
                          borderRadius: "5px",
                        }}
                      />
                    </div>
                  ))}
                </div>
              </Form.Group>
            </Form>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Hủy
          </Button>
          <Button variant="primary" onClick={handleSaveChanges}>
            Lưu
          </Button>
        </Modal.Footer>
      </Modal>
      <main>
        <div >
          <div className="container">
            <div className="row">
              <div className="col-12">
                <div className="d-flex align-items-center justify-content-between mb-3">
                  <div className="d-flex align-items-center">
                    <div className="col-6 me-3">
                      <form action="#">
                        <div className="input-group">
                          <input
                            className="form-control"
                            type="search"
                            placeholder="Từ Khoá Tìm Kiếm"
                            value={keyword}
                            onChange={(e) => {
                              setKeyword(e.target.value);
                              setPage((prev) => ({
                                ...prev,
                                pageNumber: 0,
                              }));
                            }}
                          />
                        </div>
                      </form>
                    </div>

                    <div className="col-6 me-4">
                      <Select
                        options={options}
                        value={selectedOption}
                        onChange={handleSortChange}
                        className="react-select-container"
                        classNamePrefix="Sort"
                        placeholder="Sắp xếp theo"
                      />
                    </div>
                  </div>

                  <Button
                    style={{
                      backgroundColor: "f6891a",
                    }}
                    onClick={() => {
                      setEditingProduct({
                        name: "",
                        categoryId: 1,
                        price: 0,
                        discount: 0,
                        description: "",
                        images: [],
                        imageResponses: [],
                        IdImageDelete: [],
                      });
                      setShowModal(true);
                    }}
                  >
                    Thêm Mới Sản Phẩm
                  </Button>
                </div>
              </div>
            </div>
          </div>
          <section >
            <div className="container-fluid table-container">
              <div className="row">
                <div className="col-lg-12">
                  <div className="table-responsive">
                    <table className="table text-nowrap table-with-checkbox">
                      <thead className="table-light">
                        <tr>
                          <th className="text-center">Ảnh</th>
                          <th className="text-center">Tên Sản Phẩm</th>
                          <th className="text-center">Giá Gốc</th>
                          <th className="text-center">Danh Mục</th>
                          <th className="text-center">Discount</th>
                          <th className="text-center">Hành Động</th>
                        </tr>
                      </thead>
                      <tbody>
                        {productList.map((product, index) => (
                          <tr key={index}>
                            <td className="align-middle">
                              <div>
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
                                      product.imageResponses[0]?.url ||
                                      "/assets/images/default-avatar.png"
                                    }
                                    className="icon-shape"
                                    alt={product.name}
                                    style={{
                                      width: 150,
                                      height: 150,
                                    }}
                                  />
                                </a>
                              </div>
                            </td>
                            <td className="align-middle">
                              {product.name.length > 50
                                ? product.name.substring(0, 50) + "..."
                                : product.name}
                            </td>
                            <td className="align-middle">{product.price}$</td>
                            <td className="align-middle">{product.category}</td>
                            <td className="align-middle">
                              <span
                                className={`discount-badge ${
                                  product.discount >= 50
                                    ? "high"
                                    : product.discount >= 20
                                    ? "medium"
                                    : "low"
                                }`}
                              >
                                {product.discount}%
                              </span>
                            </td>
                            <td className="align-middle">
                              <button
                                className="btn btn-primary btn-sm me-2"
                                onClick={() => handleEditProduct(product)}
                              >
                                Sửa
                              </button>
                              <button
                                className="btn btn-danger btn-sm"
                                onClick={() => handleDeleteProduct(product.id)}
                              >
                                Xóa
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                  <div className="pagination-container">
                    <Pagination
                      pageNumber={page.pageNumber}
                      totalPages={page.totalPages}
                      handleNextPage={handleNextPage}
                      handlePreviousPage={handlePreviousPage}
                    />
                  </div>
                </div>
              </div>
            </div>
          </section>
        </div>
      </main>
    </>
  );
}

export default AdminProduct;
