import { useApi } from "@/lib/useApi";
import type { AuthResponse, AuthStoreState, LoginCredentials, RefreshTokenResponse, Users } from "@/utils/types";
import axios from "axios";
import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

interface AuthStoreActions {
    // Auth actions
    login: (credentials: LoginCredentials) => Promise<void>;
    register: (credentials: Partial<Users>) => Promise<Users>;
    registerClientAdmin: (credentials: Partial<Users>) => Promise<Users>;
    logout: () => Promise<void>;
    refreshAccessToken: () => Promise<void>;

    // Password reset
    requestPasswordReset: (email: string) => Promise<void>;
    resetPassword: (newPassword: string, resetPasswordToken: string) => Promise<void>;

    // Email verification
    verifyEmail: (verificationToken: string) => Promise<void>;

    // State Management
    setLoading: (isLoading: boolean) => void;
    setError: (error: string | null) => void;
    clearError: () => void;
    clearAuth: () => void;
}

export const useAuthStore = create<AuthStoreState & AuthStoreActions>()(
    persist(
        (set, get) => ({
            // initial state
            user: null,
            accessToken: null,
            refreshToken: null,
            isLoading: false,
            error: null,

            // set loading state
            setLoading: (isLoading: boolean) => set({ isLoading }),

            // set error state
            setError: (error: string | null) => set({ error }),

            // clear error
            clearError: () => set({ error: null }),

            // clear auth data
            clearAuth: () => set({
                user: null,
                accessToken: null,
                refreshToken: null,
            }),

            login: async (credentials: LoginCredentials) => {
                set({ isLoading: true, error: null });

                try {
                    const response = await axios.post(`${useApi}/auth/login`, { credentials })
                
                    if (response.status !== 201) {
                        const errorData = await response.data;
                        throw new Error(errorData.message || 'Login failed');
                    }

                    const authData: AuthResponse = await response.data;

                    const user = decodeToken(authData.accessToken);
          
                    set({ 
                        user,
                        accessToken: authData.accessToken,
                        refreshToken: authData.refreshToken,
                        isLoading: false 
                    });
                } catch (error) {
                    set({ 
                      error: error instanceof Error ? error.message : 'Login failed',
                      isLoading: false 
                    });
                    throw error;
                  }
                },
          
                // Register regular user
                register: async (credentials: Partial<Users>) => {
                  set({ isLoading: true, error: null });
                  
                  try {
                    const response = await axios.post(`${useApi}/auth/register`, { credentials });
          
                    if (response.status !== 201) {
                      const errorData = await response.data;
                      throw new Error(errorData.message || 'Registration failed');
                    }
          
                    const user: Users = await response.data;
                    set({ isLoading: false });
                    
                    // Registration successful - user needs to verify email
                    return user;
                  } catch (error: any) {
                    const errorMessage = error.response?.data?.message || error.message || "Registration failed";
                    set({ 
                      error: errorMessage,
                      isLoading: false 
                    });
                    throw error;
                  }
                },
            // Register client admin
            registerClientAdmin: async (credentials: Partial<Users>) => {
              set({ isLoading: true, error: null });
              
              try {
                const response = await axios.post(`${useApi}/auth/register-admin`, { credentials },
                  {
                    headers: {
                      'Content-Type': "application/json"
                    }
                  }
                );

                if (response.status !== 201) {
                  const errorData = await response.data;
                  throw new Error(errorData.message || 'Registration failed');
                }

                const user: Users = await response.data;
                set({ isLoading: false });
                
                return user;
              } catch (error) {
                set({ 
                  error: error instanceof Error ? error.message : 'Registration failed',
                  isLoading: false 
                });
                throw error;
              }
            },

            // Logout
            logout: async () => {
              const { refreshToken } = get();
              
              if (refreshToken) {
                try {
                  await axios.post(`${useApi}/auth/logout`, 
                    { refreshToken },
                    {
                      headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${get().accessToken}`,
                      }
                  });
                } catch (error) {
                  console.error('Logout API call failed:', error);
                }
              }
              
              set({ 
                user: null,
                accessToken: null,
                refreshToken: null,
                error: null 
              });
            },

            // Refresh access token
            refreshAccessToken: async () => {
              const { refreshToken } = get();
              
              if (!refreshToken) {
                throw new Error('No refresh token available');
              }
              
              set({ isLoading: true, error: null });
              
              try {
                const response = await axios.post(`${useApi}/auth/refresh-token`, { refreshToken });

                if (response.status !== 200) {
                  const errorData = await response.data;
                  throw new Error(errorData.message || 'Token refresh failed');
                }

                const tokenData: RefreshTokenResponse = await response.data;
                const user = decodeToken(tokenData.accessToken);
                
                set({ 
                  user,
                  accessToken: tokenData.accessToken,
                  refreshToken: tokenData.refreshToken,
                  isLoading: false 
                });
              } catch (error) {
                set({ 
                  error: error instanceof Error ? error.message : 'Token refresh failed',
                  isLoading: false 
                });
                
                // Clear auth data if refresh fails
                get().clearAuth();
                throw error;
              }
            },

            // Request password reset
            requestPasswordReset: async (email: string) => {
              set({ isLoading: true, error: null });
              
              try {
                const response = await axios.post(`${useApi}/auth/request-password-reset`, { email });

                if (response.status != 200) {
                  const errorData = await response.data;
                  throw new Error(errorData.message || 'Password reset request failed');
                }

                set({ isLoading: false });
              } catch (error) {
                set({ 
                  error: error instanceof Error ? error.message : 'Password reset request failed',
                  isLoading: false 
                });
                throw error;
              }
            },

            // Reset password
            resetPassword: async (newPassword: string, resetPasswordToken: string) => {
              set({ isLoading: true, error: null });
              
              try {
                const newPasswordDTO = { password: newPassword };
                
                const response = await axios.post(
                  `${useApi}/auth/reset-password?resetPasswordToken=${resetPasswordToken}`,
                  newPasswordDTO
                );

                if (response.status !== 200) {
                  const errorData = await response.data;
                  throw new Error(errorData.message || 'Password reset failed');
                }

                set({ isLoading: false });
              } catch (error) {
                set({ 
                  error: error instanceof Error ? error.message : 'Password reset failed',
                  isLoading: false 
                });
                throw error;
              }
            },

            // Verify email
            verifyEmail: async (verificationToken: string) => {
              set({ isLoading: true, error: null });
              
              try {
                const response = await axios.post(
                  `${useApi}/auth/verify-email?verificationToken=${verificationToken}`);

                if (response.status !== 200) {
                  const errorData = await response.data;
                  throw new Error(errorData.message || 'Email verification failed');
                }

                set({ isLoading: false });
              } catch (error) {
                set({ 
                  error: error instanceof Error ? error.message : 'Email verification failed',
                  isLoading: false 
                });
                throw error;
              }
            },
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
      }),
    }
  )
);

// Helper function to decode JWT token
const decodeToken = (token: string): Users | null => {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );

    const decoded = JSON.parse(jsonPayload);
    
    return {
      userId: decoded.sub,
      email: decoded.email || '',
      firstname: decoded.name || '',
      lastname: decoded.lastname || '',
      role: decoded.role,
      identificationNumber: decoded.identificationNumber,
      createdBy: decoded.createdBy,
      createdDate: decoded.createdDate,
      password: '',
      status: decoded.status,
      organizationId: decoded.organizationId,
      deletedBy: decoded.deletedBy,
      verificationTokenExpiresAt: decoded.verificationTokenExpiresAt,
      profilePic: decoded.profilePic,
      deletedDate: decoded.deletedDate,
      resetPasswordExpiresAt: decoded.resetPasswordExpiresAt,
      resetPasswordToken: decoded.resetPasswordToken,
      updatedBy: decoded.updatedBy,
      updatedDate: decoded.updatedDate,
      verificationToken: decoded.verificationToken
    };
  } catch (error) {
    console.error('Failed to decode token:', error);
    return null;
  }
};