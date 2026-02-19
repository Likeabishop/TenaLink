import React from 'react'
import { Link } from 'react-router-dom'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../components/ui/card'
import { Button } from '../components/ui/button'
import { Label } from '../components/ui/label'
import { Input } from '../components/ui/input'
import { LockIcon, UserIcon } from 'lucide-react';

const Login = () => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-cover bg-center">
      <div className="max-w-4xl w-100 shadow-lg rounded-lg overflow-hidden">
       
        <Card className="bg-transparent backdrop-blur-sm">
          <CardHeader>
            <CardTitle className="text-2xl font-bold text-center text-blue-800 dark:text-blue-600">Welcome Back</CardTitle>
            <CardDescription className="text-center text-blue-600">Please enter your credentials to login</CardDescription>
          </CardHeader>
          <CardContent>
            <form /*onSubmit={handleSubmit(onSubmit)}*/ className="space-y-4">
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
              <div className="space-y-2">
                <Label htmlFor="password">Password</Label>
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
                {/*errors.password && */<p className="text-red-500 text-sm">{/*errors.password.message*/}</p>}
              </div>
              <Button type="submit" className="w-full bg-blue-500 hover:bg-blue-700 text-white">
                Login
              </Button>
            </form>
          </CardContent>
          <CardFooter className="flex flex-col items-center">
            <Link to='/forgot-password' className='text-sm' style={{ color: '#0401A0', textDecoration: 'underline' }}>
              Forgot password?
            </Link>
            <div className="text-sm flex space-x-2">
              <p>Don't have an account?</p> <p className='text-blue-500'>Contact Admin</p>
            </div>
          </CardFooter>
        </Card>
      </div>
      
    </div>
  )
}

export default Login