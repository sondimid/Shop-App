import React, { useState, useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import axios from "axios";
import Loading from "./Loading";
import Select from "react-select";
import Cookies from "js-cookie";
import Pagination from "./Pagination";
import { Form, Modal, Button } from "react-bootstrap";

function AdminCategory() {
  const [loading, setLoading] = useState(true);
  const [accessToken, setAccessToken] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState(null);
  const [categories, setCategories] = useState([]);
  const [newImage, setNewImage] = useState(null);

  const fetchCategories = async () => {
    try {
      const response = await axios.get(
        "http://localhost:8080/api/v1/categories"
      );
      setCategories(response.data);
    } catch (error) {
      alert("Không thể tải danh sách danh mục.");
    }
    setLoading(false);
  };

  const handleDeleteCategory = (categoryId) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa danh mục này này?")) {
      axiosInstance
        .delete(`/categories/${categoryId}`, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        })
        .then(() => {
          alert("Xóa danh mục thành công!");
          fetchCategories();
        })
        .catch(() => {
          alert("Không thể xóa danh mục.");
        });
    }
  };

  const handleEditCategory = (category) => {
    setEditingCategory(category);
    setShowModal(true);
  };

  useEffect(() => {
    setAccessToken(Cookies.get("accessToken"));
    fetchCategories();
  }, []);

  if (loading) {
    return <Loading />;
  }

  const handleSaveChanges = () => {
    const formData = new FormData();
    if (editingCategory.id) {
      formData.append("id", editingCategory.id);
    }
    formData.append("name", editingCategory.name);
    formData.append("image", editingCategory.image);
    try {
      const response = axiosInstance.post("/categories", formData, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      alert("Thành công!");
      setNewImage(null);
      setShowModal(false);
      fetchCategories();
    } catch (error) {
      if (error.response) {
        alert(error.response.data);
      } else {
        console.error("Lỗi không xác định:", error.message);
        alert("Có lỗi xảy ra!");
      }
    }
  };

  const handleImageUpload = (e) => {
    setEditingCategory({
      ...editingCategory,
      image: e.target.files[0],
    });
    setNewImage(e.target.files[0]);
  };

  return (
    <>
      <Modal show={showModal} onHide={() => setShowModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Chỉnh Sửa Danh Mục</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {editingCategory && (
            <Form>
              <Form.Group className="mb-3">
                <Form.Label>Tên Danh Mục</Form.Label>
                <Form.Control
                  type="text"
                  value={editingCategory.name}
                  onChange={(e) =>
                    setEditingCategory({
                      ...editingCategory,
                      name: e.target.value,
                    })
                  }
                />
              </Form.Group>

              {editingCategory.imageUrl && (
                <Form.Group className="mb-3">
                  <Form.Label>Hình Ảnh Hiện Tại</Form.Label>
                  <div className="d-flex flex-wrap">
                    <div
                      key={editingCategory.id}
                      className="me-2 mb-2"
                      style={{ position: "relative" }}
                    >
                      <img
                        src={editingCategory.imageUrl}
                        alt={`Hình ảnh ${editingCategory.id + 1}`}
                        style={{
                          width: "100px",
                          height: "100px",
                          objectFit: "cover",
                          border: "1px solid #ddd",
                          borderRadius: "5px",
                        }}
                      />
                    </div>
                  </div>
                </Form.Group>
              )}

              <Form.Group className="mb-3">
                <Form.Label>Tải Lên Hình Ảnh </Form.Label>
                <Form.Control
                  type="file"
                  accept="image/*"
                  onChange={(e) => handleImageUpload(e)}
                />
                <div className="d-flex flex-wrap">
                  {newImage && (
                    <div key={editingCategory.id} className="me-2 mb-2">
                      <img
                        src={URL.createObjectURL(newImage)}
                        alt={`Hình ảnh mới ${editingCategory.id + 1}`}
                        style={{
                          width: "100px",
                          height: "100px",
                          objectFit: "cover",
                          border: "1px solid #ddd",
                          borderRadius: "5px",
                        }}
                      />
                    </div>
                  )}
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
        <div className="container-fluid">
          <div className="row">
            <div className="col-12">
              <div className="d-flex align-items-center justify-content-between mb-3">
                <div className="d-flex align-items-center">
                  <div className="col-6 me-3">
                    <form action="#">
                      <div className="input-group"></div>
                    </form>
                  </div>

                  <div className="col-6 me-4"></div>
                </div>

                <Button
                  style={{
                    backgroundColor: "f6891a",
                  }}
                  onClick={() => {
                    setEditingCategory({
                      name: null,
                      images: [],
                    });
                    setShowModal(true);
                  }}
                >
                  Thêm Mới Danh Mục
                </Button>
              </div>
            </div>
          </div>
          <table className="table text-nowrap table-with-checkbox">
            <thead className="table-light">
              <tr>
                <th className="text-center">Tên Danh Mục</th>
                <th className="text-center">Hình Ảnh</th>
                <th className="text-center">Số Lượng SP</th>
                <th className="text-center">Hành Động</th>
              </tr>
            </thead>
            <tbody>
              {categories.map((category, index) => (
                <tr key={index}>
                  <td
                    className="align-middle"
                    style={{
                      width: "200px",
                      textAlign: "center",
                    }}
                  >
                    {category.name}
                  </td>
                  <td
                    className="align-middle"
                    style={{
                      width: "200px",
                      textAlign: "center",
                    }}
                  >
                    <img
                      src={
                        category.imageUrl || "/assets/images/default-avatar.png"
                      }
                      className="icon-shape icon-xxl"
                      alt={category.imageUrl}
                      style={{
                        width: "100px",
                        height: "100px",
                      }}
                    />
                  </td>
                  <td
                    className="align-middle"
                    style={{
                      width: "200px",
                      textAlign: "center",
                    }}
                  >
                    {category.productQuantity}
                  </td>
                  <td
                    className="align-middle"
                    style={{
                      width: "200px",
                      textAlign: "center",
                    }}
                  >
                    <button
                      className="btn btn-primary btn-sm me-2"
                      onClick={() => handleEditCategory(category)}
                    >
                      Sửa
                    </button>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDeleteCategory(category.id)}
                    >
                      Xóa
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </main>
    </>
  );
}

export default AdminCategory;
