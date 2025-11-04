INSERT INTO riders (name, phone) VALUES
  ('Alice Johnson', '+1555000001')
ON CONFLICT (phone) DO NOTHING;

INSERT INTO riders (name, phone) VALUES
  ('Bob Singh', '+1555000002')
ON CONFLICT (phone) DO NOTHING;

INSERT INTO riders (name, phone) VALUES
  ('Carlos Diaz', '+1555000003')
ON CONFLICT (phone) DO NOTHING;

INSERT INTO drivers (name, car_number, status) VALUES
  ('Dev Patel', 'MH01AB1234', 'AVAILABLE')
ON CONFLICT (car_number) DO NOTHING;

INSERT INTO drivers (name, car_number, status) VALUES
  ('Emma Watson', 'DL05CD5678', 'AVAILABLE')
ON CONFLICT (car_number) DO NOTHING;

-- Seed approximate coordinates and compute geospatial location
UPDATE drivers SET current_lat = 19.0760, current_long = 72.8777
WHERE car_number = 'MH01AB1234';

UPDATE drivers SET current_lat = 28.6139, current_long = 77.2090
WHERE car_number = 'DL05CD5678';

UPDATE drivers SET location = ST_SetSRID(ST_MakePoint(current_long, current_lat), 4326)::geography
WHERE current_lat IS NOT NULL AND current_long IS NOT NULL;
