import React, { useEffect, useState } from 'react';
import Navbar from '../components/Navbar';
import { endInterview, getInterviewQuestions, getInterviewAnswers, getInterviewChat, getMyInterviews, sendChatMessage, startInterview, submitAnswer } from '../api';

const Interviews: React.FC = () => {
  const [interviews, setInterviews] = useState<any[]>([]);
  const [selectedInterviewId, setSelectedInterviewId] = useState<number | null>(null);
  const [questions, setQuestions] = useState<any[]>([]);
  const [answers, setAnswers] = useState<any[]>([]);
  const [chatMessages, setChatMessages] = useState<any[]>([]);
  const [chatInput, setChatInput] = useState('');
  const [currentAnswer, setCurrentAnswer] = useState('');
  const [score, setScore] = useState('');
  const [company, setCompany] = useState('');
  const [technology, setTechnology] = useState('');
  const [difficulty, setDifficulty] = useState('medium');
  const [interviewType, setInterviewType] = useState('technical');
  const [selectedQuestionId, setSelectedQuestionId] = useState<number | null>(null);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadInterviews = async () => {
      try {
        const data = await getMyInterviews();
        setInterviews(data);
      } catch (e) {
        setError('Unable to load interviews.');
      }
    };
    loadInterviews();
  }, []);

  const handleStart = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!company || !technology) {
      setError('Please provide company and technology.');
      return;
    }
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const interview = await startInterview(company, technology, difficulty, interviewType);
      setInterviews((prev) => [interview, ...prev]);
      setMessage('Interview started successfully.');
      setCompany('');
      setTechnology('');
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Unable to start interview.');
    } finally {
      setLoading(false);
    }
  };

  const handleSelectInterview = async (id: number) => {
    setSelectedInterviewId(id);
    setQuestions([]);
    setAnswers([]);
    setChatMessages([]);
    setError('');
    setMessage('');
    try {
      const [questionData, answerData, chatData] = await Promise.all([
        getInterviewQuestions(id),
        getInterviewAnswers(id),
        getInterviewChat(id),
      ]);
      setQuestions(questionData);
      setAnswers(answerData);
      setChatMessages(chatData);
    } catch (e) {
      setError('Unable to load interview details.');
    }
  };

  const handleSubmitAnswer = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedInterviewId || !selectedQuestionId || !currentAnswer.trim()) {
      setError('Select a question and enter an answer.');
      return;
    }
    setLoading(true);
    setError('');
    setMessage('');

    try {
      await submitAnswer(selectedInterviewId, selectedQuestionId, currentAnswer);
      setCurrentAnswer('');
      setMessage('Answer submitted successfully.');
      const updatedAnswers = await getInterviewAnswers(selectedInterviewId);
      setAnswers(updatedAnswers);
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Unable to submit answer.');
    } finally {
      setLoading(false);
    }
  };

  const handleSendChat = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!selectedInterviewId || !chatInput.trim()) {
      setError('Enter a chat message.');
      return;
    }
    setLoading(true);
    setError('');
    setMessage('');

    try {
      const newMessage = await sendChatMessage(selectedInterviewId, chatInput);
      setChatInput('');
      setChatMessages((prev) => [...prev, newMessage]);
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Unable to send chat message.');
    } finally {
      setLoading(false);
    }
  };

  const handleFinish = async () => {
    if (!selectedInterviewId || !score) {
      setError('Enter a score before finishing the interview.');
      return;
    }
    setLoading(true);
    setError('');
    setMessage('');

    try {
      await endInterview(selectedInterviewId, Number(score));
      setMessage('Interview marked complete.');
      const updated = await getMyInterviews();
      setInterviews(updated);
    } catch (e: any) {
      setError(e?.response?.data?.message || 'Unable to complete interview.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <div className="card">
        <h1>Interviews</h1>
        <div style={{ display: 'grid', gap: 24 }}>
          <div className="card">
            <h2>Start a new interview</h2>
            <form onSubmit={handleStart}>
              <div className="form-field">
                <label htmlFor="company">Company</label>
                <input id="company" value={company} onChange={(e) => setCompany(e.target.value)} required />
              </div>
              <div className="form-field">
                <label htmlFor="technology">Technology</label>
                <input id="technology" value={technology} onChange={(e) => setTechnology(e.target.value)} required />
              </div>
              <div className="form-field">
                <label htmlFor="difficulty">Difficulty</label>
                <select id="difficulty" value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
                  <option value="easy">Easy</option>
                  <option value="medium">Medium</option>
                  <option value="hard">Hard</option>
                </select>
              </div>
              <div className="form-field">
                <label htmlFor="interviewType">Type</label>
                <select id="interviewType" value={interviewType} onChange={(e) => setInterviewType(e.target.value)}>
                  <option value="technical">Technical</option>
                  <option value="behavioral">Behavioral</option>
                </select>
              </div>
              <button type="submit" disabled={loading}>{loading ? 'Starting...' : 'Start Interview'}</button>
            </form>
          </div>

          {error && <div className="message">{error}</div>}
          {message && <div className="message">{message}</div>}

          <div className="card">
            <h2>My interviews</h2>
            {interviews.length ? (
              <table className="table">
                <thead>
                  <tr>
                    <th>Company</th>
                    <th>Technology</th>
                    <th>Status</th>
                    <th>Score</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {interviews.map((interview) => (
                    <tr key={interview.id}>
                      <td>{interview.company}</td>
                      <td>{interview.technology}</td>
                      <td>{interview.status || 'In progress'}</td>
                      <td>{interview.score ?? 'N/A'}</td>
                      <td>
                        <button type="button" onClick={() => handleSelectInterview(interview.id)}>View</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>No interviews found.</p>
            )}
          </div>

          {selectedInterviewId && (
            <div className="card">
              <h2>Interview details</h2>
              <div className="form-field">
                <label htmlFor="questionSelect">Select question</label>
                <select id="questionSelect" value={selectedQuestionId ?? ''} onChange={(e) => setSelectedQuestionId(Number(e.target.value))}>
                  <option value="">Choose a question</option>
                  {questions.map((question) => (
                    <option key={question.id} value={question.id}>{question.questionText}</option>
                  ))}
                </select>
              </div>
              <form onSubmit={handleSubmitAnswer}>
                <div className="form-field">
                  <label htmlFor="answer">Your answer</label>
                  <textarea id="answer" rows={5} value={currentAnswer} onChange={(e) => setCurrentAnswer(e.target.value)} />
                </div>
                <button type="submit" disabled={loading}>{loading ? 'Submitting...' : 'Submit answer'}</button>
              </form>

              <div style={{ marginTop: 20 }}>
                <h3>Answers</h3>
                {answers.length ? (
                  <table className="table">
                    <thead>
                      <tr>
                        <th>Question</th>
                        <th>Answer</th>
                      </tr>
                    </thead>
                    <tbody>
                      {answers.map((answer) => (
                        <tr key={answer.id}>
                          <td>{answer.questionText}</td>
                          <td>{answer.answerText}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <p>No answers submitted yet.</p>
                )}
              </div>

              <div className="card" style={{ marginTop: 24 }}>
                <h3>Interview chat</h3>
                {chatMessages.length ? (
                  <div style={{ display: 'grid', gap: 12, marginBottom: 20 }}>
                    {chatMessages.map((msg) => (
                      <div key={msg.id} style={{ padding: 12, background: '#f8fafc', borderRadius: 12 }}>
                        <strong>{msg.senderType}</strong> • {new Date(msg.createdAt).toLocaleString()}
                        <p style={{ margin: '8px 0 0' }}>{msg.message}</p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p>No chat yet.</p>
                )}
                <form onSubmit={handleSendChat}>
                  <div className="form-field">
                    <label htmlFor="chatInput">Send a message</label>
                    <textarea id="chatInput" rows={3} value={chatInput} onChange={(e) => setChatInput(e.target.value)} />
                  </div>
                  <button type="submit" disabled={loading}>{loading ? 'Sending...' : 'Send message'}</button>
                </form>
              </div>

              <div style={{ marginTop: 24 }}>
                <div className="form-field">
                  <label htmlFor="score">Final score</label>
                  <input id="score" type="number" min="0" max="100" value={score} onChange={(e) => setScore(e.target.value)} />
                </div>
                <button type="button" className="secondary" onClick={handleFinish} disabled={loading}>{loading ? 'Completing...' : 'Finish interview'}</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default Interviews;
