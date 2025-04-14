import React from "react";

function HeroBanner() {
    return <>
    <div className="hero-banner-section mt-100">
        <div className="container">
            <div className="row">
                <div className="col-md-6 mb-4">
                    <div className="position-relative hero-banner">
                        <img src="/assets/images/banner/shoping1.png" className="rounded" alt="eCommerce Html Template"/>
                        <div className="hero-banner-info">
                            <h5 className="hero-banner-title">Super Sale</h5>
                            <h3 className="hero-banner-title1">New Collection</h3>
                            <a href="shop.html" className="btn-buy mt-4">Shop Now</a>
                        </div>
                    </div>
                </div>
                <div className="col-md-6 mb-4">
                    <div className="position-relative hero-banner">
                        <img src="/assets/images/banner/shoping1.png" className="rounded" alt="shop_banner_img1"/>
                        <div className="hero-banner-info">
                            <h5 className="hero-banner-title">Super Sale</h5>
                            <h3 className="hero-banner-title1">Sale 40% Off</h3>
                            <a href="shop.html" className="btn-buy mt-4">Shop Now</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </>
}

export default HeroBanner