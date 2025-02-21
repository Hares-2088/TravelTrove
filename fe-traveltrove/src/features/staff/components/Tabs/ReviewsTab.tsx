import React, { useState } from "react";
import { useTranslation } from 'react-i18next';
import { useReviewsApi } from "../../../reviews/api/review.api";
import AddReviewButton from "../AddReviewButton";
import ViewReviews from "../ViewReviews";

const ReviewsTab: React.FC = () => {
  const { t } = useTranslation();
  const { getReviewsByPackage, addReview, getAverageRating } = useReviewsApi(); // API methods
  const [reviews, setReviews] = useState<any[]>([]);
  const [averageRating, setAverageRating] = useState<number | null>(null);
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

  const handleAddReview = async (newReview: any) => {
    await addReview(newReview);
    closeModal();
  };

  const handleViewReviews = async (packageId: string) => {
    const reviews = await getReviewsByPackage(packageId);
    setReviews(reviews);
    const rating = await getAverageRating(packageId);
    setAverageRating(rating);
  };

  return (
    <div className="reviews-tab">
      <h1>{t('reviews')}</h1>
      {averageRating !== null && <p>{t('averageRating')}: {averageRating}</p>}

      <div className="package-buttons">
        <button onClick={() => openModal("view", "package123")}>{t('viewReviews')} (Package 123)</button>
        <button onClick={() => openModal("add", "package123")}>{t('addReview')} (Package 123)</button>
        <button onClick={() => openModal("view", "package456")}>{t('viewReviews')} (Package 456)</button>
        <button onClick={() => openModal("add", "package456")}>{t('addReview')} (Package 456)</button>
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
                reviews={reviews}
                onClose={closeModal}
              />
            )}
            {modalType === "add" && (
              <AddReviewButton
                packageId={currentPackageId!}
                onSubmit={(newReview: any) => handleAddReview(newReview)}
              />
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ReviewsTab;