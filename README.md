# Singing Analysis & Voice Comparison Platform

## Architecture
**Frontend:** React  
**Backend:** Spring Boot (Java)  
**Database:** MongoDB  
**Audio Storage:** S3 / MinIO (or local filesystem for prototyping)  
**Audio Analysis:** TarsosDSP (Java)  

---

## Features
- Upload or record audio
- Pitch extraction, RMS, and onset detection
- Emotion detection (future phase)
- Comparison with original songs (DTW + pitch matching)
- Voice profile creation & synthesis (future integration)
- Real-time pitch visualization via WebSocket (planned)

---

## Project Structure

sing/
├── src/
│ ├── main/
│ │ ├── java/com/sing
│ │ │ ├── SingApplication.java
│ │ │ ├── controller/
│ │ │ │ └── AudioController.java
│ │ │ └── service/
│ │ │ └── AudioFeatureService.java
│ │ └── resources/
│ │ └── application.properties
├── pom.xml
└── README.md

markdown
Copy
Edit

---

## Setup Instructions

1. **Backend**
   - Install Java 17+ and Maven
   - Run Spring Boot:
     ```bash
     mvn spring-boot:run
     ```
   - The backend runs at `http://localhost:8080`

2. **Frontend**
   - React app (planned)
   - Libraries: `react-mic`, `axios`, `recharts`/`chart.js`

3. **Database**
   - MongoDB (local or Docker)
   - Collections: Users, Songs, Recordings, AnalysisResults, ComparisonResults

4. **Audio Storage**
   - Local filesystem for development
   - S3 / MinIO recommended for production

---

## API Endpoints

### Audio Analysis
- **POST** `/api/audio/analyze`
- **Body:** `multipart/form-data` with audio file
- **Response:** JSON
```json
{
  "pitchSeries": { "timeSec": [...], "midi": [...] },
  "rmsSeries": { "timeSec": [...], "rms": [...] },
  "onsetsSeries": { "timesSec": [...] }
}
Comparison
POST /api/compare

Body:

json
Copy
Edit
{
  "originalSongId": "songId123",
  "recordingId": "recId123",
  "options": { "pitchToleranceHz": 5, "timeWarping": true }
}
Response:

json
Copy
Edit
{
  "comparisonId": "compId123",
  "pitchMatchPercent": 85.2,
  "timingMatchPercent": 78.6,
  "pitchSeriesOriginal": [...],
  "pitchSeriesUser": [...],
  "alignedPairsUrl": "https://.../alignedPairs.json"
}
MongoDB Sample Documents
User

json
Copy
Edit
{
  "_id": "userId123",
  "username": "hansika",
  "email": "h@example.com",
  "voiceProfiles": ["voiceIdA", "voiceIdB"]
}
Recording

json
Copy
Edit
{
  "_id": "recId123",
  "songId": "songId123",
  "userId": "userId123",
  "fileUrl": "s3://bucket/recordings/recId123.wav",
  "durationSec": 30.0,
  "analyzed": true,
  "analysisId": "analysisId123"
}
AnalysisResult

json
Copy
Edit
{
  "_id": "analysisId123",
  "recordingId": "recId123",
  "pitchSeries": [{"t":0.0,"f":440.0,"conf":0.98}, ...],
  "emotion": { "label": "happy", "confidence": 0.86 },
  "tempo": 120.3
}
Development & Deployment Notes
Dev: local MinIO, Mongo, Spring Boot, React

Production: S3 storage, JWT auth, worker queues for heavy analysis

Real-time pitch: WebSocket / STOMP

Use Docker Compose for easy multi-service setup
