import React from 'react'

const MainLayout = () => {
  return (
    <nav className='flex justify-center relative p-3'>
        <img
            src='#'
            alt='Logo'
        />

        <ul>
            <li><a href='#'>Dashboard</a></li>
            <li><a href='#'>Viewing</a></li>
            <li><a href='#'>Billing</a></li>
            <li><a href='#'>Staff</a></li>
        </ul>
    </nav>
  )
}

export default MainLayout