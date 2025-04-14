import React from "react";
function Banner() {
  return (
    <>
      <div className="banner py-4">
        <div className="container">
          <div className="owl-carousel owl-theme banner-slider">
            <div>
              <img
                src="/assets/images/banner/1.png"
                className="rounded"
                alt="eCommerce Template"
              />{" "}
            </div>
            <div>
              <img
                src="/assets/images/banner/2.png"
                className="rounded"
                alt="eCommerce Template"
              />{" "}
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
export default Banner;
