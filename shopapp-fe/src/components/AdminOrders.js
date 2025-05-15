import React, { useEffect, useState } from "react";
import axiosInstance from "../utils/RefreshToken";
import Cookies from "js-cookie";
import { useNavigate } from "react-router-dom";
import Loading from "./Loading";
import { Modal, Button } from "react-bootstrap";
import Pagination from "./Pagination";

function AdminOrders({ status, updateStatus, header }) {
  const [orders, setOrders] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [showModal, setShowModal] = useState(false);
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

  const handleConfirm = async (id) => {
    setIsLoading(true);
    const accessToken = Cookies.get("accessToken");
    try {
      await axiosInstance.put(
        `/orders/${id}/status`,
        { status: updateStatus },
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );
      fetchOrders();
      setIsLoading(false);
      alert("Thành Công");
    } catch (error) {
      console.log(error.response);
      setIsLoading(false);
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

  const handleViewOrder = (order) => {
    setSelectedOrder(order);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedOrder(null);
  };

  const handleProductClick = (productId) => {
    navigate(`/product/${productId}`);
  };

  if (isLoading) {
    return <Loading />;
  }

  return (
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
              <p>
                <strong>Tên Người Nhận:</strong> {selectedOrder.fullName}
              </p>
              <p>
                <strong>Số Điện Thoại:</strong> {selectedOrder.phoneNumber}
              </p>
              <p>
                <strong>Địa Chỉ:</strong> {selectedOrder.address}
              </p>
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
          <Button variant="danger" onClick={handleCloseModal}>
            Đóng
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
                  {status !== "CANCELLED" && status !== "COMPLETED" ? (
                    <button
                      className="btn btn-primary btn-sm me-2"
                      onClick={() => handleConfirm(order.id)}
                    >
                      Cập nhật trạng thái
                    </button>
                  ) : null}
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
  );
}

export default AdminOrders;
