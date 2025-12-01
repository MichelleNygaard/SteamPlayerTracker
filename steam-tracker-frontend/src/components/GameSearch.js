import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Search, Users, Clock, Plus, X } from 'lucide-react';

const GameSearch = ({ onGameSelect }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const timeoutId = setTimeout(() => {
            if (searchTerm.length >= 2) {
                searchGames();
            } else {
                setSearchResults([]);
            }
        }, 500);

        return () => clearTimeout(timeoutId);
    }, [searchTerm]);

    const searchGames = async () => {
        setLoading(true);
        setError('');

        try {
            const response = await axios.get(`http://localhost:8080/api/games/search`, {
                params: { query: searchTerm }
            });
            setSearchResults(response.data);
        } catch (err) {
            setError('Failed to search games. Make sure the backend server is running.');
            console.error('Search error:', err);
        } finally {
            setLoading(false);
        }
    };

    const startTracking = async (game) => {
        try {
            await axios.post(`http://localhost:8080/api/games/${game.appId}/track`, null, {
                params: { gameName: game.name }
            });

            // Update the game in search results to show it's now tracked
            setSearchResults(prev =>
                prev.map(g =>
                    g.appId === game.appId
                        ? { ...g, tracked: true, lastUpdated: new Date().toISOString() }
                        : g
                )
            );

            alert(`Started tracking ${game.name}!`);
        } catch (err) {
            alert('Failed to start tracking game');
            console.error('Tracking error:', err);
        }
    };

    const stopTracking = async (game) => {
        const confirmDelete = window.confirm(
            `Are you sure you want to stop tracking "${game.name}"?\n\nThis will permanently delete all collected player count data for this game.`
        );

        if (!confirmDelete) return;

        try {
            await axios.delete(`http://localhost:8080/api/games/${game.appId}/untrack`);

            // Update the game in search results to show it's no longer tracked
            setSearchResults(prev =>
                prev.map(g =>
                    g.appId === game.appId
                        ? { ...g, tracked: false, lastUpdated: null }
                        : g
                )
            );

            alert(`Stopped tracking ${game.name}!`);
        } catch (err) {
            alert('Failed to stop tracking game');
            console.error('Untracking error:', err);
        }
    };

    const formatPlayerCount = (count) => {
        if (count === null || count === undefined) return 'N/A';
        return count.toLocaleString();
    };

    return (
        <div className="game-search">
            <div className="search-header">
                <h2>Search Steam Games</h2>
                <p>Search for games to view their player count history</p>
            </div>

            <div className="search-input-container">
                <Search className="search-icon" size={20} />
                <input
                    type="text"
                    placeholder="Enter game name (e.g., Counter-Strike, Dota)"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="search-input"
                />
            </div>

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {loading && (
                <div className="loading">
                    <div className="loading-spinner"></div>
                    Searching...
                </div>
            )}

            <div className="search-results">
                {searchResults.map((game) => (
                    <div key={game.appId} className="game-card">
                        <div className="game-info">
                            <h3 className="game-name">{game.name}</h3>
                            <div className="game-stats">
                                <div className="stat">
                                    <Users size={16} />
                                    <span>{formatPlayerCount(game.currentPlayerCount)} players</span>
                                </div>
                                {game.lastUpdated && (
                                    <div className="stat">
                                        <Clock size={16} />
                                        <span>Updated: {new Date(game.lastUpdated).toLocaleString()}</span>
                                    </div>
                                )}
                            </div>
                            <div className="game-status">
                                {game.tracked ? (
                                    <span className="tracked-badge">Tracked</span>
                                ) : (
                                    <span className="not-tracked-badge">Not Tracked</span>
                                )}
                            </div>
                        </div>
                        <div className="game-actions">
                            <button
                                onClick={() => onGameSelect(game)}
                                className="view-chart-btn"
                            >
                                View Chart
                            </button>
                            {game.tracked ? (
                                <button
                                    onClick={() => stopTracking(game)}
                                    className="stop-tracking-btn"
                                >
                                    <X size={16} />
                                    Untrack
                                </button>
                            ) : (
                                <button
                                    onClick={() => startTracking(game)}
                                    className="start-tracking-btn"
                                >
                                    <Plus size={16} />
                                    Track
                                </button>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            {searchTerm.length >= 2 && searchResults.length === 0 && !loading && (
                <div className="no-results">
                    No games found for "{searchTerm}"
                </div>
            )}
        </div>
    );
};

export default GameSearch;