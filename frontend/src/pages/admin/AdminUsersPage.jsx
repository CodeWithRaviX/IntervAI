import React, { useState, useEffect } from 'react';
import { adminApi } from '../../api/adminApi';
import { LoadingSpinner, ErrorAlert, EmptyState } from '../../components/UIHelpers';
import { Users, ShieldCheck, Check, X, ChevronLeft, ChevronRight } from 'lucide-react';

export const AdminUsersPage = () => {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchUsers = async (pageNumber = 0) => {
    setLoading(true);
    setError('');
    try {
      const res = await adminApi.getUsers(pageNumber, 10);
      if (res.success && res.data) {
        setUsers(res.data.content);
        setPage(res.data.pageNumber);
        setTotalPages(res.data.totalPages || 1);
      }
    } catch (err) {
      setError(err.message || 'Failed to retrieve user directory');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers(0);
  }, []);

  const handleToggleStatus = async (userId, currentStatus) => {
    try {
      await adminApi.updateUserStatus(userId, !currentStatus);
      setUsers((prev) =>
        prev.map((u) => (u.id === userId ? { ...u, enabled: !currentStatus } : u))
      );
    } catch (err) {
      alert(err.message || 'Failed to update user status');
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-6xl mx-auto space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
          <div className="p-2 rounded-xl bg-amber-500 text-slate-950 shadow-md shadow-amber-500/20">
            <Users className="w-6 h-6" />
          </div>
          User Directory & Access Control
        </h1>
        <p className="text-xs sm:text-sm text-slate-400">
          View registered candidates and manage account active/disabled states.
        </p>
      </div>

      {error && <ErrorAlert message={error} onRetry={() => fetchUsers(page)} />}

      {loading ? (
        <LoadingSpinner text="Loading user directory..." />
      ) : (
        <div className="space-y-4">
          <div className="bg-slate-900/80 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs sm:text-sm">
                <thead className="bg-slate-950/80 text-slate-400 uppercase tracking-wider text-[11px] border-b border-slate-800">
                  <tr>
                    <th className="px-4 py-3.5">Candidate</th>
                    <th className="px-4 py-3.5">Target Role</th>
                    <th className="px-4 py-3.5">Roles</th>
                    <th className="px-4 py-3.5">Total Interviews</th>
                    <th className="px-4 py-3.5">Status</th>
                    <th className="px-4 py-3.5 text-right">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60">
                  {users.map((u) => (
                    <tr key={u.id} className="hover:bg-slate-800/40 transition">
                      <td className="px-4 py-3.5">
                        <div className="font-semibold text-white">{u.fullName}</div>
                        <div className="text-xs text-slate-400">{u.email}</div>
                      </td>
                      <td className="px-4 py-3.5 text-slate-300">
                        {u.targetRole ? u.targetRole.replace(/_/g, ' ') : 'Software Engineer'}
                      </td>
                      <td className="px-4 py-3.5">
                        <div className="flex gap-1">
                          {u.roles?.map((r) => (
                            <span key={r} className="px-2 py-0.5 text-[10px] font-bold bg-slate-800 text-slate-300 rounded">
                              {r.replace('ROLE_', '')}
                            </span>
                          ))}
                        </div>
                      </td>
                      <td className="px-4 py-3.5 text-slate-300 font-semibold">{u.totalInterviews}</td>
                      <td className="px-4 py-3.5">
                        <span className={`px-2.5 py-1 text-[11px] font-semibold rounded-full ` +
                          (u.enabled ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' : 'bg-rose-500/10 text-rose-400 border border-rose-500/20')
                        }>
                          {u.enabled ? 'Active' : 'Disabled'}
                        </span>
                      </td>
                      <td className="px-4 py-3.5 text-right">
                        <button
                          onClick={() => handleToggleStatus(u.id, u.enabled)}
                          className={`px-3 py-1 text-xs font-semibold rounded-lg transition ` +
                            (u.enabled
                              ? 'bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/20'
                              : 'bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-300 border border-emerald-500/20')
                          }
                        >
                          {u.enabled ? 'Disable' : 'Enable'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {totalPages > 1 && (
            <div className="flex items-center justify-between px-2 text-xs text-slate-400">
              <span>Page {page + 1} of {totalPages}</span>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => fetchUsers(page - 1)}
                  disabled={page === 0}
                  className="p-1.5 bg-slate-900 border border-slate-800 rounded-lg text-slate-300 hover:bg-slate-800 disabled:opacity-40 transition"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                <button
                  onClick={() => fetchUsers(page + 1)}
                  disabled={page >= totalPages - 1}
                  className="p-1.5 bg-slate-900 border border-slate-800 rounded-lg text-slate-300 hover:bg-slate-800 disabled:opacity-40 transition"
                >
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};