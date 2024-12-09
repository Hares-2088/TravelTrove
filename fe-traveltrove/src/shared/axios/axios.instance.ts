import axios, { AxiosInstance } from 'axios';
import axiosErrorResponseHandler from './axios.error.response.handler';

// axios.defaults.withCredentials = true;

const createAxiosInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: 'http://localhost:8080/api/v1/',
    headers: {
      'Content-Type': 'application/json',
    },
  });

  instance.interceptors.response.use(
    response => response,
    error => {
      handleAxiosError(error);
      return Promise.reject(error);
    }
  );

  return instance;
};

const handleAxiosError = (error: unknown): void => {
  if (axios.isAxiosError(error)) {
    const statusCode = error.response?.status ?? 0;
    axiosErrorResponseHandler(error, statusCode);
  } else {
    console.error('An unexpected error occurred:', error);
  }
};

const axiosInstance = createAxiosInstance();
export default axiosInstance;