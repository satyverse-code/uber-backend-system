import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

export let bookingLatency = new Trend('booking_latency_ms');
export let bookingSuccess = new Rate('booking_success_rate');

export let options = {
  stages: [
    { duration: '1m', target: 50 },
    { duration: '3m', target: 200 },
    { duration: '2m', target: 0 },
  ],
  thresholds: {
    booking_latency_ms: ['p(95)<1500'],
    booking_success_rate: ['rate>0.98'],
  },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';
const TOKEN = __ENV.AUTH_TOKEN || '';

export default function () {
  const payload = JSON.stringify({
    riderId: Math.floor(Math.random() * 1000) + 1,
    pickupLocation: '12.9716,77.5946',
    dropoffLocation: '12.9352,77.6245',
  });

  const headers = { 'Content-Type': 'application/json' };
  if (TOKEN) headers['Authorization'] = `Bearer ${TOKEN}`;

  const res = http.post(`${BASE}/api/rides`, payload, { headers });
  bookingLatency.add(res.timings.duration);
  bookingSuccess.add(res.status === 201);

  check(res, { 'is created': (r) => r.status === 201 });

  sleep(0.5);
}
