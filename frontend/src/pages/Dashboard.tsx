import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { getProfile } from '../api';

const Dashboard: React.FC = () => {
  const [profile, setProfile] = useState<any>(null);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const loadProfile = async () => {
      try {
        const data = await getProfile();
        setProfile(data);
      } catch (e) {
        setError('Unable to load profile. Please login again.');
        localStorage.removeItem('token');
        navigate('/login');
      }
    };
    loadProfile();
  }, [navigate]);

  return (
    <>
      <Navbar />
      <div className="card">
        <div className="page-header">
          <div>
            <h1>Dashboard</h1>
            <p>Welcome to your AI interview preparation platform.</p>
          </div>
        </div>
        {error && <div className="message">{error}</div>}
        {profile ? (
          <>
            <div style={{ display: 'grid', gap: 24 }}>
              <div>
                <h2>Your profile</h2>
                <p><strong>Name:</strong> {profile.name}</p>
                <p><strong>Email:</strong> {profile.email}</p>
                <p><strong>Phone:</strong> {profile.phone || 'Not provided'}</p>
                <p><strong>Role:</strong> {profile.role || 'Candidate'}</p>
              </div>

              <div>
                <h2>Quick actions</h2>
                <div style={{ display: 'grid', gap: 12 }}>
                  <button type="button" className="secondary" onClick={() => navigate('/upload-resume')}>Upload Resume</button>
                  <button type="button" className="secondary" onClick={() => navigate('/resume-history')}>View Resume History</button>
                  <button type="button" className="secondary" onClick={() => navigate('/job-match')}>Analyze Job Match</button>
                  <button type="button" className="secondary" onClick={() => navigate('/interviews')}>Start Interview</button>
                  <button type="button" className="secondary" onClick={() => navigate('/analytics')}>View Analytics</button>
                  <button type="button" className="secondary" onClick={() => navigate('/voice-analysis')}>Voice Analysis</button>
                </div>
              </div>
            </div>
          </>
        ) : (
          <p>Loading profile...</p>
        )}
      </div>
    </>
  );
};

export default Dashboard;
