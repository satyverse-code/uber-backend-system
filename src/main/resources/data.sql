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
