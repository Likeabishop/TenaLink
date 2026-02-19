import './App.css'
import { createBrowserRouter, createRoutesFromElements, Navigate, Route, RouterProvider } from 'react-router-dom'
import MainLayout from './Layouts/MainLayout'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import ResetPassword from './pages/ResetPassword'
import AuthLayout from './Layouts/AuthLayout'
import ForgotPassword from './pages/ForgotPassword'
import Register from './pages/Register'
import { useAuthStore } from './hooks/auth-store'

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { isAuthenticated } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/" replace/>
  }

  return <>{children}</>
}

interface RedirectAuthenticatedUserProps {
  children: ReactNode;
}

const RedirectAuthenticatedUser: React.FC<RedirectAuthenticatedUserProps> = ({ children }) => {
  const { isAuthenticated } = useAuthStore();

  if (isAuthenticated) {
    return <Navigate to="dashboard" replace />
  }

  return <>{children}</>
}

const App = () => {

  const router = createBrowserRouter(
    createRoutesFromElements(
      <>
        <Route path="/" element={<AuthLayout />}>
          <Route 
            path='/' 
            element={
              <RedirectAuthenticatedUser>
                <Login />
              </RedirectAuthenticatedUser>
            } />
          <Route 
            path='reset-password' 
            element={
              <RedirectAuthenticatedUser>
                <ResetPassword />
              </RedirectAuthenticatedUser>
            } />
          <Route 
            path="forgot-password"
            element={
              <RedirectAuthenticatedUser>
                <ForgotPassword />
              </RedirectAuthenticatedUser>
            }
          />
          <Route 
            path="register"
            element={
              <RedirectAuthenticatedUser>
                <Register />
              </RedirectAuthenticatedUser>
            }
          />
        </Route>
        
        
        <Route path='tenant' element={<MainLayout />} >
          <Route
           path='dashboard'
           element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
           }
          />
        </Route>
      </>
    )
  )

  return (
    <>
      <RouterProvider router={router} />
    </>
  )
}

export default App
