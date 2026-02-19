import React, { useEffect, useState } from 'react'
import { FaCog, FaChartLine, FaEnvelope, FaCcVisa, FaReceipt, FaClipboardCheck, FaClipboardList} from 'react-icons/fa';
import { Link, useLocation } from 'react-router-dom';
import ProfileImage from './ProfileImage';
import { Button } from './ui/button';
import { Separator } from './ui/separator';

interface SidebarProps {
    isSidebarOpen: boolean;
    setIsSidebarOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

const Sidebar: React.FC<SidebarProps> = ({ isSidebarOpen, setIsSidebarOpen }) => {
    
    const [activeIndex, setActiveIndex] = useState<number>(0);
    const [profilePicture, setProfilePicture] = useState<string | null>(null);
    const [profilePicUrl, setProfilePicUrl] = useState<string | null>(null);
    const location = useLocation();

    const menuItems = [
        { icon: <FaChartLine />, name: 'Dashboard', path: '/dashboard'},
        { icon: <FaClipboardList />, name: 'Available Tenders', path: '/available-tenders'},
        { icon: <FaReceipt />, name: 'My Bids', path: '/my-bids'},
        { icon: <FaEnvelope />, name: 'Messages', path: '/messages'},
        { icon: <FaClipboardCheck />, name: 'Evaluation', path: '/evaluation'},
        { icon: <FaCcVisa />, name: 'Payment', path: '/payment'},
        { icon: <FaCog />, name: 'Settings', path: '/settings'}
    ]

    useEffect(() => {
        const index = menuItems.findIndex((item) => item.path === location.pathname);
        setActiveIndex(index);
    }, [location.pathname, menuItems]);

    const toggleSidebar = () => {
        setIsSidebarOpen(!isSidebarOpen)
    }


  return (
    <>
        {isSidebarOpen && (
            <div className='bg-white text-black w-64 h-full flex flex-col pl-6 pt-2'>
                <div className="flex flex-row items-center space-x-3">
                    <img 
                        src='/ticket.png' 
                        alt='LOGO'
                        className='w-18 h-18 rounded-full'
                    />
                    <h1 className='text-xl font-bold'>MyTender</h1>
                    <Button 
                        variant="ghost" 
                        size="sm" 
                        className=""
                        onClick={toggleSidebar}
                    >
                    {isSidebarOpen ? (
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                            <rect x="1" y="1" width="6" height="14" fill="currentColor" opacity="0.3"/>
                            <rect x="8" y="1" width="7" height="14" stroke="currentColor" strokeWidth="1.5" fill="none"/>
                        </svg>
                    ) : (
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                            <rect x="1" y="1" width="14" height="14" stroke="currentColor" strokeWidth="1.5" fill="none"/>
                            <rect x="2" y="2" width="2" height="12" fill="currentColor" opacity="0.3"/>
                        </svg>
                    )}
                    </Button>
                </div>
                <Separator className="my-1" />
                <nav className="flex flex-col space-y-2 text-slate-600">
                    <div className="flex flex-row space-x-4 items-center">
                        <ProfileImage 
                            profilePicUrl={profilePicUrl ?? ''}
                            profilePicture={profilePicture ?? ''}
                        />
                        <h1 className='font-bold'>Lucky Jabulani</h1>
                    </div>
                    {menuItems.map((item, index) => (
                        <Link 
                            to={item.path}
                            key={index}
                            onClick={() => setActiveIndex(index)}
                            className={`flex items-center space-x-2 px-4 py-3 rounded text-left text-lg ${activeIndex === index ? 'bg-blue-300 text-sky-600' : 'hover:bg-blue-100 hover:text-sky-300'}`}
                        >
                            <span className="text-2xl">{item.icon}</span>
                            <span>{item.name}</span>
                        </Link>
                    ))}
                </nav>
            </div>
        )}
        {!isSidebarOpen && (
            <div className='bg-white text-black w-35 h-full flex flex-col pl-6 pt-2'>
                <div className="flex flex-row items-center space-x-1">
                    <img 
                        src='/ticket.png' 
                        alt='LOGO'
                        className='w-18 h-18 rounded-full'
                    />
                    <Button 
                        variant="ghost" 
                        size="sm" 
                        className=""
                        onClick={toggleSidebar}
                    >
                    {isSidebarOpen ? (
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                            <rect x="1" y="1" width="6" height="14" fill="currentColor" opacity="0.3"/>
                            <rect x="8" y="1" width="7" height="14" stroke="currentColor" strokeWidth="1.5" fill="none"/>
                        </svg>
                    ) : (
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                            <rect x="1" y="1" width="14" height="14" stroke="currentColor" strokeWidth="1.5" fill="none"/>
                            <rect x="2" y="2" width="2" height="12" fill="currentColor" opacity="0.3"/>
                        </svg>
                    )}
                    </Button>
                </div>
                <Separator className="my-2" />
                <nav className="flex flex-col space-y-2 text-slate-600">
                    <div className="flex flex-row space-x-4 items-center">
                        <ProfileImage 
                            profilePicUrl={profilePicUrl ?? ''}
                            profilePicture={profilePicture ?? ''}
                        />
                    </div>
                    {menuItems.map((item, index) => (
                        <Link 
                            to={item.path}
                            key={index}
                            onClick={() => setActiveIndex(index)}
                            className={`flex items-center space-x-2 px-4 py-3 rounded text-left text-lg ${activeIndex === index ? 'bg-blue-300 text-sky-600' : 'hover:bg-blue-100 hover:text-sky-300'}`}
                        >
                            <span className="text-2xl">{item.icon}</span>
                        </Link>
                    ))}
                </nav>
            </div>
        )}
    </>
  )
}

export default Sidebar