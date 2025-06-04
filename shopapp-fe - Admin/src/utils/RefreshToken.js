import axios from "axios";
import Cookies from "js-cookie";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api/v1",
});

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    if (error.response?.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const response = await axios.post(
          "http://localhost:8080/api/v1/users/refresh-token",
          null,
          {
            withCredentials: true,
          }
        );

        const newAccessToken = response.data.accessToken;
        Cookies.set("accessToken", newAccessToken);

        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;

        return axiosInstance(originalRequest);
      } catch (refreshError) {
        alert(refreshError.response.data);
        Cookies.remove("user");
        Cookies.remove("accessToken");
        window.location.href = "/login";
      }
    }
  }
);

export default axiosInstance;
