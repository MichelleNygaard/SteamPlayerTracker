import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Database, Clock, TrendingUp, RefreshCw, X, AlertTriangle } from 'lucide-react';

const TrackedGames = ({ onGameSelect, refreshTrigger }) => {
    const [trackedGames, setTrackedGames] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchTrackedGames();
    }, [refreshTrigger]);

    const fetchTrackedGames = async () => {
        setLoading(true);
        setError('');

        try {
            const response = await axios.get('http://localhost:8080/api/games/tracked');
            console.log('Tracked games response:', response.data); // Debug log

            // Ensure we have an array
            if (Array.isArray(response.data)) {
                setTrackedGames(response.data);
            } else {
                console.error('Expected array but got:', typeof response.data, response.data);
                setTrackedGames([]);
                setError('Invalid response format from server');
            }
        } catch (err) {
            setError('Failed to load tracked games');
            console.error('Tracked games error:', err);
            setTrackedGames([]); // Ensure it's always an array
        } finally {
            setLoading(false);
        }
    };

    const collectAllPlayerCounts = async () => {
        try {
            setLoading(true);
            await axios.post('http://localhost:8080/api/games/collect');

            // Refresh the tracked games list to get updated timestamps
            await fetchTrackedGames();

            alert('Player counts collected successfully!');
        } catch (err) {
            alert('Failed to collect player counts');
            console.error('Collection error:', err);
        } finally {
            setLoading(false);
        }
    };

    const untrackGame = async (game) => {
        const confirmDelete = window.confirm(
            `Are you sure you want to stop tracking "${game.name}"?\n\nThis will permanently delete all collected player count data for this game.`
        );

        if (!confirmDelete) return;

        try {
            setLoading(true);
            await axios.delete(`http://localhost:8080/api/games/${game.appId}/untrack`);

            // Refresh the tracked games list
            await fetchTrackedGames();

            alert(`Stopped tracking ${game.name}`);
        } catch (err) {
            alert(`Failed to untrack ${game.name}`);
            console.error('Untrack error:', err);
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'Never';
        return new Date(dateString).toLocaleString();
    };

    const getTimeSinceUpdate = (dateString) => {
        if (!dateString) return 'Never updated';

        const now = new Date();
        const updated = new Date(dateString);
        const diffMs = now - updated;
        const diffMins = Math.floor(diffMs / (1000 * 60));
        const diffHours = Math.floor(diffMins / 60);
        const diffDays = Math.floor(diffHours / 24);

        if (diffMins < 1) return 'Just now';
        if (diffMins < 60) return `${diffMins} min ago`;
        if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`;
        return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`;
    };

    return (
        <div className="tracked-games">
            <div className="tracked-header">
                <div className="header-content">
                    <Database size={24} />
                    <div>
                        <h2>Tracked Games</h2>
                        <p>Games currently being monitored for player count changes</p>
                    </div>
                </div>

                <div className="header-actions">
                    <button
                        onClick={fetchTrackedGames}
                        className="refresh-btn"
                        disabled={loading}
                    >
                        <RefreshCw size={16} className={loading ? 'spinning' : ''} />
                        Refresh
                    </button>

                    <button
                        onClick={collectAllPlayerCounts}
                        className="collect-btn"
                        disabled={loading}
                    >
                        <TrendingUp size={16} />
                        Collect Data
                    </button>
                </div>
            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {loading && (
                <div className="loading">
                    <div className="loading-spinner"></div>
                    Loading tracked games...
                </div>
            )}

            <div className="tracked-games-grid">
                {Array.isArray(trackedGames) && trackedGames.length > 0 ? (
                    trackedGames.map((game) => (
                        <div key={game.appId} className="tracked-game-card">
                            <div className="game-header">
                                <div className="game-title">
                                    <h3 className="game-name">{game.name}</h3>
                                    <span className="game-id">ID: {game.appId}</span>
                                </div>
                                <button
                                    onClick={() => untrackGame(game)}
                                    className="untrack-btn"
                                    disabled={loading}
                                    title="Stop tracking this game"
                                >
                                    <X size={16} />
                                </button>
                            </div>

                            <div className="game-metadata">
                                <div className="metadata-item">
                                    <Clock size={14} />
                                    <span>Added: {formatDate(game.createdAt)}</span>
                                </div>
                                <div className="metadata-item">
                                    <RefreshCw size={14} />
                                    <span>Updated: {getTimeSinceUpdate(game.lastUpdated)}</span>
                                </div>
                            </div>

                            <div className="game-actions">
                                <button
                                    onClick={() => onGameSelect(game)}
                                    className="view-chart-btn"
                                >
                                    <TrendingUp size={16} />
                                    View Player History
                                </button>
                            </div>
                        </div>
                    ))
                ) : (
                    !loading && (
                        <div className="no-tracked-games">
                            <Database size={48} />
                            <h3>No Games Tracked</h3>
                            <p>Use the search feature to find games and start tracking their player counts.</p>
                        </div>
                    )
                )}
            </div>

            {Array.isArray(trackedGames) && trackedGames.length > 0 && (
                <div className="tracking-info">
                    <div className="info-card">
                        <h4>Automatic Collection</h4>
                        <p>Player counts are automatically collected every 30 minutes for all tracked games.</p>
                    </div>
                    <div className="info-card">
                        <h4>Manual Collection</h4>
                        <p>Use the "Collect Data" button to manually trigger data collection for all games.</p>
                    </div>
                </div>
            )}
        </div>
    );
};

export default TrackedGames;