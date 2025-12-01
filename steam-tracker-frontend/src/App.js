import React, { useState } from 'react';
import './App.css';
import GameSearch from './components/GameSearch';
import GameChart from './components/GameChart';
import TrackedGames from './components/TrackedGames';
import QuickAdd from './components/QuickAdd';
import { Search, TrendingUp, Database } from 'lucide-react';

function App() {
    const [selectedGame, setSelectedGame] = useState(null);
    const [activeTab, setActiveTab] = useState('search');
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const handleGameAdded = () => {
        setRefreshTrigger(prev => prev + 1);
    };

    return (
        <div className="App">
            <header className="app-header">
                <div className="header-content">
                    <div className="logo">
                        <TrendingUp size={32} />
                        <h1>Steam Player Tracker</h1>
                    </div>
                    <p>Track and visualize Steam game player counts over time</p>
                </div>
            </header>

            <nav className="main-nav">
                <button
                    className={`nav-button ${activeTab === 'search' ? 'active' : ''}`}
                    onClick={() => setActiveTab('search')}
                >
                    <Search size={20} />
                    Search Games
                </button>
                <button
                    className={`nav-button ${activeTab === 'tracked' ? 'active' : ''}`}
                    onClick={() => setActiveTab('tracked')}
                >
                    <Database size={20} />
                    Tracked Games
                </button>
            </nav>

            <main className="main-content">
                {activeTab === 'search' && (
                    <div className="tab-content">
                        <QuickAdd onGameAdded={handleGameAdded} />
                        <GameSearch onGameSelect={setSelectedGame} />
                        {selectedGame && (
                            <GameChart
                                gameId={selectedGame.appId}
                                gameName={selectedGame.name}
                            />
                        )}
                    </div>
                )}

                {activeTab === 'tracked' && (
                    <div className="tab-content">
                        <TrackedGames
                            onGameSelect={setSelectedGame}
                            refreshTrigger={refreshTrigger}
                        />
                        {selectedGame && (
                            <GameChart
                                gameId={selectedGame.appId}
                                gameName={selectedGame.name}
                            />
                        )}
                    </div>
                )}
            </main>
        </div>
    );
}

export default App;