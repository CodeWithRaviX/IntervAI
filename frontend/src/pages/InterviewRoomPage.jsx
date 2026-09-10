import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { interviewApi } from '../api/interviewApi';
import { LoadingSpinner, ErrorAlert } from '../components/UIHelpers';
import {
  Bot,
  User,
  Sparkles,
  Clock,
  Send,
  CheckCircle2,
  AlertCircle,
  ArrowRight,
  Award
} from 'lucide-react';

export const InterviewRoomPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [interview, setInterview] = useState(null);
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [answerText, setAnswerText] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [starting, setStarting] = useState(false);
  const [error, setError] = useState('');
  const [lastEvaluation, setLastEvaluation] = useState(null);
  const [secondsElapsed, setSecondsElapsed] = useState(0);

  const fetchDetails = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await interviewApi.getDetails(id);
      if (res.success && res.data) {
        setInterview(res.data);
        if (res.data.status === 'COMPLETED') {
          navigate('/interview/' + id + '/report');
          return;
        }

        // Find current unanswered question
        const unanswered = res.data.questions?.find((q) => !q.answer);
        if (unanswered) {
          setCurrentQuestion(unanswered);
        } else if (res.data.completedQuestions >= res.data.totalQuestions && res.data.questions?.length > 0) {
          // All questions answered, finalize and direct to report
          await handleCompleteInterview();
          return;
        } else if (res.data.questions?.length > 0) {
          setCurrentQuestion(res.data.questions[res.data.questions.length - 1]);
        }
      }
    } catch (err) {
      setError(err.message || 'Failed to load interview room');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDetails();
  }, [id]);

  // Active Session Timer
  useEffect(() => {
    if (interview?.status === 'IN_PROGRESS' && !lastEvaluation) {
      const interval = setInterval(() => {
        setSecondsElapsed((prev) => prev + 1);
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [interview?.status, lastEvaluation]);

  const formatTimer = (totalSeconds) => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    return String(mins).padStart(2, '0') + ':' + String(secs).padStart(2, '0');
  };

  const handleStartSession = async () => {
    setStarting(true);
    setError('');
    try {
      const res = await interviewApi.start(id);
      if (res.success && res.data) {
        setInterview(res.data);
        setCurrentQuestion(res.data.currentQuestion);
      }
    } catch (err) {
      setError(err.message || 'Failed to start interview');
    } finally {
      setStarting(false);
    }
  };

  const handleSubmitAnswer = async (e) => {
    e.preventDefault();
    if (submitting) return;
    if (!answerText.trim() || answerText.trim().length < 10) {
      setError('Please provide a substantive answer (minimum 10 characters).');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      const res = await interviewApi.submitAnswer(id, {
        questionId: currentQuestion.id,
        answerText: answerText.trim(),
      });

      if (res.success && res.data) {
        setLastEvaluation(res.data);
        setAnswerText('');
      }
    } catch (err) {
      setError(err.message || 'Error submitting answer to AI evaluator');
    } finally {
      setSubmitting(false);
    }
  };

  const handleProceedNext = () => {
    if (lastEvaluation?.isInterviewCompleted) {
      handleCompleteInterview();
    } else if (lastEvaluation?.nextQuestion) {
      setCurrentQuestion(lastEvaluation.nextQuestion);
      setLastEvaluation(null);
      setInterview((prev) => ({
        ...prev,
        completedQuestions: prev.completedQuestions + 1,
      }));
    }
  };

  const handleCompleteInterview = async () => {
    setSubmitting(true);
    try {
      await interviewApi.complete(id);
      navigate('/interview/' + id + '/report');
    } catch (err) {
      setError(err.message || 'Failed to synthesize final report');
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner text="Connecting to Gemini AI Interview Room..." />;
  if (error && !interview) return <div className="p-6 max-w-4xl mx-auto"><ErrorAlert message={error} onRetry={fetchDetails} /></div>;

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-5xl mx-auto space-y-6">
      {/* Session Top Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4 sm:p-5 rounded-2xl bg-slate-900/80 border border-slate-800 shadow-md backdrop-blur-md">
        <div>
          <div className="flex items-center gap-2">
            <span className="px-2.5 py-0.5 text-xs font-bold bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 rounded-lg">
              {interview?.jobRole ? interview.jobRole.replace(/_/g, ' ') : ''}
            </span>
            <span className="text-xs text-slate-400">&bull;</span>
            <span className="text-xs text-slate-400">{interview?.difficulty}</span>
          </div>
          <h2 className="text-lg sm:text-xl font-bold text-white mt-1">
            AI Placement Mock Interview
          </h2>
        </div>

        <div className="flex items-center gap-4">
          <div className="flex items-center gap-2 px-3 py-1.5 rounded-xl bg-slate-950 border border-slate-800 text-xs font-mono text-slate-300">
            <Clock className="w-4 h-4 text-indigo-400" />
            <span>{formatTimer(secondsElapsed)}</span>
          </div>

          <div className="text-right">
            <div className="text-xs text-slate-400">Progress</div>
            <div className="text-sm font-bold text-indigo-400">
              Question {currentQuestion?.sequenceNumber || 1} / {interview?.totalQuestions || 5}
            </div>
          </div>
        </div>
      </div>

      {error && <ErrorAlert message={error} />}

      {/* State 1: CREATED */}
      {interview?.status === 'CREATED' && (
        <div className="p-8 sm:p-12 text-center rounded-2xl bg-slate-900/60 border border-slate-800 space-y-6">
          <div className="w-16 h-16 mx-auto rounded-2xl bg-indigo-600/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400">
            <Bot className="w-8 h-8" />
          </div>
          <div className="space-y-2 max-w-md mx-auto">
            <h3 className="text-xl font-bold text-white">Your Interviewer is Prepared</h3>
            <p className="text-xs sm:text-sm text-slate-400 leading-relaxed">
              Gemini AI is ready to assess your knowledge for the <strong>{interview?.jobRole ? interview.jobRole.replace(/_/g, ' ') : ''}</strong> role. Answer as you would in a real technical interview.
            </p>
          </div>
          <button
            onClick={handleStartSession}
            disabled={starting}
            className="px-8 py-3.5 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 inline-flex items-center gap-2 transition disabled:opacity-50"
          >
            {starting ? (
              <>
                <LoadingSpinner text="" />
                <span>Generating First Question with Gemini...</span>
              </>
            ) : (
              <>
                <span>Begin Interview Session</span>
                <Sparkles className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      )}

      {/* State 2: IN_PROGRESS */}
      {interview?.status === 'IN_PROGRESS' && (
        <div className="space-y-6">
          {/* Question Box */}
          <div className="p-6 rounded-2xl bg-slate-900/90 border border-slate-800 shadow-lg space-y-4">
            <div className="flex items-center justify-between border-b border-slate-800 pb-3">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center text-white shadow-md shadow-indigo-600/30">
                  <Bot className="w-5 h-5" />
                </div>
                <div>
                  <span className="text-xs font-bold text-white">AI Interviewer</span>
                  <span className="text-[10px] text-indigo-400 ml-2 font-mono">
                    [{currentQuestion?.technology || 'Technical'}]
                  </span>
                </div>
              </div>
              <span className="text-xs text-slate-400">
                Question {currentQuestion?.sequenceNumber} of {interview?.totalQuestions}
              </span>
            </div>

            <div className="text-base sm:text-lg font-medium text-slate-100 leading-relaxed">
              "{currentQuestion?.questionText}"
            </div>
          </div>

          {/* Immediate Evaluation Feedback Drawer */}
          {lastEvaluation ? (
            <div className="p-6 rounded-2xl bg-indigo-950/40 border border-indigo-500/30 shadow-xl space-y-6 animate-in fade-in duration-300">
              <div className="flex items-center justify-between border-b border-indigo-500/20 pb-4">
                <div className="flex items-center gap-2 text-indigo-300 font-bold text-base">
                  <Award className="w-5 h-5 text-indigo-400" />
                  Gemini Evaluation & Breakdown
                </div>
                <div className="flex items-center gap-2">
                  <span className="text-xs text-slate-400">Answer Score:</span>
                  <span className="px-3 py-1 bg-indigo-600 text-white font-bold text-sm rounded-lg">
                    {lastEvaluation.overallScore} / 10
                  </span>
                </div>
              </div>

              {/* 4 Dimension Score Badges */}
              <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                <div className="p-3 bg-slate-900/70 border border-slate-800 rounded-xl text-center">
                  <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">Technical Accuracy</span>
                  <span className="text-lg font-bold text-indigo-400">{lastEvaluation.technicalScore} / 10</span>
                </div>
                <div className="p-3 bg-slate-900/70 border border-slate-800 rounded-xl text-center">
                  <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">Relevance</span>
                  <span className="text-lg font-bold text-purple-400">{lastEvaluation.relevanceScore} / 10</span>
                </div>
                <div className="p-3 bg-slate-900/70 border border-slate-800 rounded-xl text-center">
                  <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">Clarity</span>
                  <span className="text-lg font-bold text-emerald-400">{lastEvaluation.clarityScore} / 10</span>
                </div>
                <div className="p-3 bg-slate-900/70 border border-slate-800 rounded-xl text-center">
                  <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider block">Depth</span>
                  <span className="text-lg font-bold text-amber-400">{lastEvaluation.depthScore} / 10</span>
                </div>
              </div>

              {/* Feedback text */}
              <div className="space-y-2">
                <h4 className="text-xs font-semibold text-slate-300 uppercase tracking-wider">Interviewer Feedback</h4>
                <p className="text-xs sm:text-sm text-slate-300 bg-slate-900/60 p-4 rounded-xl border border-slate-800 leading-relaxed">
                  {lastEvaluation.feedback}
                </p>
              </div>

              {/* Strengths & Weaknesses */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                {lastEvaluation.strengths?.length > 0 && (
                  <div className="p-3.5 rounded-xl bg-emerald-500/5 border border-emerald-500/10 space-y-1.5">
                    <span className="font-semibold text-emerald-400 block">Identified Strengths</span>
                    {lastEvaluation.strengths.map((s, idx) => (
                      <div key={idx} className="text-emerald-200/80 flex items-start gap-1.5">
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
                        <span>{s}</span>
                      </div>
                    ))}
                  </div>
                )}

                {lastEvaluation.weaknesses?.length > 0 && (
                  <div className="p-3.5 rounded-xl bg-rose-500/5 border border-rose-500/10 space-y-1.5">
                    <span className="font-semibold text-rose-400 block">Areas for Improvement</span>
                    {lastEvaluation.weaknesses.map((w, idx) => (
                      <div key={idx} className="text-rose-200/80 flex items-start gap-1.5">
                        <AlertCircle className="w-3.5 h-3.5 text-rose-400 shrink-0 mt-0.5" />
                        <span>{w}</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              <div className="pt-2 flex justify-end">
                <button
                  onClick={handleProceedNext}
                  className="px-6 py-3 bg-indigo-600 hover:bg-indigo-500 text-white text-xs sm:text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center gap-2 transition"
                >
                  <span>{lastEvaluation.isInterviewCompleted ? 'Finish & Generate Final Report' : 'Proceed to Next Adaptive Question'}</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </div>
            </div>
          ) : (
            /* Answer Form */
            <form onSubmit={handleSubmitAnswer} className="p-6 rounded-2xl bg-slate-900/80 border border-slate-800 shadow-xl space-y-4">
              <div className="flex items-center justify-between">
                <label className="text-xs font-semibold text-slate-300 uppercase tracking-wider flex items-center gap-2">
                  <User className="w-4 h-4 text-indigo-400" />
                  Your Response
                </label>
                <span className="text-[11px] text-slate-500 font-mono">
                  {answerText.length} / 5000 characters
                </span>
              </div>

              <textarea
                rows={7}
                required
                value={answerText}
                onChange={(e) => setAnswerText(e.target.value)}
                placeholder="Structure your answer clearly. Discuss core mechanics, architectural tradeoffs, and practical scenarios..."
                className="w-full p-4 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 placeholder-slate-600 focus:outline-none focus:border-indigo-500 font-sans leading-relaxed transition"
              />

              <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
                <p className="text-[11px] text-slate-500">
                  Tip: Speak to design tradeoffs and production considerations for a higher depth score.
                </p>

                <button
                  type="submit"
                  disabled={submitting || answerText.trim().length < 10}
                  className="w-full sm:w-auto px-6 py-3 bg-indigo-600 hover:bg-indigo-500 text-white text-xs sm:text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition disabled:opacity-50"
                >
                  {submitting ? (
                    <span>Evaluating with Gemini AI...</span>
                  ) : (
                    <>
                      <span>Submit Answer</span>
                      <Send className="w-4 h-4" />
                    </>
                  )}
                </button>
              </div>
            </form>
          )}
        </div>
      )}
    </div>
  );
};