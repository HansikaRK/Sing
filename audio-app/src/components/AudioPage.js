import React, { useState, useRef } from "react";

const AudioPage = () => {
  const [file, setFile] = useState(null);
  const [analysis, setAnalysis] = useState(null);
  const [recording, setRecording] = useState(false);
  const [audioURL, setAudioURL] = useState(null);
  const mediaRecorderRef = useRef(null);
  const audioChunksRef = useRef([]);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const startRecording = async () => {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    const mediaRecorder = new MediaRecorder(stream);
    mediaRecorderRef.current = mediaRecorder;
    audioChunksRef.current = [];

    mediaRecorder.ondataavailable = (event) => {
      audioChunksRef.current.push(event.data);
    };

    mediaRecorder.onstop = () => {
      const blob = new Blob(audioChunksRef.current, { type: "audio/wav" });
      const url = URL.createObjectURL(blob);
      setAudioURL(url);
      setFile(new File([blob], "recording.wav", { type: "audio/wav" }));
    };

    mediaRecorder.start();
    setRecording(true);
  };

  const stopRecording = () => {
    mediaRecorderRef.current.stop();
    setRecording(false);
  };

  const handleUpload = async () => {
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("http://localhost:8080/api/audio/analyze", {
        method: "POST",
        body: formData,
      });
      const data = await response.json();
      setAnalysis(data);
    } catch (error) {
      console.error("Error:", error);
    }
  };

  return (
    <div style={{ backgroundColor: "#d4edf0", minHeight: "100vh", padding: "2rem" }}>
      <h1 style={{ color: "black" }}>Audio Analysis</h1>

      {/* File Upload */}
      <input type="file" onChange={handleFileChange} accept=".wav" />
      <button
        style={{ backgroundColor: "#ff5055", color: "white", padding: "0.5rem 1rem", border: "none", marginLeft: "1rem", cursor: "pointer" }}
        onClick={handleUpload}
      >
        Analyze
      </button>

      {/* Recording Controls */}
      <div style={{ marginTop: "1rem" }}>
        {!recording ? (
          <button
            style={{ backgroundColor: "#ff5055", color: "white", padding: "0.5rem 1rem", border: "none", cursor: "pointer" }}
            onClick={startRecording}
          >
            Start Recording
          </button>
        ) : (
          <button
            style={{ backgroundColor: "#ff5055", color: "white", padding: "0.5rem 1rem", border: "none", cursor: "pointer" }}
            onClick={stopRecording}
          >
            Stop Recording
          </button>
        )}
      </div>

      {/* Playback */}
      {audioURL && (
        <div style={{ marginTop: "1rem" }}>
          <h3 style={{ color: "black" }}>Preview:</h3>
          <audio controls src={audioURL}></audio>
        </div>
      )}

      {/* Analysis Result */}
      {analysis && (
        <div style={{ marginTop: "2rem" }}>
          <h2 style={{ color: "black" }}>Analysis Result:</h2>
          <pre>{JSON.stringify(analysis, null, 2)}</pre>
        </div>
      )}
    </div>
  );
};

export default AudioPage;
