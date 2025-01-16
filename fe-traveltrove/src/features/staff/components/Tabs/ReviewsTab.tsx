import React, { useState } from "react";
import { useReviewsApi } from "../../../reviews/api/review.api"; 
import AddReview from "../AddReviewButton";
import ViewReviews from "../ViewReviews";

const ReviewsTab: React.FC = () => {
  const { getReviewsByPackage, addReview } = useReviewsApi(); // API methods
  const [reviews, setReviews] = useState<any[]>([]);
  const [isOverlayVisible, setIsOverlayVisible] = useState(false);
  const [modalType, setModalType] = useState<"add" | "view" | null>(null);
  const [currentPackageId, setCurrentPackageId] = useState<string | null>(null);

  const openModal = (type: "add" | "view", packageId: string) => {
    setModalType(type);
    setCurrentPackageId(packageId);
    setIsOverlayVisible(true);
  };

  const closeModal = () => {
    setIsOverlayVisible(false);
    setModalType(null);
    setCurrentPackageId(null);
  };

  const handleAddReview = async (packageId: string, newReview: any) => {
    await addReview(packageId, newReview);
    closeModal();
  };

  const handleViewReviews = async (packageId: string) => {
    const reviews = await getReviewsByPackage(packageId);
    setReviews(reviews);
  };

  return (
    <div className="reviews-tab">
      <h1>Reviews</h1>

      <div className="package-buttons">
        <button onClick={() => openModal("view", "package123")}>View Reviews (Package 123)</button>
        <button onClick={() => openModal("add", "package123")}>Add Review (Package 123)</button>
        <button onClick={() => openModal("view", "package456")}>View Reviews (Package 456)</button>
        <button onClick={() => openModal("add", "package456")}>Add Review (Package 456)</button>
      </div>

      {isOverlayVisible && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button className="close-button" onClick={closeModal}>
              &times;
            </button>

            {modalType === "view" && (
              <ViewReviews
                packageId={currentPackageId!}
                reviews={reviews} onClose={function (): void {
                  throw new Error("Function not implemented.");
                } }              />
            )}
            {modalType === "add" && (
              <AddReview
                packageId={currentPackageId!}
                onSubmit={(newReview) => handleAddReview(currentPackageId!, newReview)}
              />
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ReviewsTab;
