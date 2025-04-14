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
        const refreshToken = Cookies.get("refreshToken");
        const response = await axios.post(
          "http://localhost:8080/api/v1/users/refresh-token",null,
          { 
            headers: {
              refresh_token: refreshToken,
            },
           }
        );

        const newAccessToken = response.data.accessToken;
        Cookies.set("accessToken", newAccessToken);

        originalRequest.headers["Authorization"] = `Bearer ${newAccessToken}`;

        return axiosInstance(originalRequest);
      } catch (refreshError) {
        Cookies.remove("user");
        Cookies.remove("accessToken");
        Cookies.remove("refreshToken");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;