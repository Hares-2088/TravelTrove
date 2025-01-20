export interface UserRequestModel {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  travelerIds?: string[]; // Matches backend's travelerIds
}

export interface UserResponseModel {
  userId: string;
  travelerId: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  permissions: string[];
  travelerIds?: string[];
}
