import React from "react";

function LaptopBanner() {
    return <>
    <div className="section-offer mt-100 d-none d-sm-none d-md-none d-lg-block">
        <div className="container">
            <div className="position-relative">
                <img src="/assets/images/banner/shoping-laptop.png" className="w-100 rounded"
                    alt="eCommerce Html Template"/>
                <div className="section-offer-info">
                    <p className="offer-info-dis">Sale <b className="text-warning font-italic">30% Off</b> </p>
                    <p className="offer-info-title">Mi Latop</p>
                    <p className="offer-specific">HD Touchscreen, 11th Gen Intel</p>
                    <a href="shop.html" className="btn-buy mt-3">Shop Now</a>
                </div>
            </div>
        </div>
        </div>
    </>
}

export default LaptopBanner