## How to run the application

### 1. Database setup 
1. **Install PostgeSQL** 
2. **Start PostgreSQL service**
3. **Create a database** named `player_count`

### 2. API key
My Steam API key is included and already set up in the application.yml 
file and is valid for till the end of July before i dissable it. 
To achieve your own:
1. **Visit**: https://steamcommunity.com/dev/apikey
2. **log in** with your Steam account
3. **Fill out the form**:
   - Domain Name: `localhost`
   - Agree to the Steam Web API terms
4. **Copy your API key** and insert in the application.yml file. 

#### 3. Install Dependencies and Run
1. **Open the project in IntelliJ IDEA** (or any idea you can import maven dependencies)
2. **Import maven dependencies** (Usually done automatically in IntelliJ)
3. **Run the main application**:
   - Find `SteamPlayerTrackerApplication.java`
   - Right-click -> run 
   - Or use the green play button.
The backend will start on `http://localhost:8080`

### 4. Frontend Setup 
#### Navigate to the Frontend Directory. 
```bash
cd steam-tracker-frontend
```

#### Install dependencies 
```bash
npm install
```

#### Start Development Server
```bash
npm start
```

The frontend will start on `http://localhost:3000` and automatically open in your browser. 


## Usage Guide

### Getting Started

1. **Start both servers** (backend on 8080, frontend on 3000)
2. **Open your browser** to `http://localhost:3000`
3. **Add some games** using the Quick Add buttons or search functionality
4. **Wait for data collection** or click "Collect Data" manually
5. **View charts** by clicking "View Player History"

### Adding Games

#### Method 1: Quick Add (Recommended for Testing)
- Use the **Quick Add** buttons for popular games like Counter-Strike 2, Dota 2, etc.
- These games have guaranteed player data

#### Method 2: Search
- Type a game name in the search box (minimum 2 characters)
- Click **"Track"** on any game you want to monitor
- Click **"View Chart"** to see historical data

### Viewing Data

1. **Tracked Games Tab**: See all games you're currently monitoring
2. **Charts**: Interactive graphs with customizable time ranges (24h, 3d, 7d, 30d)
3. **Real-time Updates**: Data automatically updates every 30 seconds
4. **Manual Refresh**: Use "Collect Data" button for immediate updates

### Managing Tracked Games

- **Untrack Games**: Click the red X button on any tracked game
- **Confirmation**: System will ask before deleting all historical data
- **Search Status**: Games show "Tracked" or "Not Tracked" status in search results