use serde::{Deserialize, Serialize};
use uuid::Uuid;

#[derive(Debug, Deserialize)]
#[serde(tag = "type", rename_all = "snake_case")]
pub enum ClientMessage {
    Hello {
        player_id: Uuid,
        name: String,
        avatar: u32,
        #[serde(default)]
        mmr: i32,
        #[serde(default = "default_level")]
        level: u32,
        #[serde(default)]
        unlocked_sets: Vec<String>,
    },
    JoinQueue,
    LeaveQueue,
    Ready {
        ready: bool,
    },
    ScoreEvent {
        similarity: Similarity,
    },
    Surrender,
}

fn default_level() -> u32 {
    1
}

#[derive(Debug, Clone, Copy, Deserialize, Serialize, PartialEq, Eq)]
#[serde(rename_all = "snake_case")]
pub enum Similarity {
    Medium,
    High,
    Perfect,
}

#[derive(Debug, Clone, Serialize)]
#[serde(tag = "type", rename_all = "snake_case")]
pub enum ServerMessage {
    QueueStatus {
        position: usize,
        wait_ms: u128,
    },
    MatchFound {
        match_id: Uuid,
        side: Side,
        opponent: OpponentInfo,
    },
    ReadyCheck {
        timeout_ms: u64,
    },
    Countdown {
        seconds: u32,
    },
    GameStart {
        kana: String,
        rope: i32,
    },
    NextKana {
        kana: String,
    },
    RopeUpdate {
        rope: i32,
        p1_combo: u32,
        p2_combo: u32,
        last_points: i32,
        last_side: Side,
    },
    GameOver {
        winner: Option<Side>,
        reason: String,
        rope: i32,
        xp_gained: u32,
        coins_gained: u32,
        mmr_delta: i32,
    },
    Error {
        message: String,
    },
}

#[derive(Debug, Clone, Copy, Serialize, Deserialize, PartialEq, Eq)]
#[serde(rename_all = "snake_case")]
pub enum Side {
    P1,
    P2,
}

#[derive(Debug, Clone, Serialize)]
pub struct OpponentInfo {
    pub name: String,
    pub avatar: u32,
    pub is_bot: bool,
}

/// Messages sent into a running room actor.
#[derive(Debug)]
pub enum RoomInput {
    Ready { player: Uuid, ready: bool },
    Score { player: Uuid, similarity: Similarity },
    Surrender { player: Uuid },
    Disconnect { player: Uuid },
}
