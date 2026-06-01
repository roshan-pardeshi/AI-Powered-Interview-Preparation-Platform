import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { getUserAnalytics } from '../api';

const Analytics: React.FC = () => {
  const [analytics, setAnalytics] = useState<any>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadAnalytics = async () => {
      try {
        const data = await getUserAnalytics();
        setAnalytics(data);
      } catch (e) {
        setError('Unable to load analytics at this time.');
      }
    };
    loadAnalytics();
  }, []);

  return (
    <>
      <Navbar />
      <div className="card">
        <h1>Analytics</h1>
        {error && <div className="message">{error}</div>}
        {analytics ? (
          <div>
            <div className="form-field">
              <label>Total interviews</label>
              <span>{analytics.totalInterviews ?? '0'}</span>
            </div>
            <div className="form-field">
              <label>Average interview score</label>
              <span>{analytics.averageScore ?? 'N/A'}</span>
            </div>
            <div className="form-field">
              <label>Average resume score</label>
              <span>{analytics.resumeScoreAvg ?? 'N/A'}</span>
            </div>
            <div className="form-field">
              <label>Average match score</label>
              <span>{analytics.matchScoreAvg ?? 'N/A'}</span>
            </div>
            <div className="form-field">
              <label>Last interview</label>
              <span>{analytics.lastInterviewDate || 'No interviews yet'}</span>
            </div>
          </div>
        ) : (
          <p>Loading analytics...</p>
        )}
      </div>
    </>
  );
};

export default Analytics;
