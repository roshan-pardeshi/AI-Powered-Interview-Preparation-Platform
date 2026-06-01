import axios from 'axios';

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api';
const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  }
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  name: string;
  email: string;
  password: string;
  phone?: string;
}

export async function login(payload: LoginPayload) {
  return api.post('/auth/login', payload).then((res) => res.data.data);
}

export async function register(payload: RegisterPayload) {
  return api.post('/auth/register', payload).then((res) => res.data.data);
}

export async function getProfile() {
  return api.get('/user/profile').then((res) => res.data.data);
}

export async function uploadResume(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return api.post('/resume/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then((res) => res.data.data);
}

export async function getMyResumes() {
  return api.get('/resume/my-resumes').then((res) => res.data.data);
}

export async function analyzeJobMatch(resumeId: number, jobDescription: string) {
  return api.post('/job-match/analyze', { resumeId, jobDescription }).then((res) => res.data.data);
}

export async function getMyJobMatches() {
  return api.get('/job-match/my-matches').then((res) => res.data.data);
}

export async function getMyInterviews() {
  return api.get('/interview/my-interviews').then((res) => res.data.data);
}

export async function getUserAnalytics() {
  return api.get('/analytics/my-analytics').then((res) => res.data.data);
}

export async function getMyVoiceAnalyses() {
  return api.get('/voice/my-analyses').then((res) => res.data.data);
}

export async function startInterview(company: string, technology: string, difficulty: string, interviewType: string) {
  return api.post('/interview/start', { company, technology, difficulty, interviewType }).then((res) => res.data.data);
}

export async function endInterview(interviewId: number, score: number) {
  return api.post(`/interview/${interviewId}/end`, null, { params: { score } }).then((res) => res.data.data);
}

export async function getInterviewQuestions(interviewId: number) {
  return api.get(`/interview/${interviewId}/questions`).then((res) => res.data.data);
}

export async function getInterviewAnswers(interviewId: number) {
  return api.get(`/interview/${interviewId}/answers`).then((res) => res.data.data);
}

export async function submitAnswer(interviewId: number, questionId: number, answerText: string) {
  return api.post(`/interview/${interviewId}/questions/${questionId}/answer`, { answerText }).then((res) => res.data.data);
}

export async function getInterviewChat(interviewId: number) {
  return api.get(`/interview/${interviewId}/chat`).then((res) => res.data.data);
}

export async function sendChatMessage(interviewId: number, message: string, senderType = 'user') {
  return api.post(`/interview/${interviewId}/chat`, { message, senderType }).then((res) => res.data.data);
}

export async function analyzeVoice(interviewId: number, voiceText: string, duration: number) {
  return api.post('/voice/analyze', null, { params: { interviewId, voiceText, duration } }).then((res) => res.data.data);
}

export default api;
