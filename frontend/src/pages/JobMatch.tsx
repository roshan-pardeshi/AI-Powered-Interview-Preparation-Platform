import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { analyzeJobMatch, getMyResumes, getMyJobMatches } from '../api';

const JobMatch: React.FC = () => {
  const [resumes, setResumes] = useState<any[]>([]);
  const [matches, setMatches] = useState<any[]>([]);
  const [selectedResume, setSelectedResume] = useState<number | null>(null);
  const [jobDescription, setJobDescription] = useState('');
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [resumeData, matchData] = await Promise.all([getMyResumes(), getMyJobMatches()]);
        setResumes(resumeData);
        setMatches(matchData);
      } catch (e) {
        setError('Unable to load resumes or matches.');
      }
    };
    loadData();
  }, []);

  const handleAnalyze = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedResume) {
      setError('Please select a resume first.');
      return;
    }
    if (!jobDescription.trim()) {
      setError('Please enter a job description.');
      return;
    }
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const result = await analyzeJobMatch(selectedResume, jobDescription);
      setMessage(`Match score: ${result.matchScore}. Recommendation: ${result.recommendation || 'No recommendation available.'}`);
      setMatches((prev) => [result, ...prev]);
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Job match failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="card">
        <h1>Job Match</h1>
        <form onSubmit={handleAnalyze}>
          <div className="form-field">
            <label htmlFor="resume">Choose resume</label>
            <select id="resume" value={selectedResume ?? ''} onChange={(e) => setSelectedResume(Number(e.target.value))}>
              <option value="">Select a resume</option>
              {resumes.map((resume) => (
                <option key={resume.id} value={resume.id}>{resume.fileName || `Resume ${resume.id}`}</option>
              ))}
            </select>
          </div>
          <div className="form-field">
            <label htmlFor="jobDescription">Job description</label>
            <textarea id="jobDescription" rows={6} value={jobDescription} onChange={(e) => setJobDescription(e.target.value)} />
          </div>
          <button type="submit" disabled={loading}>{loading ? 'Analyzing...' : 'Analyze Match'}</button>
        </form>
        {message && <div className="message">{message}</div>}
        {error && <div className="message">{error}</div>}
        <div style={{ marginTop: 24 }}>
          <h2>Recent matches</h2>
          {matches.length ? (
            <table className="table">
              <thead>
                <tr>
                  <th>Resume</th>
                  <th>Score</th>
                  <th>Recommendation</th>
                </tr>
              </thead>
              <tbody>
                {matches.map((match) => (
                  <tr key={match.id || `${match.resumeId}-${match.matchScore}`}>
                    <td>{match.resumeFileName || `Resume ${match.resumeId}`}</td>
                    <td>{match.matchScore}</td>
                    <td>{match.recommendation || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p>No matches yet.</p>
          )}
        </div>
      </div>
    </>
  );
};

export default JobMatch;
