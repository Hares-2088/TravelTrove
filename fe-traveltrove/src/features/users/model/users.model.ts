export interface UserRequestModel {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface UserResponseModel {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
  permissions: string[];
}
