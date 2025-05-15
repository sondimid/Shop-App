import React, { useEffect, useState } from "react";
import axiosInstance from "../utils/RefreshToken";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import Loading from "./Loading";
import { Modal, Button, Form } from "react-bootstrap";
import Pagination from "./Pagination";
function UserOrder({ header, status }) {
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showCommentModal, setShowCommentModal] = useState(false);
  const [commentDetails, setCommentDetails] = useState([]);
  const navigate = useNavigate();
  const [page, setPage] = useState({
    pageNumber: 0,
    totalPages: 1,
  });

  const fetchOrders = async () => {
    const accessToken = Cookies.get("accessToken");
    try {
      const response = await axiosInstance.get(`/orders/${status}/keyword`, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
        params: {
          page: page.pageNumber,
          limit: 10,
        },
      });
      setOrders(response.data.content);
      setPage({
        pageNumber: response.data.pageNumber,
        totalPages: response.data.totalPages,
      });
      setIsLoading(false);
    } catch (error) {
      console.error("Lỗi khi lấy danh sách đơn hàng:", error.response);
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, [page.pageNumber, status]);

  const handleViewOrder = (order) => {
    setSelectedOrder(order);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedOrder(null);
  };

  const handleSaveChanges = async () => {
    const accessToken = Cookies.get("accessToken");
    try {
      await axiosInstance.put(`/orders/${selectedOrder.code}`, selectedOrder, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
    } catch (error) {
      console.log(error.response.data);
    }
    fetchOrders();
    setShowModal(false);
  };

  const handleCancelOrder = async (id) => {
    const accessToken = Cookies.get("accessToken");
    if (
      window.confirm("Bạn Có Muốn Huỷ Đơn, Nếu Huỷ Bạn Sẽ Không Được Hoàn Tiền")
    ) {
      try {
        await axiosInstance.put(`orders/${id}/cancel`, null, {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        });
        alert("Huỷ Đơn Hàng Thành Công");
      } catch (error) {
        console.log(error);
      }
    }
  };

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
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

  const handleComment = (orderDetails) => {
    setCommentDetails(
      orderDetails.map((detail) => ({
        productDetail: detail,
        comment: "",
        image: null,
      }))
    );
    setShowCommentModal(true);
  };

  const handleCommentChange = (index, field, value) => {
    const updatedComments = [...commentDetails];
    updatedComments[index][field] = value;
    setCommentDetails(updatedComments);
  };

  const handleSubmitComments = async () => {
    const accessToken = Cookies.get("accessToken");
    try {
      await Promise.all(
        commentDetails.map(async (comment) => {
          const formData = new FormData();
          formData.append("content", comment.comment);
          if (comment.image) {
            formData.append("image", comment.image); // Chỉ thêm ảnh nếu có
          }
          formData.append("productId", comment.productDetail.productId);

          // Gửi dữ liệu đến API
          await axiosInstance.post("/comments", formData, {
            headers: {
              Authorization: `Bearer ${accessToken}`,
              "Content-Type": "multipart/form-data",
            },
          });
        })
      );
      alert("Đánh giá thành công!");
      setShowCommentModal(false); // Đóng modal sau khi gửi thành công
    } catch (error) {
      console.error("Lỗi khi gửi đánh giá:", error);
      alert("Đã xảy ra lỗi khi gửi đánh giá. Vui lòng thử lại.");
    }
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
    <>
      <main>
        <style>
          {`
          .table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
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

          .modal .table {
            margin-top: 0;
            box-shadow: none;
            border-radius: 0;
          }

          .modal .table th {
            background-color: #f4f4f4;
            font-weight: bold;
          }

          .modal .table td {
            text-align: left;
          }

          .modal .table img {
            width: 50px;
            height: 50px;
            object-fit: cover;
            border-radius: 4px;
          }
        `}
        </style>
        <Modal
          show={showCommentModal}
          onHide={() => setShowCommentModal(false)}
          centered
        >
          <Modal.Header closeButton>
            <Modal.Title>Đánh Giá Sản Phẩm</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {commentDetails.map((detail, index) => (
              <div key={index} className="mb-3">
                <p>
                  <strong>Hình Ảnh:</strong>
                  <img
                    onClick={() =>
                      (window.location.href = `product/${detail.productDetail.productId}`)
                    }
                    src={detail.productDetail.image}
                    alt={detail.productDetail.image}
                    style={{
                      width: "150px",
                      height: "150px",
                      objectFit: "cover",
                      cursor: "pointer",
                    }}
                  />
                </p>
                <p>
                  <strong>Tên Sản Phẩm:</strong> {detail.productDetail.name}
                </p>
                <p>
                  <strong>Số Lượng:</strong>{" "}
                  {detail.productDetail.numberOfProducts}
                </p>
                <p>
                  <strong>Đơn Giá:</strong> {detail.productDetail.price} $
                </p>
                <p>
                  <strong>Tổng Tiền:</strong> {detail.productDetail.totalMoney}{" "}
                  $
                </p>

                <Form.Group>
                  <Form.Label>Bình Luận</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={3}
                    value={detail.comment}
                    onChange={(e) =>
                      handleCommentChange(index, "comment", e.target.value)
                    }
                  />
                </Form.Group>
                <Form.Group className="mt-2">
                  <Form.Label>Hình Ảnh</Form.Label>
                  <Form.Control
                    type="file"
                    accept="image/*" 
                    onChange={(e) =>
                      handleCommentChange(index, "image", e.target.files[0])
                    }
                  />
                </Form.Group>
              </div>
            ))}
          </Modal.Body>
          <Modal.Footer>
            <Button
              variant="secondary"
              onClick={() => setShowCommentModal(false)}
            >
              Đóng
            </Button>
            <Button variant="primary" onClick={handleSubmitComments}>
              Gửi Đánh Giá
            </Button>
          </Modal.Footer>
        </Modal>
        <Modal show={showModal} onHide={handleCloseModal} centered>
          <Modal.Header closeButton>
            <Modal.Title>Chi Tiết Đơn Hàng</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {selectedOrder && (
              <div>
                <p>
                  <strong>Mã Đơn Hàng:</strong> {selectedOrder.code}
                </p>
                <div>
                  <label>
                    <strong>Tên Người Nhận:</strong>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    value={selectedOrder.fullName}
                    onChange={(e) =>
                      setSelectedOrder({
                        ...selectedOrder,
                        fullName: e.target.value,
                      })
                    }
                  />
                </div>
                <div>
                  <label>
                    <strong>Số Điện Thoại:</strong>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    value={selectedOrder.phoneNumber}
                    onChange={(e) =>
                      setSelectedOrder({
                        ...selectedOrder,
                        phoneNumber: e.target.value,
                      })
                    }
                  />
                </div>
                <div>
                  <label>
                    <strong>Địa Chỉ:</strong>
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    value={selectedOrder.address}
                    onChange={(e) =>
                      setSelectedOrder({
                        ...selectedOrder,
                        address: e.target.value,
                      })
                    }
                  />
                </div>
                <p>
                  <strong>Ngày Đặt:</strong>{" "}
                  {new Date(selectedOrder.orderDate).toLocaleDateString()}
                </p>
                <p>
                  <strong>Trạng Thái:</strong> {selectedOrder.status}
                </p>
                <p>
                  <strong>Tổng Tiền:</strong> {selectedOrder.totalMoney} $
                </p>
                <h5 className="mt-4">Danh Sách Sản Phẩm</h5>
                <table className="table">
                  <thead>
                    <tr>
                      <th>Hình Ảnh</th>
                      <th>Tên Sản Phẩm</th>
                      <th>Số Lượng</th>
                      <th>Đơn Giá</th>
                      <th>Tổng</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedOrder.orderDetails.map((detail, index) => (
                      <tr key={index}>
                        <td>
                          <img
                            src={detail.image}
                            alt="Sản phẩm"
                            onClick={() => handleProductClick(detail.productId)}
                          />
                        </td>
                        <td>{detail.name}</td>
                        <td>{detail.numberOfProducts}</td>
                        <td>{detail.price} $</td>
                        <td>{detail.totalMoney} $</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleCloseModal}>
              Đóng
            </Button>
            <Button variant="primary" onClick={handleSaveChanges}>
              Lưu Thay Đổi
            </Button>
          </Modal.Footer>
        </Modal>
        <div className="container">
          <h1 className="mb-4">{header}</h1>
          <table className="table text-nowrap table-with-checkbox">
            <thead className="table-light">
              <tr>
                <th className="text-center">Mã Đơn Hàng</th>
                <th className="text-center">Tên Khách Hàng</th>
                <th className="text-center">Ngày Đặt Hàng</th>
                <th className="text-center">Địa Chỉ Giao Hàng</th>
                <th className="text-center">Số Điện Thoại</th>
                <th className="text-center">Tổng Tiền</th>
                <th className="text-center">Hành Động</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order, index) => (
                <tr key={index}>
                  <td className="align-middle text-center">{order.code}</td>
                  <td className="align-middle text-center">{order.fullName}</td>
                  <td className="align-middle text-center">
                    {new Date(order.orderDate).toLocaleDateString()}
                  </td>
                  <td className="align-middle text-center">{order.address}</td>
                  <td className="align-middle text-center">
                    {order.phoneNumber}
                  </td>
                  <td className="align-middle text-center">
                    {order.totalMoney} $
                  </td>
                  <td className="align-middle text-center">
                    <button
                      className="btn btn-primary btn-sm me-2"
                      onClick={() => handleViewOrder(order)}
                    >
                      Xem Chi Tiết
                    </button>
                    {status === "PENDING" && (
                      <button
                        className="btn btn-danger btn-sm me-2"
                        onClick={() => handleCancelOrder(order.id)}
                      >
                        Huỷ Đơn
                      </button>
                    )}
                    {status === "COMPLETED" && (
                      <button
                        className="btn btn-info btn-sm me-2"
                        onClick={() => handleComment(order.orderDetails)}
                      >
                        Đánh Giá
                      </button>
                    )}
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
      </main>
    </>
  );
}

export default UserOrder;
