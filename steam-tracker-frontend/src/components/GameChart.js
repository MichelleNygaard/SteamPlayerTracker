import React, { useState, useEffect } from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import axios from 'axios';
import { TrendingUp, Calendar, Users, RefreshCw } from 'lucide-react';

const GameChart = ({ gameId, gameName }) => {
    const [chartData, setChartData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [timeRange, setTimeRange] = useState(7);

    useEffect(() => {
        if (gameId) {
            fetchPlayerCountHistory();
        }
    }, [gameId, timeRange]);

    const fetchPlayerCountHistory = async () => {
        setLoading(true);
        setError('');

        try { // Frontend API call
            const response = await axios.get(`http://localhost:8080/api/games/${gameId}/history`, {
                params: { days: timeRange }
            });

            const formattedData = response.data.map(item => ({
                timestamp: new Date(item.timestamp).toLocaleString(),
                playerCount: item.playerCount,
                date: new Date(item.timestamp)
            }));

            setChartData(formattedData);
        } catch (err) {
            setError('Failed to load player count history');
            console.error('Chart data error:', err);
        } finally {
            setLoading(false);
        }
    };

    const getStats = () => {
        if (chartData.length === 0) return null;

        const counts = chartData.map(d => d.playerCount);
        const current = counts[counts.length - 1] || 0;
        const max = Math.max(...counts);
        const min = Math.min(...counts);
        const avg = Math.round(counts.reduce((a, b) => a + b, 0) / counts.length);

        return { current, max, min, avg };
    };

    const stats = getStats();

    const formatPlayerCount = (count) => {
        if (count >= 1000000) return `${(count / 1000000).toFixed(1)}M`;
        if (count >= 1000) return `${(count / 1000).toFixed(1)}K`;
        return count.toString();
    };

    const CustomTooltip = ({ active, payload, label }) => {
        if (active && payload && payload.length) {
            return (
                <div className="chart-tooltip">
                    <p className="tooltip-time">{label}</p>
                    <p className="tooltip-value">
                        Players: {payload[0].value.toLocaleString()}
                    </p>
                </div>
            );
        }
        return null;
    };

    return (
        <div className="game-chart">
            <div className="chart-header">
                <div className="chart-title">
                    <TrendingUp size={24} />
                    <h3>{gameName} - Player Count History</h3>
                </div>

                <div className="chart-controls">
                    <div className="time-range-selector">
                        <Calendar size={16} />
                        <select
                            value={timeRange}
                            onChange={(e) => setTimeRange(Number(e.target.value))}
                            className="time-range-select"
                        >
                            <option value={1}>Last 24 hours</option>
                            <option value={3}>Last 3 days</option>
                            <option value={7}>Last 7 days</option>
                            <option value={30}>Last 30 days</option>
                        </select>
                    </div>

                    <button
                        onClick={fetchPlayerCountHistory}
                        className="refresh-btn"
                        disabled={loading}
                    >
                        <RefreshCw size={16} className={loading ? 'spinning' : ''} />
                        Refresh
                    </button>
                </div>
            </div>

            {stats && (
                <div className="chart-stats">
                    <div className="stat-card">
                        <Users size={16} />
                        <div>
                            <span className="stat-label">Current</span>
                            <span className="stat-value">{stats.current.toLocaleString()}</span>
                        </div>
                    </div>
                    <div className="stat-card">
                        <TrendingUp size={16} />
                        <div>
                            <span className="stat-label">Peak</span>
                            <span className="stat-value">{stats.max.toLocaleString()}</span>
                        </div>
                    </div>
                    <div className="stat-card">
                        <div>
                            <span className="stat-label">Average</span>
                            <span className="stat-value">{stats.avg.toLocaleString()}</span>
                        </div>
                    </div>
                    <div className="stat-card">
                        <div>
                            <span className="stat-label">Minimum</span>
                            <span className="stat-value">{stats.min.toLocaleString()}</span>
                        </div>
                    </div>
                </div>
            )}

            {error && (
                <div className="error-message">
                    {error}
                </div>
            )}

            {loading && (
                <div className="loading">
                    <div className="loading-spinner"></div>
                    Loading chart data...
                </div>
            )}

            {!loading && !error && (
                <div className="chart-container">
                    {chartData.length > 0 ? (
                        <ResponsiveContainer width="100%" height={400}>
                            <LineChart data={chartData}>
                                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                                <XAxis
                                    dataKey="timestamp"
                                    tick={{ fontSize: 12 }}
                                    interval="preserveStartEnd"
                                />
                                <YAxis
                                    tick={{ fontSize: 12 }}
                                    tickFormatter={formatPlayerCount}
                                />
                                <Tooltip content={<CustomTooltip />} />
                                <Line
                                    type="monotone"
                                    dataKey="playerCount"
                                    stroke="#2563eb"
                                    strokeWidth={2}
                                    dot={{ fill: '#2563eb', strokeWidth: 2, r: 3 }}
                                    activeDot={{ r: 6, stroke: '#2563eb', strokeWidth: 2 }}
                                />
                            </LineChart>
                        </ResponsiveContainer>
                    ) : (
                        <div className="no-data">
                            <TrendingUp size={48} />
                            <h4>No data available</h4>
                            <p>
                                {timeRange === 1
                                    ? "No player count data recorded in the last 24 hours"
                                    : `No player count data recorded in the last ${timeRange} days`
                                }
                            </p>
                            <p>Start tracking this game to collect data over time.</p>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default GameChart;