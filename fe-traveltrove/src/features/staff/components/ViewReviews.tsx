import React from "react";
import { useTranslation } from 'react-i18next';

interface ViewReviewsProps {
    packageId: string;
    reviews: any[];
    onClose: () => void;
}

const ViewReviews: React.FC<ViewReviewsProps> = ({ packageId, reviews, onClose }) => {
    const { t } = useTranslation();

    return (
        <div>
            <h2>{t('reviews')} {t('forPackage')} {packageId}</h2>
            <button onClick={onClose}>{t('close')}</button>
            <ul>
                {reviews.map((review, index) => (
                    <li key={index}>{review.reviewText}</li>
                ))}
            </ul>
        </div>
    );
};

export default ViewReviews;
