import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import GameHvH from './pages/GameHvH';
import GameHvAi from './pages/GameHvAi';
import GameAiVsAi from './pages/GameAivAi';
import Results from './pages/Results';
import NotFound from './pages/NotFound';

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/humanVhuman" element={<GameHvH />} />
      <Route path="/humanVAi" element={<GameHvAi />} />
      <Route path="/AiVAi" element={<GameAiVsAi />} />
      <Route path="/results" element={<Results />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default App;
