import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 25,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.1'],
  },
};

function startSaga() {
  const url = 'http://localhost:8090/orchestrator/start';
  const payload = JSON.stringify({
    riderId: 1,
    pickup: 'Mumbai',
    dropoff: 'Andheri',
    lat: 19.0760,
    lng: 72.8777,
  });
  const params = { headers: { 'Content-Type': 'application/json' } };
  const res = http.post(url, payload, params);
  check(res, { 'start 202/200': (r) => r.status === 202 || r.status === 200 });
  try {
    const body = JSON.parse(res.body);
    return body.sagaId;
  } catch (_) {
    return null;
  }
}

function reserveDriver(sagaId) {
  if (!sagaId) return null;
  const url = `http://localhost:8090/orchestrator/${sagaId}/reserve-driver`;
  const res = http.post(url, null, { headers: { 'Content-Type': 'application/json' } });
  check(res, { 'reserve 200': (r) => r.status === 200 });
  return res;
}

export default function () {
  const sagaId = startSaga();
  sleep(0.2); // small think time
  reserveDriver(sagaId);
  sleep(0.3);
}
