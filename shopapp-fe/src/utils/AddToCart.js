import axiosInstance from "../utils/RefreshToken";
import Cookies from "js-cookie";

export const handleAddToCart = async (productId, numberOfProduct) => {
  const cartDTO = {
    cartDetailDTOS: [
      {
        productId: productId,
        numberOfProducts: numberOfProduct,
      },
    ],
  };

  try {
    const accessToken = await Cookies.get("accessToken");
    if(!accessToken) {
      alert("Vui lòng đăng nhập trước khi thêm sản phẩm vào giỏ hàng!");
      window.location.href = "/login";
    }
    const response = await axiosInstance.put("/carts", cartDTO, {
      headers: {
        Authorization: `Bearer ${accessToken}`,
      },
    });
    alert("Thêm sản phẩm vào giỏ hàng thành công!");
  } catch (error) {
    alert(error.response.data);
  }
};
