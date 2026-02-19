import axios, { AxiosError } from 'axios';
import { useApi } from '../useApi';
import type { Users } from '../../utils/types';

// Function to convert binary data (ArrayBuffer) to base64 string
const arrayBufferToBase64 = (buffer: ArrayBuffer): string => {
  let binary = '';
  const bytes = new Uint8Array(buffer);
  const length = bytes.byteLength;
  for (let i = 0; i < length; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary);  
};

export const fetchUserProfile = async (uId: string) => {
  try {
    const response = await fetch(`${useApi}/users/${uId}`,{
      credentials: 'include'
    });
    if (!response.ok) {  // Check if the response is successful
      const errorData = await response.json();
      console.error('Error fetching user profile:', errorData.message || 'Unknown error');
      return null;  // Return null in case of error
    }
    const user = await response.json();    
      if (user && user.profilePic && user.profilePic.data) {
        const buffer = user.profilePic.data;
        const base64ProfilePic = arrayBufferToBase64(buffer);
        user.profilePic = `data:image/png;base64,${base64ProfilePic}`;
      } else {
        console.warn('Profile picture not found or not in expected format');
        user.profilePic = null;  
      }
    return user;
  } catch (error) {
    console.error('Error fetching user profile:', error);
    return null;  // Return null in case of fetch failure
  }
};



export const updateUserProfile = async (uId: string, userData: Partial<Users>) => {
  try {
    const response = await axios.put(`${useApi}/users/${uId}`, userData);
    return response.data;
  } catch (error) {
    if (error instanceof Error) {
      console.error('Error updating user profile:', error.message);
    } else {
      console.error('An unknown error occurred');
    }
    throw error;
  }
};

export const uploadProfilePic = async (uId: string, formData: FormData) => {
  try {
    const response = await axios.put(`${useApi}/users/uploadProfilePic/${uId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  } catch (error) {
    if (error instanceof Error) {
      console.error('Error updating profile picture:', error.message);
    } else {
      console.error('An unknown error occurred');
    }
    throw error;
  }
};

export const fetchImage = async (uId: string) => {
  try {
    const response = await axios.get(`${useApi}/users/profilePicture/${uId}`);
    return response.data;
  } catch (error) {
    if (error instanceof AxiosError && error.response && error.response.status === 404) {
      // Handle the case where the profile picture is not found
      console.warn('Profile picture not found, using default image');
      return 'default-profile-pic-url'; // Replace with your default profile picture URL
    } else if (error instanceof Error) {
      console.error('Error fetching profile picture:', error.message);
    } else {
      console.error('An unknown error occurred');
    }
    return null; // Return null or an appropriate fallback value
  }
};