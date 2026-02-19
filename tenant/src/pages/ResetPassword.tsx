import React, { useState } from 'react';  
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Lock, LockIcon } from 'lucide-react';
import axios, { AxiosError } from 'axios';
import { useApi } from '@/lib/useApi';
import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';

interface ErrorResponse {
  message: string;
}

const ResetPassword: React.FC = () => {
  
  const [alertMessage, setAlertMessage] = useState<string | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [alertType, setAlertType] = useState<'success' | 'error'>('error'); // 'success' or 'error' type
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  
  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    setError(null);
    setIsLoading(true);

    try {
      const response = await axios.post(`${useApi}/auth/reset-password/${token}`, {
        password,
      });
      setAlertType("success")
      setAlertMessage(response.data.message);
      navigate('/');
    } catch (error) {
      const axiosError = error as AxiosError;
      const errorMessage = axiosError.response
        ? (axiosError.response.data as ErrorResponse).message
        : "An unexpected error occurred.";
        setAlertType("error")
        setAlertMessage(errorMessage);
        setError(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center">
      <div className="max-w-4xl w-100 shadow-lg rounded-lg overflow-hidden bg-transparent">
      <Card className=" bg-transparent backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-center text-blue-800 dark:text-blue-600">
            Reset Password
          </CardTitle>
        </CardHeader>
          {error && <p className='text-red-500 text-sm mb-4'>{error}</p>}

          <form onSubmit={handleSubmit} className='ml-6 mr-6 space-y-4'>
            <div className="space-y-2">
              <Label htmlFor="password">New Password</Label>
              <div className="relative">
                <LockIcon className="absolute left-3 top-3 h-5 w-5 text-blue-500" />
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  className="pl-10"
                  //{...register('password', { required: 'Password is required' })}
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="password">Confirm New Password</Label>
              <div className="relative">
                <LockIcon className="absolute left-3 top-3 h-5 w-5 text-blue-500" />
                <Input
                  id="confirmPassword"
                  type="password"
                  placeholder="••••••••"
                  className="pl-10"
                  
                />
              </div>
            </div>

            <Button
              type='submit'
              className='w-full bg-blue-500 hover:bg-blue-700 text-white'
              disabled={isLoading}
            >
              {isLoading ? "Resetting..." : "Set New Password"}
            </Button>
            
          </form>

        <div className='px-8 py-4 flex justify-center'>
          <Link to='/' className='flex items-center text-sm' style={{ color: '#0401A0' }}>
            <Lock className='h-4 w-4 mr-2' /> Back to Login
          </Link>
        </div>
      </Card>
    </div>
    </div>
  );
};

export default ResetPassword;
