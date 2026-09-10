import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { interviewApi } from '../api/interviewApi';
import { LoadingSpinner, ErrorAlert } from '../components/UIHelpers';
import { ArrowLeft, MessageSquare, Bot, User, Award } from 'lucide-react';

export const InterviewDetailPage = () => {
  const { id } = useParams();
  const [interview, setInterview] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDetails = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await interviewApi.getDetails(id);
        if (res.success) setInterview(res.data);
      } catch (err) {
        setError(err.message || 'Failed to load interview details');
      } finally {
        setLoading(false);
      }
    };
    fetchDetails();
  }, [id]);

  if (loading) return <LoadingSpinner text="Loading interview session transcript..." />;
  if (error) return <div className="p-6 max-w-4xl mx-auto"><ErrorAlert message={error} /></div>;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between border-b border-slate-800 pb-4">
        <div>
          <Link to="/history" className="text-xs font-semibold text-slate-400 hover:text-white flex items-center gap-1.5 mb-1 transition">
            <ArrowLeft className="w-3.5 h-3.5" /> Back to History
          </Link>
          <h1 className="text-xl sm:text-2xl font-bold text-white">
            Interview Q&A Transcript
          </h1>
          <p className="text-xs text-slate-400">
            {interview?.jobRole ? interview.jobRole.replace(/_/g, ' ') : ''} &bull; {interview?.difficulty}
          </p>
        </div>

        {interview?.report && (
          <Link
            to={'/interview/' + id + '/report'}
            className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold rounded-xl transition flex items-center gap-1.5"
          >
            <Award className="w-4 h-4" /> View Full Report
          </Link>
        )}
      </div>

      <div className="space-y-6">
        {interview?.questions?.map((q, idx) => (
          <div key={q.id || idx} className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-4">
            {/* Question */}
            <div className="flex items-start gap-3">
              <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center text-white shrink-0 mt-0.5">
                <Bot className="w-4 h-4" />
              </div>
              <div className="space-y-1 flex-1">
                <div className="flex items-center justify-between">
                  <span className="text-xs font-bold text-indigo-400 uppercase tracking-wider">
                    Question {q.sequenceNumber} [{q.technology}]
                  </span>
                  {q.answer && (
                    <span className="text-xs font-bold px-2 py-0.5 bg-indigo-500/10 text-indigo-300 border border-indigo-500/20 rounded-md">
                      Score: {q.answer.overallScore} / 10
                    </span>
                  )}
                </div>
                <p className="text-sm font-medium text-white">{q.questionText}</p>
              </div>
            </div>

            {/* Answer */}
            {q.answer ? (
              <div className="pl-11 space-y-3">
                <div className="p-4 rounded-xl bg-slate-950 border border-slate-800/80 space-y-2">
                  <span className="text-xs font-semibold text-slate-400 flex items-center gap-1.5">
                    <User className="w-3.5 h-3.5 text-slate-400" /> Candidate Answer
                  </span>
                  <p className="text-xs sm:text-sm text-slate-200 leading-relaxed">{q.answer.answerText}</p>
                </div>

                {/* Feedback */}
                <div className="p-4 rounded-xl bg-indigo-950/20 border border-indigo-500/10 space-y-2 text-xs">
                  <span className="font-semibold text-indigo-400">AI Interviewer Critique:</span>
                  <p className="text-slate-300 leading-relaxed">{q.answer.feedback}</p>
                </div>
              </div>
            ) : (
              <div className="pl-11 text-xs text-slate-500 italic">No answer recorded yet.</div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};