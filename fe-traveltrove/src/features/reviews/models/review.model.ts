export interface ReviewRequestModel {
  packageId: string;
  userId: string;
  reviewerName: string;
  rating: number;
  review: string;
  date: string;
  averageRating: number;
  }

export interface ReviewResponseModel {
  reviewId: string;
  packageId: string;
  userId: string;
  reviewerName: string;
  rating: number;
  review: string;
  date: string;
  averageRating: number;
}