import React, { useState } from 'react';
import axios from 'axios';
import { Plus, Zap } from 'lucide-react';

const QuickAdd = ({ onGameAdded }) => {
    const [loading, setLoading] = useState(false);

    const popularGames = [
        { appId: 730, name: "Counter-Strike 2" },
        { appId: 570, name: "Dota 2" },
        { appId: 440, name: "Team Fortress 2" },
        { appId: 578080, name: "PLAYERUNKNOWN'S BATTLEGROUNDS" },
        { appId: 271590, name: "Grand Theft Auto V" },
        { appId: 252490, name: "Rust" },
        { appId: 1172470, name: "Apex Legends" },
        { appId: 252950, name: "Rocket League" }
    ];

    const quickAddGame = async (game) => {
        setLoading(true);
        try {
            await axios.post(`http://localhost:8080/api/games/add-by-id`, null, {
                params: {
                    appId: game.appId,
                    gameName: game.name
                }
            });

            alert(`Added ${game.name} to tracking!`);
            if (onGameAdded) onGameAdded();
        } catch (err) {
            alert(`Failed to add ${game.name}`);
            console.error('Quick add error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="quick-add">
            <div className="quick-add-header">
                <Zap size={20} />
                <h3>Quick Add Popular Games</h3>
            </div>
            <div className="quick-add-grid">
                {popularGames.map((game) => (
                    <button
                        key={game.appId}
                        onClick={() => quickAddGame(game)}
                        disabled={loading}
                        className="quick-add-btn"
                    >
                        <Plus size={16} />
                        {game.name}
                    </button>
                ))}
            </div>
        </div>
    );
};

export default QuickAdd;