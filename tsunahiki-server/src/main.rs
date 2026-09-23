mod protocol;
mod state;

use std::net::SocketAddr;

use anyhow::Result;
use axum::{
    extract::{
        ws::{Message, WebSocket, WebSocketUpgrade},
        State,
    },
    response::IntoResponse,
    routing::get,
    Router,
};
use futures::{sink::SinkExt, stream::StreamExt};
use tracing_subscriber::{layer::SubscriberExt, util::SubscriberInitExt};

use crate::{state::AppState};

#[tokio::main]
async fn main() -> Result<()> {
    tracing_subscriber::registry()
        .with(
            tracing_subscriber::EnvFilter::try_from_default_env()
                .unwrap_or_else(|_| "info".into()),
        )
        .with(tracing_subscriber::fmt::layer())
        .init();

    let catalog = KanaCatalog::load("kana_sets.toml")?;
    tracing::info!("loaded {} kana sets", catalog.sets.len());

    let state = AppState::new(catalog);

    tokio::spawn(matchmaking::run(state.clone()));

    let app = Router::new()
        .route("/health", get(|| async { "ok" }))
        .route("/ws", get(ws_handler))
        .with_state(state);

    let addr: SocketAddr = "0.0.0.0:8080".parse()?;
    tracing::info!("listening on {addr}");
    let listener = tokio::net::TcpListener::bind(addr).await?;
    axum::serve(listener, app).await?;
    Ok(())
}

async fn ws_handler(
    ws: WebSocketUpgrade,
    State(state): State<AppState>,
) -> impl IntoResponse {
    ws.on_upgrade(move |socket| client_socket(socket, state))
}

async fn client_socket(socket: WebSocket, state: AppState) {
    let (mut sender, mut receiver) = socket.split();

    // Wait for the first message: it must be a `hello` with a player_id/name.
    let first = match receiver.next().await {
        Some(Ok(Message::Text(t))) => t,
        _ => return,
    };

    let hello: protocol::ClientMessage = match serde_json::from_str(&first) {
        Ok(m) => m,
        Err(_) => return,
    };

    let (player_id, name, avatar) = match hello {
        protocol::ClientMessage::Hello { player_id, name, avatar } => {
            (player_id, name, avatar)
        }
        _ => return,
    };

    let (tx, mut rx) = tokio::sync::mpsc::unbounded_channel::<protocol::ServerMessage>();

    // Outbound task.
    let outbound = tokio::spawn(async move {
        while let Some(msg) = rx.recv().await {
            let txt = match serde_json::to_string(&msg) {
                Ok(t) => t,
                Err(_) => continue,
            };
            if sender.send(Message::Text(txt)).await.is_err() {
                break;
            }
        }
    });

    handle_socket(state, player_id, name, avatar, tx, &mut receiver).await;

    outbound.abort();
}
