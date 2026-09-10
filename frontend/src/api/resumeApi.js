import axiosClient from './axiosClient';

export const resumeApi = {
  uploadResume: (formData) => axiosClient.post('/resume/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  }),
  createResumeInterview: (data) => axiosClient.post('/resume/interview', data),
};