import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { analyzeVoice, getMyInterviews, getMyVoiceAnalyses } from '../api';

const VoiceAnalysis: React.FC = () => {
  const [interviews, setInterviews] = useState<any[]>([]);
  const [analyses, setAnalyses] = useState<any[]>([]);
  const [selectedInterviewId, setSelectedInterviewId] = useState<number | null>(null);
  const [voiceText, setVoiceText] = useState('');
  const [duration, setDuration] = useState(60);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadData = async () => {
      try {
        const [interviewData, analysisData] = await Promise.all([getMyInterviews(), getMyVoiceAnalyses()]);
        setInterviews(interviewData);
        setAnalyses(analysisData);
      } catch (e) {
        setError('Unable to load voice analysis resources.');
      }
    };
    loadData();
  }, []);

  const handleAnalyze = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedInterviewId) {
      setError('Please choose an interview.');
      return;
    }
    if (!voiceText.trim()) {
      setError('Please enter the voice transcript text.');
      return;
    }

    setLoading(true);
    setError('');
    setMessage('');

    try {
      const analysis = await analyzeVoice(selectedInterviewId, voiceText, duration);
      setAnalyses((prev) => [analysis, ...prev]);
      setMessage('Voice analysis completed successfully.');
      setVoiceText('');
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Unable to analyze voice.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="card">
        <h1>Voice Analysis</h1>
        {message && <div className="message">{message}</div>}
        {error && <div className="message">{error}</div>}
        <form onSubmit={handleAnalyze}>
          <div className="form-field">
            <label htmlFor="interview">Interview</label>
            <select id="interview" value={selectedInterviewId ?? ''} onChange={(e) => setSelectedInterviewId(Number(e.target.value))}>
              <option value="">Select an interview</option>
              {interviews.map((interview) => (
                <option key={interview.id} value={interview.id}>{`${interview.company} - ${interview.technology}`}</option>
              ))}
            </select>
          </div>
          <div className="form-field">
            <label htmlFor="voiceText">Voice transcript text</label>
            <textarea id="voiceText" rows={5} value={voiceText} onChange={(e) => setVoiceText(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="duration">Duration (seconds)</label>
            <input id="duration" type="number" min="5" value={duration} onChange={(e) => setDuration(Number(e.target.value))} />
          </div>
          <button type="submit" disabled={loading}>{loading ? 'Analyzing...' : 'Analyze Voice'}</button>
        </form>

        <div style={{ marginTop: 24 }}>
          <h2>Recent voice analyses</h2>
          {analyses.length ? (
            <table className="table">
              <thead>
                <tr>
                  <th>Interview</th>
                  <th>Confidence</th>
                  <th>Communication</th>
                  <th>Technical</th>
                  <th>Suggestions</th>
                </tr>
              </thead>
              <tbody>
                {analyses.map((analysis) => (
                  <tr key={analysis.id}>
                    <td>{analysis.interviewId}</td>
                    <td>{analysis.confidenceScore}</td>
                    <td>{analysis.communicationScore}</td>
                    <td>{analysis.technicalScore}</td>
                    <td>{analysis.suggestions || '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <p>No voice analyses yet.</p>
          )}
        </div>
      </div>
    </>
  );
};

export default VoiceAnalysis;
