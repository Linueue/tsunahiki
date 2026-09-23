use std::sync::Arc;

use dashmap::DashMap;
use tokio::sync::mpsc::UnboundedSender;
use uuid::Uuid;

use crate::{kana::KanaCatalog, protocol::ServerMessage};

pub type PlayerId = Uuid;

#[derive(Clone)]
pub struct AppState {
    pub inner: Arc<Inner>,
}

pub struct Inner {
    pub catalog: KanaCatalog,
    /// Connected players: player_id -> outbound channel.
    pub sessions: DashMap<PlayerId, UnboundedSender<ServerMessage>>,
    /// Players currently in queue.
    pub queue: DashMap<PlayerId, QueuedPlayer>,
    /// Active game rooms: match_id -> room handle.
    pub rooms: DashMap<Uuid, RoomHandle>,
    /// Which match a player belongs to (so we can route inputs).
    pub player_match: DashMap<PlayerId, Uuid>,
}

#[derive(Clone)]
pub struct QueuedPlayer {
    pub player_id: PlayerId,
    pub name: String,
    pub avatar: u32,
    pub mmr: i32,
    pub level: u32,
    pub unlocked_sets: Vec<String>,
    pub joined_at: std::time::Instant,
}

#[derive(Clone)]
pub struct RoomHandle {
    pub tx: UnboundedSender<protocol::RoomInput>,
}

impl AppState {
    pub fn new(catalog: KanaCatalog) -> Self {
        Self {
            inner: Arc::new(Inner {
                catalog,
                sessions: DashMap::new(),
                queue: DashMap::new(),
                rooms: DashMap::new(),
                player_match: DashMap::new(),
            }),
        }
    }

    pub fn send(&self, player_id: PlayerId, msg: ServerMessage) {
        if let Some(tx) = self.inner.sessions.get(&player_id) {
            let _ = tx.send(msg);
        }
    }
}
