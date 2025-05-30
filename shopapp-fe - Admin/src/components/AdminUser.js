import React from "react";
import { useState, useEffect } from "react";
import axiosInstance from "../utils/RefreshToken";
import Loading from "./Loading";
import Select from "react-select";
import Cookies from "js-cookie";
import Pagination from "./Pagination";

function AdminUser() {
  const [loading, setLoading] = useState(true);
  const [userList, setUserList] = useState([]);
  const [accessToken, setAccessToken] = useState(null);
  const [keyword, setKeyword] = useState(null);
  const [page, setPage] = useState({
    pageNumber: 0,
    totalPages: 1,
  });
  const [sortField, setSortField] = useState("id");
  const [sortDirection, setSortDirection] = useState("ASC");

  const options = [
    { value: "name", label: "Tên" },
    { value: "createdAt", label: "Ngày Tạo" },
  ];

  const [selectedOption, setSelectedOption] = useState(null);

  const fetchUser = async () => {
    try {
      const response = await axiosInstance.get("/admin/users", {
        params: {
          page: page.pageNumber,
          limit: 5,
          keyword: keyword,
          sortField: sortField,
          sortDirection: sortDirection,
        },
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      setUserList(response.data.content);
      setPage({
        pageNumber: response.data.pageNumber,
        totalPages: response.data.totalPages,
      });
      setLoading(false);
    } catch (error) {
      alert("Không thể tải danh sách người dùng.");
      setLoading(false);
    }
  };

  const handleLockUser = async (userId) => {
    const ids = [userId];
    try {
      await axiosInstance.put("/admin/users/lock", ids, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      fetchUser();
    } catch (error) {
      alert("Không thể thay đổi trạng thái người dùng.");
    }
  };

  const handleUnlockUser = async (userId) => {
    const ids = [userId];
    try {
      await axiosInstance.put("/admin/users/unlock", ids, {
        headers: {
          Authorization: `Bearer ${accessToken}`,
        },
      });
      fetchUser();
    } catch (error) {
      alert("Không thể thay đổi trạng thái người dùng.");
    }
  };

  useEffect(() => {
    setAccessToken(Cookies.get("accessToken"));
    fetchUser();
  }, [page.pageNumber, keyword, sortDirection, sortField]);

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
    if (value === "name") {
      setSortDirection("ASC");
      setSortField("fullName");
    } else if (value === "createdAt") {
      setSortDirection("ASC");
      setSortField("createdAt");
    }
  };

  if (loading) {
    return <Loading />;
  }
  return (
    <>
      <style>
        {`
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
          .pagination-container {
            margin-top: 20px;
            display: flex;
            justify-content: center;
          }
        `}
      </style>
      <main>
        <div className="product-wishlist">
          <div className="container">
            <div className="row">
              <div className="col-12">
                <div>
                  <div className="d-flex align-items-center">
                    <div className="col-3 me-3">
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

                    <div className="col-2">
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
                </div>
              </div>
            </div>
          </div>
          <section className="mt-5 mb-5">
            <div className="container-fluid">
              <div className="row">
                <div className="col-lg-12">
                  <div>
                    <div className="table-responsive">
                      <table className="table text-nowrap table-with-checkbox">
                        <thead className="table-light">
                          <tr>
                            <th className="text-center">Ảnh Đại Diện</th>
                            <th>Tên Người Dùng</th>
                            <th>Email</th>
                            <th>Số Điện Thoại</th>
                            <th>Địa Chỉ</th>
                            <th>Trạng Thái</th>
                            <th>Khoá/ Mở Khoá</th>
                          </tr>
                        </thead>
                        <tbody>
                          {userList.map((user, index) => (
                            <tr key={index}>
                              <td className="align-middle">
                                <img
                                  src={
                                    user.avatar ||
                                    "/assets/images/default-avatar.png"
                                  }
                                  className="icon-shape icon-xxl"
                                  alt={user.fullName}
                                />
                              </td>

                              <td className="align-middle">
                                <div>
                                  <h5 className="fs-6 mb-0">
                                    <a href="#!" className="text-black">
                                      {user.fullName}
                                    </a>
                                  </h5>
                                </div>
                              </td>

                              <td className="align-middle">{user.email}</td>

                              <td className="align-middle">
                                {user.phoneNumber}
                              </td>

                              <td className="align-middle">{user.address}</td>

                              <td className="align-middle">
                                <span
                                  className={`badge ${
                                    user.isActive === true
                                      ? "bg-success"
                                      : "bg-danger"
                                  }`}
                                >
                                  {user.isActive === true
                                    ? "Đã Kích Hoạt"
                                    : "Bị khóa"}
                                </span>
                              </td>

                              <td className="align-middle">
                                <button
                                  className={`btn btn-md ${
                                    user.isActive === true
                                      ? "btn-danger"
                                      : "btn-success"
                                  } py-2 rounded`}
                                  onClick={() =>
                                    user.isActive === true
                                      ? handleLockUser(user.id)
                                      : handleUnlockUser(user.id)
                                  }
                                >
                                  {user.isActive === true ? "Khoá" : "Mở Khoá"}
                                </button>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <Pagination
              pageNumber={page.pageNumber}
              totalPages={page.totalPages}
              handleNextPage={handleNextPage}
              handlePreviousPage={handlePreviousPage}
            />
          </section>
        </div>
      </main>
    </>
  );
}

export default AdminUser;
