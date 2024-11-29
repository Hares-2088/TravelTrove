import axios from 'axios';

const API_URL = 'http://localhost:8080/api/v1/tours'; // Update this if your backend has a different base URL

export const getAllTours = async () => {
  try {
    const response = await axios.get(API_URL);
    return response.data;
  } catch (error) {
    console.error('Error fetching tours:', error);
    throw error;
  }
};
