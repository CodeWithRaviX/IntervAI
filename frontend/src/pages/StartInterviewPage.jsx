import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { interviewApi } from '../api/interviewApi';
import { Sparkles, ArrowRight, Loader2, AlertCircle } from 'lucide-react';

export const StartInterviewPage = () => {
  const [jobRole, setJobRole] = useState('JAVA_BACKEND_DEVELOPER');
  const [experienceLevel, setExperienceLevel] = useState('MID_LEVEL');
  const [difficulty, setDifficulty] = useState('ADAPTIVE');
  const [interviewType, setInterviewType] = useState('TECHNICAL');
  const [totalQuestions, setTotalQuestions] = useState(5);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleStart = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const createRes = await interviewApi.create({
        jobRole,
        experienceLevel,
        difficulty,
        interviewType,
        totalQuestions: Number(totalQuestions),
      });

      if (createRes.success && createRes.data) {
        const interviewId = createRes.data.id;
        navigate('/interview/' + interviewId);
      }
    } catch (err) {
      setError(err.message || 'Failed to create interview session');
      setLoading(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-3xl mx-auto space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
          <div className="p-2 rounded-xl bg-indigo-600 text-white shadow-md shadow-indigo-600/30">
            <Sparkles className="w-6 h-6" />
          </div>
          Configure AI Mock Interview
        </h1>
        <p className="text-xs sm:text-sm text-slate-400">
          Tailor the technical domain, seniority level, and question volume for your mock placement round.
        </p>
      </div>

      {error && (
        <div className="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      <form onSubmit={handleStart} className="p-6 sm:p-8 rounded-2xl bg-slate-900/80 border border-slate-800 shadow-xl space-y-6">
        {/* Job Role Selection */}
        <div>
          <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
            Target Job Role / Domain
          </label>
          <select
            value={jobRole}
            onChange={(e) => setJobRole(e.target.value)}
            className="w-full px-4 py-3 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
          >
            <option value="JAVA_BACKEND_DEVELOPER">Java Backend Developer (Spring Boot, Microservices, SQL)</option>
            <option value="FULL_STACK_DEVELOPER">Full Stack Developer (React, Java/Node, Cloud, APIs)</option>
            <option value="REACT_FRONTEND_DEVELOPER">React Frontend Developer (React, TypeScript, CSS, Performance)</option>
            <option value="DATA_STRUCTURES_AND_ALGORITHMS">Data Structures & Algorithms (Problem Solving)</option>
            <option value="SYSTEM_DESIGN_ENGINEER">System Design Engineer (Scalability, Distributed Systems)</option>
            <option value="PYTHON_DEVELOPER">Python Developer (Django, FastAPI, Scripting)</option>
            <option value="DATABASE_ENGINEER">Database Engineer (SQL Server, Indexing, Query Optimization)</option>
            <option value="HR_GENERAL">HR & Behavioral Fitment</option>
          </select>
        </div>

        {/* Experience Level & Difficulty */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
              Experience Level
            </label>
            <select
              value={experienceLevel}
              onChange={(e) => setExperienceLevel(e.target.value)}
              className="w-full px-4 py-3 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
            >
              <option value="ENTRY_LEVEL">Entry Level / Graduate (0-1 yrs)</option>
              <option value="JUNIOR">Junior Developer (1-2 yrs)</option>
              <option value="MID_LEVEL">Mid-Level Developer (3-5 yrs)</option>
              <option value="SENIOR">Senior Developer (5+ yrs)</option>
              <option value="LEAD">Tech Lead / Architect</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
              Difficulty Mode
            </label>
            <select
              value={difficulty}
              onChange={(e) => setDifficulty(e.target.value)}
              className="w-full px-4 py-3 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
            >
              <option value="ADAPTIVE">✨ Adaptive AI (Calibrates per answer)</option>
              <option value="EASY">Easy (Core syntax & definitions)</option>
              <option value="MEDIUM">Medium (Real-world scenarios)</option>
              <option value="HARD">Hard (Edge cases & performance)</option>
            </select>
          </div>
        </div>

        {/* Interview Type & Question Count */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
              Interview Format
            </label>
            <select
              value={interviewType}
              onChange={(e) => setInterviewType(e.target.value)}
              className="w-full px-4 py-3 bg-slate-950 border border-slate-800 rounded-xl text-sm text-slate-100 focus:outline-none focus:border-indigo-500 transition"
            >
              <option value="TECHNICAL">Technical Deep-Dive</option>
              <option value="BEHAVIORAL">Behavioral (STAR method)</option>
              <option value="HR">HR Culture & Leadership</option>
              <option value="MIXED">Mixed (Technical + Behavioral)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2">
              Number of Questions: {totalQuestions}
            </label>
            <div className="flex items-center gap-3 pt-2">
              {[3, 5, 7, 10].map((num) => (
                <button
                  type="button"
                  key={num}
                  onClick={() => setTotalQuestions(num)}
                  className={`flex-1 py-2 rounded-lg text-xs font-semibold transition ` +
                    (totalQuestions === num
                      ? 'bg-indigo-600 text-white shadow-md shadow-indigo-600/30'
                      : 'bg-slate-950 text-slate-400 border border-slate-800 hover:bg-slate-800')
                  }
                >
                  {num} Questions
                </button>
              ))}
            </div>
          </div>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full py-3.5 px-4 bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-sm rounded-xl shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition disabled:opacity-50 active:scale-95"
        >
          {loading ? (
            <Loader2 className="w-5 h-5 animate-spin" />
          ) : (
            <>
              <span>Initialize Interview Session</span>
              <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </form>
    </div>
  );
};