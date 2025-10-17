import axios, { AxiosError } from 'axios';
import React, { useState } from 'react'
import { useNavigate, useParams, useSearchParams, type ErrorResponse } from 'react-router-dom';
import { useApi } from '../lib/useApi';

const ResetPassword = () => {
    const [password, setPassword] = useState<string>("");
    const [confirmPassword, setConfirmPassword] = useState<string>("");
    const [isLoading, setIsLoading] = useState<boolean>(false);

    const [searchParams] = useSearchParams();
    const resetPasswordToken = searchParams.get("resetPasswordToken");
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();

        if (password !== confirmPassword) {
            console.log("Passwords do not match");
            return;
        }

        try {
            const response = await axios.post(`${useApi}/auth/reset-password?resetPasswordToken=${resetPasswordToken}`, 
                { password },{
                headers: { 'Content-Type': 'application/json' },
                withCredentials: true
            });
            navigate("/")
        } catch (error) {
            const axiosError = error as AxiosError;
            const errorMessage = axiosError.response
            ? (axiosError.response.data as ErrorResponse).message
            : "An unexpected error occured";
        }
    }

  return (
    <div>
        <form onSubmit={handleSubmit}>
            <input
                type='password'
                id='password'
                placeholder='New Password'
                className='w-full p-2 rounded border border-gray-500 bg-gray-200 text-gray-700'
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
            />
            <input
                type='password'
                id='confirmPassword'
                placeholder='Confirm New Password'
                className='w-full p-2 rounded border border-gray-500 bg-gray-200 text-gray-700'
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
            />
            <button
                type='submit'
                className='w-full py-3 px-4 bg-gradient-to-r from-[#0f3ca3] to-[#071840] text-white font-bold rounded-lg hover:from-[#071840] hover:to-[#0f3ca3] focus:outline-none transition duration-200'
                disabled={isLoading}
            >
                {isLoading ? "Resetting..." : "Set New Password"}
            </button>
        </form>
    </div>
  )
}

export default ResetPassword