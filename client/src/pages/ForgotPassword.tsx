import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useApi } from '@/lib/useApi';
import { UserIcon } from 'lucide-react';
import React, { useState } from 'react';

interface ForgotPasswordFormData {
  email: string;
}

export default function ForgotPassword() {
  const [alertType, setAlertType] = useState<'success' | 'error'>('error');
  const [formData, setFormData] = useState<ForgotPasswordFormData>({ email: '' });
  const [errors, setErrors] = useState<{ email?: string }>({});
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [alertMessage, setAlertMessage] = useState<string | null>(null);
  
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({ ...prevData, [name]: value }));
  };

  const validate = () => {
    const newErrors: { email?: string } = {};
    if (!formData.email) {
      newErrors.email = 'Email is required';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      const response = await fetch(`${useApi}/auth/forgot-password`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData),
      });

      const result = await response.json();
      if (response.ok) {
        setAlertType('success')
        setAlertMessage("Password reset email has been sent. Please check your inbox.");
      } else {
        setAlertType('error')
        setAlertMessage(result.message || 'Failed to send password reset email.');
      }
      setIsDialogOpen(true);
    } catch (error) {
      setAlertType('error')
      console.error('Error during password reset:', error);
      setAlertMessage('A network or server error occurred. Please try again.');
      setIsDialogOpen(true);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center">
      <div className="max-w-4xl w-100 shadow-lg rounded-lg overflow-hidden bg-transparent">
      <Card className=" bg-transparent backdrop-blur-sm">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-center text-blue-800 dark:text-blue-600">
            Enter your email to receive a password reset link.
          </CardTitle>
        </CardHeader>
    
        <form onSubmit={handleSubmit} className="ml-6 mr-6 space-y-4">
          <div className="space-y-2">
            <Label htmlFor="email">Email</Label>
            <div className="relative">
              <UserIcon className="absolute left-3 top-3 h-5 w-5 text-blue-500" />
              <Input
                id="email"
                type="email"
                placeholder="admin@example.com"
                className="pl-10"
                //{...register('email', { required: 'Email is required' })}
              />
            </div>
            {/*errors.email && */<p className="text-red-500 text-sm">{/*errors.email.message*/}</p>}
          </div>

          <Button type="submit" className="w-full bg-blue-500 hover:bg-blue-700 text-white py-2">
            Send Reset Link
          </Button>
        </form>
      </Card>
      </div>
    </div>
  );
}
