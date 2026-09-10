import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { interviewApi } from '../api/interviewApi';
import { LoadingSpinner, ErrorAlert, EmptyState } from '../components/UIHelpers';
import { History, Sparkles, ChevronLeft, ChevronRight, Eye, Play } from 'lucide-react';

export const InterviewHistoryPage = () => {
  const [interviews, setInterviews] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const fetchHistory = async (pageNumber = 0) => {
    setLoading(true);
    setError('');
    try {
      const res = await interviewApi.getUserInterviews(pageNumber, 10);
      if (res && res.data) {
        const list = Array.isArray(res.data) ? res.data : (res.data.content || []);
        setInterviews(list);
        setPage(res.data.pageNumber || 0);
        setTotalPages(res.data.totalPages || 1);
      } else if (Array.isArray(res)) {
        setInterviews(res);
        setTotalPages(1);
      }
    } catch (err) {
      console.error('Error loading history:', err);
      setError(err.message || 'Failed to load interview history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory(0);
  }, []);

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-6xl mx-auto space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
            <div className="p-2 rounded-xl bg-indigo-600 text-white shadow-md shadow-indigo-600/30">
              <History className="w-6 h-6" />
            </div>
            Interview History & Records
          </h1>
          <p className="text-xs sm:text-sm text-slate-400 mt-1">
            Review past mock interview sessions, transcripts, and evaluation metrics.
          </p>
        </div>

        <Link
          to="/interview/new"
          className="px-4 py-2.5 bg-indigo-600 hover:bg-indigo-500 text-white text-xs sm:text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center gap-2 transition"
        >
          <Sparkles className="w-4 h-4" />
          Start New Round
        </Link>
      </div>

      {error && <ErrorAlert message={error} onRetry={() => fetchHistory(page)} />}

      {loading ? (
        <LoadingSpinner text="Retrieving interview history..." />
      ) : error ? null : interviews.length === 0 ? (
        <EmptyState
          title="No interview history found"
          description="You haven't conducted any mock interviews yet."
          actionText="Start First Interview"
          onAction={() => window.location.href = '/interview/new'}
        />
      ) : (
        <div className="space-y-4">
          <div className="bg-slate-900/80 border border-slate-800 rounded-2xl overflow-hidden shadow-sm">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs sm:text-sm">
                <thead className="bg-slate-950/80 text-slate-400 uppercase tracking-wider text-[11px] border-b border-slate-800">
                  <tr>
                    <th className="px-4 py-3.5">Role & Difficulty</th>
                    <th className="px-4 py-3.5">Type</th>
                    <th className="px-4 py-3.5">Status</th>
                    <th className="px-4 py-3.5">Questions</th>
                    <th className="px-4 py-3.5">Overall Score</th>
                    <th className="px-4 py-3.5">Date</th>
                    <th className="px-4 py-3.5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-800/60">
                  {interviews.map((item) => (
                    <tr key={item.id} className="hover:bg-slate-800/40 transition">
                      <td className="px-4 py-3.5 font-semibold text-white">
                        <div>{item.jobRole ? item.jobRole.replace(/_/g, ' ') : ''}</div>
                        <div className="text-xs text-slate-400 font-normal">{item.difficulty} &bull; {item.experienceLevel}</div>
                      </td>
                      <td className="px-4 py-3.5 text-slate-300">{item.interviewType}</td>
                      <td className="px-4 py-3.5">
                        <span className={`px-2.5 py-1 text-[11px] font-semibold rounded-full ` +
                          (item.status === 'COMPLETED' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' :
                          item.status === 'IN_PROGRESS' ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20' :
                          'bg-slate-800 text-slate-400')
                        }>
                          {item.status ? item.status.replace(/_/g, ' ') : ''}
                        </span>
                      </td>
                      <td className="px-4 py-3.5 text-slate-300">
                        {item.completedQuestions} / {item.totalQuestions}
                      </td>
                      <td className="px-4 py-3.5 font-bold">
                        {item.overallScore ? (
                          <span className={item.overallScore >= 75 ? 'text-emerald-400' : 'text-amber-400'}>
                            {item.overallScore}%
                          </span>
                        ) : (
                          <span className="text-slate-500">-</span>
                        )}
                      </td>
                      <td className="px-4 py-3.5 text-slate-400 text-xs">
                        {item.createdAt ? new Date(item.createdAt).toLocaleDateString() : 'Recent'}
                      </td>
                      <td className="px-4 py-3.5 text-right">
                        <div className="flex items-center justify-end gap-2">
                          {item.status === 'COMPLETED' ? (
                            <>
                              <Link
                                to={'/interview/' + item.id + '/report'}
                                className="px-3 py-1 bg-emerald-600/10 hover:bg-emerald-600/20 text-emerald-400 border border-emerald-500/20 text-xs font-semibold rounded-lg transition flex items-center gap-1"
                              >
                                Report
                              </Link>
                              <Link
                                to={'/interview/' + item.id + '/details'}
                                className="p-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg transition"
                                title="View Transcript"
                              >
                                <Eye className="w-4 h-4" />
                              </Link>
                            </>
                          ) : (
                            <Link
                              to={'/interview/' + item.id}
                              className="px-3 py-1 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold rounded-lg transition flex items-center gap-1"
                            >
                              <Play className="w-3.5 h-3.5" /> Resume
                            </Link>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="flex items-center justify-between px-2 pt-2 text-xs text-slate-400">
              <span>Page {page + 1} of {totalPages}</span>
              <div className="flex items-center gap-2">
                <button
                  onClick={() => fetchHistory(page - 1)}
                  disabled={page === 0}
                  className="p-1.5 bg-slate-900 border border-slate-800 rounded-lg text-slate-300 hover:bg-slate-800 disabled:opacity-40 transition"
                >
                  <ChevronLeft className="w-4 h-4" />
                </button>
                <button
                  onClick={() => fetchHistory(page + 1)}
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