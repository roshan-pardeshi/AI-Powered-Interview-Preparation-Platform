import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';

const links = [
  { label: 'Dashboard', path: '/' },
  { label: 'Upload Resume', path: '/upload-resume' },
  { label: 'Resume History', path: '/resume-history' },
  { label: 'Job Match', path: '/job-match' },
  { label: 'Interviews', path: '/interviews' },
  { label: 'Analytics', path: '/analytics' },
  { label: 'Voice Analysis', path: '/voice-analysis' }
];

const Navbar: React.FC = () => {
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <nav className="navbar card">
      <div className="navbar-brand" onClick={() => navigate('/')}>Interview AI</div>
      <div className="navbar-links">
        {links.map((link) => (
          <NavLink
            key={link.path}
            to={link.path}
            className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
          >
            {link.label}
          </NavLink>
        ))}
      </div>
      <button className="secondary logout-button" onClick={logout}>
        Logout
      </button>
    </nav>
  );
};

export default Navbar;
