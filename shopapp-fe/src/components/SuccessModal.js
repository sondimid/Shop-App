import React from "react";

const SuccessModal = ({ message, onClose, onConfirm }) => {
  return (
    <div className="modal fade show d-block" tabIndex="-1" role="dialog">
      <div className="modal-dialog modal-dialog-centered" role="document">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title">Notification</h5>
            <button
              type="button"
              className="btn-close"
              onClick={onClose}
            ></button>
          </div>
          <div className="modal-body">
            <p>{message}</p>
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-primary"
              onClick={onConfirm}
            >
              OK
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SuccessModal;
