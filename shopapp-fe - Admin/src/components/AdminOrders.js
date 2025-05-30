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
            border-collapse: separate;
            border-spacing: 0;
            margin-top: 24px;
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

          .btn-success {
            background-color: #a3e635;
            color: #fff;
            border: none;
            box-shadow: 0 1px 4px rgba(163,230,53,0.08);
          }

          .btn-success:hover {
            background-color: #65a30d;
            color: #fff;
          }

          .modal .table {
            margin-top: 0;
            box-shadow: none;
            border-radius: 0;
          }

          .modal .table th {
            background-color: #fff9f4;
            font-weight: bold;
            color: #f7a94d;
          }

          .modal .table td {
            text-align: left;
          }

          .modal .table img {
            width: 50px;
            height: 50px;
            object-fit: cover;
            border-radius: 6px;
          }

          .pagination-container {
            margin-top: 32px;
            display: flex;
            justify-content: center;
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
        <h1
          className="mb-4"
          style={{
            color: "#f6891f",
            fontWeight: 700,
            letterSpacing: "0.5px",
            textAlign: "left",
            marginTop: "24px",
            fontSize: "1.5rem", 
          }}
        >
          {header}
        </h1>
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
