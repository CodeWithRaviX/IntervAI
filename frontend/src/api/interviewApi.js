import axiosClient from './axiosClient';

export const interviewApi = {
  create: (data) => axiosClient.post('/interviews', data),
  getUserInterviews: (page = 0, size = 10) => axiosClient.get('/interviews?page=' + page + '&size=' + size),
  getDetails: (id) => axiosClient.get('/interviews/' + id),
  start: (id) => axiosClient.post('/interviews/' + id + '/start'),
  submitAnswer: (id, data) => axiosClient.post('/interviews/' + id + '/answers', data),
  complete: (id) => axiosClient.post('/interviews/' + id + '/complete'),
  cancel: (id) => axiosClient.delete('/interviews/' + id),
  getReport: (id) => axiosClient.get('/interviews/' + id + '/report'),
};