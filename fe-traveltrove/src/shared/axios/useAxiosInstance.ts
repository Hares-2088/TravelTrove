import axios, { AxiosInstance } from 'axios';
import { useAuth0 } from '@auth0/auth0-react';
import axiosErrorResponseHandler from './axios.error.response.handler';

export const useAxiosInstance = (): AxiosInstance => {
  const { getAccessTokenSilently } = useAuth0();

  // Create Axios instance
  const instance = axios.create({
    baseURL: 'http://localhost:8080/api/v1/',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request Interceptor: Attach Access Token
  instance.interceptors.request.use(
    async config => {
      try {
        const token = await getAccessTokenSilently();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
          console.log('Access token attached:', token); // Debug log
        } else {
          console.warn('Access token not found.');
        }
      } catch (error) {
        console.error('Error fetching access token:', error);
      }
      return config;
    },
    error => {
      console.error('Request error:', error);
      return Promise.reject(error);
    }
  );

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
