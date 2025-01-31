import axios, { AxiosInstance } from 'axios';
import { useAuth0 } from '@auth0/auth0-react';
import axiosErrorResponseHandler from './axios.error.response.handler';

export const useAxiosInstance = (): AxiosInstance => {
  const { getAccessTokenSilently } = useAuth0();

  // Create Axios instance
  const instance = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
    headers: { "Content-Type": "application/json" },
  });

  // Attach Authorization Token
  instance.interceptors.request.use(async config => {
    try {
      const token = await getAccessTokenSilently();
      if (token) {
        console.log(token)
        config.headers.Authorization = `Bearer ${token}`;
      } else {
        console.warn("No JWT token found.");
      }
    } catch (error) {
      console.error("Error getting JWT token:", error);
    }
    return config;
  });


  // Response Interceptor: Handle Errors
  instance.interceptors.response.use(
    response => response,
    error => {
      handleAxiosError(error);
      return Promise.reject(error);
    }
  );

  return instance;
};

// Error Handling Logic
const handleAxiosError = (error: unknown): void => {
  if (axios.isAxiosError(error)) {
    const statusCode = error.response?.status ?? 0;
    console.error(`Axios error [${statusCode}]:`, error.response?.data ?? error.message);
    axiosErrorResponseHandler(error, statusCode);
  } else {
    console.error('An unexpected error occurred:', error);
  }
};
