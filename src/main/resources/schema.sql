CREATE TABLE IF NOT EXISTS riders (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS drivers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    car_number VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    current_lat DOUBLE PRECISION,
    current_long DOUBLE PRECISION,
    location GEOGRAPHY(Point, 4326)
);

CREATE TABLE IF NOT EXISTS rides (
    id BIGSERIAL PRIMARY KEY,
    rider_id BIGINT NOT NULL REFERENCES riders(id),
    driver_id BIGINT REFERENCES drivers(id),
    source VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Enable PostGIS (safe if already enabled)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Index for geospatial lookups
CREATE INDEX IF NOT EXISTS idx_drivers_location ON drivers USING GIST (location);
