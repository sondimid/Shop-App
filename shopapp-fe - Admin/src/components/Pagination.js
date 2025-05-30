/* eslint-disable jsx-a11y/anchor-is-valid */
import React from "react";

function Pagination({
  pageNumber,
  totalPages,
  handlePreviousPage,
  handleNextPage,
}) {
  return (
    <>
      <div className="row mt-5">
        <div className="col d-flex justify-content-center">
          <nav>
            <ul className="pagination">
              <li className={`page-item ${pageNumber === 0 ? "disabled" : ""}`}>
                <a
                  style={{ cursor: "pointer" }}
                  className="page-link mx-1"
                  aria-label="Previous"
                  onClick={(e) => {
                    e.preventDefault();
                    handlePreviousPage();
                  }}
                >
                  <i className="bi bi-arrow-left-short"></i>
                </a>
              </li>

              <li className="page-item">
                <a className="page-link mx-1 active">
                  {pageNumber + 1} / {totalPages}
                </a>
              </li>
              <li
                className={`page-item ${
                  pageNumber >= totalPages - 1 ? "disabled" : ""
                }`}
              >
                <a
                  style={{ cursor: "pointer" }}
                  className="page-link mx-1"
                  aria-label="Next"
                  onClick={(e) => {
                    e.preventDefault();
                    handleNextPage();
                  }}
                >
                  <i className="bi bi-arrow-right-short"></i>
                </a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </>
  );
}

export default Pagination;
