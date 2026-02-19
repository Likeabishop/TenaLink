import './App.css'
import { createBrowserRouter, createRoutesFromElements, Route, RouterProvider } from 'react-router-dom'
import MainLayout from './Layouts/MainLayout'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import ResetPassword from './pages/ResetPassword'
import AuthLayout from './Layouts/AuthLayout'
import ForgotPassword from './pages/ForgotPassword'

function App() {

  const router = createBrowserRouter(
    createRoutesFromElements(
      <>
        <Route path="/" element={<AuthLayout />}>
          <Route path='/' element={<Login />} />
          <Route path='reset-password' element={<ResetPassword />} />
          <Route 
            path="forgot-password"
            element={<ForgotPassword />}
          />
        </Route>
        
        
        <Route path='tenant' element={<MainLayout />} >
          <Route
           path='dashboard'
           element={
            <Dashboard />
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
