import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { resumeApi } from '../api/resumeApi';
import { FileUp, Sparkles, CheckCircle, AlertCircle, Loader2, ArrowRight } from 'lucide-react';

export const ResumeInterviewPage = () => {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [parsedData, setParsedData] = useState(null);
  const [error, setError] = useState('');
  const [starting, setStarting] = useState(false);

  const navigate = useNavigate();

  const handleFileChange = (e) => {
    const selected = e.target.files[0];
    if (selected) {
      if (!selected.name.toLowerCase().endsWith('.pdf')) {
        setError('Please upload a valid PDF document (.pdf)');
        return;
      }
      setFile(selected);
      setError('');
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;

    setUploading(true);
    setError('');

    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await resumeApi.uploadResume(formData);
      if (res.success && res.data) {
        setParsedData(res.data);
      }
    } catch (err) {
      setError(err.message || 'Failed to parse resume PDF. Ensure the PDF has selectable text.');
    } finally {
      setUploading(false);
    }
  };

  const handleStartResumeInterview = async () => {
    if (!parsedData) return;

    setStarting(true);
    setError('');

    try {
      const res = await resumeApi.createResumeInterview({
        jobRole: parsedData.suggestedRole || 'JAVA_BACKEND_DEVELOPER',
        experienceLevel: 'MID_LEVEL',
        difficulty: 'ADAPTIVE',
        interviewType: 'TECHNICAL',
        totalQuestions: 5,
        resumeText: parsedData.extractedText,
      });

      if (res.success && res.data) {
        navigate(/interview/ + res.data.id);
      }
    } catch (err) {
      setError(err.message || 'Failed to create resume interview session');
      setStarting(false);
    }
  };

  return (
    <div className="p-4 sm:p-6 lg:p-8 max-w-3xl mx-auto space-y-6">
      <div className="space-y-1">
        <h1 className="text-2xl sm:text-3xl font-bold text-white flex items-center gap-3">
          <div className="p-2 rounded-xl bg-purple-600 text-white shadow-md shadow-purple-600/30">
            <FileUp className="w-6 h-6" />
          </div>
          Resume-Driven Mock Interview
        </h1>
        <p className="text-xs sm:text-sm text-slate-400">
          Upload your resume in PDF format. Gemini AI will extract your projects, technologies, and generate cross-examination questions.
        </p>
      </div>

      {error && (
        <div className="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs flex items-center gap-2">
          <AlertCircle className="w-4 h-4 text-rose-400 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {!parsedData ? (
        <form onSubmit={handleUpload} className="p-8 rounded-2xl bg-slate-900/80 border border-slate-800 text-center space-y-6">
          <div className="border-2 border-dashed border-slate-700 hover:border-indigo-500/60 rounded-2xl p-8 transition flex flex-col items-center justify-center space-y-3">
            <FileUp className="w-10 h-10 text-indigo-400" />
            <div>
              <p className="text-sm font-semibold text-white">Select your resume PDF</p>
              <p className="text-xs text-slate-400 mt-0.5">Maximum file size: 5MB</p>
            </div>
            <input
              type="file"
              accept=".pdf"
              onChange={handleFileChange}
              className="text-xs text-slate-400 file:mr-3 file:py-2 file:px-4 file:rounded-xl file:border-0 file:text-xs file:font-semibold file:bg-indigo-600 file:text-white hover:file:bg-indigo-500 file:cursor-pointer"
            />
          </div>

          <button
            type="submit"
            disabled={!file || uploading}
            className="w-full py-3 px-4 bg-indigo-600 hover:bg-indigo-500 text-white text-sm font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition disabled:opacity-50"
          >
            {uploading ? (
              <>
                <Loader2 className="w-4 h-4 animate-spin" />
                <span>Extracting Skills & Projects with AI...</span>
              </>
            ) : (
              <>
                <span>Analyze Resume with Gemini AI</span>
                <Sparkles className="w-4 h-4" />
              </>
            )}
          </button>
        </form>
      ) : (
        <div className="p-6 sm:p-8 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-6">
          <div className="flex items-center gap-2 text-emerald-400 text-sm font-bold">
            <CheckCircle className="w-5 h-5" />
            Resume Analyzed Successfully
          </div>

          <div className="space-y-4">
            <div>
              <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Detected Technologies & Skills</h4>
              <div className="flex flex-wrap gap-2">
                {parsedData.detectedSkills?.map((skill, idx) => (
                  <span key={idx} className="px-3 py-1 bg-indigo-500/10 text-indigo-300 border border-indigo-500/20 text-xs font-medium rounded-lg">
                    {skill}
                  </span>
                ))}
              </div>
            </div>

            <div>
              <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Detected Resume Projects</h4>
              <ul className="space-y-1.5">
                {parsedData.detectedProjects?.map((proj, idx) => (
                  <li key={idx} className="text-xs text-slate-300 flex items-center gap-2">
                    <span className="w-1.5 h-1.5 rounded-full bg-purple-400"></span>
                    <span>{proj}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          <div className="pt-4 flex gap-3">
            <button
              onClick={() => setParsedData(null)}
              className="flex-1 py-3 px-4 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl transition"
            >
              Upload Different Resume
            </button>
            <button
              onClick={handleStartResumeInterview}
              disabled={starting}
              className="flex-2 py-3 px-6 bg-indigo-600 hover:bg-indigo-500 text-white text-xs font-semibold rounded-xl shadow-lg shadow-indigo-600/30 flex items-center justify-center gap-2 transition disabled:opacity-50"
            >
              {starting ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                <>
                  <span>Begin Resume Interview</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>
          </div>
        </div>
      )}
    </div>
  );
};