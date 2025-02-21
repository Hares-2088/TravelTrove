import React, { useState } from "react";
import { useTranslation } from 'react-i18next';

interface AddReviewButtonProps {
    packageId: string;
    onSubmit: (newReview: any) => void;
}

const AddReviewButton: React.FC<AddReviewButtonProps> = ({ packageId, onSubmit }) => {
    const { t } = useTranslation();
    const [reviewText, setReviewText] = useState("");

    const handleSubmit = () => {
        const newReview = { packageId, reviewText };
        onSubmit(newReview);
        setReviewText("");
    };

    return (
        <div>
            <textarea
                value={reviewText}
                onChange={(e) => setReviewText(e.target.value)}
                placeholder={t('writeYourReview')}
            />
            <button onClick={handleSubmit}>{t('submitReview')}</button>
        </div>
    );
};

export default AddReviewButton;
