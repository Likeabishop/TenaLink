import { useState } from 'react'
import Sidebar from '../components/Sidebar'
import { Outlet } from 'react-router-dom'
import Navbar from '../components/Navbar'

const MainLayout = () => {
  const [isSidebarOpen, setIsSidebarOpen] = useState<boolean>(false)
  
  return (
    <div className='bg-white text-black flex flex-1'>
        {/* Sidebar on the left */}
        <div className='flex'>
          <div>
            <Sidebar 
                isSidebarOpen={isSidebarOpen}
                setIsSidebarOpen={setIsSidebarOpen} />
          </div>
        </div>
        <div className="flex-1">
          <Navbar isSidebarOpen={isSidebarOpen} />
          <Outlet />
        </div>
    </div>
  )
}

export default MainLayout