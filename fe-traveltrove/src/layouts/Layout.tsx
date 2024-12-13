import React from "react";
import NavBar from "./NavBar";
import Footer from "./Footer";
import { Outlet } from "react-router-dom";

const Layout: React.FC = () => {
  return (
    <div className="app-container">
      <NavBar />
      <main className="content">
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default Layout;
