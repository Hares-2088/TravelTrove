import { ReviewRequestModel, ReviewResponseModel } from "../models/review.model";
import { useAxiosInstance } from "../../../shared/axios/useAxiosInstance";

export const useReviewsApi = () => {
  const axiosInstance = useAxiosInstance(); // Use Axios Hook

  // Fetch all reviews for a specific package
  const getReviewsByPackage = async (packageId: string): Promise<ReviewResponseModel[]> => {
    try {
      const response = await axiosInstance.get(`/reviews/${packageId}`);

      return response.data;
    } catch (error) {
      console.error("Error fetching reviews:", error);
      return [];  // Return empty array in case of error
    }
  };

  const addReview = async (review: ReviewRequestModel): Promise<ReviewResponseModel> => {
    const response = await axiosInstance.post<ReviewResponseModel>(`/reviews`, review);
    return response.data;
  };

  const getAverageRating = async (packageId: string): Promise<number | null> => {
    try {
      const response = await axiosInstance.get(`/reviews/${packageId}/average-rating`);
      if (response.status === 200) {
        return response.data?.averageRating ?? null;
      }
      return null;
    } catch (error) {
      console.error("Error fetching average rating:", error);
      return null;
    }
  };

  return {
    getReviewsByPackage,
    addReview,
    getAverageRating,
  };
};
