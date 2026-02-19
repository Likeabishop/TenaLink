import { Button } from '@/components/ui/button';
import React from 'react'
import { FaBell } from 'react-icons/fa'

interface NavbarProps {
    isSidebarOpen: boolean;
}

const Navbar: React.FC<NavbarProps> = ({ isSidebarOpen }) => {
    
  return (
    <div>
        
        <header className={`bg-white shadow-sm border-b flex justify-between items-center ${isSidebarOpen ? "h-21" : "h-22"} w-full px-4`}>
          <div className="flex flex-col justify-start">  
            <h1 className="text-lg font-semibold text-gray-900">Dashboard</h1>
            <p className="text-sm">
                Manage your bids and discover new opportunities
            </p>
          </div>
          <div className="flex flex-row items-center space-x-4  justify-end">
            <Button variant="ghost" size="sm" className="relative">
              <FaBell className="h-5 w-5" />
              
            </Button>
            <Button 
                size="sm" 
                className="relative h-8 w-auto bg-sky-500"
            >
                + Submit New Bid
            </Button>
            
          </div>
        </header>
    </div>
  )
}

export default Navbar