import { Outlet } from 'react-router-dom'

const AuthLayout = () => {
  
  return (
    <div
        style={{ 
            backgroundImage: "url('https://images.unsplash.com/photo-1433849665221-d2f93042ae54?q=80&w=1632&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D')",
            backgroundPosition: 'center',
            backgroundSize: 'cover',
            backgroundRepeat: 'no-repeat',
            minHeight: '100vh',
            width: '100%'
        }}
    >
       <Outlet />
    </div>
  )
}

export default AuthLayout