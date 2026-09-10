import axiosClient from './axiosClient';

export const adminApi = {
  getUsers: (page = 0, size = 10) => axiosClient.get('/admin/users?page=' + page + '&size=' + size),
  updateUserStatus: (id, enabled) => axiosClient.patch('/admin/users/' + id + '/status', { enabled }),
  getStatistics: () => axiosClient.get('/admin/statistics'),
};