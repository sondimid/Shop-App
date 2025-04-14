import Header from "../components/Header";
import Banner from "../components/Banner";
import Category from "../components/Category";
import NewProduct from "../components/NewProduct";
import HeroBanner from "../components/HeroBanner";
import Footer from "../components/Footer";
import DealOfTheDays from "../components/DealOfTheDays";
import LaptopBanner from "../components/LaptopBanner";
import Policy from "../components/Policy";
import { useEffect } from "react";
import axios from "axios";
import Cookies from "js-cookie";

function HomePage() {

   

  return (
    <>
      <Header />
      <Banner />
      <Category />
      <NewProduct />
      <HeroBanner />
      <DealOfTheDays />
      <LaptopBanner />
      <Policy/>
      <Footer />
    </>
  );
}

export default HomePage;
