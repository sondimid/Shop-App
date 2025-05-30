import React from "react";

const Loading = ({ size = "50px" }) => {
  const styles = {
    container: {
      position: "fixed", // Giữ nguyên vị trí trên màn hình
      top: 0,
      left: 0,
      width: "100vw", // Chiếm toàn bộ chiều rộng
      height: "100vh", // Chiếm toàn bộ chiều cao
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      justifyContent: "center",
      backgroundColor: "rgba(255, 255, 255, 0.8)", // Làm mờ nền
      zIndex: 9999, // Đảm bảo hiển thị trên cùng
    },
    spinner: {
      border: "4px solid rgba(0, 0, 255, 0.2)",
      borderTop: "4px solid #007bff",
      borderRadius: "50%",
      width: size,
      height: size,
      animation: "spin 1s linear infinite",
    },
    "@keyframes spin": {
      "0%": { transform: "rotate(0deg)" },
      "100%": { transform: "rotate(360deg)" },
    },
  };

  return (
    <div style={styles.container}>
      <div style={styles.spinner}></div>
    </div>
  );
};

export default Loading;
