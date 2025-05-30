import React, { useState, useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import axios from "axios";
import Loading from "./Loading";
import Select from "react-select";
import Cookies from "js-cookie";
import Pagination from "./Pagination";
import { Form, Modal, Button } from "react-bootstrap";
import { Editor } from "@tinymce/tinymce-react";

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
  const [limit, setLimit] = useState(4);
  const [detailProduct, setDetailProduct] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [compareMode, setCompareMode] = useState(false);
  const [compareProduct, setCompareProduct] = useState(null);
  const [showCompareModal, setShowCompareModal] = useState(false);
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
            limit: limit,
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

  // Hiển thị modal chi tiết sản phẩm
  const handleShowDetail = (product) => {
    setDetailProduct(product);
    setShowDetailModal(true);
    setCompareMode(false);
    setCompareProduct(null);
    setShowCompareModal(false);
  };

  // Bắt đầu chế độ so sánh
  const handleStartCompare = () => {
    setCompareMode(true);
    setShowCompareModal(false);
    setCompareProduct(null);
  };

  // Chọn sản phẩm để so sánh
  const handleSelectCompareProduct = (product) => {
    setCompareProduct(product);
    setShowCompareModal(true);
  };

  // Đóng modal so sánh
  const handleCloseCompare = () => {
    setShowCompareModal(false);
    setCompareMode(false);
    setCompareProduct(null);
  };

  useEffect(() => {
    setAccessToken(Cookies.get("accessToken"));
    fetchProducts();
    fetchCategories();
  }, [page.pageNumber, keyword, sort, sortField, limit]); // Thêm limit vào dependency

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
    // Kiểm tra từng trường và báo lỗi cụ thể
    if (!editingProduct.name || editingProduct.name.trim().length === 0) {
      alert("Vui lòng nhập tên sản phẩm.");
      return;
    }
    if (!editingProduct.categoryId || editingProduct.categoryId === "") {
      alert("Vui lòng chọn danh mục.");
      return;
    }
    if (
      editingProduct.price === undefined ||
      editingProduct.price === null ||
      editingProduct.price === "" ||
      Number(editingProduct.price) <= 0
    ) {
      alert("Giá gốc phải lớn hơn 0.");
      return;
    }
    if (
      editingProduct.quantity === undefined ||
      editingProduct.quantity === null ||
      editingProduct.quantity === "" ||
      Number(editingProduct.quantity) <= 0
    ) {
      alert("Số lượng phải lớn hơn 0.");
      return;
    }
    if (
      editingProduct.discount === undefined ||
      editingProduct.discount === null ||
      editingProduct.discount === "" ||
      Number(editingProduct.discount) < 0
    ) {
      alert("Giảm giá phải lớn hơn hoặc bằng 0.");
      return;
    }
    if (
      !editingProduct.description ||
      editingProduct.description.trim().length < 10
    ) {
      alert("Yêu cầu mô tả phải có ít nhất 10 ký tự.");
      return;
    }
    const totalImages =
      (editingProduct.imageResponses
        ? editingProduct.imageResponses.length
        : 0) + (editingProduct.images ? editingProduct.images.length : 0);
    if (totalImages === 0) {
      alert("Yêu cầu phải có ít nhất 1 ảnh.");
      return;
    }

    const formData = new FormData();
    formData.append("name", editingProduct.name);
    formData.append("categoryId", editingProduct.categoryId);
    formData.append("price", editingProduct.price);
    formData.append("discount", editingProduct.discount);
    formData.append("quantity", editingProduct.quantity);
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
            min-height: calc(100vh - 200px);
          }
          .table-responsive {
            flex-grow: 1;
            overflow-y: auto;
          }
          .table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            background-color: #fff;
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 4px 24px rgba(246,137,31,0.06);
          }
          .table th,
          .table td {
            padding: 14px 18px;
            text-align: center;
            border-bottom: none;
            background: none;
          }
          .table th {
            background-color: #fff9f4;
            font-weight: bold;
            color: #f7a94d;
            font-size: 16px;
            border-bottom: 2px solid #f7a94d;
            letter-spacing: 0.5px;
          }
          .table tr {
            transition: background 0.2s;
          }
          .table tbody tr:hover {
            background-color: #fff6ea;
          }
          .table td img {
            border-radius: 6px;
            transition: transform 0.2s;
            box-shadow: 0 1px 4px rgba(246,137,31,0.06);
          }
          .table td img:hover {
            transform: scale(1.08);
            cursor: pointer;
          }
          .btn {
            padding: 7px 16px;
            font-size: 15px;
            border-radius: 8px;
            transition: background-color 0.2s, color 0.2s;
            font-weight: 500;
          }
          .btn-primary {
            background-color: #f7a94d;
            color: #fff;
            border: none;
            box-shadow: 0 1px 4px rgba(246,137,31,0.06);
          }
          .btn-primary:hover {
            background-color: #e08b1d;
            color: #fff;
          }
          .btn-danger {
            background-color: #ef4444;
            color: #fff;
            border: none;
            box-shadow: 0 1px 4px rgba(239,68,68,0.08);
          }
          .btn-danger:hover {
            background-color: #b91c1c;
            color: #fff;
          }
          .discount-badge {
            display: inline-block;
            padding: 6px 14px;
            border-radius: 16px;
            font-weight: 600;
            font-size: 15px;
            background: #fff6ea;
            color: #f7a94d;
            border: 1.5px solid #f7a94d;
            min-width: 48px;
          }
          .discount-badge.high {
            background: #f7a94d;
            color: #fff;
            border: 1.5px solid #f7a94d;
          }
          .discount-badge.medium {
            background: #ffe6b7;
            color: #f7a94d;
            border: 1.5px solid #f7a94d;
          }
          .discount-badge.low {
            background: #fff9f4;
            color: #f7a94d;
            border: 1.5px solid #f7a94d;
          }
          .pagination-container {
            margin-top: 20px;
            display: flex;
            justify-content: center;
          }
          .no-product-row {
            background: #fff9f4;
            color: #f7a94d;
            font-size: 20px;
            font-weight: 600;
            letter-spacing: 1px;
            border-radius: 8px;
            height: 120px;
            vertical-align: middle;
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
                <div className="col-md-4">
                  <Form.Group className="mb-3">
                    <Form.Label>Giá Gốc</Form.Label>
                    <Form.Control
                      type="number"
                      value={editingProduct.price}
                      onChange={(e) =>
                        setEditingProduct({
                          ...editingProduct,
                          price: e.target.value, // giữ nguyên kiểu string để tránh lỗi đảo ký tự
                        })
                      }
                    />
                  </Form.Group>
                </div>

                <div className="col-md-4">
                  <Form.Group className="mb-3">
                    <Form.Label>Giảm Giá</Form.Label>
                    <Form.Control
                      type="number"
                      value={editingProduct.discount}
                      onChange={(e) =>
                        setEditingProduct({
                          ...editingProduct,
                          discount: e.target.value, // giữ nguyên kiểu string để tránh lỗi đảo ký tự
                        })
                      }
                    />
                  </Form.Group>
                </div>

                <div className="col-md-4">
                  <Form.Group className="mb-3">
                    <Form.Label>Số Lượng</Form.Label>
                    <Form.Control
                      type="number"
                      value={editingProduct.quantity}
                      onChange={(e) =>
                        setEditingProduct({
                          ...editingProduct,
                          quantity: e.target.value, // giữ nguyên kiểu string để tránh lỗi đảo ký tự
                        })
                      }
                    />
                  </Form.Group>
                </div>
              </div>
              <Form.Group className="mb-3">
                <Form.Label>Mô Tả</Form.Label>
                <Editor
                  apiKey="qye6ktec8ak0vo3irq6raf1xgti73p86xqzzwjjiio7pb7cr"
                  value={editingProduct.description} // Sử dụng prop value thay vì initialValue
                  init={{
                    height: 400,
                    branding: false,
                    menubar: true,
                    content_style:
                      "body { font-family:Helvetica,Arial,sans-serif; font-size:14px }",
                    plugins: [
                      "preview importcss searchreplace autolink autosave save directionality",
                      "visualblocks visualchars fullscreen image link media template codesample",
                      "table charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount",
                      "help charmap quickbars emoticons",
                    ],
                    toolbar:
                      "undo redo | bold italic underline strikethrough | fontfamily fontsize blocks | alignleft aligncenter alignright alignjustify | outdent indent |  numlist bullist | forecolor backcolor removeformat | pagebreak | charmap emoticons | fullscreen  preview save print | insertfile image media template link anchor codesample | ltr rtl",
                    content_css: "//www.tiny.cloud/css/codepen.min.css",
                  }}
                  onEditorChange={(content) => {
                    setEditingProduct({
                      ...editingProduct,
                      description: content,
                    });
                  }}
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

      <Modal
        show={showDetailModal}
        onHide={() => setShowDetailModal(false)}
        size="xl" // đổi từ "lg" thành "xl" để modal to hơn
      >
        <Modal.Header closeButton>
          <Modal.Title>Chi tiết sản phẩm: {detailProduct?.name}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {detailProduct && (
            <div>
              <div className="mb-3">
                <img
                  src={
                    detailProduct.imageResponses?.[0]?.url ||
                    "/assets/images/default-avatar.png"
                  }
                  alt={detailProduct.name}
                  style={{
                    width: 180,
                    height: 180,
                    objectFit: "cover",
                    borderRadius: 8,
                    border: "1px solid #eee",
                  }}
                />
              </div>
              <div>
                <b>Tên:</b> {detailProduct.name}
              </div>
              <div>
                <b>Danh mục:</b> {detailProduct.category}
              </div>
              <div>
                <b>Giá:</b> {detailProduct.price}$
              </div>
              <div>
                <b>Giảm giá:</b> {detailProduct.discount}%
              </div>
              <div>
                <b>Số lượng:</b> {detailProduct.quantity}
              </div>
              <div>
                <b>Mô tả:</b>{" "}
                <span
                  dangerouslySetInnerHTML={{
                    __html: detailProduct.description,
                  }}
                />
              </div>
            </div>
          )}
        </Modal.Body>
        <Modal.Footer>
          {!compareMode && (
            <Button variant="warning" onClick={handleStartCompare}>
              So sánh
            </Button>
          )}
          <Button variant="secondary" onClick={() => setShowDetailModal(false)}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal chọn sản phẩm để so sánh */}
      <Modal
        show={compareMode && !showCompareModal}
        onHide={() => setCompareMode(false)}
      >
        <Modal.Header closeButton>
          <Modal.Title>Chọn sản phẩm để so sánh</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div style={{ maxHeight: 350, overflowY: "auto" }}>
            <table className="table table-bordered">
              <thead>
                <tr>
                  <th>Tên</th>
                  <th>Danh mục</th>
                  <th>Giá</th>
                  <th>Chọn</th>
                </tr>
              </thead>
              <tbody>
                {productList
                  .filter((p) => p.id !== detailProduct?.id)
                  .map((p) => (
                    <tr key={p.id}>
                      <td>{p.name}</td>
                      <td>{p.category}</td>
                      <td>{p.price}$</td>
                      <td>
                        <Button
                          size="sm"
                          variant="primary"
                          onClick={() => handleSelectCompareProduct(p)}
                        >
                          Chọn
                        </Button>
                      </td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setCompareMode(false)}>
            Hủy
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Modal so sánh 2 sản phẩm */}
      <Modal show={showCompareModal} onHide={handleCloseCompare} size="xl">
        <Modal.Header closeButton>
          <Modal.Title>So sánh sản phẩm</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="row">
            {(() => {
              const d = detailProduct;
              const c = compareProduct;
              // Bịa thông số ram, rom cho mỗi máy (ví dụ tĩnh)
              const getFakeSpecs = (id) => {
                // Chỉ để demo, bạn có thể random hoặc dựa vào id
                const fakeData = [
                  { ram: "4GB", rom: "64GB" },
                  { ram: "6GB", rom: "128GB" },
                  { ram: "8GB", rom: "256GB" },
                  { ram: "12GB", rom: "512GB" },
                ];
                if (!id) return { ram: "4GB", rom: "64GB" };
                const idx = Number(id) % fakeData.length;
                return fakeData[idx];
              };
              const dSpecs = getFakeSpecs(d?.id);
              const cSpecs = getFakeSpecs(c?.id);

              // So sánh số GB (chỉ lấy số đầu)
              const ramD = parseInt(dSpecs.ram);
              const ramC = parseInt(cSpecs.ram);
              const romD = parseInt(dSpecs.rom);
              const romC = parseInt(cSpecs.rom);

              const isDRamGreater = ramD > ramC;
              const isCRamGreater = ramC > ramD;
              const isDRomGreater = romD > romC;
              const isCRomGreater = romC > romD;

              // Xác định giá trị lớn hơn cho từng thông số
              const isDPriceGreater = d && c && d.price > c.price;
              const isCPriceGreater = d && c && c.price > d.price;
              const isDDiscountGreater = d && c && d.discount > c.discount;
              const isCDiscountGreater = d && c && c.discount > d.discount;
              const isDQuantityGreater = d && c && d.quantity > c.quantity;
              const isCQuantityGreater = d && c && c.quantity > d.quantity;
              const highlight = {
                background: "#f6891a",
                color: "#fff",
                borderRadius: 8,
                padding: "2px 8px",
                marginLeft: 8,
                fontSize: 13,
                fontWeight: 600,
              };
              return (
                <>
                  <div className="col-md-6">
                    <h5>{d?.name}</h5>
                    <img
                      src={
                        d?.imageResponses?.[0]?.url ||
                        "/assets/images/default-avatar.png"
                      }
                      alt={d?.name}
                      style={{
                        width: 160,
                        height: 160,
                        objectFit: "cover",
                        borderRadius: 8,
                        border: "1px solid #eee",
                      }}
                    />
                    <div>
                      <b>Danh mục:</b> {d?.category}
                    </div>
                    <div>
                      <b>RAM:</b>{" "}
                      <span style={isDRamGreater ? highlight : {}}>
                        {dSpecs.ram}
                      </span>
                    </div>
                    <div>
                      <b>ROM:</b>{" "}
                      <span style={isDRomGreater ? highlight : {}}>
                        {dSpecs.rom}
                      </span>
                    </div>
                    <div>
                      <b>Giá:</b>{" "}
                      <span style={isDPriceGreater ? highlight : {}}>
                        {d?.price}$
                      </span>
                    </div>
                    <div>
                      <b>Giảm giá:</b>{" "}
                      <span style={isDDiscountGreater ? highlight : {}}>
                        {d?.discount}%
                      </span>
                    </div>
                    <div>
                      <b>Số lượng:</b>{" "}
                      <span style={isDQuantityGreater ? highlight : {}}>
                        {d?.quantity}
                      </span>
                    </div>
                    <div>
                      <b>Mô tả:</b>{" "}
                      <span
                        dangerouslySetInnerHTML={{
                          __html: d?.description,
                        }}
                      />
                    </div>
                  </div>
                  <div className="col-md-6">
                    <h5>{c?.name}</h5>
                    <img
                      src={
                        c?.imageResponses?.[0]?.url ||
                        "/assets/images/default-avatar.png"
                      }
                      alt={c?.name}
                      style={{
                        width: 160,
                        height: 160,
                        objectFit: "cover",
                        borderRadius: 8,
                        border: "1px solid #eee",
                      }}
                    />
                    <div>
                      <b>Danh mục:</b> {c?.category}
                    </div>
                    <div>
                      <b>RAM:</b>{" "}
                      <span style={isCRamGreater ? highlight : {}}>
                        {cSpecs.ram}
                      </span>
                    </div>
                    <div>
                      <b>ROM:</b>{" "}
                      <span style={isCRomGreater ? highlight : {}}>
                        {cSpecs.rom}
                      </span>
                    </div>
                    <div>
                      <b>Giá:</b>{" "}
                      <span style={isCPriceGreater ? highlight : {}}>
                        {c?.price}$
                      </span>
                    </div>
                    <div>
                      <b>Giảm giá:</b>{" "}
                      <span style={isCDiscountGreater ? highlight : {}}>
                        {c?.discount}%
                      </span>
                    </div>
                    <div>
                      <b>Số lượng:</b>{" "}
                      <span style={isCQuantityGreater ? highlight : {}}>
                        {c?.quantity}
                      </span>
                    </div>
                    <div>
                      <b>Mô tả:</b>{" "}
                      <span
                        dangerouslySetInnerHTML={{
                          __html: c?.description,
                        }}
                      />
                    </div>
                  </div>
                </>
              );
            })()}
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseCompare}>
            Đóng
          </Button>
        </Modal.Footer>
      </Modal>

      <main>
        <div>
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

                    <div className="col-6 me-2">
                      <Select
                        options={options}
                        value={selectedOption}
                        onChange={handleSortChange}
                        className="react-select-container"
                        classNamePrefix="Sort"
                        placeholder="Sắp xếp"
                        styles={{
                          container: (base) => ({
                            ...base,
                            width: 200,
                          }),
                          control: (base) => ({
                            ...base,
                            minHeight: 36,
                            fontSize: 14,
                          }),
                          valueContainer: (base) => ({
                            ...base,
                            padding: "0 6px",
                          }),
                          indicatorsContainer: (base) => ({
                            ...base,
                            height: 36,
                          }),
                        }}
                      />
                    </div>
                    <div className="col-6 ">
                      <select
                        className="form-select"
                        style={{
                          minWidth: 90,
                          maxWidth: 120,
                          // fontSize: 14,
                          height: 36,
                          padding: "4px 8px",
                        }}
                        value={limit}
                        onChange={(e) => {
                          setLimit(Number(e.target.value));
                          setPage((prev) => ({
                            ...prev,
                            pageNumber: 0,
                          }));
                        }}
                      >
                        <option value={4}>4/trang</option>
                        <option value={8}>8/trang</option>
                        <option value={12}>12/trang</option>
                        <option value={16}>16/trang</option>
                      </select>
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
                        quantity: 0,
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
          <section>
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
                          <th className="text-center">Số Lượng</th>
                          <th className="text-center">Danh Mục</th>
                          <th className="text-center">Discount</th>
                          <th className="text-center">Hành Động</th>
                        </tr>
                      </thead>
                      <tbody>
                        {productList.length === 0 ? (
                          <tr>
                            <td colSpan="7" className="text-center no-product-row">
                              <div>
                                <img
                                  src="/assets/images/empty-box.png"
                                  alt="Không tìm thấy sản phẩm"
                                  style={{ width: 60, marginBottom: 10, opacity: 0.7 }}
                                  onError={e => { e.target.style.display = 'none'; }}
                                />
                                <div>Không tìm thấy sản phẩm</div>
                              </div>
                            </td>
                          </tr>
                        ) : (
                          productList.map((product, index) => (
                            <tr
                              key={index}
                              style={{ cursor: "pointer" }}
                              onClick={() => handleShowDetail(product)}
                            >
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
                              <td className="align-middle">{product.quantity}</td>
                              <td className="align-middle">{product.category}</td>
                              <td className="align-middle">
                                <span
                                  className={`discount-badge ${product.discount >= 50
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
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleEditProduct(product);
                                  }}
                                >
                                  Sửa
                                </button>
                                <button
                                  className="btn btn-danger btn-sm me-2"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    handleDeleteProduct(product.id);
                                  }}
                                >
                                  Xóa
                                </button>
                                {/* Nút xem chi tiết đã bỏ, click vào dòng sẽ xem chi tiết */}
                              </td>
                            </tr>
                          ))
                        )}
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
