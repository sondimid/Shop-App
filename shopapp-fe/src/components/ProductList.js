import React, { useEffect, useState } from "react";
import axios from "axios";
import ProductCard from "./ProductCart";
import axiosInstance from "../utils/RefreshToken";
const ProductList = () => {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await axiosInstance.get(
          "/products/lastest"
        );
        setProducts(response.data);
        console.log(response.data);
      } catch (error) {
        console.log(error.response);
      }
    };

    fetchProducts();
  }, []);

  return (
    <>
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </>
  );
};

export default ProductList;
